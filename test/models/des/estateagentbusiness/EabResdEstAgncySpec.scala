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

package models.des.estateagentbusiness

import models.fe.estateagentbusiness._
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class EabResdEstAgncySpec extends PlaySpec {
  "EstateAgentBusiness" must {

    val services = Services(Set(Residential, Commercial, Auction))
    val professionalBody = ProfessionalBodyYes("details")
    val penalisedUnderEAAct =  PenalisedUnderEstateAgentsActYes("test")
    val redressSchemeOther = Other("test")

    val eabResdEstModel = EabResdEstAgncy(false,None,None)

    val eab = EstateAgentBusiness(Some(services),Some(redressSchemeOther), None, None)
    val eab1 = EstateAgentBusiness(Some(services),Some(RedressSchemedNo), None, None)
    val eab2 = EstateAgentBusiness(Some(services),None, None, None)

    "serialise eabresdestagency model " in {
      EabResdEstAgncy.format.writes(eabResdEstModel) must be(Json.obj("regWithRedressScheme"->false))
    }

    "successfully convert frontend eab to des model" in {
      EabResdEstAgncy.convert(Some(eab)) must be(Some(EabResdEstAgncy(true,Some("Other"),Some("test"))))
      EabResdEstAgncy.convert(Some(eab1)) must be(Some(EabResdEstAgncy(false, None, None)))
      EabResdEstAgncy.convert(Some(eab2)) must be(None)
      EabResdEstAgncy.convert(None) must be(None)

    }
  }

}
