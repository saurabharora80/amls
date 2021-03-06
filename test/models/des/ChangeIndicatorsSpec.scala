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

import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.test.FakeApplication

class ChangeIndicatorsSpec extends PlaySpec with OneAppPerSuite {

  implicit override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> false))

  "ChangeIndicators" must {
    "serialize correctly" in {

      val json = Json.parse(
        """{
    "businessDetails": false,
    "businessAddress": false,
    "businessReferences": true,
    "tradingPremises": true,
    "businessActivities": true,
    "bankAccountDetails": true,
    "msb": true,
    "hvd": false,
    "asp": true,
    "aspOrTcsp": false,
    "tcsp": true,
    "eab": true,
    "responsiblePersons": false,
    "filingIndividual": true
  }""")

      val changeIndicators = ChangeIndicators(false,false,true,true,true,true,true,false,true,false,true,true,false,true)

      ChangeIndicators.format.writes(changeIndicators) must be(json)

    }
  }

}

class ChangeIndicatorsR7Spec extends PlaySpec with OneAppPerSuite {

  implicit override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> true))

  "ChangeIndicators" must {
    "serialize correctly" in {

      val json = Json.parse(
        """{
    "businessDetails": false,
    "businessAddress": false,
    "businessReferences": true,
    "tradingPremises": true,
    "businessActivities": true,
    "bankAccountDetails": true,
    "msb": {
    "msb": true
    },
    "hvd": {
    "hvd": false
    },
    "asp": {
    "asp": true
    },
    "aspOrTcsp": {
    "aspOrTcsp": false
    },
    "tcsp": {
    "tcsp": true
    },
    "eab": {
    "eab": true
    },
    "responsiblePersons": false,
    "filingIndividual": true
  }""")

      val changeIndicators = ChangeIndicators(false,false,true,true,true,true,true,false,true,false,true,true,false,true)

      ChangeIndicators.format.writes(changeIndicators) must be(json)

    }
  }

}
