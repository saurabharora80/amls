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

package models.fe.bankdetails

import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}

class BankAccountTypeSpec extends PlaySpec with MockitoSugar {

  "BankAccountType" must {

    "validate Json read" in {
      Json.fromJson[BankAccountType](Json.obj("bankAccountType" -> "01")) must
        be (JsSuccess(PersonalAccount, JsPath \ "bankAccountType"))
      Json.fromJson[BankAccountType](Json.obj("bankAccountType" -> "02")) must
        be (JsSuccess(BelongsToBusiness, JsPath \ "bankAccountType"))
      Json.fromJson[BankAccountType](Json.obj("bankAccountType" -> "03")) must
        be (JsSuccess(BelongsToOtherBusiness, JsPath \ "bankAccountType"))

    }

    "fail Json read on invalid data" in  {
      Json.fromJson[BankAccountType](Json.obj("bankAccountType" ->"10")) must
        be (JsError(JsPath \ "bankAccountType", ValidationError("error.invalid")))
    }

    "write correct Json value" in  {
      Json.toJson(PersonalAccount) must be (Json.obj("bankAccountType" -> "01"))
      Json.toJson(BelongsToBusiness) must be (Json.obj("bankAccountType" -> "02"))
      Json.toJson(BelongsToOtherBusiness) must be (Json.obj("bankAccountType" -> "03"))
    }
  }

}