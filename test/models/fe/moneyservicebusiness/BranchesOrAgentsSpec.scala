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

package models.fe.moneyservicebusiness

import models.des.msb.{CountriesList, MsbAllDetails}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json._

class BranchesOrAgentsSpec extends PlaySpec {

  "MsbServices" must {


    "round trip through Json correctly" in {

      val model: BranchesOrAgents = BranchesOrAgents(Some(Seq("GB")))

      Json.fromJson[BranchesOrAgents](Json.toJson(model)) mustBe JsSuccess(model)
    }

    "convMsbAll: return none when is input is none" in {
      BranchesOrAgents.convMsbAll(None) must be(None)
    }

    "convMsbAll: return none when is countriesList is empty" in {

      Some(MsbAllDetails(
        Some("999999"),
        true,
        Some(CountriesList(List.empty)),
        true)
      )
      BranchesOrAgents.convMsbAll(None) must be(None)
    }
 }
}
