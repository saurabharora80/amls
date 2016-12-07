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
import models.fe.hvd
import models.fe.hvd.ReceiveCashPayments
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{Json, JsSuccess}

class ReceiveCashPaymentsSpec extends PlaySpec {

  "ReceiveCashPayments" must {

    val paymentMethods = PaymentMethods(courier = true, direct = true, true, Some("foo"))

    "roundtrip through json" in {
      val data = ReceiveCashPayments(true, Some(paymentMethods))
      ReceiveCashPayments.format.reads(ReceiveCashPayments.format.writes(data)) mustEqual JsSuccess(data)
    }

    "roundtrip through json when ReceiveCashPayments is none" in {
      val data = ReceiveCashPayments(false, None)
      ReceiveCashPayments.format.reads(ReceiveCashPayments.format.writes(data)) mustEqual JsSuccess(data)
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
      ReceiveCashPayments.conv(desModel) must be(Some(ReceiveCashPayments(true, Some(PaymentMethods(true,true, true, Some("aaaaaaaaaaaaa"))))))
    }

    "converting the des model must yield None if hvdFromUnseenCustDetails is None" in {
      ReceiveCashPayments.conv(DesConstants.testHvd.copy(hvdFromUnseenCustDetails = None)) must be(None)
    }
  }

  "ReceiveCashPayments deserialisation" when {
    "payment methods is an empty object" must {
      "indicate that payments are not Received" in {
        val json = Json.parse(
          """
            |{
            |    "receivePayments": false,
            |    "paymentMethods": {}
            |}
          """.stripMargin
        )

        ReceiveCashPayments.format.reads(json) must be (JsSuccess(ReceiveCashPayments(false, None)))
      }
    }
  }
}
