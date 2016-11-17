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

package models.des.aboutthebusiness

import models.fe.aboutthebusiness._
import models.des
import org.joda.time.LocalDate
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class BusinessContactDetailsSpec extends PlaySpec {

  "BusinessContactDetails" must {

    "seralize model with uk registered office and uk alternate address " in {
      val desBusinesContact = BusinessContactDetails(
        Address("doornumber", "some street", None, None, "GB", Some("AA1 1AA")),
        true,
        Some(AlternativeAddress("Some Name", "Some business",
          Address("alt 1", "alt 2", None, None, "GB", Some("BB1 1BB")))),
        "07000111222",
        "test@gmail.com")

      val json = Json.obj("businessAddress" -> Json.obj("addressLine1" -> "doornumber",
        "addressLine2" -> "some street",
        "country" -> "GB",
        "postcode" -> "AA1 1AA"),
        "altCorrespondenceAddress" -> true,
        "alternativeAddress" -> Json.obj("name" -> "Some Name",
          "tradingName" -> "Some business",
          "address" -> Json.obj("addressLine1" -> "alt 1",
            "addressLine2" -> "alt 2",
            "country" -> "GB",
            "postcode" -> "BB1 1BB")),
        "businessTelNo" -> "07000111222",
        "businessEmail" -> "test@gmail.com")


      BusinessContactDetails.format.writes(desBusinesContact) must be(json)
    }
  }

  "seralize model with uk registered office and non uk alternate address" in {
    val desBusinesContact = BusinessContactDetails(
      Address("number", "some street", None, None, "GB",  Some("AA1 1AA")),
      true,
      Some(AlternativeAddress("Some Name", "Some business",
        Address("alt 1", "alt 2", None, None, "GB", None))),
      "07000111222",
      "test@gmail.com")

    val json = Json.obj("businessAddress" -> Json.obj("addressLine1" -> "number",
      "addressLine2" -> "some street",
      "country" -> "GB",
      "postcode" -> "AA1 1AA"),
      "altCorrespondenceAddress" -> true,
      "alternativeAddress" -> Json.obj("name" -> "Some Name",
        "tradingName" -> "Some business",
        "address" -> Json.obj("addressLine1" -> "alt 1",
          "addressLine2" -> "alt 2",
          "country" -> "GB"
          )),
      "businessTelNo" -> "07000111222",
      "businessEmail" -> "test@gmail.com")


    BusinessContactDetails.format.writes(desBusinesContact) must be(json)
  }


  "seralize model with uk registered office and alternate address none" in {
    val desBusinesContact = BusinessContactDetails(
      Address("doornumber", "some street", None, None, "GB",  Some("AA1 1AA")),
      false,
      None,
      "07000111222",
      "test@gmail.com")

    val json = Json.obj("businessAddress" -> Json.obj("addressLine1" -> "doornumber",
      "addressLine2" -> "some street",
      "country" -> "GB",
      "postcode" -> "AA1 1AA"),
      "altCorrespondenceAddress" -> false,
      "businessTelNo" -> "07000111222",
      "businessEmail" -> "test@gmail.com")


    BusinessContactDetails.format.writes(desBusinesContact) must be(json)
  }



  "seralize model with non uk registered office and non uk alternate address" in {
    val desBusinesContact = BusinessContactDetails(
      Address("doornumber", "some street", None, None, "GB",  None),
      true,
      Some(AlternativeAddress("Some Name", "Some business",
        Address("alt 1", "alt 2", None, None, "GB", None))),
      "07000111222",
      "test@gmail.com")

    val json = Json.obj("businessAddress" -> Json.obj("addressLine1" -> "doornumber",
      "addressLine2" -> "some street",
      "country" -> "GB"),
      "altCorrespondenceAddress" -> true,
      "alternativeAddress" -> Json.obj("name" -> "Some Name",
        "tradingName" -> "Some business",
        "address" -> Json.obj("addressLine1" -> "alt 1",
          "addressLine2" -> "alt 2",
          "country" -> "GB"
        )),
      "businessTelNo" -> "07000111222",
      "businessEmail" -> "test@gmail.com")


    BusinessContactDetails.format.writes(desBusinesContact) must be(json)
  }

  "seralize model with non uk registered office and uk alternate address" in {
    val desBusinesContact = BusinessContactDetails(
      Address("doornumber", "some street", None, None, "GB",  None),
      true,
      Some(AlternativeAddress("Some Name", "Some business",
        Address("alt 1", "alt 2", None, None, "GB", Some("BB1 1BB")))),
      "07000111222",
      "test@gmail.com")

    val json = Json.obj("businessAddress" -> Json.obj("addressLine1" -> "doornumber",
      "addressLine2" -> "some street",
      "country" -> "GB"),
      "altCorrespondenceAddress" -> true,
      "alternativeAddress" -> Json.obj("name" -> "Some Name",
        "tradingName" -> "Some business",
        "address" -> Json.obj("addressLine1" -> "alt 1",
          "addressLine2" -> "alt 2",
          "country" -> "GB",
           "postcode" -> "BB1 1BB"
        )),
      "businessTelNo" -> "07000111222",
      "businessEmail" -> "test@gmail.com")


    BusinessContactDetails.format.writes(desBusinesContact) must be(json)
  }

  "convert BusinessContactDetails correctly with Registered office and alternate address are in UK" in {
    // scalastyle:off magic.number
    val from  = AboutTheBusiness(PreviouslyRegisteredYes("12345678"),
      Some(ActivityStartDate(new LocalDate(1990, 2, 24))),
      Some(VATRegisteredYes("123456789")),
      Some(CorporationTaxRegisteredYes("1234567890")),
      ContactingYou("07000111222", "test@gmail.com"),
      RegisteredOfficeUK("doornumber", "some street", None, None, "AA1 1AA"),
      Some(UKCorrespondenceAddress("Some Name", "Some business", "alt 1", "alt 2", None, None, "BB1 1BB")))

    val desBusinesContact = des.aboutthebusiness.BusinessContactDetails(
      Address("doornumber", "some street", None, None, "GB", Some("AA1 1AA")),
      true,
      Some(AlternativeAddress("Some Name", "Some business",
        Address("alt 1", "alt 2", None, None, "GB", Some("BB1 1BB")))),
      "07000111222",
      "test@gmail.com")

    BusinessContactDetails.convert(from) must be(desBusinesContact)
  }

  "convert BusinessContactDetails correctly with registered office non UK and alternate Address non UK" in {
    val from  = AboutTheBusiness(PreviouslyRegisteredYes("12345678"),
      Some(ActivityStartDate(new LocalDate(1990, 2, 24))),
      Some(VATRegisteredYes("123456789")),
      Some(CorporationTaxRegisteredYes("1234567890")),
      ContactingYou("07000111222", "test@gmail.com"),
      RegisteredOfficeNonUK("doornumber", "some street", None, None, "AG"),
      Some(NonUKCorrespondenceAddress("Some Name", "Some business", "alt 1", "alt 2", None, None, "AR")))

    val desBusinesContact = des.aboutthebusiness.BusinessContactDetails(
      Address("doornumber", "some street", None, None, "AG", None),
      true,
      Some(AlternativeAddress("Some Name", "Some business",
        Address("alt 1", "alt 2", None, None, "AR", None))),
      "07000111222",
      "test@gmail.com")

    BusinessContactDetails.convert(from) must be(desBusinesContact)
  }

  "convert BusinessContactDetails correctly with registered office UK and alternate Address non UK" in {
    val from  = AboutTheBusiness(PreviouslyRegisteredYes("12345678"),
      Some(ActivityStartDate(new LocalDate(1990, 2, 24))),
      Some(VATRegisteredYes("123456789")),
      Some(CorporationTaxRegisteredYes("1234567890")),
      ContactingYou("07000111222", "test@gmail.com"),
      RegisteredOfficeUK("doornumber", "some street", None, None, "BB1 1BB"),
      Some(NonUKCorrespondenceAddress("Some Name", "Some business", "alt 1", "alt 2", None, None, "AR")))

    val desBusinesContact = des.aboutthebusiness.BusinessContactDetails(
      Address("doornumber", "some street", None, None, "GB", Some("BB1 1BB")),
      true,
      Some(AlternativeAddress("Some Name", "Some business",
        Address("alt 1", "alt 2", None, None, "AR", None))),
      "07000111222",
      "test@gmail.com")

    BusinessContactDetails.convert(from) must be(desBusinesContact)
  }

  "convert BusinessContactDetails correctly when Registered office is non UK and alternate address is UK" in {

    val from  = AboutTheBusiness(PreviouslyRegisteredYes("12345678"),
     Some(ActivityStartDate(new LocalDate(1990, 2, 24))),
      Some(VATRegisteredYes("123456789")),
      Some(CorporationTaxRegisteredYes("1234567890")),
      ContactingYou("07000111222", "test@gmail.com"),
      RegisteredOfficeNonUK("doornumber", "some street", None, None, "AB"),
      Some(UKCorrespondenceAddress("Some Name", "Some business", "alt 1", "alt 2", None, None, "BB1 1BB")))

    val desBusinesContact = des.aboutthebusiness.BusinessContactDetails(
      Address("doornumber", "some street", None, None, "AB", None),
      true,
      Some(AlternativeAddress("Some Name", "Some business",
        Address("alt 1", "alt 2", None, None, "GB", Some("BB1 1BB")))),
      "07000111222",
      "test@gmail.com")

    BusinessContactDetails.convert(from) must be(desBusinesContact)
  }

  "convert BusinessContactDetails correctly when Registered office is non UK and alternate address is none" in {

    val from  = AboutTheBusiness(PreviouslyRegisteredYes("12345678"),
      Some(ActivityStartDate(new LocalDate(1990, 2, 24))),
      Some(VATRegisteredYes("123456789")),
      Some(CorporationTaxRegisteredYes("1234567890")),
      ContactingYou("07000111222", "test@gmail.com"),
      RegisteredOfficeNonUK("doornumber", "some street", None, None, "AB"),
      None)

    val desBusinesContact = des.aboutthebusiness.BusinessContactDetails(
      Address("doornumber", "some street", None, None, "AB", None),
      false,
      None,
      "07000111222",
      "test@gmail.com")

    BusinessContactDetails.convert(from) must be(desBusinesContact)
  }
}