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

import audit.AmendmentEvent
import exceptions.HttpStatusException
import metrics.API6
import models.des
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.{JsSuccess, Json, Writes}
import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.{ HeaderCarrier, HeaderNames, HttpPut, HttpReads, HttpResponse }

trait AmendVariationDESConnector extends DESConnector {

  private[connectors] def httpPut: HttpPut

  def amend
  (amlsRegistrationNumber: String, data: des.AmendVariationRequest)
  (implicit
   ec: ExecutionContext,
   wr1: Writes[des.AmendVariationRequest],
   wr2: Writes[des.AmendVariationResponse],
   hc: HeaderCarrier
  ): Future[des.AmendVariationResponse] = {
    val prefix = "[DESConnector][amend]"
    val bodyParser = JsonParsed[des.AmendVariationResponse]
    val timer = metrics.timer(API6)
    Logger.debug(s"$prefix - Request body: ${Json.toJson(data)}")

    val url = s"$fullUrl/$amlsRegistrationNumber"

    httpPut.PUT[des.AmendVariationRequest, HttpResponse](url, data)(wr1, implicitly[HttpReads[HttpResponse]], desHeaderCarrier,ec) map {
      response =>
        timer.stop()
        Logger.debug(s"$prefix - Base Response: ${response.status}")
        Logger.debug(s"$prefix - Response Body: ${response.body}")
        response
    } flatMap {
      case r@status(OK) & bodyParser(JsSuccess(body: des.AmendVariationResponse, _)) =>
        metrics.success(API6)
        auditConnector.sendExtendedEvent(AmendmentEvent(amlsRegistrationNumber, data, body))
        Logger.debug(s"$prefix - Success response")
        Logger.debug(s"$prefix - Response body: ${Json.toJson(body)}")
        Logger.debug(s"$prefix - CorrelationId: ${r.header("CorrelationId") getOrElse ""}")
        Future.successful(body)
      case r@status(s) =>
        metrics.failed(API6)
        Logger.warn(s"$prefix - Failure response: $s")
        Logger.warn(s"$prefix - CorrelationId: ${r.header("CorrelationId") getOrElse ""}")
        Future.failed(HttpStatusException(s, Option(r.body)))
    } recoverWith {
      case e: HttpStatusException =>
        Logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(e)
      case e =>
        timer.stop()
        metrics.failed(API6)
        Logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage)))
    }
  }

}
