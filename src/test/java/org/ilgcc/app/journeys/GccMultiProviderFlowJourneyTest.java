package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ilgcc.app.data.importer.FakeResourceOrganizationAndCountyData.ACTIVE_FOUR_C_COUNTY;

import java.io.IOException;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"il-gcc.enable-multiple-providers=true"})
public class GccMultiProviderFlowJourneyTest extends AbstractBasePageTest {

    @Test
    void fullGccFlow() throws IOException {
        // Home page
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("index.title"));
        testPage.clickButton(getEnMessage("index.apply-now"));
        // onboarding-getting-started
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-getting-started.title"));
        testPage.clickContinue();
        //onboarding-2-part.html
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-2-part.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("onboarding-2-part.header"));
        testPage.clickContinue();
        // onboarding-language-pref
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-language-pref.title"));
        testPage.selectFromDropdown("languageRead", "English");
        testPage.selectFromDropdown("languageSpeak", "Español");
        testPage.clickContinue();
        // onboarding-county
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-county.title"));
        testPage.selectFromDropdown("applicationCounty", "DEKALB");
        testPage.clickContinue();

        // parent-info-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-intro.title"));
        assertThat(testPage.findElementTextById("parent-info-intro-step")).isEqualTo("Step 1 of 5");
        testPage.clickContinue();
        // parent-info-basic-1
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-basic-1.title"));
        testPage.enter("parentLastName", "parent last");
        testPage.enter("parentPreferredName", "Preferred Parent First");
        testPage.enter("parentOtherLegalName", "Parent Other Legal Name");
        testPage.enter("parentBirthMonth", "12");
        testPage.enter("parentBirthDay", "25");
        testPage.enter("parentBirthYear", "1985");
        testPage.clickElementById("parentGender-MALE-label");
        testPage.clickContinue();

        // parent-info-basic-1
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
        assertThat(testPage.findElementsByClass("notice--toolbar").size()).isEqualTo(1);
        testPage.enter("parentFirstName", "parent first");
        testPage.clickContinue();
        // parent-info-service
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-service.title"));
        testPage.clickContinue();
        // parent-info-disability
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-disability.title"));
        testPage.clickYes();
        //parent-home-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-home-address.title"));
        testPage.enter("parentHomeStreetAddress1", "123 Main St");
        testPage.enter("parentHomeStreetAddress2", "5th floor");
        testPage.enter("parentHomeCity", "Sycamore");
        testPage.selectFromDropdown("parentHomeState", "IL - Illinois");
        testPage.enter("parentHomeZipCode", ACTIVE_FOUR_C_COUNTY.getZipCode().toString());
        testPage.clickContinue();

        // confirm-parent-home-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("confirm-address.title"));
        testPage.clickButton(getEnMessage("address-validation.button.use-this-address"));

        // parent-mailing-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-mailing-address.title"));
        testPage.clickElementById("parentMailingAddressSameAsHomeAddress-yes");
        testPage.clickContinue();

        // parent-confirm-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-confirm-address.title"));
        testPage.clickButton(getEnMessage("address-validation.button.use-this-address"));
        // parent-comm-preference
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-comm-preference.title"));
        testPage.selectRadio("parentContactPreferredCommunicationMethod", "email");
        testPage.clickContinue();
        // parent-contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-contact-info.title"));
        testPage.enter("parentContactEmail", "test@email.org");
        testPage.clickContinue();

        //parent-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-review.title"));
        testPage.clickContinue();
        //parent-have-a-partner
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-have-a-partner.title"));
        testPage.clickNo();

        // parent-other-family
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-other-family.title"));
        testPage.clickNo();

        //children-info-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-info-intro.title"));
        assertThat(testPage.findElementTextById("children-info-intro-step")).isEqualTo("Step 2 of 5");
        testPage.clickContinue();

        // children-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-add.title"));
        testPage.clickButton(getEnMessage("children-add.add-button"));

        //children-info-basic
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-info-basic.title"));
        testPage.enter("childFirstName", "child");
        testPage.enter("childLastName", "mcchild");
        testPage.enter("childDateOfBirthMonth", "12");
        testPage.enter("childDateOfBirthDay", "25");
        testPage.enter("childDateOfBirthYear", "2018");
        testPage.selectFromDropdown("childRelationship", getEnMessage("general.relationship-option.foster-child"));
        testPage.clickContinue();

        //children-info-assistance
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-info-assistance.title"));
        testPage.clickYes();

        //children-ccap-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-ccap-info.title"));
        testPage.clickElementById("childGender-MALE");
        testPage.clickElementById("childGender-TRANSGENDER");
        testPage.selectRadio("childHasDisability", "No");
        testPage.selectRadio("childIsUsCitizen", "Yes");
        testPage.clickElementById("none__checkbox-childRaceEthnicity");
        testPage.clickContinue();

        //children-ccap-child-other-ed
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-ccap-child-other-ed.title"));
        testPage.clickYes();

        // children-school-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-school-weekly-schedule.title"));
        testPage.enter("childOtherEdHoursDescription", "M-F (8am - 5pm)");
        testPage.clickContinue();

        //children-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-add.title"));
        testPage.clickButton(getEnMessage("children-add.thats-all"));

        //providers-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-intro.title"));
        testPage.clickContinue();

        //providers-chosen
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-chosen.title"));
        testPage.clickYes();

        //First Iteration
        //providers-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-add.header"));
        testPage.clickButton(getEnMessage("providers-add.button.add-a-provider"));

        //providers-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-info.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-info.header"));
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-street"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-city"))).isTrue();

        testPage.enter("familyIntendedProviderName", "ACME Daycare");
        testPage.enter("familyIntendedProviderAddress", "101 Test St");
        testPage.enter("familyIntendedProviderCity", "Chicago");
        testPage.selectFromDropdown("familyIntendedProviderState", "IL - Illinois");
        testPage.clickContinue();

        //providers-contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-contact-info.title"));
        testPage.enter("familyIntendedProviderEmail", "test.com");
        testPage.enter("familyIntendedProviderPhoneNumber", "555-55");
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-phone-number"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-email"))).isTrue();

        testPage.enter("familyIntendedProviderEmail", "test@test.com");
        testPage.enter("familyIntendedProviderPhoneNumber", "(555)555-5555");
        testPage.clickContinue();

        //providers-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-add.header"));
        assertThat(testPage.findElementTextById("continue-link")).isEqualTo(getEnMessage("providers-add.button.that-is-all"));
        assertThat(testPage.findElementById("continue-link").getCssValue("pointer-events")).isEqualTo("auto");
        assertThat(testPage.findElementById("add-providers").getCssValue("pointer-events")).isEqualTo("auto");
        testPage.clickButton(getEnMessage("providers-add.button.add-a-provider"));

        //Second Iteration
        //provider-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-info.title"));
        testPage.enter("familyIntendedProviderName", "Nope Test");
        testPage.enter("familyIntendedProviderAddress", "151 Second St");
        testPage.enter("familyIntendedProviderCity", "Chicago");
        testPage.selectFromDropdown("familyIntendedProviderState", "IL - Illinois");
        testPage.clickContinue();

        //providers-contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-contact-info.title"));
        testPage.enter("familyIntendedProviderEmail", "second@test.com");
        testPage.enter("familyIntendedProviderPhoneNumber", "(333)333-2222");
        testPage.clickContinue();

        //providers-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
        testPage.clickButton(getEnMessage("providers-add.button.that-is-all"));

        //providers-info-confirm
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-info-confirm.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-info-confirm.header"));
        testPage.clickContinue();

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-intro.title"));
        assertThat(testPage.findElementTextById("activities-parent-intro-step")).isEqualTo("Step 3 of 5");
        testPage.clickContinue();

        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-type.title"));
        testPage.clickElementById("activitiesParentChildcareReason-WORKING");
        testPage.clickContinue();

        //activities-add-jobs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-add-jobs.title"));
        testPage.clickButton(getEnMessage("activities-add-jobs.add-a-job"));
        // Add First Job
        //activities-employer-name
        assertThat(testPage.getTitle()).isEqualTo("Activities Employer Name");
        testPage.enter("companyName", "testCompany");
        testPage.clickContinue();
        //activities-employer-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-employer-address.title"));
        testPage.enter("employerPhoneNumber", "3333333");
        testPage.enter("employerCity", "Chicago");
        testPage.enter("employerStreetAddress", "123 Test Me");
        testPage.enter("employerState", "IL - Illinois");
        testPage.enter("employerZipCode", "6042");
        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-phone-number"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-zipcode"))).isTrue();
        testPage.enter("employerPhoneNumber", "3333333333");
        testPage.enter("employerZipCode", "60423");
        testPage.clickContinue();
        //activities-employer-start-date
        testPage.enter("activitiesJobStartMonth", "10");
        testPage.enter("activitiesJobStartDay", "1");
        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("activities-employer-start-date.error"))).isTrue();
        testPage.enter("activitiesJobStartYear", "1899");
        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("activities-employer-start-date.error"))).isTrue();
        testPage.enter("activitiesJobStartYear", "2000");
        testPage.clickContinue();
        //activities-self-employment
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-self-employment.title"));
        testPage.clickYes();

        //activities-work-schedule-vary
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-work-schedule-vary.title"));
        testPage.clickYes();

        //activities-next-work-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-next-work-schedule.title"));
        testPage.clickContinue();

        //activities-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-job-weekly-schedule.title"));
        testPage.clickElementById("activitiesJobWeeklySchedule-Monday");
        testPage.clickElementById("activitiesJobWeeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-job-hourly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-job-hourly-schedule.title"));

        testPage.selectFromDropdown("activitiesJobStartTimeMondayHour", "12");
        testPage.enter("activitiesJobStartTimeMondayMinute", "00");
        testPage.selectFromDropdown("activitiesJobStartTimeMondayAmPm", "PM");

        testPage.selectFromDropdown("activitiesJobEndTimeMondayHour", "1");
        testPage.enter("activitiesJobEndTimeMondayMinute", "00");
        testPage.selectFromDropdown("activitiesJobEndTimeMondayAmPm", "PM");

        testPage.selectFromDropdown("activitiesJobStartTimeSundayHour", "2");
        testPage.enter("activitiesJobStartTimeSundayMinute", "00");
        testPage.selectFromDropdown("activitiesJobStartTimeSundayAmPm", "PM");

        testPage.selectFromDropdown("activitiesJobEndTimeSundayHour", "3");
        testPage.enter("activitiesJobEndTimeSundayMinute", "00");
        testPage.selectFromDropdown("activitiesJobEndTimeSundayAmPm", "PM");

        testPage.clickContinue();

        //activities-work-commute-time
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-work-commute-time.title"));
        testPage.selectFromDropdown("activitiesJobCommuteTime", getEnMessage("general.hours.1.hour"));
        testPage.clickContinue();

        //activities-add-jobs (list)
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-add-jobs.title"));
        testPage.clickButton(getEnMessage("activities-add-jobs.this-is-all-my-jobs"));

        //unearned-income-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-intro.title"));
        assertThat(testPage.findElementTextById("unearned-income-intro-step")).isEqualTo("Step 4 of 5");
        testPage.clickContinue();

        //unearned-income-source
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-source.title"));
        testPage.clickElementById("unearnedIncomeSource-ROYALTIES-label");
        testPage.clickElementById("unearnedIncomeSource-RENTAL-label");
        testPage.clickContinue();

        //unearned-income-amount
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-amount.title"));
        testPage.enter("unearnedIncomeRoyalties", "35");
        testPage.clickContinue();

        //unearned-income-assets
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-assets.title"));
        testPage.clickElementById("unearnedIncomeAssetsMoreThanOneMillionDollars-true");
        //unearned-income-child-support
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-child-support.title"));
        testPage.clickElementById("doesAnyoneInHouseholdPayChildSupport-true");
        //unearned-income-child-support-amount
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-child-support-account.title"));
        testPage.enter("amountYourHouseholdPaysInChildSupport", "1453");
        testPage.clickContinue();
        //unearned-income-programs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-programs.title"));
        testPage.clickElementById("unearnedIncomePrograms-CASH_ASSISTANCE");
        testPage.clickElementById("unearnedIncomePrograms-SNAP");
        testPage.clickContinue();

        //unearned-income-referral-services
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-referral-services.title"));
        testPage.clickElementById("unearnedIncomeReferralServices-SAFE_SUPPORT");
        testPage.clickContinue();

        // submit-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-intro.title"));
        assertThat(testPage.findElementTextById("submit-intro-step")).isEqualTo("Step 5 of 5");
        testPage.clickContinue();

        // submit-ccap-terms
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-ccap-terms.title"));
        testPage.clickElementById("agreesToLegalTerms-true");
        testPage.clickContinue();

        // submit-sign-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-sign-name.title"));
        testPage.enter("signedName", "parent first parent last");
        testPage.clickButton(getEnMessage("submit-sign-name.submit-application"));

        // after-submit-contact-provider
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("after-submit-contact-provider.title"));
        testPage.clickElementById("contactProviderMethod-EMAIL-label");
        testPage.clickElementById("contactProviderMethod-TEXT-label");
        testPage.clickElementById("contactProviderMethod-OTHER-label");
        testPage.clickContinue();

        // contact-providers-start

    }

    @Test
    void AllowsForThreeProvidersToBeAddedWhenClientAnswersYesToHavingChosenAProviderForEveryChildJourneyTest() {
        testPage.navigateToFlowScreen("gcc/parent-info-disability");

        saveSubmission(getSessionSubmissionTestBuilder().withParentBasicInfo().with("familyIntendedProviderName", "ACME Daycare")
                .with("applicationCounty", "LEE").withChild("First", "Child", "true").withChild("Second", "Child", "true")
                .withShortCode("familyShortCode").build());

        testPage.navigateToFlowScreen("gcc/children-add");
        testPage.clickButton(getEnMessage("children-add.thats-all"));

        //providers-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-intro.title"));
        testPage.clickContinue();

        //providers-chosen
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-chosen.title"));
        testPage.clickYes();

        //providers-all-ccap-children
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-all-ccap-children.title"));
        testPage.clickYes();

        //First Iteration
        //providers-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-add.header"));
        testPage.clickButton(getEnMessage("providers-add.button.add-a-provider"));

        //providers-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-info.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-info.header"));
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-street"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-city"))).isTrue();

        testPage.enter("familyIntendedProviderName", "ACME Daycare");
        testPage.enter("familyIntendedProviderAddress", "101 Test St");
        testPage.enter("familyIntendedProviderCity", "Chicago");
        testPage.selectFromDropdown("familyIntendedProviderState", "IL - Illinois");
        testPage.clickContinue();

        //providers-contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-contact-info.title"));
        testPage.enter("familyIntendedProviderEmail", "test.com");
        testPage.enter("familyIntendedProviderPhoneNumber", "555-55");
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-phone-number"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-email"))).isTrue();

        testPage.enter("familyIntendedProviderEmail", "test@test.com");
        testPage.enter("familyIntendedProviderPhoneNumber", "(555)555-5555");
        testPage.clickContinue();

        //providers-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-add.header"));
        assertThat(testPage.findElementTextById("continue-link")).isEqualTo(getEnMessage("providers-add.button.that-is-all"));
        assertThat(testPage.findElementById("continue-link").getCssValue("pointer-events")).isEqualTo("auto");
        assertThat(testPage.findElementById("add-providers").getCssValue("pointer-events")).isEqualTo("auto");
        testPage.clickButton(getEnMessage("providers-add.button.add-a-provider"));

        //Second Iteration
        //provider-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-info.title"));
        testPage.enter("familyIntendedProviderName", "Nope Test");
        testPage.enter("familyIntendedProviderAddress", "151 Second St");
        testPage.enter("familyIntendedProviderCity", "Chicago");
        testPage.selectFromDropdown("familyIntendedProviderState", "IL - Illinois");
        testPage.clickContinue();

        //providers-contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-contact-info.title"));
        testPage.enter("familyIntendedProviderEmail", "second@test.com");
        testPage.enter("familyIntendedProviderPhoneNumber", "(333)333-2222");
        testPage.clickContinue();

        //providers-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
        assertThat(testPage.findElementTextById("continue-link")).isEqualTo(getEnMessage("providers-add.button.that-is-all"));
        assertThat(testPage.findElementById("continue-link").getCssValue("pointer-events")).isEqualTo("auto");
        assertThat(testPage.findElementById("add-providers").getCssValue("pointer-events")).isEqualTo("auto");
        testPage.clickButton(getEnMessage("providers-add.button.add-a-provider"));

        //Third Iteration
        //provider-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-info.title"));
        testPage.enter("familyIntendedProviderName", "Third Provider");
        testPage.enter("familyIntendedProviderAddress", "441 Third St");
        testPage.enter("familyIntendedProviderCity", "Chicago");
        testPage.selectFromDropdown("familyIntendedProviderState", "IL - Illinois");
        testPage.clickContinue();

        //providers-contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-contact-info.title"));
        testPage.enter("familyIntendedProviderEmail", "third@test.com");
        testPage.enter("familyIntendedProviderPhoneNumber", "(243)555-5555");
        testPage.clickContinue();

        //providers-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-add.header"));
        assertThat(testPage.findElementTextById("continue-link")).isEqualTo(getEnMessage("providers-add.button.that-is-all"));
        assertThat(testPage.findElementById("continue-link").getCssValue("pointer-events")).isEqualTo("auto");
        assertThat(testPage.findElementById("add-providers").getCssValue("pointer-events")).isEqualTo("none");

        //delete-provider -- don't actually delete!
        assertThat(testPage.findElementsByClass("spacing-below-0").get(4).getText()).isEqualTo("Nope Test");
        testPage.findElementsByClass("subflow-delete").get(1).click();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("delete-confirmation.title"));
        testPage.clickButton(getEnMessage("delete-confirmation.no"));

        // back to providers-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));

        //delete-provider -- actually delete!
        assertThat(testPage.findElementsByClass("spacing-below-0").get(4).getText()).isEqualTo("Nope Test");
        testPage.findElementsByClass("subflow-delete").get(1).click();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("delete-confirmation.title"));
        testPage.clickButton(getEnMessage("delete-confirmation.yes"));

        // back to providers-add -- check the provider is gone and then re-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
        assertThat(testPage.findElementTextById("continue-link")).isEqualTo(getEnMessage("providers-add.button.that-is-all"));
        assertThat(testPage.findElementById("continue-link").getCssValue("pointer-events")).isEqualTo("auto");
        assertThat(testPage.findElementById("add-providers").getCssValue("pointer-events")).isEqualTo("auto");
        testPage.clickButton(getEnMessage("providers-add.button.add-a-provider"));

        //provider-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-info.title"));
        testPage.enter("familyIntendedProviderName", "Nope Test");
        testPage.enter("familyIntendedProviderAddress", "151 Second St");
        testPage.enter("familyIntendedProviderCity", "Chicago");
        testPage.selectFromDropdown("familyIntendedProviderState", "IL - Illinois");
        testPage.clickContinue();

        //providers-contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-contact-info.title"));
        testPage.enter("familyIntendedProviderEmail", "second@test.com");
        testPage.enter("familyIntendedProviderPhoneNumber", "(333)333-2222");
        testPage.clickContinue();

        // back to providers-add -- check the provider count is back to the max
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-add.header"));
        assertThat(testPage.findElementTextById("continue-link")).isEqualTo(getEnMessage("providers-add.button.that-is-all"));
        assertThat(testPage.findElementById("continue-link").getCssValue("pointer-events")).isEqualTo("auto");
        assertThat(testPage.findElementById("add-providers").getCssValue("pointer-events")).isEqualTo("none");
        testPage.clickButton(getEnMessage("providers-add.button.that-is-all"));

        //providers-info-confirm
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-info-confirm.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-info-confirm.header"));
    }

    @Test
    void AllowsOnlyTwoProvidersToBeAddedWhenClientAnswersNoToHavingChosenAProviderForEveryChildJourneyTest() {
        testPage.navigateToFlowScreen("gcc/parent-info-disability");

        saveSubmission(getSessionSubmissionTestBuilder().withParentBasicInfo().with("familyIntendedProviderName", "ACME Daycare")
                .with("applicationCounty", "LEE").withChild("First", "Child", "true").withChild("Second", "Child", "true")
                .withShortCode("familyShortCode").build());

        testPage.navigateToFlowScreen("gcc/children-add");
        testPage.clickButton(getEnMessage("children-add.thats-all"));
        //providers-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-intro.title"));
        testPage.clickContinue();

        //providers-chosen
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-chosen.title"));
        testPage.clickYes();

        //providers-all-ccap-children
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-all-ccap-children.title"));
        testPage.clickNo();

        //First Iteration
        //providers-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-add.header"));
        testPage.clickButton(getEnMessage("providers-add.button.add-a-provider"));

        //providers-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-info.title"));

        testPage.enter("familyIntendedProviderName", "First Daycare");
        testPage.enter("familyIntendedProviderAddress", "222 Test St");
        testPage.enter("familyIntendedProviderCity", "Chicago");
        testPage.selectFromDropdown("familyIntendedProviderState", "IL - Illinois");
        testPage.clickContinue();

        //providers-contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-contact-info.title"));

        testPage.enter("familyIntendedProviderEmail", "test@first.com");
        testPage.enter("familyIntendedProviderPhoneNumber", "(533)555-5555");
        testPage.clickContinue();

        //providers-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-add.header"));
        assertThat(testPage.findElementTextById("continue-link")).isEqualTo(getEnMessage("providers-add.button.that-is-all"));
        assertThat(testPage.findElementById("continue-link").getCssValue("pointer-events")).isEqualTo("auto");
        assertThat(testPage.findElementById("add-providers").getCssValue("pointer-events")).isEqualTo("auto");

        testPage.clickButton(getEnMessage("providers-add.button.add-a-provider"));

        //Second Iteration
        //provider-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-info.title"));
        testPage.enter("familyIntendedProviderName", "No Provider");
        testPage.enter("familyIntendedProviderAddress", "323 Second St");
        testPage.enter("familyIntendedProviderCity", "Chicago");
        testPage.selectFromDropdown("familyIntendedProviderState", "IL - Illinois");
        testPage.clickContinue();

        //providers-contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-contact-info.title"));
        testPage.enter("familyIntendedProviderEmail", "test@second.com");
        testPage.enter("familyIntendedProviderPhoneNumber", "(355)333-2222");
        testPage.clickContinue();

        //providers-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
        assertThat(testPage.findElementTextById("continue-link")).isEqualTo(getEnMessage("providers-add.button.that-is-all"));
        assertThat(testPage.findElementById("continue-link").getCssValue("pointer-events")).isEqualTo("auto");
        assertThat(testPage.findElementById("add-providers").getCssValue("pointer-events")).isEqualTo("none");
        testPage.clickLink(getEnMessage("general.edit"));

        //providers-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-info.title"));
        assertThat(testPage.getInputValue("familyIntendedProviderName")).isEqualTo("First Daycare");
        testPage.clickContinue();

        //providers-contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-contact-info.title"));
        testPage.clickContinue();

        //providers-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
        assertThat(testPage.findElementById("continue-link").getCssValue("pointer-events")).isEqualTo("auto");
        assertThat(testPage.findElementById("add-providers").getCssValue("pointer-events")).isEqualTo("none");
        testPage.clickButton(getEnMessage("providers-add.button.that-is-all"));
    }

    @Test
    void MultiProviderNavigationWhenNoProviderJourneyTest() {
        testPage.navigateToFlowScreen("gcc/parent-info-disability");

        saveSubmission(getSessionSubmissionTestBuilder().withParentBasicInfo().with("familyIntendedProviderName", "ACME Daycare")
                .with("applicationCounty", "LEE").withChild("First", "Child", "true").withChild("Second", "Child", "true")
                .withShortCode("familyShortCode").build());

        testPage.navigateToFlowScreen("gcc/providers-intro");
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-intro.title"));
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-chosen.title"));
        testPage.clickNo();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-no-provider-intro.title"));
        testPage.clickButton(getEnMessage("providers-no-provider-intro.continue"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-intro.title"));
    }
}
