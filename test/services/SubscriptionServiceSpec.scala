/*
 * Copyright 2017 HM Revenue & Customs
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

import com.eclipsesource.schema.SchemaValidator
import connectors.{DESConnector, GovernmentGatewayAdminConnector, SubscribeDESConnector}
import models.des
import models.{KnownFact, KnownFactsForService, fe}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, OneServerPerSuite, PlaySpec}
import org.mockito.Mockito._
import org.mockito.Matchers.{eq => eqTo, _}
import play.api.libs.json.{JsResult, JsValue, Json, Writes}
import repositories.FeeResponseRepository
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.eclipsesource.schema._
import exceptions.HttpStatusException
import models.des.SubscriptionRequest
import play.api.test.Helpers.BAD_REQUEST

class SubscriptionServiceSpec extends PlaySpec with MockitoSugar with ScalaFutures with IntegrationPatience with OneAppPerSuite {

  case class Foo(bar: String, quux: Option[String])

  implicit val format = Json.format[Foo]

  val successValidate: JsResult[JsValue] = mock[JsResult[JsValue]]
  val duplicateSubscriptionMessage = "Business Partner already has an active AMLS Subscription with MLR Ref Number"


  object SubscriptionService extends SubscriptionService {
    override private[services] val desConnector = mock[SubscribeDESConnector]
    override private[services] val ggConnector = mock[GovernmentGatewayAdminConnector]
    override private[services] val feeResponseRepository = mock[FeeResponseRepository]

    override private[services] def validateResult(request: SubscriptionRequest) = successValidate
  }

  val response = des.SubscriptionResponse(
    etmpFormBundleNumber = "111111",
    amlsRefNo = "XAML00000567890",
    Some(150.00),
    Some(100.0),
    300.0,
    550.0,
    "XA353523452345"
  )

  val request = mock[des.SubscriptionRequest]

  val safeId = "safeId"

  val knownFacts = KnownFactsForService(Seq(
    KnownFact("SafeId", safeId),
    KnownFact("MLRRefNumber", response.amlsRefNo)
  ))

  implicit val hc = HeaderCarrier()

  "SubscriptionService" must {

    "return a successful response" when {

      "connector returns full response" in {

        when {
          successValidate.isSuccess
        } thenReturn true

        when {
          SubscriptionService.desConnector.subscribe(eqTo(safeId), eqTo(request))(any(), any(), any(), any())
        } thenReturn Future.successful(response)

        when {
          SubscriptionService.ggConnector.addKnownFacts(eqTo(knownFacts))(any(), any())
        } thenReturn Future.successful(mock[HttpResponse])

        when(SubscriptionService.feeResponseRepository.insert(any())).thenReturn(Future.successful(true))

        whenReady(SubscriptionService.subscribe(safeId, request)) {
          result =>
            result mustEqual (response)
            verify(SubscriptionService.ggConnector, times(1)).addKnownFacts(eqTo(knownFacts))(any(), any())
        }
      }

      "connector returns duplicate response with amlsregno" in {

        val jsonBody = Json.obj("reason" -> (duplicateSubscriptionMessage + " XGML00000000000")).toString

        when {
          successValidate.isSuccess
        } thenReturn true

        when {
          SubscriptionService.desConnector.subscribe(eqTo(safeId), eqTo(request))(any(), any(), any(), any())
        } thenReturn Future.failed(new HttpStatusException(BAD_REQUEST, Some(jsonBody)))

        val knownFacts = KnownFactsForService(Seq(
          KnownFact("SafeId", safeId),
          KnownFact("MLRRefNumber", "XGML00000000000")
        ))

        when {
          SubscriptionService.ggConnector.addKnownFacts(eqTo(knownFacts))(any(), any())
        } thenReturn Future.successful(mock[HttpResponse])

        when(SubscriptionService.feeResponseRepository.insert(any())).thenReturn(Future.successful(true))

        whenReady(SubscriptionService.subscribe(safeId, request)) {
          result =>
            result.amlsRefNo mustEqual ("XGML00000000000")
            verify(SubscriptionService.ggConnector, times(1)).addKnownFacts(eqTo(knownFacts))(any(), any())
        }
      }
    }
  }
}

