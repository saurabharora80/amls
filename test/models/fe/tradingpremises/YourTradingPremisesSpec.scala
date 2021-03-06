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

package models.fe.tradingpremises

import models.des.tradingpremises.{Address => TradingPremisesAddress, _}
import org.joda.time.LocalDate
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.json._
import play.api.test.FakeApplication

class YourTradingPremisesSpec extends WordSpec with MustMatchers with OneAppPerSuite {

  override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> false))

  "YourTradingPremises" must {

    val json = Json.obj(
      "tradingName" -> "foo",
      "addressLine1" -> "1",
      "addressLine2" -> "2",
      "postcode" -> "asdfasdf",
      "isResidential" -> true,
      "startDate" -> new LocalDate(1990, 2, 24),
      "addressDateOfChange" -> new LocalDate("2010-02-01"),
      "tradingNameChangeDate" -> new LocalDate("2012-03-01")
    )

    val model = YourTradingPremises(
      "foo",
      Address(
        "1",
        "2",
        None,
        None,
        "asdfasdf",
        dateOfChange = Some("2010-02-01")
      ),
      new LocalDate(1990, 2, 24),
      true,
      Some("2012-03-01")
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
          Some("AA1 1AA")
        ),
        true,
        Msb(true, false, true, true, true),
        Hvd(true),
        Asp(false),
        Tcsp(true),
        Eab(false),
        Bpsp(true),
        Tditpsp(false),
        Some("2001-01-01")
      )
      val agentDetail = AgentDetails("", None,None,None,agentPremises, None)

      val feModel = YourTradingPremises("TradingName",
        Address("AddressLine1", "AddressLine2", Some("AddressLine3"), Some("AddressLine4"), "AA1 1AA", None),
        new LocalDate(2001, 1, 1), true)

      YourTradingPremises.conv(agentDetail) must be(feModel)

    }
  }
}

class YourTradingPremisesRelease7Spec extends WordSpec with MustMatchers with OneAppPerSuite {

  override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> true))

  "YourTradingPremises" must {

    "convert des model to frontend model" in {

      val agentPremises = AgentPremises("TradingName",
        TradingPremisesAddress("AddressLine1",
          "AddressLine2",
          Some("AddressLine3"),
          Some("AddressLine4"),
          "AD",
          Some("AA1 1AA")
        ),
        true,
        Msb(true, false, true, true, true),
        Hvd(true),
        Asp(false),
        Tcsp(true),
        Eab(false),
        Bpsp(true),
        Tditpsp(false),
        None
      )
      val agentDetail = AgentDetails("", None,None,None,agentPremises, Some("2001-01-01"))

      val feModel = YourTradingPremises("TradingName",
        Address("AddressLine1", "AddressLine2", Some("AddressLine3"), Some("AddressLine4"), "AA1 1AA", None),
        new LocalDate(2001, 1, 1), true)

      YourTradingPremises.conv(agentDetail) must be(feModel)

    }
  }
}