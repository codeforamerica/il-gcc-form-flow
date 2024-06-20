package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;

public class ActivitiesJourneyTest extends AbstractBasePageTest {

    @Test
    void AddNameFirstNameLastName() throws IOException {
        testPage.navigateToFlowScreen("gcc/parent-info-intro");
        Submission submission = getSessionSubmissionTestBuilder().withDayCareProvider()
            .with("parentFirstName", "AnaBanana")
            .with("parentLastName", "Chibuisi")
            .build();
        saveSubmission(submission);

        List fieldsToTest = List.of("APPLICANT_NAME_FIRST", "APPLICANT_NAME_LAST");
        verifyMatchingFields(generatedPdfFieldsFromSubmission(submission), generateExpectedFields(Thread.currentThread().getStackTrace()[1].getMethodName()), fieldsToTest);

    }
    @Test
    void ParentOnlyWithJobAndWorkTest() throws IOException {
        // Activities Screen
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        Submission submission = getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child")
            .withChild("Second", "Child")
            .build();
        saveSubmission(submission);

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

        //Add Parent Jobs
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
        //activities-partner-add-job
        assertThat(testPage.getTitle()).isEqualTo("Activities Partner Add Jobs");
        assertThat(testPage.getHeader()).isEqualTo("Now, let's add partner's jobs.");
        assertThat(testPage.findElementById("header-help-message").getText()).isEqualTo("We will ask for their income and work schedule for each jobs.");
        testPage.clickButton("Add a job");
        //activities-partner-employer-name
        assertThat(testPage.getTitle()).isEqualTo("Activities Partner Employer Name");
        testPage.enter("companyName", "testPartnerCompany");
        testPage.clickContinue();
        //activities--partner-employer-address
        assertThat(testPage.getTitle()).isEqualTo("Activities Partner Employer Address");
        testPage.enter("employerPhoneNumber", "4444");
        testPage.enter("employerCity", "Oakland");
        testPage.enter("employerStreetAddress", "123 Partner Employer Address");
        testPage.enter("employerZipCode", "6042");
        testPage.clickContinue();
        assertThat(testPage.hasErrorText("Make sure the phone number you entered includes 10 digits.")).isTrue();
        assertThat(testPage.hasErrorText("Make sure the zip code you entered follows the right format.")).isTrue();
        testPage.enter("employerPhoneNumber", "4333333333");
        testPage.enter("employerZipCode", "92453");
        testPage.clickContinue();
        //activities-partner-self-employment
        assertThat(testPage.getTitle()).isEqualTo("Activities Partner Self Employment");
        testPage.clickButton("No");

        //check that selecting yes on activities-partner-work-schedule-vary screen send you to ...next-work-schedule
        // and no sends you to the ...job-weekly-schedule screen

        //activities-partner-work-schedule-vary
        assertThat(testPage.getTitle()).isEqualTo("Partner Work Schedule Varies");
        assertThat(testPage.getHeader()).isEqualTo("Do partner's work days or hours vary at this job?");
        testPage.clickButton("No");

        //activities-partner-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Partner Weekly Schedule");
        testPage.goBack();

        //activities-partner-work-schedule-vary
        assertThat(testPage.getTitle()).isEqualTo("Partner Work Schedule Varies");
        testPage.clickButton("Yes");

        //activities-partner-next-work-schedule
        assertThat(testPage.getTitle()).isEqualTo("Notice Partner Work Schedule Varies");
        assertThat(testPage.getHeader()).isEqualTo("Next, we'll ask about partner's work schedule");
        testPage.clickContinue();
        //activities-partner-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Partner Weekly Schedule");
        testPage.clickElementById("activitiesJobWeeklySchedule-Monday");
        testPage.clickElementById("activitiesJobWeeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-partner-job-hourly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Partner Hourly Schedule");
        testPage.enter("activitiesJobStartTimeMonday", "1200PM");
        testPage.enter("activitiesJobEndTimeMonday", "0100PM");
        testPage.enter("activitiesJobStartTimeSunday", "0200PM");
        testPage.enter("activitiesJobEndTimeSunday", "0300PM");
        testPage.clickContinue();

        //activities-partner-commute-time
        assertThat(testPage.getTitle()).isEqualTo("Activities Partner Commute Time");
        testPage.selectFromDropdown("activitiesJobCommuteTime", "1.5 hours");
        testPage.clickContinue();
        //activities-partner-add-job
        assertThat(testPage.getHeader()).isEqualTo("Does partner have any other jobs?");
        testPage.clickButton("That is all my jobs");
        //activities-partner-add-ed-program
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

        //activities-partner-add-job
        assertThat(testPage.getTitle()).isEqualTo("Activities Partner Add Jobs");
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
