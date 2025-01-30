package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;

public class ActivitiesJourneyTest extends AbstractBasePageTest {

    @Test
    void ParentOnlyWithJobAndWorkTest() {
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-intro.title"));
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-type.title"));
        testPage.clickElementById("activitiesParentChildcareReason-WORKING");
        testPage.clickElementById("activitiesParentChildcareReason-SCHOOL");
        testPage.clickContinue();

        //activities-add-jobs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-add-jobs.title"));
        testPage.clickButton(getEnMessage("activities-add-jobs.add-a-job"));
        //activities-employer-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-employer-name.title"));
        testPage.enter("companyName", "testCompany");
        testPage.clickContinue();
        //activities-employer-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-employer-address.title"));
        testPage.enter("employerPhoneNumber", "3333333");
        testPage.enter("employerCity", "Chicago");
        testPage.enter("employerStreetAddress", "123 Test Me");
        testPage.enter("employerZipCode", "6042");
        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-phone-number"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-zipcode"))).isTrue();
        testPage.enter("employerPhoneNumber", "3333333333");
        testPage.enter("employerZipCode", "60423");
        testPage.clickContinue();
        //activities-employer-start-date
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-employer-start-date.title"));
        testPage.enter("activitiesJobStartYear", "2000");
        testPage.enter("activitiesJobStartDay", "08");
        testPage.enter("activitiesJobStartMonth", "10");
        testPage.clickContinue();
        //activities-self-employment
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-self-employment.title"));
        testPage.clickYes();

        //activities-work-schedule-vary
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-work-schedule-vary.title"));
        testPage.clickYes();

        //activities-partner-next-work-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-next-work-schedule.title"));
        testPage.clickContinue();

        //activities-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-job-weekly-schedule.title"));
        testPage.clickElementById("activitiesJobWeeklySchedule-Monday");
        testPage.clickElementById("activitiesJobWeeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-job-weekly-schedule
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
        testPage.selectFromDropdown("activitiesJobCommuteTime", "1 hour");
        testPage.clickContinue();

        //activities-add-jobs (list)
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-add-jobs.title"));
        testPage.clickButton(getEnMessage("activities-add-jobs.this-is-all-my-jobs"));

        //activities-add-ed-program
        assertThat(testPage.getHeader()).isEqualTo(
                getEnMessageWithParams("activities-add-ed-program.header", new Object[]{"parent first"}));
        testPage.clickContinue();

        //parent-info-bachelors
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-bachelors.title"));
        testPage.clickElementById("applicantHasBachelorsDegree-skip");

        //activities-ed-program-type
        assertThat(testPage.getElementText("educationType-highSchoolOrGed-label")).isEqualTo(
                getEnMessage("activities-ed-program-type.highSchool"));
        testPage.clickElementById("educationType-highSchoolOrGed-label");
        assertThat(testPage.getHeader()).isEqualTo(getRequiredEnMessage("activities-ed-program-type.header"));
        testPage.clickContinue();

        //activities-ed-program-name
        testPage.enter("applicantSchoolName", "World");
        assertThat(testPage.getHeader()).isEqualTo(getRequiredEnMessage("activities-ed-program-name.header"));
        testPage.clickContinue();

        //activities-ed-program-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.clickContinue();

        //activities-ed-program-method
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-method.header"));
        testPage.clickElementById("programTaught-Online-label");
        testPage.clickElementById("programSchedule-No-label");
        testPage.clickContinue();

        //activities-next-class-schedule
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("activities-next-class-schedule.header"));
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

        //activities-class-commute-time
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-class-commute-time.title"));
        testPage.selectFromDropdown("activitiesEdCommuteTime", getEnMessage("general.hours.1.5.hours"));
        testPage.clickContinue();

        //activities-ed-program-dates
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-dates.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-intro.title"));
    }

    @Test
    void ParentOnlyWithJobOnlyTest() {
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-intro.title"));
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-type.title"));
        testPage.clickElementById("activitiesParentChildcareReason-WORKING");
        testPage.clickContinue();

        //activities-add-jobs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-add-jobs.title"));
        testPage.clickButton(getEnMessage("activities-add-jobs.add-a-job"));
        //activities-employer-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-employer-name.title"));
        testPage.enter("companyName", "testCompany");
        testPage.clickContinue();
        //activities-employer-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-employer-address.title"));
        testPage.enter("employerPhoneNumber", "3333333");
        testPage.enter("employerCity", "Chicago");
        testPage.enter("employerStreetAddress", "123 Test Me");
        testPage.enter("employerZipCode", "6042");
        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-phone-number"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-zipcode"))).isTrue();
        testPage.enter("employerPhoneNumber", "3333333333");
        testPage.enter("employerZipCode", "60423");
        testPage.clickContinue();
        //activities-employer-start-date
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-employer-start-date.title"));
        testPage.enter("activitiesJobStartYear", "2000");
        testPage.enter("activitiesJobStartDay", "08");
        testPage.enter("activitiesJobStartMonth", "10");
        testPage.clickContinue();
        //activities-self-employment
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-self-employment.title"));
        testPage.clickYes();

        //activities-work-schedule-vary
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-work-schedule-vary.title"));
        testPage.clickYes();

        //activities-partner-next-work-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-next-work-schedule.title"));
        testPage.clickContinue();

        //activities-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-job-weekly-schedule.title"));
        testPage.clickElementById("activitiesJobWeeklySchedule-Monday");
        testPage.clickElementById("activitiesJobWeeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Job hourly schedule");

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
        testPage.selectFromDropdown("activitiesJobCommuteTime", "1 hour");
        testPage.clickContinue();

        //activities-add-jobs (list)
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-add-jobs.title"));
        testPage.clickButton(getEnMessage("activities-add-jobs.this-is-all-my-jobs"));

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-intro.title"));
    }

    @Test
    void ParentOnlyWithSchoolOnlyTest() {
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-intro.title"));
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-type.title"));
        testPage.clickElementById("activitiesParentChildcareReason-SCHOOL");
        testPage.clickContinue();

        //activities-add-ed-program
        assertThat(testPage.getHeader()).isEqualTo(
                getEnMessageWithParams("activities-add-ed-program.header", new Object[]{"parent first"}));
        testPage.clickContinue();

        //parent-info-bachelors
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-bachelors.title"));
        testPage.clickElementById("applicantHasBachelorsDegree-skip");

        //activities-ed-program-type
        assertThat(testPage.getElementText("educationType-highSchoolOrGed-label")).isEqualTo(
                getEnMessage("activities-ed-program-type.highSchool"));
        testPage.clickElementById("educationType-highSchoolOrGed-label");
        assertThat(testPage.getHeader()).isEqualTo(getRequiredEnMessage("activities-ed-program-type.header"));
        testPage.clickContinue();

        //activities-ed-program-name
        testPage.enter("applicantSchoolName", "World");
        assertThat(testPage.getHeader()).isEqualTo(getRequiredEnMessage("activities-ed-program-name.header"));
        testPage.clickContinue();

        //activities-ed-program-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.clickContinue();

        //activities-ed-program-method
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-method.header"));
        testPage.clickElementById("programTaught-Online-label");
        testPage.clickElementById("programSchedule-No-label");
        testPage.clickContinue();

        //activities-next-class-schedule
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("activities-next-class-schedule.header"));
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

        //activities-class-commute-time
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-class-commute-time.title"));
        testPage.clickContinue();

        //activities-ed-program-dates
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-dates.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-intro.title"));
    }

    @Test
    void ParentOnlyWithNoJobOrSchoolTest() {
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-intro.title"));
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-type.title"));
        testPage.clickElementById("activitiesParentChildcareReason-TANF_TRAINING");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-intro.title"));
    }

    @Test
    void ParentPartnerDoesNotAppearIfIneligible() {
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .with("parentHasPartner", "true")
                .build());

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-intro.title"));
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-type.title"));
        assertThat(testPage.findElementById("activitiesParentChildcareReason-WORKING")).isNotNull();
        assertThat(testPage.elementDoesNotExistById("activitiesParentPartnerChildcareReason-WORKING")).isTrue();
    }

    @Test
    void ParentPartnerAppearsIfEligible() {
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .with("parentHasPartner", "true")
                .with("parentHasQualifyingPartner", "true")
                .build());

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-intro.title"));
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-type.title"));
        assertThat(testPage.findElementById("activitiesParentChildcareReason-WORKING")).isNotNull();
        assertThat(testPage.findElementTextById("activitiesParentPartnerChildcareReason-WORKING")).isNotNull();
    }

    @Test
    void ParentAndPartnerWithJobAndWorkTest() {
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .withParentPartnerDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-intro.title"));
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-type.title"));
        testPage.clickElementById("activitiesParentChildcareReason-WORKING");
        testPage.clickElementById("activitiesParentChildcareReason-SCHOOL");
        testPage.clickElementById("activitiesParentPartnerChildcareReason-WORKING");
        testPage.clickElementById("activitiesParentPartnerChildcareReason-SCHOOL");
        testPage.clickContinue();

        //Add Parent Jobs
        //activities-add-jobs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-add-jobs.title"));
        testPage.clickButton(getEnMessage("activities-add-jobs.add-a-job"));
        //activities-employer-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-employer-name.title"));
        testPage.enter("companyName", "testCompany");
        testPage.clickContinue();
        //activities-employer-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-employer-address.title"));
        testPage.enter("employerPhoneNumber", "3333333");
        testPage.enter("employerCity", "Chicago");
        testPage.enter("employerStreetAddress", "123 Test Me");
        testPage.enter("employerZipCode", "6042");
        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-phone-number"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-zipcode"))).isTrue();
        testPage.enter("employerPhoneNumber", "3333333333");
        testPage.enter("employerZipCode", "60423");
        testPage.clickContinue();
        //activities-employer-start-date
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-employer-start-date.title"));
        testPage.enter("activitiesJobStartYear", "2000");
        testPage.enter("activitiesJobStartDay", "08");
        testPage.enter("activitiesJobStartMonth", "10");
        testPage.clickContinue();
        //activities-self-employment
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-self-employment.title"));
        testPage.clickYes();

        //activities-work-schedule-vary
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-work-schedule-vary.title"));
        testPage.clickYes();

        //activities-partner-next-work-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-next-work-schedule.title"));
        testPage.clickContinue();

        //activities-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-job-weekly-schedule.title"));
        testPage.clickElementById("activitiesJobWeeklySchedule-Monday");
        testPage.clickElementById("activitiesJobWeeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-job-hourly-schedule.title"));

        testPage.selectFromDropdown("activitiesJobStartTimeMondayHour", "12");
        testPage.enter("activitiesJobStartTimeMondayMinute", "00");
        testPage.selectFromDropdown("activitiesJobStartTimeMondayAmPm", "PM");

        testPage.selectFromDropdown("activitiesJobEndTimeMondayHour", "4");
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
        testPage.selectFromDropdown("activitiesJobCommuteTime", "1 hour");
        testPage.clickContinue();

        //activities-add-jobs (list)
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-add-jobs.title"));
        testPage.clickButton(getEnMessage("activities-add-jobs.this-is-all-my-jobs"));

        //activities-add-ed-program
        assertThat(testPage.getHeader()).isEqualTo(
                getEnMessageWithParams("activities-add-ed-program.header", new Object[]{"parent first"}));
        testPage.clickContinue();

        //parent-info-bachelors
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-bachelors.title"));
        testPage.clickElementById("applicantHasBachelorsDegree-skip");

        //activities-ed-program-type
        assertThat(testPage.getElementText("educationType-highSchoolOrGed-label")).isEqualTo(
                getEnMessage("activities-ed-program-type.highSchool"));
        testPage.clickElementById("educationType-highSchoolOrGed-label");
        assertThat(testPage.getHeader()).isEqualTo(getRequiredEnMessage("activities-ed-program-type.header"));
        testPage.clickContinue();

        //activities-ed-program-name
        testPage.enter("applicantSchoolName", "World");
        assertThat(testPage.getHeader()).isEqualTo(getRequiredEnMessage("activities-ed-program-name.header"));
        testPage.clickContinue();

        //activities-ed-program-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.clickContinue();

        //activities-ed-program-method
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-method.header"));
        testPage.clickElementById("programTaught-Online-label");
        testPage.clickElementById("programSchedule-No-label");
        testPage.clickContinue();

        //activities-next-class-schedule
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("activities-next-class-schedule.header"));
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

        //activities-class-commute-time
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-class-commute-time.title"));
        testPage.clickContinue();

        //activities-ed-program-dates
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-dates.title"));
        testPage.clickContinue();
        //activities-partner-add-job
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-add-jobs.title"));
        assertThat(testPage.getHeader()).isEqualTo(
                getEnMessageWithParams("activities-partner-add-jobs.header", new Object[]{"partner"}));
        assertThat(testPage.findElementById("header-help-message").getText()).isEqualTo(
                getEnMessage("activities-partner-add-jobs.subtext"));
        testPage.clickButton(getEnMessage("activities-add-jobs.add-a-job"));
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
        //activities-partner-employer-start-date
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-employer-start-date.title"));
        testPage.clickContinue();
        //activities-partner-self-employment
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-self-employment.title"));
        testPage.clickNo();

        //check that selecting yes on activities-partner-work-schedule-vary screen send you to ...next-work-schedule
        // and no sends you to the ...job-weekly-schedule screen

        //activities-partner-work-schedule-vary
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-work-schedule-vary.title"));
        assertThat(testPage.getHeader()).isEqualTo(
                getEnMessageWithParams("activities-partner-work-schedule-vary.header", new Object[]{"partner"}));
        testPage.clickNo();

        //activities-partner-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-job-weekly-schedule.title"));
        testPage.goBack();

        //activities-partner-work-schedule-vary
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-work-schedule-vary.title"));
        testPage.clickYes();

        //activities-partner-next-work-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-next-work-schedule.title"));
        assertThat(testPage.getHeader()).isEqualTo(
                getEnMessageWithParams("activities-partner-next-work-schedule.header", new Object[]{"partner"}));
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
        testPage.selectFromDropdown("activitiesJobStartTimeSundayAmPm", "PM");

        testPage.selectFromDropdown("activitiesJobEndTimeSundayHour", "3");
        testPage.enter("activitiesJobEndTimeSundayMinute", "00");
        testPage.selectFromDropdown("activitiesJobEndTimeSundayAmPm", "PM");
        testPage.clickContinue();

        //activities-partner-commute-time
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-commute-time.title"));
        testPage.selectFromDropdown("activitiesJobCommuteTime", getEnMessage("general.hours.1.5.hours"));
        testPage.clickContinue();
        //activities-partner-add-job
        assertThat(testPage.getHeader()).isEqualTo(
                getEnMessageWithParams("activities-partner-add-job.header.any-other-jobs", new Object[]{"partner"}));
        testPage.clickButton(getEnMessage("activities-partner-add-jobs.this-is-all-their-jobs"));
        //activities-partner-add-ed-program
        assertThat(testPage.getHeader()).isEqualTo(
                getEnMessageWithParams("activities-partner-add-ed-program.header", new Object[]{"partner"}));
        testPage.clickContinue();

        //parent-partner-info-bachelors
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-partner-info-bachelors.title"));
        testPage.clickElementById("partnerHasBachelorsDegree-skip");

        testPage.clickElementById("partnerEducationType-fourYearCollege-label");
        assertThat(testPage.getHeader()).isEqualTo(
                getRequiredEnMessageWithParams("activities-partner-ed-program-type.header", new Object[]{"partner"}));
        testPage.clickContinue();
        testPage.enter("partnerProgramName", "World University");
        assertThat(testPage.getHeader()).isEqualTo(getRequiredEnMessage("activities-ed-program-name.header"));
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-method.header"));
        testPage.clickElementById("partnerProgramTaught-In-Person-label");
        testPage.clickElementById("partnerProgramSchedule-Yes-label");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-class-weekly-schedule.title"));
        testPage.clickElementById("partnerClassWeeklySchedule-Monday");
        testPage.clickElementById("partnerClassWeeklySchedule-Tuesday");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-class-hourly-schedule.title"));

        testPage.selectFromDropdown("partnerClassStartTimeMondayHour", "9");
        testPage.enter("partnerClassStartTimeMondayMinute", "00");
        testPage.selectFromDropdown("partnerClassStartTimeMondayAmPm", "AM");

        testPage.selectFromDropdown("partnerClassEndTimeMondayHour", "11");
        testPage.enter("partnerClassEndTimeMondayMinute", "00");
        testPage.selectFromDropdown("partnerClassEndTimeMondayAmPm", "AM");

        testPage.selectFromDropdown("partnerClassStartTimeTuesdayHour", "2");
        testPage.enter("partnerClassStartTimeTuesdayMinute", "00");
        testPage.selectFromDropdown("partnerClassStartTimeTuesdayAmPm", "PM");

        testPage.selectFromDropdown("partnerClassEndTimeTuesdayHour", "3");
        testPage.enter("partnerClassEndTimeTuesdayMinute", "00");
        testPage.selectFromDropdown("partnerClassEndTimeTuesdayAmPm", "PM");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-class-commute-time.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-dates.title"));
        testPage.clickContinue();

        //unearned-income-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-intro.title"));
    }

    @Test
    void ParentAndPartnerWithPartnerSchoolOnlyTest() {
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .withParentPartnerDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-intro.title"));
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-type.title"));
        testPage.clickElementById("activitiesParentChildcareReason-TANF_TRAINING");
        testPage.clickElementById("activitiesParentPartnerChildcareReason-SCHOOL");
        testPage.clickContinue();

        // Note that currently, we are skipping the parent partner job questions
        assertThat(testPage.getHeader()).isEqualTo(
                getEnMessageWithParams("activities-partner-add-ed-program.header", new Object[]{"partner"}));
        testPage.clickContinue();

        //parent-partner-info-bachelors
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-partner-info-bachelors.title"));
        testPage.clickElementById("partnerHasBachelorsDegree-skip");

        testPage.clickElementById("partnerEducationType-fourYearCollege-label");
        assertThat(testPage.getHeader()).isEqualTo(
                getRequiredEnMessageWithParams("activities-partner-ed-program-type.header", new Object[]{"partner"}));
        testPage.clickContinue();
        testPage.enter("partnerProgramName", "World University");
        assertThat(testPage.getHeader()).isEqualTo(getRequiredEnMessage("activities-ed-program-name.header"));
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-method.header"));
        testPage.clickElementById("partnerProgramTaught-In-Person-label");
        testPage.clickElementById("partnerProgramSchedule-Yes-label");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-class-weekly-schedule.title"));
        testPage.clickElementById("partnerClassWeeklySchedule-Monday");
        testPage.clickElementById("partnerClassWeeklySchedule-Tuesday");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-class-hourly-schedule.title"));

        testPage.selectFromDropdown("partnerClassStartTimeMondayHour", "9");
        testPage.enter("partnerClassStartTimeMondayMinute", "15");
        testPage.selectFromDropdown("partnerClassStartTimeMondayAmPm", "AM");

        testPage.selectFromDropdown("partnerClassEndTimeMondayHour", "11");
        testPage.enter("partnerClassEndTimeMondayMinute", "00");
        testPage.selectFromDropdown("partnerClassEndTimeMondayAmPm", "AM");

        testPage.selectFromDropdown("partnerClassStartTimeTuesdayHour", "2");
        testPage.enter("partnerClassStartTimeTuesdayMinute", "00");
        testPage.selectFromDropdown("partnerClassStartTimeTuesdayAmPm", "PM");

        testPage.selectFromDropdown("partnerClassEndTimeTuesdayHour", "3");
        testPage.enter("partnerClassEndTimeTuesdayMinute", "00");
        testPage.selectFromDropdown("partnerClassEndTimeTuesdayAmPm", "PM");
        testPage.clickContinue();
        //activities-class-commute-time
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-class-commute-time.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-dates.title"));
        testPage.clickContinue();

        //unearned-income-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-intro.title"));
    }

    @Test
    void ParentAndPartnerWithPartnerJobOnlyTest() {
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .withParentPartnerDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-intro.title"));
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-type.title"));
        testPage.clickElementById("activitiesParentChildcareReason-TANF_TRAINING");
        testPage.clickElementById("activitiesParentPartnerChildcareReason-WORKING");
        testPage.clickContinue();

        //activities-partner-add-job
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-add-jobs.title"));
    }

    @Test
    void ParentAndPartnerWithoutJobOrSchoolTest() {
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .withParentPartnerDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-intro.title"));
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-type.title"));
        testPage.clickElementById("activitiesParentChildcareReason-TANF_TRAINING");
        testPage.clickElementById("activitiesParentPartnerChildcareReason-TANF_TRAINING");
        testPage.clickContinue();

        //unearned-income-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("unearned-income-intro.title"));
    }
}
