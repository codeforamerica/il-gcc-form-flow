package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;

public class HourlyScheduleJourneyTest extends AbstractBasePageTest {
    @Test
    void missingChildCareTimeRaisesError() {
        addAChild();

        //children-ccap-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-childcare-weekly-schedule.title"));
        testPage.clickElementById("childcareWeeklySchedule-Thursday");
        testPage.clickContinue();

        //children-childcare-hourly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-childcare-hourly-schedule.title"));
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isTrue();

        testPage.selectFromDropdown("childcareEndTimeThursdayHour", "1");
        testPage.enter("childcareEndTimeThursdayMinute", "00");
        testPage.selectFromDropdown("childcareEndTimeThursdayAmPm", "PM");

        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isFalse();

        testPage.selectFromDropdown("childcareStartTimeThursdayHour", "10");
        testPage.enter("childcareStartTimeThursdayMinute", "15");
        testPage.selectFromDropdown("childcareStartTimeThursdayAmPm", "AM");

        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-ccap-child-other-ed.title"));
    }

    @Test
    void invalidChildCareTimeRaisesError() {
        addAChild();

        //children-ccap-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-childcare-weekly-schedule.title"));
        testPage.clickElementById("childcareWeeklySchedule-Thursday");
        testPage.clickContinue();

        //children-childcare-hourly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-childcare-hourly-schedule.title"));

        testPage.selectFromDropdown("childcareEndTimeThursdayHour", "1");
        testPage.enter("childcareEndTimeThursdayMinute", "00");
        testPage.selectFromDropdown("childcareEndTimeThursdayAmPm", "PM");

        testPage.selectFromDropdown("childcareStartTimeThursdayHour", "10");
        testPage.enter("childcareStartTimeThursdayMinute", "65");
        testPage.selectFromDropdown("childcareStartTimeThursdayAmPm", "AM");

        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.minute"))).isTrue();

        testPage.enter("childcareStartTimeThursdayMinute", "15");

        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.minute"))).isFalse();

        testPage.goBack();

        testPage.enter("childcareEndTimeThursdayMinute", "-15");
        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.minute"))).isTrue();

        testPage.enter("childcareEndTimeThursdayMinute", "15");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-ccap-child-other-ed.title"));
    }

    @Test
    void missingParentJobTimeRaisesError() {
        testPage.navigateToFlowScreen("gcc/activities-add-jobs");

        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());

        testPage.clickButton(getEnMessage("activities-add-jobs.add-a-job"));
        assertThat(testPage.getTitle()).isEqualTo("Activities Employer Name");

        addPrimaryParentJob("1");

        //activities-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-job-weekly-schedule.title"));
        testPage.clickElementById("activitiesJobWeeklySchedule-Monday");
        testPage.clickElementById("activitiesJobWeeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Job hourly schedule");

        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isTrue();

        testPage.selectFromDropdown("activitiesJobStartTimeMondayHour", "12");
        testPage.enter("activitiesJobStartTimeMondayMinute", "00");
        testPage.selectFromDropdown("activitiesJobStartTimeMondayAmPm", "PM");

        testPage.selectFromDropdown("activitiesJobEndTimeMondayHour", "1");
        testPage.enter("activitiesJobEndTimeMondayMinute", "00");
        testPage.selectFromDropdown("activitiesJobEndTimeMondayAmPm", "PM");

        testPage.clickContinue();

        // Errors triggered by missing data on Sunday
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isTrue();

        testPage.selectFromDropdown("activitiesJobStartTimeSundayHour", "2");
        testPage.enter("activitiesJobStartTimeSundayMinute", "00");

        testPage.clickContinue();

        // Errors Triggered by missing Sunday AM.PM
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isTrue();

        testPage.selectFromDropdown("activitiesJobStartTimeSundayAmPm", "PM");

        testPage.selectFromDropdown("activitiesJobEndTimeSundayHour", "3");
        testPage.enter("activitiesJobEndTimeSundayMinute", "-1");
        testPage.selectFromDropdown("activitiesJobEndTimeSundayAmPm", "PM");
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.minute"))).isTrue();

        testPage.enter("activitiesJobEndTimeSundayMinute", "1");
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
    void invalidParentJobTimeRaisesError() {
        testPage.navigateToFlowScreen("gcc/activities-add-jobs");

        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());

        testPage.clickButton(getEnMessage("activities-add-jobs.add-a-job"));
        assertThat(testPage.getTitle()).isEqualTo("Activities Employer Name");

        addPrimaryParentJob("1");

        //activities-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-job-weekly-schedule.title"));
        testPage.clickElementById("activitiesJobWeeklySchedule-Monday");
        testPage.clickContinue();

        //activities-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Job hourly schedule");

        testPage.selectFromDropdown("activitiesJobStartTimeMondayHour", "12");
        testPage.enter("activitiesJobStartTimeMondayMinute", "00");
        testPage.selectFromDropdown("activitiesJobStartTimeMondayAmPm", "PM");

        testPage.selectFromDropdown("activitiesJobEndTimeMondayHour", "1");
        testPage.enter("activitiesJobEndTimeMondayMinute", "100");
        testPage.selectFromDropdown("activitiesJobEndTimeMondayAmPm", "PM");

        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.minute"))).isTrue();

        testPage.enter("activitiesJobEndTimeMondayMinute", "10");
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.minute"))).isFalse();

        testPage.goBack();
        testPage.enter("activitiesJobStartTimeMondayMinute", "-50");

        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.minute"))).isTrue();

        testPage.enter("activitiesJobStartTimeMondayMinute", "50");
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.minute"))).isFalse();

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
    void missingParentSchoolTimeRaisesError() {
        addParentSchool();

        //activities-class-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-class-weekly-schedule.title"));
        testPage.clickElementById("weeklySchedule-Tuesday");
        testPage.clickElementById("weeklySchedule-Thursday");
        testPage.clickElementById("weeklySchedule-Saturday");
        testPage.clickContinue();

        //activities-class-hourly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-class-hourly-schedule.title"));
        testPage.clickElementById("activitiesClassHoursSameEveryDay-Yes");

        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isTrue();

        testPage.selectFromDropdown("activitiesClassStartTimeAllDaysHour", "9");
        testPage.enter("activitiesClassStartTimeAllDaysMinute", "00");
        testPage.selectFromDropdown("activitiesClassStartTimeAllDaysAmPm", "AM");

        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isTrue();


        testPage.selectFromDropdown("activitiesClassEndTimeAllDaysHour", "1");
        testPage.enter("activitiesClassEndTimeAllDaysMinute", "00");
        testPage.selectFromDropdown("activitiesClassEndTimeAllDaysAmPm", "PM");

        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isFalse();

        //activities-ed-program-dates
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-dates.title"));
    }

    @Test
    void invalidParentSchoolTimeRaisesError() {
        addParentSchool();

        //activities-class-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-class-weekly-schedule.title"));
        testPage.clickElementById("weeklySchedule-Tuesday");
        testPage.clickElementById("weeklySchedule-Thursday");
        testPage.clickElementById("weeklySchedule-Saturday");
        testPage.clickContinue();

        //activities-class-hourly-schedule
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-class-hourly-schedule.title"));
        testPage.clickElementById("activitiesClassHoursSameEveryDay-Yes");

        testPage.selectFromDropdown("activitiesClassStartTimeAllDaysHour", "9");
        testPage.enter("activitiesClassStartTimeAllDaysMinute", "105");
        testPage.selectFromDropdown("activitiesClassStartTimeAllDaysAmPm", "AM");

        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.minute"))).isTrue();

        testPage.selectFromDropdown("activitiesClassEndTimeAllDaysHour", "1");
        testPage.enter("activitiesClassEndTimeAllDaysMinute", "00");
        testPage.selectFromDropdown("activitiesClassEndTimeAllDaysAmPm", "PM");

        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.minute"))).isTrue();

        testPage.enter("activitiesClassStartTimeAllDaysMinute", "55");
        testPage.clickContinue();

        testPage.goBack();
        testPage.enter("activitiesClassEndTimeAllDaysMinute", "12300");
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.minute"))).isTrue();

        testPage.enter("activitiesClassEndTimeAllDaysMinute", "12");
        testPage.clickContinue();

        //activities-ed-program-dates
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-dates.title"));
    }

    @Test
    void missingParentPartnerJobTimeRaisesError() {
        testPage.navigateToFlowScreen("gcc/activities-partner-add-job");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .withParentPartnerDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());

        //activities-partner-add-job
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-add-jobs.title"));
        testPage.clickButton("Add a job");

        addParentPartnerJob("1");
        //activities-partner-job-weekly-schedule
        testPage.clickElementById("activitiesJobWeeklySchedule-Monday");
        testPage.clickElementById("activitiesJobWeeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-partner-job-hourly-schedule
        testPage.clickElementById("activitiesJobHoursSameEveryDay-Yes");

        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isTrue();

        testPage.selectFromDropdown("activitiesJobStartTimeAllDaysHour", "9");
        testPage.enter("activitiesJobStartTimeAllDaysMinute",  "00");
        testPage.selectFromDropdown("activitiesJobStartTimeAllDaysAmPm", "PM");

        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isTrue();

        testPage.selectFromDropdown("activitiesJobEndTimeAllDaysHour", "1");
        testPage.enter("activitiesJobEndTimeAllDaysMinute",  "00");
        testPage.selectFromDropdown("activitiesJobEndTimeAllDaysAmPm", "PM");

        testPage.clickContinue();

        //activities-partner-commute-time
        testPage.selectFromDropdown("activitiesJobCommuteTime", getEnMessage("general.hours.1.5.hours"));
        testPage.clickContinue();
    }

    @Test
    void invalidParentPartnerJobTimeRaisesError() {
        testPage.navigateToFlowScreen("gcc/activities-partner-add-job");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .withParentPartnerDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());

        //activities-partner-add-job
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-add-jobs.title"));
        testPage.clickButton("Add a job");

        addParentPartnerJob("1");
        //activities-partner-job-weekly-schedule
        testPage.clickElementById("activitiesJobWeeklySchedule-Monday");
        testPage.clickElementById("activitiesJobWeeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-partner-job-hourly-schedule
        testPage.clickElementById("activitiesJobHoursSameEveryDay-Yes");

        testPage.selectFromDropdown("activitiesJobStartTimeAllDaysHour", "9");
        testPage.enter("activitiesJobStartTimeAllDaysMinute",  "78");
        testPage.selectFromDropdown("activitiesJobStartTimeAllDaysAmPm", "PM");

        testPage.selectFromDropdown("activitiesJobEndTimeAllDaysHour", "1");
        testPage.enter("activitiesJobEndTimeAllDaysMinute",  "00");
        testPage.selectFromDropdown("activitiesJobEndTimeAllDaysAmPm", "PM");

        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.minute"))).isTrue();

        testPage.enter("activitiesJobStartTimeAllDaysMinute",  "22");
        testPage.clickContinue();

        testPage.goBack();

        testPage.enter("activitiesJobEndTimeAllDaysMinute",  "-23");
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.minute"))).isTrue();

        testPage.enter("activitiesJobEndTimeAllDaysMinute",  "23");
        testPage.clickContinue();

        //activities-partner-commute-time
        testPage.selectFromDropdown("activitiesJobCommuteTime", getEnMessage("general.hours.1.5.hours"));
        testPage.clickContinue();
    }

    @Test
    void missingParentPartnerSchoolTimeRaisesError() {
        addParentPartnerSchool();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-class-weekly-schedule.title"));

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-class-weekly-schedule.title"));
        testPage.clickElementById("partnerClassWeeklySchedule-Monday");
        testPage.clickElementById("partnerClassWeeklySchedule-Wednesday");

        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-class-hourly-schedule.title"));
        testPage.clickElementById("partnerClassHoursSameEveryDay-Yes");

        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isTrue();

        testPage.selectFromDropdown("partnerClassStartTimeAllDaysHour", "11");
        testPage.enter("partnerClassStartTimeAllDaysMinute", "00");
        testPage.selectFromDropdown("partnerClassStartTimeAllDaysAmPm", "AM");

        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isTrue();

        testPage.selectFromDropdown("partnerClassEndTimeAllDaysHour", "4");
        testPage.enter("partnerClassEndTimeAllDaysMinute", "00");
        testPage.selectFromDropdown("partnerClassEndTimeAllDaysAmPm", "PM");

        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-dates.title"));
    }

    @Test
    void invalidParentPartnerSchoolTimeRaisesError() {
        addParentPartnerSchool();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-class-weekly-schedule.title"));

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-class-weekly-schedule.title"));
        testPage.clickElementById("partnerClassWeeklySchedule-Monday");
        testPage.clickElementById("partnerClassWeeklySchedule-Wednesday");

        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-class-hourly-schedule.title"));
        testPage.clickElementById("partnerClassHoursSameEveryDay-Yes");

        testPage.selectFromDropdown("partnerClassStartTimeAllDaysHour", "11");
        testPage.enter("partnerClassStartTimeAllDaysMinute", "65");
        testPage.selectFromDropdown("partnerClassStartTimeAllDaysAmPm", "AM");

        testPage.selectFromDropdown("partnerClassEndTimeAllDaysHour", "4");
        testPage.enter("partnerClassEndTimeAllDaysMinute", "00");
        testPage.selectFromDropdown("partnerClassEndTimeAllDaysAmPm", "PM");

        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.minute"))).isTrue();

        testPage.clickContinue();

        testPage.enter("partnerClassStartTimeAllDaysMinute", "55");
        testPage.clickContinue();

        testPage.goBack();

        testPage.enter("partnerClassEndTimeAllDaysMinute", "-22");
        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.start.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.end.time"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("errors.validate.minute"))).isTrue();

        testPage.enter("partnerClassEndTimeAllDaysMinute", "22");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-dates.title"));
    }

    void addAChild(){
        // children-add
        testPage.navigateToFlowScreen("gcc/children-add");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .build());

        // children-add
        testPage.clickButton(getEnMessage("children-add.add-button"));

        //children-info-basic
        testPage.enter("childFirstName", "Firstly");
        testPage.enter("childLastName", "McChild");
        testPage.enter("childDateOfBirthMonth", "10");
        testPage.enter("childDateOfBirthDay", "25");
        testPage.enter("childDateOfBirthYear", "2018");
        testPage.selectFromDropdown("childRelationship", getEnMessage("children-ccap-info.relationship-option.fosterchild"));
        testPage.selectRadio("needFinancialAssistanceForChild", "Yes");
        testPage.clickContinue();

        //children-ccap-info
        testPage.clickElementById("childGender-MALE");
        testPage.clickElementById("childGender-TRANSGENDER");
        testPage.selectRadio("childHasDisability", "No");
        testPage.selectRadio("childIsUsCitizen", "Yes");
        testPage.clickElementById("none__checkbox-childRaceEthnicity");
        testPage.clickContinue();

        //children-ccap-in-care
        testPage.clickYes();
        testPage.enter("ccapStartMonth", "11");
        testPage.enter("ccapStartDay", "1");
        testPage.enter("ccapStartYear", "2010");
        testPage.clickContinue();
    }

    void addParentSchool(){
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-add-ed-program");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());

        //activities-add-ed-program
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.clickContinue();

        //parent-info-bachelors
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-bachelors.title"));
        testPage.clickYes();

        //activities-ed-program-type
        assertThat(testPage.getElementText("educationType-highSchoolOrGed-label")).isEqualTo(getEnMessage("activities-ed-program-type.highSchool"));
        testPage.clickElementById("educationType-highSchoolOrGed-label");
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.clickContinue();

        //activities-ed-program-name
        testPage.enter("applicantSchoolName", "World Partner Training Program");
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
    }

    void addParentPartnerSchool(){
        //parent-partner-info-bachelors
        testPage.navigateToFlowScreen("gcc/parent-partner-info-bachelors");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .withParentPartnerDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());

        testPage.clickElementById("partnerHasBachelorsDegree-skip");

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.clickElementById("partnerEducationType-twoYearCollege-label");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.enter("partnerProgramName", "World University");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program.title"));
        testPage.enter("partnerEdPhoneNumber", "(217) 323-1238");
        testPage.enter("partnerEdStreetAddress", "234 Main St");
        testPage.enter("partnerEdCity", "Springfield");
        testPage.enter("partnerEdState", "IL - Illinois");
        testPage.enter("partnerEdZipCode", "62628");

        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-method.header"));
        testPage.clickElementById("partnerProgramTaught-In-Person-label");
        testPage.clickElementById("partnerProgramSchedule-Yes-label");
        testPage.clickContinue();
    }
}
