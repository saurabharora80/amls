/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import java.io.InputStream
import javax.inject.Inject

import audit.SubscriptionValidationFailedEvent
import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import config.{AppConfig, MicroserviceAuditConnector}
import connectors.{EnrolmentStoreConnector, GovernmentGatewayAdminConnector, SubscribeDESConnector}
import exceptions.{DuplicateSubscriptionException, HttpExceptionBody, HttpStatusException}
import models.des.SubscriptionRequest
import models.enrolment.AmlsEnrolmentKey
import models.fe.{SubscriptionErrorResponse, SubscriptionFees, SubscriptionResponse}
import models.{Fees, KnownFact, KnownFactsForService}
import play.api.Logger
import play.api.libs.json.{JsResult, JsValue, Json}
import play.mvc.Http.Status._
import repositories.FeesRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.{ExecutionContext, Future}

class SubscriptionService @Inject()(
                                     private[services] val desConnector: SubscribeDESConnector,
                                     private[services] val ggConnector: GovernmentGatewayAdminConnector,
                                     private[services] val enrolmentStoreConnector: EnrolmentStoreConnector,
                                     private[services] val auditConnector: AuditConnector = MicroserviceAuditConnector,
                                     val config: AppConfig
                                   ) {

  private[services] val feeResponseRepository: FeesRepository = FeesRepository()

  private val amlsRegistrationNumberRegex = "X[A-Z]ML00000[0-9]{6}$".r

  private[services] def validateResult(request: SubscriptionRequest): JsResult[JsValue] = {
    SchemaValidator().validate(Json.fromJson[SchemaType](Json.parse(linesString.trim.drop(1))).get, Json.toJson(request))
  }

  private lazy val stream: InputStream = getClass.getResourceAsStream("/resources/API4_Request.json")
  private lazy val lines = scala.io.Source.fromInputStream(stream).getLines
  protected[SubscriptionService] lazy val linesString: String = lines.foldLeft[String]("")((x, y) => x.trim ++ y.trim)

  private def duplicateSubscriptionErrorHandler(request: SubscriptionRequest)
                                               (implicit ec: ExecutionContext): PartialFunction[Throwable, Future[SubscriptionResponse]] = {
    case ex@HttpStatusException(BAD_REQUEST, _) => {
      ex.jsonBody map {
        case body if body.reason.startsWith(Constants.duplicateSubscriptionErrorMessage) =>
          amlsRegistrationNumberRegex
            .findFirstIn(body.reason)
            .fold[Future[SubscriptionResponse]](failResponse(ex, body)) {
            amlsRegNo => {
              Logger.warn(s"[SubscriptionService] - Reconstructing Subscription Response after Duplicate error for $amlsRegNo")
              constructedSubscriptionResponse(amlsRegNo, request)(ec) flatMap {
                case response if response.subscriptionFees.isDefined => Future.successful(response)
                case _ =>
                  Logger.warn(s"[SubscriptionService] - Reconstructed Subscription Response contains no fees for $amlsRegNo; failing..")
                  failResponse(DuplicateSubscriptionException(ex, amlsRegNo, body.reason), body)
              }
            }
          }
        case body =>
          failResponse(ex, body)
      }
    }.getOrElse(Future.failed(ex))

    case e@HttpStatusException(status, Some(body)) =>
      Logger.warn(s" - Status: $status, Message: $body")
      Future.failed(e)
  }

  def subscribe
  (safeId: String, request: SubscriptionRequest)
  (implicit
   hc: HeaderCarrier,
   ec: ExecutionContext
  ): Future[SubscriptionResponse] = {

    validateRequest(safeId, request)

    for {
      response <- desConnector.subscribe(safeId, request)
        .map(desResponse => SubscriptionResponse.convert(desResponse))
        .recoverWith {
          duplicateSubscriptionErrorHandler(request)
        }
      _ <- Fees.convertSubscription(response) match {
        case Some(fees) => feeResponseRepository.insert(fees)
        case _ => Future.successful(false)
      }
      _ <- addKnownFacts(safeId, request, response)

    } yield response

  }

  private def getKnownFacts(safeId: String, request: SubscriptionRequest, response: SubscriptionResponse) = {
    val facts = Seq(
      KnownFact("MLRRefNumber", response.amlsRefNo),
      KnownFact("SafeId", safeId)
    )

    if (request.businessContactDetails.businessAddress.postcode.isDefined) {
      KnownFactsForService(facts :+ KnownFact("POSTCODE", request.businessContactDetails.businessAddress.postcode.get))
    } else {
      KnownFactsForService(facts)
    }
  }

  private def validateRequest(safeId: String, request: SubscriptionRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext): Unit = {
    val result = validateResult(request)
    if (!result.isSuccess) {
      val errors = result.fold(invalid = { errors =>
        errors.foldLeft[String]("") {
          (a, b) => a + "," + b._1.toJsonString
        }
      }, valid = identity)

      result.fold(invalid = validationResult => {
        val resultObjects = validationResult.map {
          case (path, messages) => Json.obj(
            "path" -> path.toJsonString,
            "messages" -> messages.map(_.messages)
          )
        }

        auditConnector.sendExtendedEvent(SubscriptionValidationFailedEvent(safeId, request, resultObjects))
      }, valid = identity)

      Logger.warn(s"[SubscriptionService][subscribe] Schema Validation Failed : safeId: $safeId : Error Paths : $errors")
    } else {
      Logger.debug(s"[SubscriptionService][subscribe] : safeId: $safeId : Validation passed")
    }
  }

  private def failResponse(ex: Throwable, body: HttpExceptionBody) = {
    ex match {
      case e: HttpStatusException => Logger.warn(s" - Status: ${e.status}, Message: $body")
      case _ => Logger.warn(s" - Exception thrown - Message: $body")
    }

    Future.failed(ex)
  }

  private def constructedSubscriptionResponse(amlsRegNo: String, request: SubscriptionRequest)(implicit ec: ExecutionContext) = {

    def tradingPremisesCount = {
      (for {
        agentPremises <- request.tradingPremises.agentBusinessPremises
        agentPremisesDetails <- agentPremises.agentDetails
      } yield agentPremisesDetails.size).getOrElse(0) +
        (for {
          ownPremises <- request.tradingPremises.ownBusinessPremises
          ownBusinessPremisesDetails <- ownPremises.ownBusinessPremisesDetails
        } yield ownBusinessPremisesDetails.size).getOrElse(0)
    }

    def responsiblePersonsCount = {
      request.responsiblePersons.fold(0) { rp => rp.size }
    }

    def responsiblePersonsPassedFitAndProperCount = {
      request.responsiblePersons.fold(0) { rp =>
        rp.count(_.msbOrTcsp.fold(false) {
          _.passedFitAndProperTest

        })
      }
    }

    feeResponseRepository.findLatestByAmlsReference(amlsRegNo) map {
      case Some(fees) => SubscriptionResponse("",
        amlsRegNo,
        responsiblePersonsCount,
        responsiblePersonsPassedFitAndProperCount,
        tradingPremisesCount,
        Some(SubscriptionFees(fees.paymentReference.getOrElse(""),
          fees.registrationFee, fees.fpFee, None, fees.premiseFee, None, fees.totalFees)), previouslySubmitted = true)

      case None => SubscriptionResponse("",
        amlsRegNo,
        responsiblePersonsCount,
        responsiblePersonsPassedFitAndProperCount,
        tradingPremisesCount,
        None,
        previouslySubmitted = true)
    }

  }

  private def addKnownFacts(safeId: String, request: SubscriptionRequest, response: SubscriptionResponse)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext) = {
    (if (config.enrolmentStoreToggle) {
      enrolmentStoreConnector.addKnownFacts(AmlsEnrolmentKey(response.amlsRefNo), getKnownFacts(safeId, request, response))
    } else {
      ggConnector.addKnownFacts(getKnownFacts(safeId, request, response))
    }) map (_ => response) recover {
      case ex => Logger.warn("[AddKnownFactsFailed]", ex)
        response
    }
  }

}