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

package connectors

import config.{AmlsConfig, MicroserviceAuditConnector, WSHttp}
import metrics.Metrics
import play.mvc.Http.HeaderNames
import uk.gov.hmrc.play.audit.model.Audit
import uk.gov.hmrc.play.config.AppName
import uk.gov.hmrc.play.http.logging.Authorization
import uk.gov.hmrc.play.http.{HeaderNames => _, _}
import utils.HttpResponseHelper

trait DESConnector extends HttpResponseHelper {

  private[connectors] def baseUrl: String
  private[connectors] def env: String
  private[connectors] def token: String
  private[connectors] def httpPost: HttpPost
  private[connectors] def httpGet: HttpGet
  private[connectors] def metrics: Metrics
  private[connectors] def audit: Audit
  private[connectors] def fullUrl: String


  val requestUrl = "anti-money-laundering/subscription"

  protected implicit val hc = HeaderCarrier(
    extraHeaders = Seq(
      "Environment" -> env,
      HeaderNames.ACCEPT -> "application/json"
    ),
    authorization = Some(Authorization(token))
  )
}


object DESConnector extends SubscribeDESConnector
  with SubscriptionStatusDESConnector
  with ViewDESConnector
  with AmendVariationDESConnector {

  // $COVERAGE-OFF$
  override private[connectors] lazy val baseUrl: String = AmlsConfig.desUrl
  override private[connectors] lazy val token: String = s"Bearer ${AmlsConfig.desToken}"
  override private[connectors] lazy val env: String = AmlsConfig.desEnv
  override private[connectors] lazy val httpPost: HttpPost = WSHttp
  override private[connectors] lazy val httpPut: HttpPut = WSHttp
  override private[connectors] lazy val httpGet: HttpGet = WSHttp
  override private[connectors] val metrics: Metrics = Metrics
  override private[connectors] val audit: Audit = new Audit(AppName.appName, MicroserviceAuditConnector)
  override private[connectors] val fullUrl: String = s"$baseUrl/$requestUrl"
}
