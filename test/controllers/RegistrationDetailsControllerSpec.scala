/*
 * Copyright 2017 HM Revenue & Customs
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

package controllers

import connectors.RegistrationDetailsDesConnector
import models.des.registrationdetails.RegistrationDetails
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.MustMatchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.test.FakeRequest
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class RegistrationDetailsControllerSpec extends PlaySpec with MustMatchers with ScalaFutures with MockitoSugar {

  implicit val hc = HeaderCarrier()

  val controller = new RegistrationDetailsController {
    override private[controllers] val registrationDetailsConnector = mock[RegistrationDetailsDesConnector]
  }

  "The RegistrationDetailsController" must {
    "use the Des connector to retrieve registration details" in {
      val safeId = "SAFEID"

      when {
        controller.registrationDetailsConnector.getRegistrationDetails(eqTo(safeId))(any(), any())
      } thenReturn Future.successful(mock[RegistrationDetails])

      whenReady(controller.get(safeId)(FakeRequest())) { _ =>
        verify(controller.registrationDetailsConnector).getRegistrationDetails(eqTo(safeId))(any(), any())
      }
    }
  }

}
