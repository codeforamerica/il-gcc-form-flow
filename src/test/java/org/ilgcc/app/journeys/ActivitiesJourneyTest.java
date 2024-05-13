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
            .withChild("First", "Child")
            .withChild("Second", "Child")
            .build());

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Intro");
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Type");
        testPage.clickElementById("activitiesParentChildcareReason-WORKING");
        testPage.clickElementById("activitiesParentChildcareReason-SCHOOL");
        testPage.clickContinue();

        //activities-add-jobs
        assertThat(testPage.getTitle()).isEqualTo("Activities Add Jobs");
        testPage.clickButton("Add a job");
        //activities-employer-name
        assertThat(testPage.getTitle()).isEqualTo("Activities Employer Name");
        testPage.enter("companyName", "testCompany");
        testPage.clickContinue();
        //activities-employer-address
        assertThat(testPage.getTitle()).isEqualTo("Activities Employer Address");
        testPage.enter("employerPhoneNumber", "3333333");
        testPage.enter("employerCity", "Chicago");
        testPage.enter("employerStreetAddress", "123 Test Me");
        testPage.enter("employerZipCode", "6042");
        testPage.clickContinue();
        assertThat(testPage.hasErrorText("Make sure the phone number you entered includes 10 digits.")).isTrue();
        assertThat(testPage.hasErrorText("Make sure the zip code you entered follows the right format.")).isTrue();
        testPage.enter("employerPhoneNumber", "3333333333");
        testPage.enter("employerZipCode", "60423");
        testPage.clickContinue();
        //activities-self-employment
        assertThat(testPage.getTitle()).isEqualTo("Activities Self Employment");
        testPage.clickButton("Yes");

        //activities-work-schedule-vary
        assertThat(testPage.getTitle()).isEqualTo("Work schedule vary");
        testPage.clickButton("Yes");

        //activities-partner-next-work-schedule
        assertThat(testPage.getTitle()).isEqualTo("Work Schedule");
        testPage.clickContinue();

        //activities-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Weekly Schedule");
        testPage.clickElementById("activitiesJobWeeklySchedule-Monday");
        testPage.clickElementById("activitiesJobWeeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Job hourly schedule");
        testPage.enter("activitiesJobStartTimeMonday", "1200PM");
        testPage.enter("activitiesJobEndTimeMonday", "0100PM");
        testPage.enter("activitiesJobStartTimeSunday", "0200PM");
        testPage.enter("activitiesJobEndTimeSunday", "0300PM");
        testPage.clickContinue();

        //activities-work-commute-time
        assertThat(testPage.getTitle()).isEqualTo("Work commute time");
        testPage.selectFromDropdown("activitiesJobCommuteTime", "1 hour");
        testPage.clickContinue();

        //activities-add-jobs (list)
        assertThat(testPage.getTitle()).isEqualTo("Activities Add Jobs");
        testPage.clickButton("That is all my jobs");

        //activities-add-ed-program
        assertThat(testPage.getHeader()).isEqualTo("Tell us about your school or training program.");
        testPage.clickContinue();

        //activities-ed-program-type
        assertThat(testPage.getElementText("educationType-highSchool-label")).isEqualTo("High School or GED");
        testPage.clickElementById("educationType-highSchool-label");
        assertThat(testPage.getHeader()).isEqualTo("What type of school or training are you enrolled in?");
        testPage.clickContinue();

        //activities-ed-program-name
        testPage.enter("schoolName", "World");
        assertThat(testPage.getHeader()).isEqualTo("What is the school or training program name?*");
        testPage.clickContinue();

        //activities-ed-program-info
        assertThat(testPage.getTitle()).isEqualTo("School or training program");
        testPage.clickContinue();

        //activities-ed-program-method
        assertThat(testPage.getTitle()).isEqualTo("Learning style");
        testPage.clickElementById("programTaught-Online-label");
        testPage.clickElementById("programSchedule-No-label");
        testPage.clickContinue();

        //activities-next-class-schedule
        assertThat(testPage.getHeader()).isEqualTo("Next, we'll ask about your class schedule.");
        testPage.clickContinue();

        //activities-class-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Weekly Class Schedule");
        testPage.clickElementById("weeklySchedule-Monday");
        testPage.clickElementById("weeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-class-hourly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Hourly Class Schedule");
        testPage.clickElementById("activitiesClassHoursSameEveryDay-Yes");
        testPage.enter("activitiesClassStartTimeAllDays", "0900AM");
        testPage.enter("activitiesClassEndTimeAllDays", "13:00");
        testPage.clickContinue();

        //activities-ed-program-dates
        assertThat(testPage.getTitle()).isEqualTo("Time of Program");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo("Unearned Income Intro");
    }

    @Test
    void ParentOnlyWithJobOnlyTest() {
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child")
            .withChild("Second", "Child")
            .build());

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Intro");
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Type");
        testPage.clickElementById("activitiesParentChildcareReason-WORKING");
        testPage.clickContinue();

        //activities-add-jobs
        assertThat(testPage.getTitle()).isEqualTo("Activities Add Jobs");
        testPage.clickButton("Add a job");
        //activities-employer-name
        assertThat(testPage.getTitle()).isEqualTo("Activities Employer Name");
        testPage.enter("companyName", "testCompany");
        testPage.clickContinue();
        //activities-employer-address
        assertThat(testPage.getTitle()).isEqualTo("Activities Employer Address");
        testPage.enter("employerPhoneNumber", "3333333");
        testPage.enter("employerCity", "Chicago");
        testPage.enter("employerStreetAddress", "123 Test Me");
        testPage.enter("employerZipCode", "6042");
        testPage.clickContinue();
        assertThat(testPage.hasErrorText("Make sure the phone number you entered includes 10 digits.")).isTrue();
        assertThat(testPage.hasErrorText("Make sure the zip code you entered follows the right format.")).isTrue();
        testPage.enter("employerPhoneNumber", "3333333333");
        testPage.enter("employerZipCode", "60423");
        testPage.clickContinue();
        //activities-self-employment
        assertThat(testPage.getTitle()).isEqualTo("Activities Self Employment");
        testPage.clickButton("Yes");

        //activities-work-schedule-vary
        assertThat(testPage.getTitle()).isEqualTo("Work schedule vary");
        testPage.clickButton("Yes");

        //activities-partner-next-work-schedule
        assertThat(testPage.getTitle()).isEqualTo("Work Schedule");
        testPage.clickContinue();

        //activities-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Weekly Schedule");
        testPage.clickElementById("activitiesJobWeeklySchedule-Monday");
        testPage.clickElementById("activitiesJobWeeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Job hourly schedule");
        testPage.enter("activitiesJobStartTimeMonday", "1200PM");
        testPage.enter("activitiesJobEndTimeMonday", "0100PM");
        testPage.enter("activitiesJobStartTimeSunday", "0200PM");
        testPage.enter("activitiesJobEndTimeSunday", "0300PM");
        testPage.clickContinue();

        //activities-work-commute-time
        assertThat(testPage.getTitle()).isEqualTo("Work commute time");
        testPage.selectFromDropdown("activitiesJobCommuteTime", "1 hour");
        testPage.clickContinue();

        //activities-add-jobs (list)
        assertThat(testPage.getTitle()).isEqualTo("Activities Add Jobs");
        testPage.clickButton("That is all my jobs");

        assertThat(testPage.getTitle()).isEqualTo("Unearned Income Intro");
    }

    @Test
    void ParentOnlyWithSchoolOnlyTest() {
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child")
            .withChild("Second", "Child")
            .build());

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Intro");
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Type");
        testPage.clickElementById("activitiesParentChildcareReason-SCHOOL");
        testPage.clickContinue();

        //activities-add-ed-program
        assertThat(testPage.getHeader()).isEqualTo("Tell us about your school or training program.");
        testPage.clickContinue();

        //activities-ed-program-type
        assertThat(testPage.getElementText("educationType-highSchool-label")).isEqualTo("High School or GED");
        testPage.clickElementById("educationType-highSchool-label");
        assertThat(testPage.getHeader()).isEqualTo("What type of school or training are you enrolled in?");
        testPage.clickContinue();

        //activities-ed-program-name
        testPage.enter("schoolName", "World");
        assertThat(testPage.getHeader()).isEqualTo("What is the school or training program name?*");
        testPage.clickContinue();

        //activities-ed-program-info
        assertThat(testPage.getTitle()).isEqualTo("School or training program");
        testPage.clickContinue();

        //activities-ed-program-method
        assertThat(testPage.getTitle()).isEqualTo("Learning style");
        testPage.clickElementById("programTaught-Online-label");
        testPage.clickElementById("programSchedule-No-label");
        testPage.clickContinue();

        //activities-next-class-schedule
        assertThat(testPage.getHeader()).isEqualTo("Next, we'll ask about your class schedule.");
        testPage.clickContinue();

        //activities-class-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Weekly Class Schedule");
        testPage.clickElementById("weeklySchedule-Monday");
        testPage.clickElementById("weeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-class-hourly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Hourly Class Schedule");
        testPage.clickElementById("activitiesClassHoursSameEveryDay-Yes");
        testPage.enter("activitiesClassStartTimeAllDays", "0900AM");
        testPage.enter("activitiesClassEndTimeAllDays", "13:00");
        testPage.clickContinue();

        //activities-ed-program-dates
        assertThat(testPage.getTitle()).isEqualTo("Time of Program");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo("Unearned Income Intro");
    }

    @Test
    void ParentOnlyWithNoJobOrSchoolTest() {
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child")
            .withChild("Second", "Child")
            .build());

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Intro");
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Type");
        testPage.clickElementById("activitiesParentChildcareReason-TANF_TRAINING");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo("Unearned Income Intro");
    }

    @Test
    void ParentAndPartnerWithJobAndWorkTest() {
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withParentPartnerDetails()
            .withChild("First", "Child")
            .withChild("Second", "Child")
            .build());

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Intro");
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Type");
        testPage.clickElementById("activitiesParentChildcareReason-WORKING");
        testPage.clickElementById("activitiesParentChildcareReason-SCHOOL");
        testPage.clickElementById("activitiesParentPartnerChildcareReason-WORKING");
        testPage.clickElementById("activitiesParentPartnerChildcareReason-SCHOOL");
        testPage.clickContinue();

        //activities-add-jobs
        assertThat(testPage.getTitle()).isEqualTo("Activities Add Jobs");
        testPage.clickButton("Add a job");
        //activities-employer-name
        assertThat(testPage.getTitle()).isEqualTo("Activities Employer Name");
        testPage.enter("companyName", "testCompany");
        testPage.clickContinue();
        //activities-employer-address
        assertThat(testPage.getTitle()).isEqualTo("Activities Employer Address");
        testPage.enter("employerPhoneNumber", "3333333");
        testPage.enter("employerCity", "Chicago");
        testPage.enter("employerStreetAddress", "123 Test Me");
        testPage.enter("employerZipCode", "6042");
        testPage.clickContinue();
        assertThat(testPage.hasErrorText("Make sure the phone number you entered includes 10 digits.")).isTrue();
        assertThat(testPage.hasErrorText("Make sure the zip code you entered follows the right format.")).isTrue();
        testPage.enter("employerPhoneNumber", "3333333333");
        testPage.enter("employerZipCode", "60423");
        testPage.clickContinue();
        //activities-self-employment
        assertThat(testPage.getTitle()).isEqualTo("Activities Self Employment");
        testPage.clickButton("Yes");

        //activities-work-schedule-vary
        assertThat(testPage.getTitle()).isEqualTo("Work schedule vary");
        testPage.clickButton("Yes");

        //activities-partner-next-work-schedule
        assertThat(testPage.getTitle()).isEqualTo("Work Schedule");
        testPage.clickContinue();

        //activities-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Weekly Schedule");
        testPage.clickElementById("activitiesJobWeeklySchedule-Monday");
        testPage.clickElementById("activitiesJobWeeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Job hourly schedule");
        testPage.enter("activitiesJobStartTimeMonday", "1200PM");
        testPage.enter("activitiesJobEndTimeMonday", "0100PM");
        testPage.enter("activitiesJobStartTimeSunday", "0200PM");
        testPage.enter("activitiesJobEndTimeSunday", "0300PM");
        testPage.clickContinue();

        //activities-work-commute-time
        assertThat(testPage.getTitle()).isEqualTo("Work commute time");
        testPage.selectFromDropdown("activitiesJobCommuteTime", "1 hour");
        testPage.clickContinue();

        //activities-add-jobs (list)
        assertThat(testPage.getTitle()).isEqualTo("Activities Add Jobs");
        testPage.clickButton("That is all my jobs");

        //activities-add-ed-program
        assertThat(testPage.getHeader()).isEqualTo("Tell us about your school or training program.");
        testPage.clickContinue();

        //activities-ed-program-type
        assertThat(testPage.getElementText("educationType-highSchool-label")).isEqualTo("High School or GED");
        testPage.clickElementById("educationType-highSchool-label");
        assertThat(testPage.getHeader()).isEqualTo("What type of school or training are you enrolled in?");
        testPage.clickContinue();

        //activities-ed-program-name
        testPage.enter("schoolName", "World");
        assertThat(testPage.getHeader()).isEqualTo("What is the school or training program name?*");
        testPage.clickContinue();

        //activities-ed-program-info
        assertThat(testPage.getTitle()).isEqualTo("School or training program");
        testPage.clickContinue();

        //activities-ed-program-method
        assertThat(testPage.getTitle()).isEqualTo("Learning style");
        testPage.clickElementById("programTaught-Online-label");
        testPage.clickElementById("programSchedule-No-label");
        testPage.clickContinue();

        //activities-next-class-schedule
        assertThat(testPage.getHeader()).isEqualTo("Next, we'll ask about your class schedule.");
        testPage.clickContinue();

        //activities-class-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Weekly Class Schedule");
        testPage.clickElementById("weeklySchedule-Monday");
        testPage.clickElementById("weeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-class-hourly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Hourly Class Schedule");
        testPage.clickElementById("activitiesClassHoursSameEveryDay-Yes");
        testPage.enter("activitiesClassStartTimeAllDays", "0900AM");
        testPage.enter("activitiesClassEndTimeAllDays", "13:00");
        testPage.clickContinue();

        //activities-ed-program-dates
        assertThat(testPage.getTitle()).isEqualTo("Time of Program");
        testPage.clickContinue();

        // Note that currently, we are skipping the parent partner job questions
        assertThat(testPage.getHeader()).isEqualTo("Now tell us about partner's school or training program.");
        testPage.clickContinue();
        testPage.clickElementById("partnerEducationType-college-label");
        assertThat(testPage.getHeader()).isEqualTo("What type of school or training is partner enrolled in?");
        testPage.clickContinue();
        testPage.enter("partnerProgramName", "World University");
        assertThat(testPage.getHeader()).isEqualTo("What is the school or training program name?*");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("School or training program");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("Learning style");
        testPage.clickElementById("partnerProgramTaught-In-Person-label");
        testPage.clickElementById("partnerProgramSchedule-Yes-label");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("Weekly Class Schedule");
        testPage.clickElementById("partnerClassWeeklySchedule-Monday");
        testPage.clickElementById("partnerClassWeeklySchedule-Tuesday");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("Hourly Class Schedule");
        testPage.enter("partnerClassStartTimeMonday", "0900AM");
        testPage.enter("partnerClassEndTimeMonday", "1100AM");
        testPage.enter("partnerClassStartTimeTuesday", "14:00");
        testPage.enter("partnerClassEndTimeTuesday", "15:00");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("Time of Program");
        testPage.clickContinue();

        //unearned-income-intro
        assertThat(testPage.getTitle()).isEqualTo("Unearned Income Intro");
    }

    @Test
    void ParentAndPartnerWithPartnerSchoolOnlyTest() {
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withParentPartnerDetails()
            .withChild("First", "Child")
            .withChild("Second", "Child")
            .build());

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Intro");
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Type");
        testPage.clickElementById("activitiesParentChildcareReason-TANF_TRAINING");
        testPage.clickElementById("activitiesParentPartnerChildcareReason-SCHOOL");
        testPage.clickContinue();

        // Note that currently, we are skipping the parent partner job questions
        assertThat(testPage.getHeader()).isEqualTo("Now tell us about partner's school or training program.");
        testPage.clickContinue();
        testPage.clickElementById("partnerEducationType-college-label");
        assertThat(testPage.getHeader()).isEqualTo("What type of school or training is partner enrolled in?");
        testPage.clickContinue();
        testPage.enter("partnerProgramName", "World University");
        assertThat(testPage.getHeader()).isEqualTo("What is the school or training program name?*");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("School or training program");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("Learning style");
        testPage.clickElementById("partnerProgramTaught-In-Person-label");
        testPage.clickElementById("partnerProgramSchedule-Yes-label");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("Weekly Class Schedule");
        testPage.clickElementById("partnerClassWeeklySchedule-Monday");
        testPage.clickElementById("partnerClassWeeklySchedule-Tuesday");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("Hourly Class Schedule");
        testPage.enter("partnerClassStartTimeMonday", "0900AM");
        testPage.enter("partnerClassEndTimeMonday", "1100AM");
        testPage.enter("partnerClassStartTimeTuesday", "14:00");
        testPage.enter("partnerClassEndTimeTuesday", "15:00");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("Time of Program");
        testPage.clickContinue();

        //unearned-income-intro
        assertThat(testPage.getTitle()).isEqualTo("Unearned Income Intro");
    }

    @Test
    void ParentAndPartnerWithPartnerJobOnlyTest() {
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withParentPartnerDetails()
            .withChild("First", "Child")
            .withChild("Second", "Child")
            .build());

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Intro");
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Type");
        testPage.clickElementById("activitiesParentChildcareReason-TANF_TRAINING");
        testPage.clickElementById("activitiesParentPartnerChildcareReason-WORKING");
        testPage.clickContinue();

        // Note that currently, we are skipping the parent partner job questions

        //unearned-income-intro
        assertThat(testPage.getTitle()).isEqualTo("Unearned Income Intro");
    }

    @Test
    void ParentAndPartnerWithoutJobOrSchoolTest() {
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withParentPartnerDetails()
            .withChild("First", "Child")
            .withChild("Second", "Child")
            .build());

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Intro");
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Type");
        testPage.clickElementById("activitiesParentChildcareReason-TANF_TRAINING");
        testPage.clickElementById("activitiesParentPartnerChildcareReason-TANF_TRAINING");
        testPage.clickContinue();

        //unearned-income-intro
        assertThat(testPage.getTitle()).isEqualTo("Unearned Income Intro");
    }
}
