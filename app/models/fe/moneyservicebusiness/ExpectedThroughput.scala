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

package models.fe.moneyservicebusiness

import play.api.data.validation.ValidationError
import models.des.msb.{MsbAllDetails, MoneyServiceBusiness => DesMoneyServiceBusiness}
import play.api.libs.json._

sealed trait ExpectedThroughput

object ExpectedThroughput {

  case object First extends ExpectedThroughput
  case object Second extends ExpectedThroughput
  case object Third extends ExpectedThroughput
  case object Fourth extends ExpectedThroughput
  case object Fifth extends ExpectedThroughput
  case object Sixth extends ExpectedThroughput
  case object Seventh extends ExpectedThroughput

  import utils.MappingUtils.Implicits._

  implicit val jsonReads = {
    import play.api.libs.json.Reads.StringReads
    (__ \ "throughput").read[String].flatMap[ExpectedThroughput] {
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

  implicit val jsonWrites = Writes[ExpectedThroughput] {
    case First => Json.obj("throughput" -> "01")
    case Second => Json.obj("throughput" -> "02")
    case Third => Json.obj("throughput" -> "03")
    case Fourth => Json.obj("throughput" -> "04")
    case Fifth => Json.obj("throughput" -> "05")
    case Sixth => Json.obj("throughput" -> "06")
    case Seventh => Json.obj("throughput" -> "07")
  }

  implicit def convMsbAll(msbAll: Option[MsbAllDetails]): Option[ExpectedThroughput] = {
    msbAll match {
      case Some(msbDtls) => msbDtls.anticipatedTotThrputNxt12Mths.map(x => convThroughput(x))
      case None => None
    }
  }

  def convThroughput(msbAll: String): ExpectedThroughput = {
    msbAll match {
      case "99999" => First
      case "499999" => Second
      case "999999" => Third
      case "20000000" => Fourth
      case "100000000" => Fifth
      case "1000000000" => Sixth
      case "10000000000" => Seventh
    }
  }
}