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

package models.fe

import models._
import models.des.DesConstants
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class SubscriptionViewSpec extends PlaySpec {

  "SubscriptionView" must {
    "deserialise the subscription json" when {
      "given valid json" in {

        val json = Json.toJson(GetSuccessModel)

        val subscriptionViewModel = GetSuccessModel

        json.as[SubscriptionView] must be(subscriptionViewModel)

        Json.toJson(GetSuccessModel) must be(json)
      }

      "convert des model to frontend model" in {

        SubscriptionView.convert(DesConstants.SubscriptionViewModelForRp) must be(SubscriptionViewModel.convertedViewModel)
      }
    }
  }
  val GetSuccessModel = SubscriptionView(
    etmpFormBundleNumber = "111111",
    businessMatchingSection = BusinessMatchingSection.model,
    eabSection = EabSection.model,
    aboutTheBusinessSection = AboutTheBusinessSection.model ,
    tradingPremisesSection = TradingPremisesSection.model ,
    bankDetailsSection = BankDetailsSection.model ,
    aboutYouSection = AboutYouSection.model,
    businessActivitiesSection = BusinessActivitiesSection.model ,
    responsiblePeopleSection = ResponsiblePeopleSection.model,
    tcspSection = ASPTCSPSection.TcspSection,
    aspSection = ASPTCSPSection.AspSection,
    msbSection = MsbSection.completeModel,
    hvdSection = HvdSection.completeModel,
    supervisionSection = SupervisionSection.completeModel
  )
}
