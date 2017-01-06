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

package models.fe.tcsp

import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}

class ServicesOfAnotherTCSPSpec extends PlaySpec with MockitoSugar {

  "ServicesOfAnotherTCSP" must {

    "JSON validation" must {

      "successfully validate given an enum value" in {
        Json.fromJson[ServicesOfAnotherTCSP](Json.obj("servicesOfAnotherTCSP" -> false)) must
          be(JsSuccess(ServicesOfAnotherTCSPNo))
      }

      "successfully validate given an `Yes` value" in {
        Json.fromJson[ServicesOfAnotherTCSP](Json.obj("servicesOfAnotherTCSP" -> true, "mlrRefNumber" -> "12345678")) must
          be(JsSuccess(ServicesOfAnotherTCSPYes("12345678"), JsPath \ "mlrRefNumber"))
      }

      "fail to validate when given an empty `Yes` value" in {
        val json = Json.obj("servicesOfAnotherTCSP" -> true)

        Json.fromJson[ServicesOfAnotherTCSP](json) must
          be(JsError((JsPath \ "mlrRefNumber") -> ValidationError("error.path.missing")))
      }

      "write the correct value" in {
        Json.toJson(ServicesOfAnotherTCSPNo) must
          be(Json.obj("servicesOfAnotherTCSP" -> false))

        Json.toJson(ServicesOfAnotherTCSPYes("12345678")) must
          be(Json.obj(
            "servicesOfAnotherTCSP" -> true,
            "mlrRefNumber" -> "12345678"
          ))
      }
    }
  }
}
