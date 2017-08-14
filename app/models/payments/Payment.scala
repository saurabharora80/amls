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

package models.payments

import java.time.LocalDateTime

import models.payapi.{Payment => PayApiPayment, _}
import play.api.libs.json.Json
import utils.EnumFormat

case class Payment(
                    _id: String,
                    amlsRefNo: String,
                    reference: String,
                    description: String,
                    amountInPence: Int,
                    status: PaymentStatus,
                    createdAt: LocalDateTime,
                    isBacs: Option[Boolean] = None,
                    updatedAt: Option[LocalDateTime] = None
                  )

object Payment {

  val from: (String, PayApiPayment) => Payment = (amlsRefNo, apiPayment) =>
    Payment(
      apiPayment._id,
      amlsRefNo,
      apiPayment.reference,
      apiPayment.description,
      apiPayment.amountInPence,
      apiPayment.status,
      LocalDateTime.now
    )

  implicit val statusFormat = EnumFormat(PaymentStatuses)
  implicit val format = Json.format[Payment]
}
