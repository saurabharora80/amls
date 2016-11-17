/*
 * Copyright 2016 HM Revenue & Customs
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

package controllers

import models.des.FeeResponse
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.Action
import repositories.FeeResponseRepository
import uk.gov.hmrc.play.microservice.controller.BaseController
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

trait FeeResponseController extends BaseController {

  private[controllers] def repository: FeeResponseRepository

  def get(accountType: String, ref: String, amlsRegistrationNumber: String) =
    Action.async {
      implicit request => {
        repository.findLatestByAmlsReference(amlsRegistrationNumber) map {
          case Some(feeResponse) => {
            Logger.debug(s"[FeeResponseController - get : ${Json.toJson(feeResponse)}]")
            Ok(Json.toJson[FeeResponse](feeResponse))
          }
          case None => NotFound
        }
      }.recoverWith {
        case e:Throwable => {
          Logger.error(s"[FeeResponseController - get] ",e)
          Future.successful(InternalServerError)
        }
      }

    }

}

object FeeResponseController extends FeeResponseController {
  // $COVERAGE-OFF$
  override private[controllers] val repository: FeeResponseRepository = FeeResponseRepository()
}