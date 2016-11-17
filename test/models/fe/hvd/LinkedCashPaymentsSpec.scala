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

package models.fe.hvd

import models.des.DesConstants
import models.des.hvd.{HvdFromUnseenCustDetails, ReceiptMethods, Hvd => DesHvd}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsPath, JsSuccess}

class LinkedCashPaymentsSpec extends PlaySpec {

  "LinkedCashPayments" should {

    "Json Validation" must {

      "successfully read and write json data" in {

        LinkedCashPayments.format.reads(LinkedCashPayments.format.writes(LinkedCashPayments(true))) must be(JsSuccess(LinkedCashPayments(true),
          JsPath \ "linkedCashPayments"))

      }
    }
  }

  "converting the des model must yield a frontend model" in {
    val desModel= DesHvd(true,
      Some("2001-01-01"),
      true,
      Some(0),
      Some(HvdFromUnseenCustDetails(
        true,
        Some(ReceiptMethods(true, true, true, Some("aaaaaaaaaaaaa")))
      ))
    )
    LinkedCashPayments.conv(DesConstants.testHvd) must be(Some(LinkedCashPayments(true)))
  }

}