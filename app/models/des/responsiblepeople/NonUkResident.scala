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

package models.des.responsiblepeople

import models.fe.responsiblepeople.{PassportTypeNoPassport, PassportTypeNonUKPassport, PassportTypeUKPassport, NonUKResidence}
import play.api.libs.json.Json

case class NonUkResident (dateOfBirth: String,
                          passportHeld: Boolean,
                          passportDetails: Option[PassportDetail]
                         )

object NonUkResident {
  implicit val format = Json.format[NonUkResident]

  implicit def convert(dtls: NonUKResidence) : Option[IdDetail] = {
    dtls.passportType match {
      case PassportTypeUKPassport(num) => Some(IdDetail(None, Some(NonUkResident(dtls.dateOfBirth.toString, true,
        Some(PassportDetail(true, PassportNum(ukPassportNumber = Some(num))))))))
      case PassportTypeNonUKPassport(num) => Some(IdDetail(None, Some(NonUkResident(dtls.dateOfBirth.toString, true,
        Some(PassportDetail(false, PassportNum(nonUkPassportNumber = Some(num))))))))
      case PassportTypeNoPassport => Some(IdDetail(None, Some(NonUkResident(dtls.dateOfBirth.toString, false, None))))
    }
  }
}
