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

package models.fe.businessactivities

import models.des.businessactivities.{BusinessActivitiesAll, BusinessActivityDetails}
import play.Logger
import play.api.data.validation.ValidationError
import play.api.libs.json._

sealed trait ExpectedAMLSTurnover

object ExpectedAMLSTurnover {

  case object First extends ExpectedAMLSTurnover

  case object Second extends ExpectedAMLSTurnover

  case object Third extends ExpectedAMLSTurnover

  case object Fourth extends ExpectedAMLSTurnover

  case object Fifth extends ExpectedAMLSTurnover

  case object Sixth extends ExpectedAMLSTurnover

  case object Seventh extends ExpectedAMLSTurnover

  import utils.MappingUtils.Implicits._

  implicit val jsonReads = {
    import play.api.libs.json.Reads.StringReads
    (__ \ "expectedAMLSTurnover").read[String].flatMap[ExpectedAMLSTurnover] {
      case "01" => First
      case "02" => Second
      case "03" => Third
      case "04" => Fourth
      case "05" => Fifth
      case "06" => Sixth
      case "07" => Seventh
      case _ =>
        ValidationError("error.invalid")
    }
  }

  implicit val jsonWrites = Writes[ExpectedAMLSTurnover] {
    case First => Json.obj("expectedAMLSTurnover" -> "01")
    case Second => Json.obj("expectedAMLSTurnover" -> "02")
    case Third => Json.obj("expectedAMLSTurnover" -> "03")
    case Fourth => Json.obj("expectedAMLSTurnover" -> "04")
    case Fifth => Json.obj("expectedAMLSTurnover" -> "05")
    case Sixth => Json.obj("expectedAMLSTurnover" -> "06")
    case Seventh => Json.obj("expectedAMLSTurnover" -> "07")
  }

  implicit def conv(activityDtls: BusinessActivityDetails): Option[ExpectedAMLSTurnover] = {
    Logger.debug(s"[ExpectedAMLSTurnover][conv] desValue = $activityDtls")
    activityDtls.respActvtsBusRegForOnlyActvtsCarOut match {
      case Some(data) => activityDtls.actvtsBusRegForOnlyActvtsCarOut match {
        case true => data.mlrActivityTurnover
        case false => data.otherBusActivitiesCarriedOut match {
          case Some(other) => convertAMLSTurnover(Some(other.mlrActivityTurnover))
          case None => None
        }
      }
      case None => None
    }
  }

  implicit def convertAMLSTurnover(to: Option[String]): Option[ExpectedAMLSTurnover] = {
    to match {
      case Some("14999") => Some(First)
      case Some("49999") => Some(Second)
      case Some("99999") => Some(Third)
      case Some("249999") => Some(Fourth)
      case Some("999999") => Some(Fifth)
      case Some("10000000") => Some(Sixth)
      case Some("100000000") => Some(Seventh)
      case _ => None
    }
  }
}