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

package models.fe.businessmatching

import models.des.businessdetails.{BusinessDetails, CorpAndBodyLlps}
import play.api.libs.json.Json

case class CompanyRegistrationNumber(companyRegistrationNumber: String)

object CompanyRegistrationNumber {

  implicit val formats = Json.format[CompanyRegistrationNumber]

  implicit def conv(desBusinessDetails: BusinessDetails): Option[CompanyRegistrationNumber] = {
    desBusinessDetails.corpAndBodyLlps match {
      case Some(data) => Some(CompanyRegistrationNumber(data.companyRegNo))
      case _ => None
    }
  }
}
