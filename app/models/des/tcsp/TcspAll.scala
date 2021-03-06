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

package models.des.tcsp

import models.fe.tcsp.{ServicesOfAnotherTCSP, ServicesOfAnotherTCSPYes, ServicesOfAnotherTCSPNo, Tcsp}
import play.api.libs.json.Json

case class TcspAll (anotherTcspServiceProvider: Boolean, tcspMlrRef: Option[String])

object TcspAll {

  implicit val format = Json.format[TcspAll]

  implicit def conv(tcsp: Tcsp) : TcspAll = {

    tcsp.servicesOfAnotherTCSP match {
      case Some(data) => data
      case _ => TcspAll(false, None)
    }
  }

  implicit def conv1(anotherTcsp: ServicesOfAnotherTCSP): TcspAll = {
    anotherTcsp match {
      case ServicesOfAnotherTCSPYes(dtls) => TcspAll(true, Some(dtls))
      case ServicesOfAnotherTCSPNo => TcspAll(false, None)
    }
  }
}
