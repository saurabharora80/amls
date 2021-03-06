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

package models


import models.fe.SubscriptionView
import models.fe.declaration.{Other => DeclarationOther, RoleWithinBusiness}
import models.fe.responsiblepeople.TimeAtAddress.ThreeYearsPlus
import org.joda.time.LocalDate
import utils.StatusConstants

object ASPTCSPSection {

  import models.fe.asp._
  import models.fe.tcsp.{Other, _}

  private val DefaultProvidedServices = ProvidedServices(Set(PhonecallHandling, Other("other service")))
  private val DefaultCompanyServiceProviders = TcspTypes(Set(NomineeShareholdersProvider,
    TrusteeProvider,
    CompanyDirectorEtc,
    RegisteredOfficeEtc,
    CompanyFormationAgent(true, true)))
  private val DefaultServicesOfAnotherTCSP = ServicesOfAnotherTCSPYes("12345678")

  val TcspSection = Some(Tcsp(
    Some(DefaultCompanyServiceProviders),
    Some(DefaultProvidedServices),
    Some(DefaultServicesOfAnotherTCSP))
  )
  val aspOtherBusinessTax = OtherBusinessTaxMattersYes

  val aspServices = ServicesOfBusiness(Set(Accountancy, Auditing, FinancialOrTaxAdvice))

  val AspSection = Some(Asp(Some(aspServices), Some(aspOtherBusinessTax)))
  val TcspModelForView = Some(Tcsp(Some(TcspTypes(Set(CompanyDirectorEtc,
    NomineeShareholdersProvider, TrusteeProvider, RegisteredOfficeEtc, CompanyFormationAgent(true, true)))),
    Some(ProvidedServices(Set(SelfCollectMailboxes, ConferenceRooms,
      PhonecallHandling, EmailHandling, Other("SpecifyOther"), EmailServer))), Some(ServicesOfAnotherTCSPYes("111111111111111"))))
  val AspModelForView = Some(Asp(Some(ServicesOfBusiness(Set(Auditing,
    FinancialOrTaxAdvice, BookKeeping, PayrollServices, Accountancy))), Some(OtherBusinessTaxMattersYes)))
}

object SupervisionSection {

  import models.fe.supervision._

  private val supervisor = "Company A"
  //scalastyle:off magic.number
  private val start = new LocalDate(1993, 8, 25)
  private val end = new LocalDate(1999, 8, 25)
  //scalastyle:off magic.number
  private val reason = "Ending reason"
  private val anotherBody = AnotherBodyYes(supervisor, start, end, reason)
  private val professionalBody = ProfessionalBodyYes("details")
  private val professionalBodyMember = ProfessionalBodyMemberYes
  private val businessTypes = BusinessTypes(Set(AccountingTechnicians, CharteredCertifiedAccountants, Other("test")))

  val completeModel = Some(Supervision(
    Some(anotherBody),
    Some(professionalBodyMember),
    Some(businessTypes),
    Some(professionalBody)
  ))

  val modelForView = Some(Supervision(
    Some(AnotherBodyYes("NameOfLastSupervisor", new LocalDate(2001, 1, 1), new LocalDate(2001, 1, 1), "SupervisionEndingReason")),
    Some(ProfessionalBodyMemberYes),
    Some(BusinessTypes(Set(
      AccountantsIreland,
      CharteredCertifiedAccountants,
      AssociationOfBookkeepers,
      AccountantsEnglandandWales,
      Bookkeepers,
      AccountingTechnicians,
      TaxationTechnicians,
      InternationalAccountants,
      Other("SpecifyOther"),
      LawSociety,
      InstituteOfTaxation,
      AccountantsScotland,
      FinancialAccountants,
      ManagementAccountants
    ))),
    Some(ProfessionalBodyYes("DetailsIfFinedWarned"))))
}

object AboutYouSection {

  import models.fe.declaration.AddPerson
  import models.fe.declaration.BeneficialShareholder

  val model = AddPerson("fName", None, "lName", RoleWithinBusiness(Set(DeclarationOther("Agent"))))

  val modelforView = AddPerson("FirstName", Some("MiddleName"), "LastName", RoleWithinBusiness(Set(BeneficialShareholder)))
}

object BusinessActivitiesSection {

  import models.fe.businessactivities._
  import models.fe.businessactivities.ExpectedAMLSTurnover.Third

  val model = BusinessActivities(
    Some(InvolvedInOtherNo),
    None,
    Some(ExpectedAMLSTurnover.First),
    Some(BusinessFranchiseYes("Name")),
    Some(true),
    Some(CustomersOutsideUK(true, Some(Seq("GB", "AB")))),
    Some(NCARegistered(true)),
    Some(AccountantForAMLSRegulations(true)),
    Some(IdentifySuspiciousActivity(true)),
    Some(RiskAssessmentPolicyYes(Set(Digital))),
    Some(HowManyEmployees("10", "5")),
    Some(WhoIsYourAccountant("Name", Some("TradingName"),
      UkAccountantsAddress("Line1", "Line2", Some("Line3"), Some("Line4"), "AA1 1AA"))),
    Some(TaxMatters(true)),
    Some(TransactionTypes(Set(Paper, DigitalSpreadsheet, DigitalSoftware("value"))))
  )


  val modelForView = BusinessActivities(Some(InvolvedInOtherNo),
    None,
    Some(Third),
    Some(BusinessFranchiseYes("FranchiserName1")),
    Some(true),
    Some(CustomersOutsideUK(true, Some(List("AD", "GB")))), Some(NCARegistered(true)),
    Some(AccountantForAMLSRegulations(true)), Some(IdentifySuspiciousActivity(true)),
    Some(RiskAssessmentPolicyYes(Set(Digital, PaperBased))), Some(HowManyEmployees("12345678901", "11223344556")),
    Some(WhoIsYourAccountant("Name", Some("TradingName"),
      UkAccountantsAddress("AdvisorAddressLine1", "AdvisorAddressLine2", Some("AdvisorAddressLine3"), Some("AdvisorAddressLine4"), "AA1 1AA"))),
    Some(TaxMatters(true)),
    Some(TransactionTypes(Set(Paper, DigitalSpreadsheet, DigitalSoftware("CommercialPackageName"))))
  )
}

object EabSection {

  import models.fe.estateagentbusiness._

  val model = Some(EstateAgentBusiness(
    Some(Services(Set(Auction, BusinessTransfer))),
    Some(OmbudsmanServices),
    Some(ProfessionalBodyNo),
    Some(PenalisedUnderEstateAgentsActYes("Details"))))

  val modelForView = Some(EstateAgentBusiness(Some(Services(Set(Residential, Commercial, SocialHousing,
    BusinessTransfer, Development, AssetManagement, LandManagement, Auction, Relocation))),
    Some(ThePropertyOmbudsman), Some(ProfessionalBodyYes("PrevWarnWRegProvideDetails")),
    Some(PenalisedUnderEstateAgentsActYes("EstAgncActProhibProvideDetails"))))
}

object MsbSection {

  import models.fe.moneyservicebusiness._
  import models.fe.moneyservicebusiness.ExpectedThroughput.Third

  private val businessUseAnIPSP = BusinessUseAnIPSPYes("name", "123456789123456")
  private val sendTheLargestAmountsOfMoney = SendTheLargestAmountsOfMoney("GB")

  private val whichCurrencies = WhichCurrencies(Seq("USD", "MNO", "PQR"),
    usesForeignCurrencies = Some(true),
    Some(BankMoneySource("Bank names")),
    Some(WholesalerMoneySource("wholesaler names")), customerMoneySource = true)

  private val mostTransactions = MostTransactions(Seq("LA", "LV"))

  private val msb = MoneyServiceBusiness(
    Some(ExpectedThroughput.Second),
    Some(businessUseAnIPSP),
    Some(IdentifyLinkedTransactions(true)),
    Some(SendMoneyToOtherCountry(true)),
    Some(FundsTransfer(true)),
    Some(BranchesOrAgents(true, Some(Seq("GB")))),
    Some(TransactionsInNext12Months("12345678963")),
    Some(CETransactionsInNext12Months("12345678963")),
    Some(sendTheLargestAmountsOfMoney),
    Some(mostTransactions),
    Some(whichCurrencies)
  )

  val completeModel = Some(msb)

  val modelForView = Some(MoneyServiceBusiness(Some(Third),
    Some(BusinessUseAnIPSPYes("IPSPName1", "IPSPMLRRegNo1")),
    Some(IdentifyLinkedTransactions(true)),
    Some(SendMoneyToOtherCountry(true)),
    Some(FundsTransfer(true)), Some(BranchesOrAgents(true, Some(List("AD", "GB")))),
    Some(TransactionsInNext12Months("11111111111")), Some(CETransactionsInNext12Months("11234567890")),
    Some(SendTheLargestAmountsOfMoney("GB", Some("AD"), None)), Some(MostTransactions(List("AD", "GB"))),
    Some(WhichCurrencies(List("GBP", "XYZ", "ABC"), usesForeignCurrencies = Some(true), Some(BankMoneySource("BankNames1")),
      Some(WholesalerMoneySource("CurrencyWholesalerNames")), true))))
}

object TradingPremisesSection {

  import models.fe.tradingpremises._

  val model = Some(Seq(TradingPremises(Some(RegisteringAgentPremises(false)), YourTradingPremises("string",
    Address("string", "string", Some("string"), Some("string"), "AA1 1AA"), new LocalDate(2010, 1, 1), false),
    None, None, None, None,
    WhatDoesYourBusinessDo(Set(BusinessActivity.HighValueDealing, BusinessActivity.TrustAndCompanyServices, BusinessActivity.MoneyServiceBusiness)),
    Some(MsbServices(Set(TransmittingMoney, CurrencyExchange, ChequeCashingNotScrapMetal, ChequeCashingScrapMetal)))
  ),
    TradingPremises(Some(RegisteringAgentPremises(true)), YourTradingPremises("string",
      Address("string", "string", Some("string"), Some("string"), "AA1 1AA"), new LocalDate(2008, 1, 1), true),
      Some(BusinessStructure.SoleProprietor), Some(AgentName("entity name", None, Some("1970-01-01"))), None, None,
      WhatDoesYourBusinessDo(Set(BusinessActivity.EstateAgentBusinessService, BusinessActivity.BillPaymentServices))
    )
  ))

  val modelForView = Some(List(TradingPremises(Some(RegisteringAgentPremises(true)), YourTradingPremises("aaaaaaaaaaaa",
    Address("a", "a", Some("a"), Some("a"), "AA1 1AA"), new LocalDate(1967, 8, 13), true), Some(BusinessStructure.SoleProprietor),
    Some(AgentName("AgentLegalEntityName", None, Some("1970-01-01"))),
    None, None,
    WhatDoesYourBusinessDo(Set(BusinessActivity.HighValueDealing,
      BusinessActivity.AccountancyServices,
      BusinessActivity.EstateAgentBusinessService,
      BusinessActivity.BillPaymentServices,
      BusinessActivity.TelephonePaymentService,
      BusinessActivity.MoneyServiceBusiness,
      BusinessActivity.TrustAndCompanyServices)),
    Some(MsbServices(Set(TransmittingMoney, CurrencyExchange))), Some(111111), Some("Added")),
    TradingPremises(Some(RegisteringAgentPremises(true)),
      YourTradingPremises("aaaaaaaaaaaa", Address("a", "a", Some("a"), Some("a"), "AA1 1AA"),
        new LocalDate(1967, 8, 13), true),
      Some(BusinessStructure.SoleProprietor),
      Some(AgentName("aaaaaaaaaaa", None, Some("1970-01-01"))),
      None,
      None,
      WhatDoesYourBusinessDo(Set(BusinessActivity.HighValueDealing,
        BusinessActivity.AccountancyServices,
        BusinessActivity.EstateAgentBusinessService,
        BusinessActivity.BillPaymentServices,
        BusinessActivity.TelephonePaymentService,
        BusinessActivity.MoneyServiceBusiness,
        BusinessActivity.TrustAndCompanyServices)),
      Some(MsbServices(Set(TransmittingMoney, CurrencyExchange, ChequeCashingNotScrapMetal, ChequeCashingScrapMetal))), None, Some("Added")),
    TradingPremises(Some(RegisteringAgentPremises(true)),
      YourTradingPremises("TradingName",
        Address("AgentAddressLine1", "AgentAddressLine2", Some("AgentAddressLine3"), Some("AgentAddressLine4"), "XX1 1XX"),
        new LocalDate(2001, 1, 1), true),
      Some(BusinessStructure.SoleProprietor), Some(AgentName("AgentLegalEntityName2", None, Some("1970-01-01"))),
      None,
      None,
      WhatDoesYourBusinessDo(Set(BusinessActivity.HighValueDealing,
        BusinessActivity.AccountancyServices,
        BusinessActivity.EstateAgentBusinessService,
        BusinessActivity.BillPaymentServices,
        BusinessActivity.TelephonePaymentService,
        BusinessActivity.MoneyServiceBusiness,
        BusinessActivity.TrustAndCompanyServices)),
      Some(MsbServices(Set(TransmittingMoney, CurrencyExchange, ChequeCashingNotScrapMetal, ChequeCashingScrapMetal))), None, Some("Added")),
    TradingPremises(Some(RegisteringAgentPremises(false)), YourTradingPremises("OwnBusinessTradingName",
      Address("OwnBusinessAddressLine1", "OwnBusinessAddressLine2", Some("OwnBusinessAddressLine3"), Some("OwnBusinessAddressLine4"), "YY1 1YY"),
      new LocalDate(2001, 5, 5), false),
      None, None, None, None,
      WhatDoesYourBusinessDo(Set(BusinessActivity.BillPaymentServices,
        BusinessActivity.EstateAgentBusinessService,
        BusinessActivity.TrustAndCompanyServices)), None, Some(444444), Some(StatusConstants.Unchanged)),
    TradingPremises(Some(RegisteringAgentPremises(false)), YourTradingPremises("OwnBusinessTradingName1",
      Address("OB11AddressLine1", "OB1AddressLine2", Some("OB1AddressLine3"), Some("OB1AddressLine4"), "XX1 1XX"),
      new LocalDate(2001, 1, 1), false), None, None, None, None, WhatDoesYourBusinessDo(Set(BusinessActivity.HighValueDealing,
      BusinessActivity.AccountancyServices,
      BusinessActivity.EstateAgentBusinessService,
      BusinessActivity.BillPaymentServices,
      BusinessActivity.TelephonePaymentService,
      BusinessActivity.MoneyServiceBusiness,
      BusinessActivity.TrustAndCompanyServices)),
      Some(MsbServices(Set(ChequeCashingNotScrapMetal, ChequeCashingScrapMetal))), Some(555555), Some(StatusConstants.Unchanged))))


  val tradingPremisesOnlyAgentModel = Some(List(TradingPremises(Some(RegisteringAgentPremises(true)),
    YourTradingPremises("aaaaaaaaaaaa", Address("a", "a", Some("a"), Some("a"), "AA1 1AA"),
      new LocalDate(1967, 8, 13), true),
    Some(BusinessStructure.SoleProprietor),
    Some(AgentName("aaaaaaaaaaa", None, Some("1970-01-01"))),
    None,
    None,
    WhatDoesYourBusinessDo(Set(BusinessActivity.HighValueDealing,
      BusinessActivity.AccountancyServices,
      BusinessActivity.EstateAgentBusinessService,
      BusinessActivity.BillPaymentServices,
      BusinessActivity.TelephonePaymentService,
      BusinessActivity.MoneyServiceBusiness,
      BusinessActivity.TrustAndCompanyServices)),
    Some(MsbServices(Set(TransmittingMoney, CurrencyExchange, ChequeCashingNotScrapMetal, ChequeCashingScrapMetal))), None, Some("Added"))))
}

object BankDetailsSection {

  import models.fe.bankdetails._

  val model = Seq(
    BankDetails(PersonalAccount,
      "Personal account", UKAccount("12345678", "112233")),
    BankDetails(BelongsToBusiness,
      "Business account", NonUKAccountNumber("12345678")),
    BankDetails(BelongsToOtherBusiness,
      "Another Business account", NonUKIBANNumber("12345678"))
  )

  val modelForView = List(BankDetails(BelongsToBusiness, "AccountName", UKAccount("12345678", "123456")),
    BankDetails(PersonalAccount, "AccountName1", NonUKIBANNumber("87654321")),
    BankDetails(BelongsToOtherBusiness, "AccountName2", NonUKAccountNumber("87654321")))

}

object BusinessMatchingSection {

  import models.fe.businesscustomer.{Address, ReviewDetails}
  import models.fe.businessmatching.{BusinessType, _}

  private val msbService = MsbServices(Set(TransmittingMoney, ChequeCashingNotScrapMetal, CurrencyExchange))
  private val psrPSRNumber = Some(BusinessAppliedForPSRNumberYes("123456"))
  private val bcAddress = Address("line1", "line2", Some("line3"), Some("line4"), Some("AA1 1AA"), "GB")
  private val reviewDetails = ReviewDetails("BusinessName", BusinessType.SoleProprietor, bcAddress, "XE0001234567890")
  val model = BusinessMatching(
    reviewDetails,
    models.fe.businessmatching.BusinessActivities(Set(MoneyServiceBusiness, HighValueDealing, AccountancyServices)),
    Some(msbService),
    None,
    None,
    psrPSRNumber)

  val msbServices = Some(MsbServices(Set(TransmittingMoney,
    CurrencyExchange, ChequeCashingNotScrapMetal,
    ChequeCashingScrapMetal)))
  val psrNumber = Some(BusinessAppliedForPSRNumberYes("123456"))

  val modelForView = BusinessMatching(
    ReviewDetails("CompanyName", BusinessType.SoleProprietor, Address("BusinessAddressLine1", "BusinessAddressLine2",
      Some("BusinessAddressLine3"), Some("BusinessAddressLine4"),
      Some("AA1 1AA"), "GB"), ""),
    BusinessActivities(Set(HighValueDealing, AccountancyServices, EstateAgentBusinessService,
      BillPaymentServices, TelephonePaymentService, MoneyServiceBusiness, TrustAndCompanyServices)),
    msbServices,
    Some(TypeOfBusiness("TypeOfBusiness")), Some(CompanyRegistrationNumber("12345678")), psrNumber)
}

object AboutTheBusinessSection {

  import models.fe.aboutthebusiness._

  private val regForCorpTax = CorporationTaxRegisteredYes("1234567890")
  // scalastyle:off magic.number
  val model = AboutTheBusiness(PreviouslyRegisteredYes("12345678"),
    Some(ActivityStartDate(new LocalDate(1990, 2, 24))),
    Some(VATRegisteredYes("123456789")),
    Some(regForCorpTax),
    ContactingYou("019212323222323222323222323222", "abc@hotmail.co.uk"),
    RegisteredOfficeUK("line1", "line2",
      Some("some street"), Some("some city"), "EE1 1EE"),
    true,
    Some(UKCorrespondenceAddress("kap", "Trading", "Park", "lane",
      Some("Street"), Some("city"), "EE1 1EE"))
  )

  val modelForView = AboutTheBusiness(PreviouslyRegisteredNo, Some(ActivityStartDate(new LocalDate(2001, 1, 1))),
    Some(VATRegisteredYes("123456789")),
    Some(CorporationTaxRegisteredYes("1234567891")),
    ContactingYou("07000111222", "BusinessEmail"),
    RegisteredOfficeUK("BusinessAddressLine1", "BusinessAddressLine2", Some("BusinessAddressLine3"), Some("BusinessAddressLine4"), "AA1 1AA"),
    true,
    Some(UKCorrespondenceAddress("Name", "TradingName", "AlternativeAddressLine1", "AlternativeAddressLine2", Some("AlternativeAddressLine3"),
      Some("AlternativeAddressLine4"), "AA1 1AA")))
}

object ResponsiblePeopleSection {

  import models.fe.responsiblepeople.TimeAtAddress.{OneToThreeYears, SixToElevenMonths, ZeroToFiveMonths}
  import models.fe.responsiblepeople._

  private val residence = UKResidence("nino")
  private val residenceCountry = "GB"
  private val residenceNationality = "GB"
  private val currentPersonAddress = PersonAddressUK("ccLine 1", "ccLine 2", None, None, "AA1 1AA")
  private val currentAddress = ResponsiblePersonAddress(currentPersonAddress, ZeroToFiveMonths)
  private val additionalPersonAddress = PersonAddressUK("Line 1", "Line 2", None, None, "BB1 1BB")
  private val additionalAddress = ResponsiblePersonAddress(additionalPersonAddress, SixToElevenMonths)
  private val extraAdditionalAddress = PersonAddressUK("e Line 1", "e Line 2", Some("e Line 3"), Some("e Line 4"), "CC1 1CC")
  private val extraAdditionalPersonAddress = ResponsiblePersonAddress(extraAdditionalAddress, OneToThreeYears)

  private val personName = PersonName(
    firstName = "name",
    middleName = Some("some"),
    lastName = "surname"
  )

  private val previousName = PreviousName(
    hasPreviousName = true,
    firstName = Some("fname"),
    middleName = Some("mname"),
    lastName = Some("lname")
  )

  private val otherNames = Some(KnownBy(true, Some("Doc")))
  private val nameDateOfChange = new LocalDate(1990, 2, 24)
  private val personResidenceType = PersonResidenceType(residence, residenceCountry, residenceNationality)
  private val saRegistered = SaRegisteredYes("0123456789")
  private val contactDetails = ContactDetails("07000001122", "test@test.com")
  private val addressHistory = ResponsiblePersonAddressHistory(Some(currentAddress), Some(additionalAddress), Some(extraAdditionalPersonAddress))
  private val vatRegistered = VATRegisteredNo
  private val training = TrainingYes("test")
  private val experienceTraining = ExperienceTrainingYes("Some training")
  private val positions = Positions(Set(SoleProprietor, NominatedOfficer), Some(new LocalDate()))

  private val ukPassport = UKPassportYes("87654321")
  private val nonUKPassport = NonUKPassportYes("87654321")

  val model = Some(Seq(ResponsiblePeople(
    Some(personName),
    Some(previousName),
    Some(nameDateOfChange),
    otherNames,
    Some(personResidenceType),
    Some(ukPassport),
    Some(nonUKPassport),
    None,
    Some(contactDetails),
    Some(addressHistory),
    Some(positions),
    Some(saRegistered),
    Some(vatRegistered),
    Some(experienceTraining),
    Some(training),
    Some(true)
  )))

  val modelForView = Some(List(
    ResponsiblePeople(
      Some(PersonName("FirstName", Some("MiddleName"), "LastName")),
      Some(PreviousName(true, Some("FirstName"), Some("MiddleName"), Some("LastName"))),
      Some(new LocalDate(2001, 1, 1)),
      Some(KnownBy(true, Some("Aliases1"))),
      Some(PersonResidenceType(NonUKResidence, "AA", "AA")),
      Some(UKPassportYes("AA1111111")),
      None,
      Some(DateOfBirth(new LocalDate(2001, 1, 1))),
      None,
      Some(ResponsiblePersonAddressHistory(
        Some(ResponsiblePersonAddress(PersonAddressUK("CurrentAddressLine1",
          "CurrentAddressLine2", Some("CurrentAddressLine3"), Some("CurrentAddressLine4"), "AA1 1AA"),
          ThreeYearsPlus)),
        None,
        None
      )),
      Some(Positions(Set(NominatedOfficer, SoleProprietor), Some(new LocalDate()))),
      Some(SaRegisteredYes("1234567890")),
      Some(VATRegisteredYes("123456789")),
      Some(ExperienceTrainingNo),
      Some(TrainingYes("TrainingDetails")),
      Some(false),
      Some(333333)),

    ResponsiblePeople(
      Some(PersonName("bbbbbbbbbbbb", Some("bbbbbbbbbbb"), "bbbbbbbbbbb")),
      Some(PreviousName(true, Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"))),
      Some(new LocalDate(1967, 8, 13)),
      Some(KnownBy(true, Some("bbbbbbbbbbb"))),
      Some(PersonResidenceType(UKResidence("BB000000A"), "GB", "GB")),
      None, None, None, None,
      Some(ResponsiblePersonAddressHistory(
        Some(ResponsiblePersonAddress(PersonAddressUK("b", "b", Some("b"), Some("b"), "AA1 1AA"), ZeroToFiveMonths)),
        Some(ResponsiblePersonAddress(PersonAddressUK("b", "b", Some("b"), Some("b"), "AA1 1AA"), ZeroToFiveMonths)),
        Some(ResponsiblePersonAddress(PersonAddressUK("a", "a", Some("a"), Some("a"), "AA1 1AA"), SixToElevenMonths)))),
      Some(Positions(Set(NominatedOfficer, SoleProprietor), Some(new LocalDate()))),
      Some(SaRegisteredYes("1111111111")),
      Some(VATRegisteredYes("111111111")),
      Some(ExperienceTrainingYes("bbbbbbbbbb")),
      Some(TrainingNo),
      Some(true),
      Some(222222)
    )))
}

object HvdSection {

  import models.fe.hvd._
  import models.fe.hvd.PercentageOfCashPaymentOver15000.Second

  private val DefaultCashPayment = CashPaymentYes(new LocalDate(1978, 2, 15))
  private val DefaultProducts = Products(Set(Antiques, Cars, OtherMotorVehicles, Other("Details")))
  private val DefaultExciseGoods = ExciseGoods(true)
  private val DefaultLinkedCashPayment = LinkedCashPayments(true)
  private val DefaultHowWillYouSellGoods = HowWillYouSellGoods(Seq(Retail, Auction))
  private val DefaultPercentageOfCashPaymentOver15000 = Second
  private val paymentMethods = PaymentMethods(courier = true, direct = true, other = true, details = Some("foo"))

  val completeModel = Some(Hvd(cashPayment = Some(DefaultCashPayment),
    products = Some(DefaultProducts),
    exciseGoods = Some(DefaultExciseGoods),
    linkedCashPayment = Some(DefaultLinkedCashPayment),
    howWillYouSellGoods = Some(DefaultHowWillYouSellGoods),
    receiveCashPayments = Some(true),
    cashPaymentMethods = Some(paymentMethods),
    percentageOfCashPaymentOver15000 = Some(DefaultPercentageOfCashPaymentOver15000)
  ))

  val modelForView = Some(Hvd(
    Some(CashPaymentYes(new LocalDate(2001, 1, 1))),
    Some(Products(Set(MobilePhones, Clothing, Jewellery, ScrapMetals, Alcohol, Caravans, Gold, Other("SpecifyOther"), Tobacco, Antiques, Cars, OtherMotorVehicles))),
    Some(ExciseGoods(true)), Some(HowWillYouSellGoods(List(Retail, Wholesale, Auction))),
    None,
    Some(true),
    Some(PaymentMethods(true, true, true, Some("aaaaaaaaaaaaa"))),
    Some(LinkedCashPayments(true))))
}

object SubscriptionViewModel {

  val convertedViewModel = SubscriptionView(
    "111111",
    BusinessMatchingSection.modelForView,
    EabSection.modelForView,
    TradingPremisesSection.modelForView,
    AboutTheBusinessSection.modelForView,
    BankDetailsSection.modelForView,
    AboutYouSection.modelforView,
    BusinessActivitiesSection.modelForView,
    ResponsiblePeopleSection.modelForView,
    ASPTCSPSection.TcspModelForView,
    ASPTCSPSection.AspModelForView,
    MsbSection.modelForView,
    HvdSection.modelForView,
    SupervisionSection.modelForView
  )
}
