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

package models.fe.responsiblepeople

import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json._

class TimeAtAddressSpec extends PlaySpec with MockitoSugar {

  val FieldName = "timeAtAddress"

  "JSON validation" must {

    val ZeroToFiveJson = Json.obj(FieldName -> "01")
    val SixToElevenJson = Json.obj(FieldName -> "02")
    val OneToThreeJson = Json.obj(FieldName -> "03")
    val MoreThanThreeJson = Json.obj(FieldName -> "04")

    "successfully validate given an enum value" in {

      Json.fromJson[TimeAtAddress](ZeroToFiveJson) must be(JsSuccess(TimeAtAddress.ZeroToFiveMonths))
      Json.fromJson[TimeAtAddress](SixToElevenJson) must be(JsSuccess(TimeAtAddress.SixToElevenMonths))
      Json.fromJson[TimeAtAddress](OneToThreeJson) must be(JsSuccess(TimeAtAddress.OneToThreeYears))
      Json.fromJson[TimeAtAddress](MoreThanThreeJson) must be(JsSuccess(TimeAtAddress.ThreeYearsPlus))
    }

    "write the correct value" in {
      Json.toJson(TimeAtAddress.ZeroToFiveMonths) must be(ZeroToFiveJson)
      Json.toJson(TimeAtAddress.SixToElevenMonths) must be(SixToElevenJson)
      Json.toJson(TimeAtAddress.OneToThreeYears) must be(OneToThreeJson)
      Json.toJson(TimeAtAddress.ThreeYearsPlus) must be(MoreThanThreeJson)
    }

    "throw error for invalid data" in {
      Json.fromJson[TimeAtAddress](Json.obj(FieldName -> "20")) must
        be(JsError(JsPath \ FieldName, ValidationError("error.invalid")))
    }
  }
}
