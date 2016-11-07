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

package models.des.hvd

import models.fe.hvd.ReceiveCashPayments
import play.api.libs.json.Json

case class HvdFromUnseenCustDetails (hvdFromUnseenCustomers: Boolean,
                                     receiptMethods: Option[ReceiptMethods])

object HvdFromUnseenCustDetails {

  implicit val format = Json.format[HvdFromUnseenCustDetails]

  implicit def conv(model: Option[ReceiveCashPayments]): Option[HvdFromUnseenCustDetails] = {

    model match {
      case Some(data) => data.paymentMethods match {
        case Some(paymentMtd) => Some(HvdFromUnseenCustDetails(true, paymentMtd))
        case None => Some(HvdFromUnseenCustDetails(false, None))
      }
      case None => None
    }
  }
}
