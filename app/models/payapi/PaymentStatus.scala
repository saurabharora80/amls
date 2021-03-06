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

package models.payapi

import enumeratum.{Enum, EnumEntry}
import utils.EnumFormat

sealed abstract class PaymentStatus(val isFinal: Boolean, val validNextStates: Seq[PaymentStatus] = Seq()) extends EnumEntry

object PaymentStatus {
  implicit val statusFormat = EnumFormat(PaymentStatuses)
}

object PaymentStatuses extends Enum[PaymentStatus] {
  case object Created extends PaymentStatus(false, Seq(Sent))
  case object Successful extends PaymentStatus(true)
  case object Sent extends PaymentStatus(false, Seq(Successful, Failed, Cancelled))
  case object Failed extends PaymentStatus(true)
  case object Cancelled extends PaymentStatus(true)

  override def values = findValues

}
