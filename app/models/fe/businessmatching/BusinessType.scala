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

import play.api.data.validation.ValidationError
import play.api.libs.json._

sealed trait BusinessType

object BusinessType {

  case object SoleProprietor extends BusinessType
  case object LimitedCompany extends BusinessType
  case object Partnership extends BusinessType
  case object LPrLLP extends BusinessType
  case object UnincorporatedBody extends BusinessType

  implicit val writes = Writes[BusinessType] {
    case LimitedCompany => JsString("Corporate Body")
    case SoleProprietor => JsString("Sole Trader")
    case Partnership => JsString("Partnership")
    case LPrLLP => JsString("LLP")
    case UnincorporatedBody => JsString("Unincorporated Body")
  }

  implicit val reads = Reads[BusinessType] {
    case JsString("Corporate Body") => JsSuccess(LimitedCompany)
    case JsString("Sole Trader") => JsSuccess(SoleProprietor)
    case JsString("Partnership") => JsSuccess(Partnership)
    case JsString("LLP") => JsSuccess(LPrLLP)
    case JsString("Unincorporated Body") => JsSuccess(UnincorporatedBody)
    case _ =>
      JsError(JsPath -> ValidationError("error.invalid"))
  }

  import models.des.businessdetails.{BusinessType => DesBT}

  implicit def conv(businessType: DesBT): BusinessType = {

    businessType match {
      case DesBT.SoleProprietor => SoleProprietor
      case DesBT.LimitedCompany => LimitedCompany
      case DesBT.Partnership => Partnership
      case DesBT.LPrLLP => LPrLLP
      case DesBT.UnincorporatedBody => UnincorporatedBody
    }
  }
}
