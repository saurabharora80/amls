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

package models.des

import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTimeZone, LocalDate, LocalDateTime}
import play.api.libs.json._

case class ReadStatusResponse(
                               processingDate: LocalDateTime,
                               formBundleStatus: String,
                               statusReason: Option[String],
                               deRegistrationDate: Option[LocalDate],
                               currentRegYearStartDate: Option[LocalDate],
                               currentRegYearEndDate: Option[LocalDate],
                               renewalConFlag: Boolean,
                               renewalSubmissionFlag: Option[Boolean] = None,
                               currentAMLSOutstandingBalance: Option[String] = None,
                               businessContactNumber: Option[String] = None,
                               safeId: Option[String] = None
                             ) {

  def isRenewalPeriod(dateTime: LocalDate = LocalDate.now()) = {

    val renewalWindow = 30

    currentRegYearEndDate match {
      case Some(endDate) => !dateTime.toDateTimeAtStartOfDay.isAfter(endDate.toDateTimeAtStartOfDay.minusDays(renewalWindow))
      case _ => false
    }

  }

}

object ReadStatusResponse {

  val dateTimeFormat = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC

  implicit val readsJodaLocalDateTime = Reads[LocalDateTime](js =>
    js.validate[String].map[LocalDateTime](dtString =>
      LocalDateTime.parse(dtString, dateTimeFormat)
    )
  )

  implicit val localDateTimeWrite: Writes[LocalDateTime] = new Writes[LocalDateTime] {
    def writes(dateTime: LocalDateTime): JsValue = JsString(dateTimeFormat.print(dateTime.toDateTime(DateTimeZone.UTC)))
  }

  implicit val format = Json.format[ReadStatusResponse]

}
