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

package connectors

import javax.inject.Inject

import audit.KnownFactsEvent
import config.{AppConfig, MicroserviceAuditConnector}
import exceptions.HttpStatusException
import metrics.{EnrolmentStoreKnownFacts, Metrics}
import models.enrolment.{AmlsEnrolmentKey, KnownFacts}
import play.api.Logger
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NO_CONTENT}
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.http.{CorePut, HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.audit.model.Audit
import uk.gov.hmrc.play.config.AppName
import utils.HttpResponseHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EnrolmentStoreConnector @Inject()(
                                         val http: CorePut,
                                         val metrics: Metrics,
                                         config: AppConfig) extends HttpResponseHelper {

  def addKnownFacts(enrolmentKey: AmlsEnrolmentKey, knownFacts: KnownFacts)(implicit
                                                                            headerCarrier: HeaderCarrier,
                                                                            writes: Writes[KnownFacts]): Future[HttpResponse] = {

    val url = s"${config.enrolmentStoreUrl}/enrolment-store/enrolments/${enrolmentKey.key}"

    val prefix = "[EnrolmentStore][Enrolments]"
    val timer = metrics.timer(EnrolmentStoreKnownFacts)

    val audit: Audit = new Audit(AppName.appName, MicroserviceAuditConnector)

    Logger.debug(s"$prefix - Request body: ${Json.toJson(knownFacts)}")

    http.PUT(url, knownFacts) map { response =>
      timer.stop()
      Logger.debug(s"$prefix - Base Response: ${response.status}")
      Logger.debug(s"$prefix - Response body: ${response.body}")
      response
    } flatMap {
      case response @ status(NO_CONTENT) =>
        metrics.success(EnrolmentStoreKnownFacts)
        audit.sendDataEvent(KnownFactsEvent(knownFacts))
        Logger.debug(s"$prefix - Success Response")
        Logger.debug(s"$prefix - Response body: ${Option(response.body) getOrElse ""}")
        Future.successful(response)
      case response @ status(s) =>
        metrics.failed(EnrolmentStoreKnownFacts)
        Logger.warn(s"$prefix - Failure Response: $s")
        Logger.warn(s"$prefix - Response body: ${Option(response.body) getOrElse ""}")
        Future.failed(HttpStatusException(s, Option(response.body)))
    } recoverWith {
      case e: HttpStatusException =>
        Logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(e)
      case e =>
        timer.stop()
        metrics.failed(EnrolmentStoreKnownFacts)
        Logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage)))
    }

  }


}
