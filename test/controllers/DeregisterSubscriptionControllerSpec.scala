package controllers

import connectors.DeregisterSubscriptionConnector
import exceptions.HttpStatusException
import models.des.DeregisterSubscriptionResponse
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import play.api.libs.json.{JsNull, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, _}

import scala.concurrent.Future

class DeregisterSubscriptionControllerSpec extends PlaySpec with MockitoSugar with ScalaFutures {

  trait Fixture {
    object deregisterSubscriptionController extends DeregisterSubscriptionController {
      private[controllers] override val deregisterubscriptionConnector = mock[DeregisterSubscriptionConnector]
    }
  }

  val amlsRegistrationNumber = "XAML00000567890"
  val success = DeregisterSubscriptionResponse("2016-09-17T09:30:47Z")

  private val inputRequest = Json.obj(
    "acknowledgementReference" -> "AEF7234BGG12539GH143856HEA123412",
    "deregistrationDate" -> "2015-08-23",
    "deregistrationReason" -> "Other, please specify",
    "deregReasonOther" -> "Other Reason"
  )

  private val postRequest = FakeRequest("POST", "/")
    .withHeaders("CONTENT_TYPE" -> "application/json")
    .withBody[JsValue](inputRequest)

  private val postRequestWithNoBody = FakeRequest("POST", "/")
    .withHeaders("CONTENT_TYPE" -> "application/json")
    .withBody[JsValue](JsNull)

  "DeregisterSubscriptionController" must {

    "successfully return success response on valid request" in new Fixture {
      when(
        deregisterSubscriptionController.deregisterSubscriptionConnector.deregistration(any(), any())(any(), any(), any())
      ) thenReturn Future.successful(success)

      private val result = deregisterSubscriptionController.deregistration(amlsRegistrationNumber)(postRequest)
      status(result) must be(OK)
      contentAsJson(result) must be(Json.toJson(success))
    }


    "successfully return failed response on invalid request" in new Fixture {
      private val response = Json.obj(
        "errors" -> Seq(
          Json.obj("path" -> "obj.deregistrationReason",
            "error" -> "error.path.missing"),
          Json.obj("path" -> "obj.acknowledgementReference",
            "error" -> "error.path.missing"),
          Json.obj("path" -> "obj.deregistrationDate",
            "error" -> "error.path.missing")
        ))

      private val result = deregisterSubscriptionController.deregistration(amlsRegistrationNumber)(postRequestWithNoBody)
      status(result) must be(BAD_REQUEST)
      contentAsJson(result) must be(response)
    }


    "return failed response on exception" in new Fixture {
      when(
        deregisterSubscriptionController.deregisterSubscriptionConnector.deregistration(any(), any())(any(), any(), any())
      ) thenReturn Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some("message")))

      whenReady(deregisterSubscriptionController.deregistration(amlsRegistrationNumber)(postRequest).failed) {
        case HttpStatusException(status, body) =>
          status must be(INTERNAL_SERVER_ERROR)
          body must be(Some("message"))
      }

    }
  }
}
