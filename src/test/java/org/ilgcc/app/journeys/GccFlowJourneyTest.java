package org.ilgcc.app.journeys;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import formflow.library.data.SubmissionRepository;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.awaitility.Awaitility.await;

@Slf4j
public class GccFlowJourneyTest extends AbstractBasePageTest {

    @Autowired
    SubmissionRepository repository;
    @Test
    void fullGccFlow() throws IOException {
        // Home page
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("index.title"));
        testPage.clickButton(getEnMessage("index.apply-now"));
        // onboarding-getting-started
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-getting-started.title"));
        testPage.clickContinue();
        //onboarding-choose-provider
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-choose-provider.title"));
        testPage.clickElementById("dayCareChoice-OPEN_SESAME-label");
        testPage.clickContinue();
        //onboarding-confirm-provider
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-confirm-provider.title"));
        testPage.clickLink(getEnMessage("onboarding-confirm-provider.yes"));
        //onboarding-language-preference
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-language-pref.title"));
        testPage.selectFromDropdown("languageRead", "English");
        testPage.selectFromDropdown("languageSpeak", "Español");
        testPage.clickContinue();

        // parent-info-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-intro.title"));
        testPage.clickContinue();
        // parent-info-basic-1
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-basic-1.title"));
        testPage.enter("parentFirstName", "parent first");
        testPage.enter("parentLastName", "parent last");
        testPage.enter("parentPreferredName", "Preferred Parent First");
        testPage.enter("parentBirthMonth", "12");
        testPage.enter("parentBirthDay", "25");
        testPage.enter("parentBirthYear", "1985");
        testPage.enter("parentSsn", "123-45-6789");
        testPage.clickElementById("parentGender-MALE-label");
        testPage.clickContinue();
        // parent-info-service
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-service.title"));
        testPage.clickContinue();
        // parent-home-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-home-address.title"));
        testPage.enter("parentHomeStreetAddress1", "972 Mission St");
        testPage.enter("parentHomeStreetAddress2", "5th floor");
        testPage.enter("parentHomeCity", "San Francisco");
        testPage.selectFromDropdown("parentHomeState", "CA - California");
        testPage.enter("parentHomeZipCode", "94103");
        testPage.clickContinue();
        // parent-mailing-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-mailing-address.title"));
        testPage.goBack();
        //parent-home-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-home-address.title"));
        testPage.clickElementById("parentHomeExperiencingHomelessness-yes");
        testPage.clickContinue();
        //parent-home-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-no-permanent-address.title"));
        testPage.clickButton(getEnMessage("parent-no-permanent-address.has-place-to-get-mail"));
        // parent-mailing-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-mailing-address.title"));
        testPage.enter("parentMailingStreetAddress1", "972 Mission St");
        testPage.enter("parentMailingStreetAddress2", "5th floor");
        testPage.enter("parentMailingCity", "San Francisco");
        testPage.selectFromDropdown("parentMailingState", "CA - California");
        testPage.enter("parentMailingZipCode", "94103");
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
        testPage.clickYes();
        // parent-qualifying-partner
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-qualifying-partner.title"));
        testPage.clickYes();
        //parent-partner-info-basic
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-partner-info-basic.title"));
        testPage.enter("parentPartnerFirstName", "partner");
        testPage.enter("parentPartnerLastName", "parent");
        testPage.enter("parentPartnerBirthMonth", "12");
        testPage.enter("parentPartnerBirthDay", "25");
        testPage.enter("parentPartnerBirthYear", "2018");
        testPage.enter("parentPartnerSSN", "123-456-7890");
        testPage.clickContinue();

        // parent-partner-contact
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-partner-contact.title"));
        testPage.enter("parentPartnerPhoneNumber", "3333333333");
        testPage.clickContinue();
        // parent-partner-info-service
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-partner-info-service.title"));
        testPage.selectRadio("parentPartnerIsServing", "No");
        testPage.selectRadio("parentPartnerInMilitaryReserveOrNationalGuard", "Yes");
        testPage.clickContinue();
        // parent-partner-info-disability
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-partner-info-disability.title"));
        testPage.clickYes();
        // parent-other-family
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-other-family.title"));
        testPage.clickYes();
        // parent-add-adults
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-add-adults.title"));
        testPage.clickButton(getEnMessage("parent-add-adults.add-member"));
        // parent-add-adult-details
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-add-adults-detail.title"));
        testPage.enter("adultDependentFirstName", "ada");
        testPage.enter("adultDependentLastName", "dolt");
        testPage.clickContinue();
        // delete-person
        testPage.clickLink("delete");
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("delete-confirmation.title"));
        testPage.clickButton(getEnMessage("delete-confirmation.yes"));

        // parent-add-adults
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-add-adults.title"));
        testPage.clickButton(getEnMessage("parent-add-adults.add-member"));
        testPage.enter("adultDependentFirstName", "adaa");
        testPage.enter("adultDependentLastName", "doltt");
        testPage.clickContinue();
        testPage.clickButton(getEnMessage("parent-add-adults.im-done"));
        // parent-intro-family-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-intro-family-info.title"));
        testPage.clickButton(getEnMessage("parent-intro-family-info.continue"));
        //children-info-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-info-intro.title"));
        testPage.clickContinue();
        // children-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-add.title"));
        testPage.clickButton(getEnMessage("children-add.add-button"));
        //children-info-basic
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-info-basic.title"));
        testPage.enter("childFirstName", "mugully");
        testPage.enter("childLastName", "glopklin");
        testPage.enter("childDateOfBirthMonth", "1");
        testPage.enter("childDateOfBirthDay", "1");
        testPage.enter("childDateOfBirthYear", "2022");
        testPage.selectRadio("needFinancialAssistanceForChild", "No");
        testPage.clickContinue();
        testPage.clickButton(getEnMessage("children-add.add-button"));
        testPage.enter("childFirstName", "child");
        testPage.enter("childLastName", "mcchild");
        testPage.enter("childDateOfBirthMonth", "12");
        testPage.enter("childDateOfBirthDay", "25");
        testPage.enter("childDateOfBirthYear", "2018");
        testPage.selectRadio("needFinancialAssistanceForChild", "Yes");
        testPage.clickContinue();
        //children-ccap-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-ccap-info.title"));
        testPage.selectFromDropdown("childRelationship", getEnMessage("children-ccap-info.relationship-option.child"));
        testPage.clickElementById("childGender-MALE");
        testPage.clickElementById("childGender-TRANSGENDER");
        testPage.selectRadio("childHasDisability", "No");
        testPage.selectRadio("childIsUsCitizen", "Yes");
        testPage.clickElementById("none__checkbox-childRaceEthnicity");
        testPage.clickContinue();
        //children-ccap-in-care
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-ccap-in-care.title"));
        testPage.clickNo();
        //children-ccap-start-date (Test No logic)
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-ccap-start-date.when-did.title"));
        testPage.goBack();
        //children-ccap-start-date (Test Yes Logic)
        testPage.clickYes();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-ccap-start-date.when-did.title"));
        testPage.enter("ccapStartMonth", "11");
        testPage.enter("ccapStartDay", "1");
        testPage.enter("ccapStartYear", "2010");
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
        //children-add (with children listed)
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-add.title"));
        List<String> li = testPage.getTextBySelector(".child-name");
        assertThat(li).containsExactly("mugully glopklin", "child mcchild");
        testPage.clickButton(getEnMessage("children-add.thats-all"));

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-intro.title"));
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-type.title"));
        testPage.clickElementById("activitiesParentChildcareReason-other");
        testPage.clickElementById("activitiesParentChildcareReason-SCHOOL");
        testPage.enter("activitiesParentChildcareReason_other", "test");
        testPage.clickElementById("activitiesParentPartnerChildcareReason-TANF_TRAINING");
        testPage.clickElementById("activitiesParentPartnerChildcareReason-WORKING");
        testPage.clickElementById("activitiesParentPartnerChildcareReason-SCHOOL");
        testPage.clickContinue();
        //activities-add-ed-program (client should be directed to this page if working is not checked)
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.goBack();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-type.title"));
        testPage.clickElementById("activitiesParentChildcareReason-WORKING");
        testPage.clickContinue();
        //activities-add-jobs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-add-jobs.title"));
        testPage.clickButton(getEnMessage("activities-add-jobs.add-a-job"));
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

        //activities-add-ed-program
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.clickContinue();

        //activities-ed-program-type
        assertThat(testPage.getElementText("educationType-highSchoolOrGed-label")).isEqualTo(getEnMessage("activities-ed-program-type.highSchool"));
        testPage.clickElementById("educationType-highSchoolOrGed-label");
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.clickContinue();

        //activities-ed-program-name
        testPage.enter("applicantSchoolName", "World Training Program");
        testPage.clickContinue();

        //activities-ed-program-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.enter("applicantSchoolPhoneNumber", "(217) 123-1233");
        testPage.enter("applicantSchoolStreetAddress", "123 Main St");
        testPage.enter("applicantSchoolCity", "Springfield");
        testPage.enter("applicantSchoolState", "IL - Illinois");
        testPage.enter("applicantSchoolZipCode", "62629");
        testPage.clickContinue();

        //activities-ed-program-method
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-method.header"));
        testPage.clickElementById("programTaught-Online-label");
        testPage.clickElementById("programSchedule-No-label");
        testPage.clickContinue();

        //activities-next-class-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.clickContinue();

        //activities-class-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-class-weekly-schedule.title"));
        testPage.clickElementById("weeklySchedule-Monday");
        testPage.clickElementById("weeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-class-hourly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-class-hourly-schedule.title"));
        testPage.clickElementById("activitiesClassHoursSameEveryDay-Yes");

        testPage.selectFromDropdown("activitiesClassStartTimeAllDaysHour", "9");
        testPage.enter("activitiesClassStartTimeAllDaysMinute", "00");
        testPage.selectFromDropdown("activitiesClassStartTimeAllDaysAmPm", "AM");

        testPage.selectFromDropdown("activitiesClassEndTimeAllDaysHour", "1");
        testPage.enter("activitiesClassEndTimeAllDaysMinute", "00");
        testPage.selectFromDropdown("activitiesClassEndTimeAllDaysAmPm", "PM");

        testPage.clickContinue();

        //activities-ed-program-dates
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-dates.title"));
        testPage.enter("activitiesProgramStartYear", "2024");
        testPage.enter("activitiesProgramStartMonth", "12");
        testPage.enter("activitiesProgramStartDay", "10");

        testPage.enter("activitiesProgramEndYear", "2025");
        testPage.enter("activitiesProgramEndMonth", "02");
        testPage.enter("activitiesProgramEndDay", "");
        testPage.clickContinue();
        //activities-partner-add-job
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-add-jobs.title"));
        testPage.clickButton("Add a job");
        //activities-partner-employer-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-employer-name.title"));
        testPage.enter("partnerCompanyName", "testPartnerCompany");
        testPage.clickContinue();
        //activities--partner-employer-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-employer-address.title"));
        testPage.enter("partnerEmployerPhoneNumber", "4444");
        testPage.enter("partnerEmployerCity", "Oakland");
        testPage.enter("partnerEmployerStreetAddress", "123 Partner Employer Address");
        testPage.enter("partnerEmployerZipCode", "6042");
        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-phone-number"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-zipcode"))).isTrue();
        testPage.enter("partnerEmployerPhoneNumber", "4333333333");
        testPage.enter("partnerEmployerZipCode", "92453");
        testPage.clickContinue();
        //activities-partner-self-employment
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-self-employment.title"));
        testPage.clickNo();
        //activities-partner-work-schedule-vary
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-work-schedule-vary.title"));
        testPage.clickNo();

        //activities-partner-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-job-weekly-schedule.title"));
        testPage.goBack();

        //activities-partner-work-schedule-vary
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-work-schedule-vary.title"));
        testPage.clickYes();

        //activities-partner-next-work-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-next-work-schedule.title"));
        testPage.clickContinue();
        //activities-partner-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-job-weekly-schedule.title"));
        testPage.clickElementById("activitiesJobWeeklySchedule-Monday");
        testPage.clickElementById("activitiesJobWeeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-partner-job-hourly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-job-hourly-schedule.title"));
        testPage.selectFromDropdown("activitiesJobStartTimeMondayHour", "12");
        testPage.enter("activitiesJobStartTimeMondayMinute", "00");
        testPage.selectFromDropdown("activitiesJobStartTimeMondayAmPm", "PM");

        testPage.selectFromDropdown("activitiesJobEndTimeMondayHour", "1");
        testPage.enter("activitiesJobEndTimeMondayMinute", "00");
        testPage.selectFromDropdown("activitiesJobEndTimeMondayAmPm", "PM");

        testPage.selectFromDropdown("activitiesJobStartTimeSundayHour", "2");
        testPage.enter("activitiesJobStartTimeSundayMinute", "00");
        testPage.selectFromDropdown("activitiesJobStartTimeSundayAmPm", "AM");

        testPage.selectFromDropdown("activitiesJobEndTimeSundayHour", "3");
        testPage.enter("activitiesJobEndTimeSundayMinute", "00");
        testPage.selectFromDropdown("activitiesJobEndTimeSundayAmPm", "PM");

        testPage.clickContinue();

        //activities-partner-commute-time
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-commute-time.title"));
        testPage.selectFromDropdown("activitiesJobCommuteTime", getEnMessage("general.hours.1.5.hours"));
        testPage.clickContinue();

        //activities-partner-add-job
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-add-jobs.title"));
        testPage.clickButton(getEnMessage("activities-partner-add-jobs.this-is-all-their-jobs"));
        // activities-partner-ed
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.clickElementById("partnerEducationType-twoYearCollege-label");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.enter("partnerProgramName", "World University");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-method.header"));
        testPage.clickElementById("partnerProgramTaught-In-Person-label");
        testPage.clickElementById("partnerProgramSchedule-Yes-label");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-class-weekly-schedule.title"));
        testPage.clickElementById("partnerClassWeeklySchedule-Monday");
        testPage.clickElementById("partnerClassWeeklySchedule-Wednesday");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-class-hourly-schedule.title"));

        testPage.selectFromDropdown("partnerClassStartTimeMondayHour", "11");
        testPage.enter("partnerClassStartTimeMondayMinute", "00");
        testPage.selectFromDropdown("partnerClassStartTimeMondayAmPm", "AM");

        testPage.selectFromDropdown("partnerClassEndTimeMondayHour", "4");
        testPage.enter("partnerClassEndTimeMondayMinute", "00");
        testPage.selectFromDropdown("partnerClassEndTimeMondayAmPm", "PM");

        testPage.selectFromDropdown("partnerClassStartTimeWednesdayHour", "2");
        testPage.enter("partnerClassStartTimeWednesdayMinute", "00");
        testPage.selectFromDropdown("partnerClassStartTimeWednesdayAmPm", "PM");

        testPage.selectFromDropdown("partnerClassEndTimeWednesdayHour", "3");
        testPage.enter("partnerClassEndTimeWednesdayMinute", "00");
        testPage.selectFromDropdown("partnerClassEndTimeWednesdayAmPm", "PM");

        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-dates.title"));
        testPage.clickContinue();

        //unearned-income-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-intro.title"));
        testPage.clickContinue();

        //unearned-income-source
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-source.title"));
        testPage.clickElementById("unearnedIncomeSource-ROYALTIES-label");
        testPage.clickElementById("unearnedIncomeSource-RENTAL-label");
        testPage.clickElementById("unearnedIncomeSource-UNEMPLOYMENT-label");
        testPage.clickContinue();

        //unearned-income-amount
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-amount.title"));
        testPage.enter("unearnedIncomeRoyalties", "35");
        testPage.enter("unearnedIncomeUnemployment", "175");
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

        // submit-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-intro.title"));
        testPage.clickContinue();

        // submit-ccap-terms
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-ccap-terms.title"));
        testPage.clickElementById("agreesToLegalTerms-true");
        testPage.clickContinue();

        // submit-sign-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-sign-name.title"));
        testPage.enter("signedName", "parent first parent last");
        testPage.enter("partnerSignedName", "partner parent");
        testPage.clickButton(getEnMessage("submit-sign-name.submit-application"));

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // doc-upload-recommended docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // doc-upload-add-files
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
        assertThat(testPage.findElementById("form-submit-button").getAttribute("class").contains("display-none")).isTrue();
        uploadJpgFile();
        // The submit button is hidden unless a file has been uploaded. The await gives the system time to remove the "display-none" class.
        await().atMost(5, TimeUnit.SECONDS).until(
                () -> !(testPage.findElementById("form-submit-button").getAttribute("class").contains("display-none"))
        );

        testPage.clickButton(getEnMessage("doc-upload-add-files.confirmation"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-submit-confirmation.title"));

        testPage.goBack();
        testPage.goBack();
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.skip"));

        // submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-next-steps.title"));
        testPage.clickContinue();

        // submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessageWithParams("submit-confirmation.title", new Object[]{"Open Sesame"}));
        testPage.clickElementById("surveyDifficulty-very-easy");
        testPage.clickButton(getEnMessage("submit-confirmation.button.feedback"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessageWithParams("submit-confirmation.title", new Object[]{"Open Sesame"}));
        assertThat(testPage.getCssSelectorText(".notice--success")).isEqualTo(getEnMessage("submit-confirmation.survey.complete"));

        // Download PDF and verify fields
        verifyPDF();
    }

    /**
     * This compares the pdf fields in the generated pdf and our expected test pdf, "test_filled_ccap.pdf".
     * If there are updates to the template pdf (used to generate the client pdf), the test pdf should be updated to have the expected fields and values.
     */
    private void verifyPDF() throws IOException {
        File pdfFile = getDownloadedPDF();

//         regenerateExpectedPDF(pdfFile); // uncomment and run test to regenerate the test pdf

        try (FileInputStream actualIn = new FileInputStream(pdfFile);
            PdfReader actualReader = new PdfReader(actualIn);
            FileInputStream expectedIn = new FileInputStream("src/test/resources/output/test_filled_ccap.pdf");
            PdfReader expectedReader = new PdfReader(expectedIn)) {
            AcroFields actualAcroFields = actualReader.getAcroFields();
            AcroFields expectedAcroFields = expectedReader.getAcroFields();

            // Get all failures at once and log them
            List<String> missMatches = getMissMatches(expectedAcroFields, actualAcroFields);

            // Do actual assertions
            for (String expectedField : missMatches) {
                var actual = actualAcroFields.getField(expectedField);
                var expected = expectedAcroFields.getField(expectedField);
                assertThat(actual)
                        .withFailMessage("Expected %s to be %s but was %s".formatted(expectedField, expected, actual))
                        .isEqualTo(expected);
            }
            assertThat(actualAcroFields.getAllFields().size()).isEqualTo(expectedAcroFields.getAllFields().size());
        } catch (IOException e) {
            fail("Failed to generate PDF: %s", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * If there are changes to the expected PDF, it can be regenerated with this method.
     */
    private static void regenerateExpectedPDF(File pdfFile) {
        try (FileInputStream regeneratedPDF = new FileInputStream(pdfFile);
             FileOutputStream testPDF = new FileOutputStream("src/test/resources/output/test_filled_ccap.pdf")) {
            testPDF.write(regeneratedPDF.readAllBytes());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @NotNull
    private static List<String> getMissMatches(AcroFields expectedAcroFields, AcroFields actualAcroFields) {
        List<String> missMatches = new ArrayList<>();
//        These fields are dynamic and untestable with the current PDF approach
        List<String> UNTESTABLE_FIELDS = List.of(
                "PARTNER_SIGNATURE_DATE",
                "APPLICANT_SIGNATURE_DATE",
                "APPLICANT_NAME_FULL",
                "RECEIVED_TIMESTAMP");
        for (String expectedField : expectedAcroFields.getAllFields().keySet()) {
            if(!UNTESTABLE_FIELDS.contains(expectedField)){
                var actual = actualAcroFields.getField(expectedField);
                var expected = expectedAcroFields.getField(expectedField);
                if (!expected.equals(actual)) {
                    missMatches.add(expectedField);
                    log.info("Expected %s to be %s but was %s".formatted(expectedField, expected, actual));
                }
            }
        }
        return missMatches;
    }

    private File getDownloadedPDF() throws IOException {
        // There should only be one
        String downloadUrl = repository.findAll().stream()
                .findFirst()
                .map(submission -> "%s/download/gcc/%s".formatted(baseUrl, submission.getId()))
                .orElseThrow(() -> new RuntimeException("Couldn't get url for pdf download"));
        driver.get(downloadUrl);
        await().until(pdfDownloadCompletes());
        return getLatestDownloadedFile(path);
    }
}
