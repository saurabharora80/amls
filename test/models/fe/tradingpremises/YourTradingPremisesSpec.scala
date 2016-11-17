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

package models.fe.tradingpremises

import models.des.tradingpremises.{Address => TradingPremisesAddress, _}
import org.joda.time.LocalDate
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json._

class YourTradingPremisesSpec extends WordSpec with MustMatchers {

  "YourTradingPremises" must {

    val data = Map(
      "tradingName" -> Seq("foo"),
      "addressLine1" -> Seq("1"),
      "addressLine2" -> Seq("2"),
      "postcode" -> Seq("asdfasdf"),
      "isResidential" -> Seq("true"),
      "startDate.day" -> Seq("24"),
      "startDate.month" -> Seq("2"),
      "startDate.year" -> Seq("1990")
    )

    val json = Json.obj(
      "tradingName" -> "foo",
      "addressLine1" -> "1",
      "addressLine2" -> "2",
      "postcode" -> "asdfasdf",
      "isResidential" -> true,
      "startDate" -> new LocalDate(1990, 2, 24)
    )

    val model = YourTradingPremises(
      "foo",
      Address(
        "1",
        "2",
        None,
        None,
        "asdfasdf"
      ),
      new LocalDate(1990, 2, 24),
      true
    )

    "Correctly serialise from json" in {

      implicitly[Reads[YourTradingPremises]].reads(json) must
        be(JsSuccess(model))
    }

    "Correctly write form model to json" in {

      implicitly[Writes[YourTradingPremises]].writes(model) must
        be(json)
    }

    "convert des model to frontend model" in {
      val agentPremises = AgentPremises("TradingName",
        TradingPremisesAddress("AddressLine1",
          "AddressLine2",
          Some("AddressLine3"),
          Some("AddressLine4"),
          "AD",
          Some("AA1 1AA")),
        true,
        Msb(true, false, true, true, true),
        Hvd(true),
        Asp(false),
        Tcsp(true),
        Eab(false),
        Bpsp(true),
        Tditpsp(false),
        "2001-01-01"
      )

      val feModel = YourTradingPremises("TradingName", Address("AddressLine1", "AddressLine2", Some("AddressLine3"),
        Some("AddressLine4"), "AA1 1AA"), new LocalDate(2001, 1, 1), true)

      YourTradingPremises.conv(agentPremises) must be(feModel)

    }
  }
}