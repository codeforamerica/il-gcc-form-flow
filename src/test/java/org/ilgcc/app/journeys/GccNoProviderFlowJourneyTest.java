package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.ilgcc.app.data.importer.FakeResourceOrganizationAndCountyData.ACTIVE_PROJECT_CHILD_COUNTY;
import static org.ilgcc.app.data.importer.FakeResourceOrganizationAndCountyData.PROJECT_CHILD_TEST_DATA;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

public class GccNoProviderFlowJourneyTest extends AbstractBasePageTest {

    String TEST_FILLED_PDF_PATH = "src/test/resources/output/test_filled_ccap_NO_PROVIDER.pdf";

    String FLOW = "gcc";

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
        //onboarding-language-preference
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-language-pref.title"));
        testPage.selectFromDropdown("languageRead", "English");
        testPage.selectFromDropdown("languageSpeak", "Español");
        testPage.clickContinue();
        
        // onboarding-county
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-county.title"));
        testPage.selectFromDropdown("applicationCounty", "DEKALB");
        testPage.clickContinue();

        // onboarding-chosen-provider
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-chosen-provider.title"));
        testPage.clickNo();

        // onboarding-no-provider-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-no-provider-intro.title"));
        testPage.clickButton(getEnMessage("onboarding-no-provider-intro.continue"));

        // parent-info-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-intro.title"));
        assertThat(testPage.findElementTextById("parent-info-intro-step")).isEqualTo("Step 1 of 5");
        testPage.clickContinue();

        // parent-info-basic-1
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-basic-1.title"));
        testPage.enter("parentFirstName", "parent first");
        testPage.enter("parentLastName", "parent last");
        testPage.enter("parentPreferredName", "Preferred Parent First");
        testPage.enter("parentOtherLegalName", "Parent Other Legal Name");
        testPage.enter("parentBirthMonth", "12");
        testPage.enter("parentBirthDay", "25");
        testPage.enter("parentBirthYear", "1985");
        testPage.clickElementById("parentGender-MALE-label");
        testPage.clickContinue();

        // parent-info-service
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-service.title"));
        testPage.clickContinue();

        // parent-info-disability
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-disability.title"));
        testPage.clickYes();

        // parent-home-address
        testPage.enter("parentHomeStreetAddress1", "10921 S Nagle Ave");
        testPage.enter("parentHomeCity", "Worth");
        testPage.selectFromDropdown("parentHomeState", getEnMessage("state.il"));
        testPage.enter("parentHomeZipCode", ACTIVE_PROJECT_CHILD_COUNTY.getZipCode().toString());
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

        // children-info-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-info-intro-single.title"));
        assertThat(testPage.findElementTextById("children-info-intro-single-step")).isEqualTo("Step 2 of 5");
        testPage.clickContinue();

        // children-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-add.title"));
        testPage.clickButton(getEnMessage("children-add.add-button"));

        // children-info-basic
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-info-basic.title"));
        testPage.enter("childFirstName", "mugully");
        testPage.enter("childLastName", "glopklin");
        testPage.enter("childDateOfBirthMonth", "1");
        testPage.enter("childDateOfBirthDay", "1");
        testPage.enter("childDateOfBirthYear", "2022");
        testPage.selectFromDropdown("childRelationship", getEnMessage("general.relationship-option.child"));
        testPage.clickContinue();

        // children-info-assistance
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-info-assistance.title"));
        testPage.clickNo();
        testPage.clickButton(getEnMessage("children-add.add-button"));
        testPage.enter("childFirstName", "child");
        testPage.enter("childLastName", "mcchild");
        testPage.enter("childDateOfBirthMonth", "12");
        testPage.enter("childDateOfBirthDay", "25");
        testPage.enter("childDateOfBirthYear", "2018");
        testPage.selectFromDropdown("childRelationship", getEnMessage("general.relationship-option.foster-child"));
        testPage.clickContinue();

        // children-info-assistance
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-info-assistance.title"));
        testPage.clickYes();

        // children-ccap-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-ccap-info.title"));
        testPage.clickElementById("childGender-MALE");
        testPage.clickElementById("childGender-TRANSGENDER");
        testPage.selectRadio("childHasDisability", "No");
        testPage.selectRadio("childIsUsCitizen", "Yes");
        testPage.clickElementById("none__checkbox-childRaceEthnicity");
        testPage.clickContinue();

        //children-ccap-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-childcare-weekly-schedule.title"));
        testPage.clickElementById("childcareWeeklySchedule-Thursday");
        testPage.clickElementById("childcareWeeklySchedule-Friday");
        testPage.clickContinue();

        //children-childcare-hourly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-childcare-hourly-schedule.title"));
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

        //children-ccap-child-other-ed
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-ccap-child-other-ed.title"));
        testPage.clickYes();

        //children-school-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-school-weekly-schedule.title"));
        testPage.clickContinue();

        //children-add (with children listed)
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-add.title"));
        testPage.clickButton(getEnMessage("children-add.thats-all"));

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
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-employer-start-date.title"));
        testPage.enter("activitiesJobStartYear", "1988");
        testPage.enter("activitiesJobStartDay", "30");
        testPage.enter("activitiesJobStartMonth", "1");
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
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("submit-intro.title"));
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
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("after-submit-contact-provider.no-provider.title"));
        testPage.clickContinue();

        // doc-upload-recommended docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // doc-upload-add-files
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
        assertThat(testPage.findElementById("form-submit-button").getAttribute("class").contains("display-none")).isFalse();
        uploadJpgFile();

        await().atMost(5, TimeUnit.SECONDS).until(
                () -> !(testPage.findElementById("form-submit-button").getAttribute("class").contains("display-none"))
        );

        testPage.clickButton(getEnMessage("doc-upload-add-files.confirmation"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-submit-confirmation.title"));

        // Done with adding documents
        testPage.clickButton(getEnMessage("doc-upload-submit-confirmation.yes"));

        // submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-next-steps.title"));
        assertThat(testPage.getTextBySelector("ul").get(1)).containsIgnoringCase(
                PROJECT_CHILD_TEST_DATA.getName());
        testPage.clickContinue();

        // complete-submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("complete-submit-confirmation.title"));

        // Download PDF and verify fields
        verifyPDF(TEST_FILLED_PDF_PATH, UNTESTABLE_FIELDS, FLOW);
    }
}