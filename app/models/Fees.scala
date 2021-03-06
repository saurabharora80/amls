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

package models

import models.des.AmendVariationResponse
import models.fe.SubscriptionResponse
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}
import org.joda.time.{DateTime, DateTimeZone}
import play.api.Logger
import play.api.data.validation.ValidationError
import play.api.libs.json._
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats

sealed trait ResponseType

case object SubscriptionResponseType extends ResponseType
case object AmendOrVariationResponseType extends ResponseType

object ResponseType {

  import utils.MappingUtils.Implicits._

  implicit val jsonWrites: Writes[ResponseType] = Writes[ResponseType] {
    case SubscriptionResponseType => JsString("SubscriptionReponse")
    case AmendOrVariationResponseType => JsString("AmendOrVariationResponse")
  }

  implicit val jsonReads : Reads[ResponseType] = {
    import play.api.libs.json.Reads.StringReads
    (__).read[String] flatMap {
      case "SubscriptionReponse" => SubscriptionResponseType
      case "AmendOrVariationResponse" => AmendOrVariationResponseType
      case _ =>
        ValidationError("error.invalid")
    }
  }
}

case class Fees(responseType: ResponseType,
                amlsReferenceNumber: String,
                registrationFee: BigDecimal = 0,
                fpFee: Option[BigDecimal],
                premiseFee: BigDecimal = 0,
                totalFees: BigDecimal = 0,
                paymentReference: Option[String],
                difference: Option[BigDecimal],
                createdAt: DateTime)

object Fees {
  def convertSubscription(subscriptionResponse: SubscriptionResponse): Option[Fees] = {
    subscriptionResponse.subscriptionFees map {
      feesResponse =>  Fees(SubscriptionResponseType,
        subscriptionResponse.amlsRefNo,
        feesResponse.registrationFee,
        feesResponse.fpFee,
        feesResponse.premiseFee,
        feesResponse.totalFees,
        Some(feesResponse.paymentReference),
        None,
        DateTime.now(DateTimeZone.UTC))
    }
  }

  implicit def convertAmendmentVariation(amendVariationResponse: AmendVariationResponse, amlsReferenceNumber: String): Fees = {
    Fees(AmendOrVariationResponseType,
      amlsReferenceNumber,
      amendVariationResponse.registrationFee.getOrElse(0),
      amendVariationResponse.fpFee,
      amendVariationResponse.premiseFee.getOrElse(0),
      amendVariationResponse.totalFees.getOrElse(0),
      amendVariationResponse.paymentReference,
      amendVariationResponse.difference,
      DateTime.now(DateTimeZone.UTC))
  }

  val dateTimeFormat: DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC

  implicit val dateFormat: Format[DateTime] = ReactiveMongoFormats.dateTimeFormats

  implicit val format: OFormat[Fees] = Json.format[Fees]
}
