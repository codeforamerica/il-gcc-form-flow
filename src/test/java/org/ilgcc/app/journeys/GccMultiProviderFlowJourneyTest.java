package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ilgcc.app.data.importer.FakeResourceOrganizationAndCountyData.ACTIVE_FOUR_C_COUNTY;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
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
        assertThat(testPage.findElementTextById("parent-info-intro-step")).isEqualTo("Step 1 of 7");
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

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-intro.title"));
        assertThat(testPage.findElementTextById("activities-parent-intro-step")).isEqualTo("Step 2 of 7");
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

        //activities-job-weekly-schedule one day
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-job-weekly-schedule.title"));
        testPage.clickElementById("activitiesJobWeeklySchedule-Monday");
        testPage.clickContinue();

        //activities-job-hourly-schedule one day
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-job-hourly-schedule.title"));
        assertThat(driver.findElements(By.id("activitiesJobHoursSameEveryDay-Yes")).isEmpty()).isTrue();
        testPage.goBack();

        //activities-job-weekly-schedule two days
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-job-weekly-schedule.title"));
        testPage.clickElementById("activitiesJobWeeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-job-hourly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-job-hourly-schedule.title"));
        assertThat(testPage.findElementById("activitiesJobHoursSameEveryDay-Yes").isDisplayed()).isTrue();

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

        //children-info-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-info-intro-multiple.title"));
        assertThat(testPage.findElementTextById("children-info-intro-multiple-step")).isEqualTo("Step 3 of 7");
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
        assertThat(testPage.findElementTextById("providers-intro-step")).isEqualTo("Step 4 of 7");
        testPage.clickContinue();

        //providers-chosen
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-chosen.title"));
        testPage.clickYes();

        //First Iteration
        //providers-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-add.header"));
        testPage.clickButton(getEnMessage("providers-add.button.add-a-provider"));
        
        //providers-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-type.title"));
        testPage.selectRadio("providerType", "Individual");
        testPage.clickContinue();
        
        //providers-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-name.title"));
        assertThat(testPage.elementDoesNotExistById("childCareProgramName")).isTrue();
        assertThat(testPage.findElementById("providerFirstName")).isNotNull();
        assertThat(testPage.findElementById("providerLastName")).isNotNull();
        
        // Go back and Select Care Program
        testPage.goBack();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-type.title"));
        testPage.selectRadio("providerType", "Care Program");
        testPage.clickContinue();
        
        //providers-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-name.title"));
        assertThat(testPage.findElementById("childCareProgramName")).isNotNull();
        assertThat(testPage.findElementById("providerFirstName")).isNotNull();
        assertThat(testPage.findElementById("providerLastName")).isNotNull();
        testPage.enter("childCareProgramName", "ACME Daycare");
        testPage.clickContinue();

        //providers-location
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-location.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-location.header"));
        testPage.clickContinue();
        
        
        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-street"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-city"))).isTrue();
        
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
        // providers-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-type.title"));
        testPage.selectRadio("providerType", "Care Program");
        testPage.clickContinue();
        
        // providers-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-name.title"));
        testPage.enter("childCareProgramName", "Nope Test");
        testPage.clickContinue();
        
        //provider-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-location.title"));
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

        //schedules-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-intro-multiple.title"));
        assertThat(testPage.findElementTextById("schedules-intro-multiple-step")).isEqualTo("Step 5 of 7");

        testPage.clickContinue();

        //schedules-start
        List<Map<String,Object>> providers = (List<Map<String, Object>>) getSessionSubmission().getInputData().get("providers");
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start.title"));
        assertThat(testPage.getHeader()).containsIgnoringCase("Child");
        testPage.clickElementById("none__checkbox-childcareProvidersForCurrentChild-label");
        testPage.clickElementById(String.format("childcareProvidersForCurrentChild-%s-label", providers.get(0).get("uuid")));
        testPage.clickContinue();

        //schedules-start-care
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start-care.title"));
        assertThat(testPage.getHeader()).containsIgnoringCase("ACME Daycare");
        assertThat(testPage.getHeader()).containsIgnoringCase("Child");
        testPage.clickYes();

        //schedules-start-date
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start-date.title"));
        testPage.enter("ccapStartMonth", "10");
        testPage.enter("ccapStartDay", "15");
        testPage.enter("ccapStartYear", "2025");

        testPage.clickContinue();

        //schedules-days
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-days.title"));
        assertThat(testPage.getHeader()).containsIgnoringCase("ACME Daycare");
        assertThat(testPage.getHeader()).containsIgnoringCase("Child");
        testPage.clickElementById("childcareWeeklySchedule-Thursday");
        testPage.clickElementById("childcareWeeklySchedule-Friday");
        testPage.clickContinue();

        //schedules-hours
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-hours.title"));
        assertThat(testPage.getHeader()).containsIgnoringCase("ACME Daycare");
        assertThat(testPage.getHeader()).containsIgnoringCase("Child");
        testPage.selectFromDropdown("childcareStartTimeThursdayHour", "10");
        testPage.enter("childcareStartTimeThursdayMinute", "00");
        testPage.selectFromDropdown("childcareStartTimeThursdayAmPm", "AM");

        testPage.selectFromDropdown("childcareEndTimeThursdayHour", "1");
        testPage.enter("childcareEndTimeThursdayMinute", "00");
        testPage.selectFromDropdown("childcareEndTimeThursdayAmPm", "PM");

        testPage.selectFromDropdown("childcareStartTimeFridayHour", "9");
        testPage.enter("childcareStartTimeFridayMinute", "00");
        testPage.selectFromDropdown("childcareStartTimeFridayAmPm", "AM");

        testPage.selectFromDropdown("childcareEndTimeFridayHour", "12");
        testPage.enter("childcareEndTimeFridayMinute", "00");
        testPage.selectFromDropdown("childcareEndTimeFridayAmPm", "PM");

        testPage.clickContinue();

        // schedules-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-review.title"));
        testPage.clickContinue();

        //unearned-income-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-intro.title"));
        assertThat(testPage.findElementTextById("unearned-income-intro-step")).isEqualTo("Step 6 of 7");
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
        assertThat(testPage.findElementTextById("submit-intro-step")).isEqualTo("Step 7 of 7");
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
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("after-submit-contact-provider.multiple-providers.title"));
        testPage.clickContinue();

        // contact-providers iteration #1
        assertThat(testPage.getHeader()).isEqualTo(getRequiredEnMessageWithParams("contact-providers-start.header",  new Object[]{"ACME Daycare"}));
        testPage.clickElementById("contactProviderMethod-OTHER-label");
        testPage.clickContinue();

        assertThat(testPage.getHeader()).contains(getEnMessageWithParams("contact-providers-share-code.header",  new Object[]{"ACME Daycare", ""}).trim());
        testPage.clickContinue();

        // contact-providers-review
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("contact-providers-review.header"));
        testPage.clickContinue();

        // doc-upload-recommended-docs
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("doc-upload-recommended-docs.header"));

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
        
        //providers-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-type.title"));
        testPage.selectRadio("providerType", "Care Program");
        testPage.clickContinue();
        
        //providers-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-name.title"));
        testPage.enter("childCareProgramName", "ACME Daycare");
        testPage.clickContinue();

        //providers-location
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-location.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-location.header"));
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-street"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-city"))).isTrue();

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
        // Adding aria-label assertion:
        assertThat(testPage.findElementsByClass("subflow-delete").get(0).getAccessibleName())
                .isEqualTo("Remove ACME Daycare");

        testPage.clickButton(getEnMessage("providers-add.button.add-a-provider"));
        //Second Iteration
        // providers-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-type.title"));
        testPage.selectRadio("providerType", "Care Program");
        testPage.clickContinue();
        
        // providers-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-name.title"));
        testPage.enter("childCareProgramName", "Nope Test");
        testPage.clickContinue();

        //provider-location
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-location.title"));
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
        //providers-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-type.title"));
        testPage.selectRadio("providerType", "Care Program");
        testPage.clickContinue();
        
        //providers-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-name.title"));
        testPage.enter("childCareProgramName", "Third Provider");
        testPage.clickContinue();
        
        
        //provider-location
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-location.title"));
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
        
        //provider-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-type.title"));
        testPage.selectRadio("providerType", "Care Program");
        testPage.clickContinue();
        
        //provider-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-name.title"));
        testPage.enter("childCareProgramName", "Favorite Daycare");
        testPage.clickContinue();

        //provider-location
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-location.title"));
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
        testPage.clickContinue();

        //schedules-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-intro-multiple.title"));
        assertThat(testPage.findElementTextById("schedules-intro-multiple-step")).isEqualTo("Step 5 of 7");

        testPage.clickContinue();

        //schedules-start
        List<Map<String,Object>> providers = (List<Map<String, Object>>) getSessionSubmission().getInputData().get("providers");
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start.title"));
        assertThat(testPage.getHeader()).containsIgnoringCase("Child");
        testPage.clickElementById(String.format("childcareProvidersForCurrentChild-%s-label", providers.get(0).get("uuid")));
        testPage.clickContinue();

        //ChildSchedule Iteration 1
        //ChildSchedule: Child || ProviderSchedule: Nope Test
        //schedules-start-care
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start-care.title"));
        assertThat(testPage.getHeader()).containsIgnoringCase("Nope Test");
        assertThat(testPage.getHeader()).containsIgnoringCase("Child");
        testPage.clickYes();

        //schedules-start-date
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start-date.title"));
        testPage.enter("ccapStartMonth", "10");
        testPage.enter("ccapStartDay", "15");
        testPage.enter("ccapStartYear", "2025");

        testPage.clickContinue();

        //schedules-days
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-days.title"));
        assertThat(testPage.getHeader()).containsIgnoringCase("Nope Test");
        assertThat(testPage.getHeader()).containsIgnoringCase("First");
        testPage.clickElementById("childcareWeeklySchedule-Thursday");
        testPage.clickElementById("childcareWeeklySchedule-Friday");
        testPage.clickContinue();

        //schedules-hours
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-hours.title"));
        assertThat(testPage.getHeader()).containsIgnoringCase("Nope Test");
        assertThat(testPage.getHeader()).containsIgnoringCase("First");
        testPage.selectFromDropdown("childcareStartTimeThursdayHour", "10");
        testPage.enter("childcareStartTimeThursdayMinute", "00");
        testPage.selectFromDropdown("childcareStartTimeThursdayAmPm", "AM");

        testPage.selectFromDropdown("childcareEndTimeThursdayHour", "1");
        testPage.enter("childcareEndTimeThursdayMinute", "00");
        testPage.selectFromDropdown("childcareEndTimeThursdayAmPm", "PM");

        testPage.selectFromDropdown("childcareStartTimeFridayHour", "9");
        testPage.enter("childcareStartTimeFridayMinute", "00");
        testPage.selectFromDropdown("childcareStartTimeFridayAmPm", "AM");

        testPage.selectFromDropdown("childcareEndTimeFridayHour", "12");
        testPage.enter("childcareEndTimeFridayMinute", "00");
        testPage.selectFromDropdown("childcareEndTimeFridayAmPm", "PM");

        testPage.clickContinue();

        //ChildSchedule Iteration 2
        //schedules-start
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start.title"));
        assertThat(testPage.getHeader()).containsIgnoringCase("Second");
        testPage.clickElementById(String.format("childcareProvidersForCurrentChild-%s-label", providers.get(0).get("uuid")));
        testPage.clickElementById(String.format("childcareProvidersForCurrentChild-%s-label", providers.get(1).get("uuid")));
        testPage.clickElementById(String.format("childcareProvidersForCurrentChild-%s-label", providers.get(2).get("uuid")));
        testPage.clickContinue();

        //ChildSchedule: Second || ProviderSchedule: Nope Test
        //schedules-start-care
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start-care.title"));
        assertThat(testPage.getHeader()).containsIgnoringCase("Nope Test");
        assertThat(testPage.getHeader()).containsIgnoringCase("Second");
        testPage.clickYes();

        //schedules-start-date
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start-date.title"));
        testPage.enter("ccapStartMonth", "6");
        testPage.enter("ccapStartDay", "12");
        testPage.enter("ccapStartYear", "2025");

        testPage.clickContinue();

        //schedules-days
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-same.title"));
        testPage.clickElementById("sameSchedule-true");
        testPage.clickContinue();

        //ChildSchedule: Second || ProviderSchedule: Third Provider
        //schedules-start-care
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start-care.title"));
        assertThat(testPage.getHeader()).containsIgnoringCase("Third Provider");
        assertThat(testPage.getHeader()).containsIgnoringCase("Second");
        testPage.clickYes();

        //schedules-start-date
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start-date.title"));
        testPage.enter("ccapStartMonth", "6");
        testPage.enter("ccapStartDay", "12");
        testPage.enter("ccapStartYear", "2025");

        testPage.clickContinue();

        //schedules-days
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-days.title"));
        assertThat(testPage.getHeader()).containsIgnoringCase("Third Provider");
        assertThat(testPage.getHeader()).containsIgnoringCase("Second");
        testPage.clickElementById("childcareWeeklySchedule-Wednesday");
        testPage.clickElementById("childcareWeeklySchedule-Saturday");
        testPage.clickContinue();

        //schedules-hours
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-hours.title"));
        assertThat(testPage.getHeader()).containsIgnoringCase("Third Provider");
        assertThat(testPage.getHeader()).containsIgnoringCase("Second");
        testPage.selectFromDropdown("childcareStartTimeWednesdayHour", "4");
        testPage.enter("childcareStartTimeWednesdayMinute", "00");
        testPage.selectFromDropdown("childcareStartTimeWednesdayAmPm", "AM");

        testPage.selectFromDropdown("childcareEndTimeWednesdayHour", "12");
        testPage.enter("childcareEndTimeWednesdayMinute", "00");
        testPage.selectFromDropdown("childcareEndTimeWednesdayAmPm", "PM");

        testPage.selectFromDropdown("childcareStartTimeSaturdayHour", "2");
        testPage.enter("childcareStartTimeSaturdayMinute", "00");
        testPage.selectFromDropdown("childcareStartTimeSaturdayAmPm", "PM");

        testPage.selectFromDropdown("childcareEndTimeSaturdayHour", "10");
        testPage.enter("childcareEndTimeSaturdayMinute", "00");
        testPage.selectFromDropdown("childcareEndTimeSaturdayAmPm", "PM");

        testPage.clickContinue();

        //ChildSchedule: Second || ProviderSchedule: Favorite Daycare
        //schedules-start-care
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start-care.title"));
        assertThat(testPage.getHeader()).containsIgnoringCase("Favorite Daycare");
        assertThat(testPage.getHeader()).containsIgnoringCase("Second");
        testPage.clickYes();

        //schedules-start-date
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start-date.title"));
        testPage.enter("ccapStartMonth", "8");
        testPage.enter("ccapStartDay", "12");
        testPage.enter("ccapStartYear", "2025");

        testPage.clickContinue();

        //schedules-days
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-days.title"));
        assertThat(testPage.getHeader()).containsIgnoringCase("Favorite Daycare");
        assertThat(testPage.getHeader()).containsIgnoringCase("Second");
        testPage.clickElementById("childcareWeeklySchedule-Wednesday");
        testPage.clickElementById("childcareWeeklySchedule-Saturday");
        testPage.clickContinue();

        //schedules-hours
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-hours.title"));
        assertThat(testPage.getHeader()).containsIgnoringCase("Favorite Daycare");
        assertThat(testPage.getHeader()).containsIgnoringCase("Second");
        testPage.selectFromDropdown("childcareStartTimeWednesdayHour", "4");
        testPage.enter("childcareStartTimeWednesdayMinute", "00");
        testPage.selectFromDropdown("childcareStartTimeWednesdayAmPm", "AM");

        testPage.selectFromDropdown("childcareEndTimeWednesdayHour", "12");
        testPage.enter("childcareEndTimeWednesdayMinute", "00");
        testPage.selectFromDropdown("childcareEndTimeWednesdayAmPm", "PM");

        testPage.selectFromDropdown("childcareStartTimeSaturdayHour", "2");
        testPage.enter("childcareStartTimeSaturdayMinute", "00");
        testPage.selectFromDropdown("childcareStartTimeSaturdayAmPm", "PM");

        testPage.selectFromDropdown("childcareEndTimeSaturdayHour", "10");
        testPage.enter("childcareEndTimeSaturdayMinute", "00");
        testPage.selectFromDropdown("childcareEndTimeSaturdayAmPm", "PM");

        testPage.clickContinue();
        // schedules-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-review.title"));
        testPage.clickContinue();
    }

    @Test
    void navigationWhenOnlyOneOrZeroProvidersAreAvailableForChildrenInNeedOfCare(){
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
        //providers-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-add.header"));
        testPage.clickButton(getEnMessage("providers-add.button.add-a-provider"));
        
        //providers-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-type.title"));
        testPage.selectRadio("providerType", "Care Program");
        testPage.clickContinue();
        
        //providers-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-name.title"));
        testPage.enter("childCareProgramName", "First Daycare");
        testPage.clickContinue();

        //providers-location
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-location.title"));

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

    }
    @Test
    void AllowsOnlyTwoProvidersToBeAddedWhenClientAnswersNoToHavingChosenAProviderForEveryChildJourneyTest() {
        AddOneProviderInMultiProviderFlow();

        //providers-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-add.header"));
        assertThat(testPage.findElementTextById("continue-link")).isEqualTo(getEnMessage("providers-add.button.that-is-all"));
        assertThat(testPage.findElementById("continue-link").getCssValue("pointer-events")).isEqualTo("auto");
        assertThat(testPage.findElementById("add-providers").getCssValue("pointer-events")).isEqualTo("auto");

        testPage.clickButton(getEnMessage("providers-add.button.add-a-provider"));

        //Second Iteration
        //provider-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-type.title"));
        testPage.selectRadio("providerType", "Care Program");
        testPage.clickContinue();

        //provider-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-name.title"));
        testPage.enter("childCareProgramName", "No Provider");
        testPage.clickContinue();

        //provider-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-location.title"));
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
        
        //providers-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-type.title"));
        assertThat(testPage.findElementById("providerType-Care Program").isSelected()).isTrue();
        testPage.clickContinue();
        
        //providers-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-name.title"));
        assertThat(testPage.getInputValue("childCareProgramName")).isEqualTo("First Daycare");
        testPage.clickContinue();
        
        //providers-location
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-location.title"));
        assertThat(testPage.getInputValue("familyIntendedProviderAddress")).isEqualTo("222 Test St");

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
    void TwoChildrenWithOneProviderAndOneChildWithNoProviderRendersCorrectly() {
        testPage.navigateToFlowScreen("gcc/providers-intro");
        saveSubmission(getSessionSubmissionTestBuilder()
            .withParentBasicInfo()
            .withChild("First", "Child", "true")
            .withChild("Second", "Child", "true")
            .withProvider("Fake_Provider", "1")
            .with("choseProviderForEveryChildInNeedOfCare", "false").build());
        testPage.navigateToFlowScreen("gcc/schedules-intro");

        //schedules-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-intro-multiple.title"));
        assertThat(testPage.findElementTextById("schedules-intro-single-step")).isEqualTo("Step 5 of 7");

        testPage.clickContinue();

        //schedules-start
        List<Map<String,Object>> providers = (List<Map<String, Object>>) getSessionSubmission().getInputData().get("providers");
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start.title"));
        assertThat(testPage.getHeader()).containsIgnoringCase("First");
        assertThat(testPage.getElementText("none__checkbox-childcareProvidersForCurrentChild-label")).isEqualTo(getEnMessage("schedules-start.no-provider"));
        assertThat(testPage.getElementText("childcareProvidersForCurrentChild-fake_provider-1-label")).isEqualTo("Fake_Provider");
        testPage.clickElementById(String.format("childcareProvidersForCurrentChild-%s-label", providers.get(0).get("uuid")));

        testPage.clickContinue();
    }
    @Test
    void MultiProviderNavigationWhenNoProviderJourneyTest() {
        testPage.navigateToFlowScreen("gcc/parent-info-disability");

        saveSubmission(getSessionSubmissionTestBuilder().withParentBasicInfo().with("familyIntendedProviderName", "ACME Daycare")
                .with("applicationCounty", "LEE").withChild("First", "Child", "true")
                .withShortCode("familyShortCode").build());

        testPage.navigateToFlowScreen("gcc/providers-intro");

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-intro.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-chosen.title"));
        testPage.clickNo();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-no-provider-intro.title"));
        testPage.clickButton(getEnMessage("providers-no-provider-intro.continue"));

        //schedules-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-intro-multiple.title"));
    }
    void AddOneProviderInMultiProviderFlow(){
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

        //providers-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-type.title"));
        testPage.selectRadio("providerType", "Care Program");
        testPage.clickContinue();

        //providers-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-name.title"));
        testPage.enter("childCareProgramName", "First Daycare");
        testPage.clickContinue();

        //providers-location
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-location.title"));
        testPage.enter("familyIntendedProviderAddress", "222 Test St");
        testPage.enter("familyIntendedProviderCity", "Chicago");
        testPage.selectFromDropdown("familyIntendedProviderState", "IL - Illinois");
        testPage.clickContinue();

        //providers-contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-contact-info.title"));

        testPage.enter("familyIntendedProviderEmail", "test@first.com");
        testPage.enter("familyIntendedProviderPhoneNumber", "(533)555-5555");
        testPage.clickContinue();
    }

}
