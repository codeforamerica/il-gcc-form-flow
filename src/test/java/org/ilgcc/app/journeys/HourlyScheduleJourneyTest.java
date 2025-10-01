package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class HourlyScheduleJourneyTest extends AbstractBasePageTest {


    @Nested
    class childcareTimeValidation {

        @Test
        void missingChildCareTimeRaisesError() {
            testPage.navigateToFlowScreen("gcc/providers-intro");

            saveSubmission(
                    getSessionSubmissionTestBuilder().withValidSubmissionUpTo5SchedulesIntro(List.of(child_1),
                                    List.of(new HashMap<>()))
                            .build());

            testPage.navigateToFlowScreen("gcc/schedules-intro");
            testPage.clickContinue();

            // schedules-start
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start.title"));
            testPage.clickContinue();

            // schedules-start-care
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start-care.title"));
            testPage.clickYes();

            //schedules-start-date
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start-date.title"));
            testPage.enter("ccapStartMonth", "12");
            testPage.enter("ccapStartDay", "15");
            testPage.enter("ccapStartYear", "2024");

            testPage.clickContinue();

            //schedules-days
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-days.title"));
            testPage.clickElementById("childcareWeeklySchedule-Thursday");
            testPage.clickContinue();

            //schedules-hours
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-hours.title"));
            testPage.clickContinue();

            assertThat(testPage.getElementText("childcareStartTimeThursday-error-message-1")
                    .equals(getEnMessage("errors.validate.time-hour")));
            assertThat(testPage.getElementText("childcareStartTimeThursday-error-message-2")
                    .equals(getEnMessage("errors.validate.time-ampm")));
            assertThat(testPage.elementDoesNotExistById("childcareStartTimeThursday-error-message-3")).isTrue();

            assertThat(testPage.getElementText("childcareEndTimeThursday-error-message-1")
                    .equals(getEnMessage("errors.validate.time-hour")));
            assertThat(testPage.getElementText("childcareEndTimeThursday-error-message-2")
                    .equals(getEnMessage("errors.validate.time-ampm")));
            assertThat(testPage.elementDoesNotExistById("childcareEndTimeThursday-error-message-3")).isTrue();

            testPage.selectFromDropdown("childcareEndTimeThursdayHour", "1");
            testPage.enter("childcareEndTimeThursdayMinute", "00");
            testPage.selectFromDropdown("childcareEndTimeThursdayAmPm", "PM");

            testPage.clickContinue();

            assertThat(testPage.getElementText("childcareStartTimeThursday-error-message-1")
                    .equals(getEnMessage("errors.validate.time-hour")));
            assertThat(testPage.getElementText("childcareStartTimeThursday-error-message-2")
                    .equals(getEnMessage("errors.validate.time-ampm")));
            assertThat(testPage.elementDoesNotExistById("childcareStartTimeThursday-error-message-3")).isTrue();

            assertThat(testPage.elementDoesNotExistById("childcareEndTimeThursday-error-message-1")).isTrue();
            assertThat(testPage.elementDoesNotExistById("childcareEndTimeThursday-error-message-2")).isTrue();
            assertThat(testPage.elementDoesNotExistById("childcareEndTimeThursday-error-message-3")).isTrue();

            testPage.selectFromDropdown("childcareStartTimeThursdayHour", "10");
            testPage.enter("childcareStartTimeThursdayMinute", "15");
            testPage.selectFromDropdown("childcareStartTimeThursdayAmPm", "AM");

            testPage.selectFromDropdown("childcareEndTimeThursdayHour", "1");
            testPage.enter("childcareEndTimeThursdayMinute", "00");
            testPage.selectFromDropdown("childcareEndTimeThursdayAmPm", "PM");

            testPage.clickContinue();

            // schedules-review
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-review.title"));
        }

        @Test
        void invalidChildCareTimeRaisesError() {
            testPage.navigateToFlowScreen("gcc/providers-intro");

            saveSubmission(
                    getSessionSubmissionTestBuilder().withValidSubmissionUpTo5SchedulesIntro(List.of(child_1),
                                    List.of(new HashMap<>()))
                            .build());

            testPage.navigateToFlowScreen("gcc/schedules-intro");
            testPage.clickContinue();

            // schedules-start
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start.title"));
            testPage.clickContinue();

            // schedules-start-care
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start-care.title"));
            testPage.clickYes();

            //schedules-start-date
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-start-date.title"));
            testPage.enter("ccapStartMonth", "12");
            testPage.enter("ccapStartDay", "15");
            testPage.enter("ccapStartYear", "2024");

            testPage.clickContinue();

            //schedules-days
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-days.title"));
            testPage.clickElementById("childcareWeeklySchedule-Thursday");
            testPage.clickContinue();

            //schedules-hours
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-hours.title"));

            testPage.selectFromDropdown("childcareStartTimeThursdayHour", "10");
            testPage.enter("childcareStartTimeThursdayMinute", "65");
            testPage.selectFromDropdown("childcareStartTimeThursdayAmPm", "AM");

            testPage.selectFromDropdown("childcareEndTimeThursdayHour", "1");
            testPage.enter("childcareEndTimeThursdayMinute", "00");
            testPage.selectFromDropdown("childcareEndTimeThursdayAmPm", "PM");

            testPage.clickContinue();

            assertThat(testPage.getElementText("childcareStartTimeThursday-error-message-1")
                    .equals(getEnMessage("errors.validate.minute")));
            assertThat(testPage.elementDoesNotExistById("childcareStartTimeThursday-error-message-2")).isTrue();
            assertThat(testPage.elementDoesNotExistById("childcareStartTimeThursday-error-message-3")).isTrue();

            assertThat(testPage.elementDoesNotExistById("childcareEndTimeThursday-error-message-1")).isTrue();
            assertThat(testPage.elementDoesNotExistById("childcareEndTimeThursday-error-message-2")).isTrue();
            assertThat(testPage.elementDoesNotExistById("childcareEndTimeThursday-error-message-3")).isTrue();

            testPage.selectFromDropdown("childcareStartTimeThursdayHour", "10");
            testPage.enter("childcareEndTimeThursdayMinute", "15");
            testPage.selectFromDropdown("childcareStartTimeThursdayAmPm", "AM");

            testPage.selectFromDropdown("childcareEndTimeThursdayHour", "1");
            testPage.enter("childcareEndTimeThursdayMinute", "00");
            testPage.selectFromDropdown("childcareEndTimeThursdayAmPm", "PM");

            testPage.clickContinue();

            // schedules-review
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("schedules-review.title"));
        }
    }

    @Test
    void missingParentJobTimeRaisesError() {
        testPage.navigateToFlowScreen("gcc/activities-add-jobs");

        saveSubmission(getSessionSubmissionTestBuilder().withValidSubmissionUpTo2ParentActivities()
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

        assertThat(testPage.getElementText("activitiesJobStartTimeMonday-error-message-1")
                .equals(getEnMessage("errors.validate.time-hour")));
        assertThat(testPage.getElementText("activitiesJobStartTimeMonday-error-message-2")
                .equals(getEnMessage("errors.validate.time-ampm")));
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeMonday-error-message-3")).isTrue();

        assertThat(testPage.getElementText("activitiesJobEndTimeMonday-error-message-1")
                .equals(getEnMessage("errors.validate.time-hour")));
        assertThat(testPage.getElementText("activitiesJobEndTimeMonday-error-message-2")
                .equals(getEnMessage("errors.validate.time-ampm")));
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeMonday-error-message-3")).isTrue();

        assertThat(testPage.getElementText("activitiesJobStartTimeSunday-error-message-1")
                .equals(getEnMessage("errors.validate.time-hour")));
        assertThat(testPage.getElementText("activitiesJobStartTimeSunday-error-message-2")
                .equals(getEnMessage("errors.validate.time-ampm")));
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeSunday-error-message-3")).isTrue();

        assertThat(testPage.getElementText("activitiesJobEndTimeSunday-error-message-1")
                .equals(getEnMessage("errors.validate.time-hour")));
        assertThat(testPage.getElementText("activitiesJobEndTimeSunday-error-message-2")
                .equals(getEnMessage("errors.validate.time-ampm")));
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeSunday-error-message-3")).isTrue();

        testPage.selectFromDropdown("activitiesJobStartTimeMondayHour", "12");
        testPage.enter("activitiesJobStartTimeMondayMinute", "00");
        testPage.selectFromDropdown("activitiesJobStartTimeMondayAmPm", "PM");

        testPage.selectFromDropdown("activitiesJobEndTimeMondayHour", "1");
        testPage.enter("activitiesJobEndTimeMondayMinute", "00");
        testPage.selectFromDropdown("activitiesJobEndTimeMondayAmPm", "PM");

        testPage.clickContinue();

        // Errors triggered by missing data on Sunday
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeMonday-error-message-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeMonday-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeMonday-error-message-3")).isTrue();

        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeMonday-error-message-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeMonday-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeMonday-error-message-3")).isTrue();

        assertThat(testPage.getElementText("activitiesJobStartTimeSunday-error-message-1")
                .equals(getEnMessage("errors.validate.time-hour")));
        assertThat(testPage.getElementText("activitiesJobStartTimeSunday-error-message-2")
                .equals(getEnMessage("errors.validate.time-ampm")));
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeSunday-error-message-3")).isTrue();

        assertThat(testPage.getElementText("activitiesJobEndTimeSunday-error-message-1")
                .equals(getEnMessage("errors.validate.time-hour")));
        assertThat(testPage.getElementText("activitiesJobEndTimeSunday-error-message-2")
                .equals(getEnMessage("errors.validate.time-ampm")));
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeSunday-error-message-3")).isTrue();

        testPage.selectFromDropdown("activitiesJobStartTimeSundayHour", "2");
        testPage.enter("activitiesJobStartTimeSundayMinute", "00");

        testPage.clickContinue();

        // Errors Triggered by missing Sunday AM.PM
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeMonday-error-message-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeMonday-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeMonday-error-message-3")).isTrue();

        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeMonday-error-message-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeMonday-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeMonday-error-message-3")).isTrue();

        assertThat(testPage.getElementText("activitiesJobStartTimeSunday-error-message-1")
                .equals(getEnMessage("errors.validate.time-ampm")));
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeSunday-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeSunday-error-message-3")).isTrue();

        assertThat(testPage.getElementText("activitiesJobEndTimeSunday-error-message-1")
                .equals(getEnMessage("errors.validate.time-hour")));
        assertThat(testPage.getElementText("activitiesJobEndTimeSunday-error-message-2")
                .equals(getEnMessage("errors.validate.time-ampm")));
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeSunday-error-message-3")).isTrue();

        testPage.selectFromDropdown("activitiesJobStartTimeSundayAmPm", "PM");

        testPage.selectFromDropdown("activitiesJobEndTimeSundayHour", "3");
        testPage.enter("activitiesJobEndTimeSundayMinute", "-1");
        testPage.selectFromDropdown("activitiesJobEndTimeSundayAmPm", "PM");
        testPage.clickContinue();

        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeMonday-error-message-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeMonday-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeMonday-error-message-3")).isTrue();

        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeMonday-error-message-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeMonday-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeMonday-error-message-3")).isTrue();

        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeSunday-error-message-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeSunday-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeSunday-error-message-3")).isTrue();

        assertThat(testPage.getElementText("activitiesJobEndTimeSunday-error-message-1")
                .equals(getEnMessage("errors.validate.minute")));
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeSunday-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeSunday-error-message-3")).isTrue();

        testPage.enter("activitiesJobEndTimeSundayMinute", "1");
        testPage.clickContinue();

        //activities-work-commute-time
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-work-commute-time.title"));
    }

    @Test
    void invalidParentJobTimeRaisesError() {
        testPage.navigateToFlowScreen("gcc/activities-add-jobs");

        saveSubmission(getSessionSubmissionTestBuilder().withValidSubmissionUpTo2ParentActivities()
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
        testPage.enter("activitiesJobEndTimeMondayMinute", "66");
        testPage.selectFromDropdown("activitiesJobEndTimeMondayAmPm", "PM");

        testPage.clickContinue();

        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeMonday-error-message-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeMonday-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeMonday-error-message-3")).isTrue();

        assertThat(testPage.getElementText("activitiesJobEndTimeMonday-error-message-1")
                .equals(getEnMessage("errors.validate.minute")));
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeMonday-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeMonday-error-message-3")).isTrue();

        testPage.enter("activitiesJobEndTimeMondayMinute", "10");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-work-commute-time.title"));

        testPage.goBack();
        testPage.enter("activitiesJobStartTimeMondayMinute", "-50");

        testPage.clickContinue();

        assertThat(testPage.getElementText("activitiesJobStartTimeMonday-error-message-1")
                .equals(getEnMessage("errors.validate.minute")));
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeMonday-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeMonday-error-message-3")).isTrue();

        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeMonday-error-message-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeMonday-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeMonday-error-message-3")).isTrue();

        testPage.enter("activitiesJobStartTimeMondayMinute", "50");
        testPage.clickContinue();

        //activities-work-commute-time
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-work-commute-time.title"));
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

        testPage.selectFromDropdown("activitiesClassStartTimeAllDaysHour", "9");
        testPage.enter("activitiesClassStartTimeAllDaysMinute", "00");
        testPage.selectFromDropdown("activitiesClassStartTimeAllDaysAmPm", "AM");

        testPage.clickContinue();

        assertThat(testPage.elementDoesNotExistById("activitiesClassStartTimeAllDays-error-message-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesClassStartTimeAllDays-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesClassStartTimeAllDays-error-message-3")).isTrue();

        assertThat(testPage.getElementText("activitiesClassEndTimeAllDays-error-message-1")
                .equals(getEnMessage("errors.validate.time-hour")));
        assertThat(testPage.getElementText("activitiesClassEndTimeAllDays-error-message-2")
                .equals(getEnMessage("errors.validate.time-ampm")));
        assertThat(testPage.elementDoesNotExistById("activitiesClassEndTimeAllDays-error-message-3")).isTrue();

        testPage.selectFromDropdown("activitiesClassEndTimeAllDaysHour", "1");
        testPage.enter("activitiesClassEndTimeAllDaysMinute", "00");
        testPage.selectFromDropdown("activitiesClassEndTimeAllDaysAmPm", "PM");

        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-class-commute-time.title"));
        testPage.clickContinue();

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
        testPage.enter("activitiesClassStartTimeAllDaysMinute", "65");
        testPage.selectFromDropdown("activitiesClassStartTimeAllDaysAmPm", "AM");

        testPage.clickContinue();

        assertThat(testPage.getElementText("activitiesClassStartTimeAllDays-error-message-1")
                .equals(getEnMessage("errors.validate.minute")));
        assertThat(testPage.elementDoesNotExistById("activitiesClassStartTimeAllDays-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesClassStartTimeAllDays-error-message-3")).isTrue();

        assertThat(testPage.getElementText("activitiesClassEndTimeAllDays-error-message-1")
                .equals(getEnMessage("errors.validate.time-hour")));
        assertThat(testPage.getElementText("activitiesClassEndTimeAllDays-error-message-2")
                .equals(getEnMessage("errors.validate.time-ampm")));
        assertThat(testPage.elementDoesNotExistById("activitiesClassEndTimeAllDays-error-message-3")).isTrue();

        testPage.selectFromDropdown("activitiesClassEndTimeAllDaysHour", "1");
        testPage.enter("activitiesClassEndTimeAllDaysMinute", "00");
        testPage.selectFromDropdown("activitiesClassEndTimeAllDaysAmPm", "PM");

        testPage.clickContinue();

        assertThat(testPage.getElementText("activitiesClassStartTimeAllDays-error-message-1")
                .equals(getEnMessage("errors.validate.minute")));
        assertThat(testPage.elementDoesNotExistById("activitiesClassStartTimeAllDays-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesClassStartTimeAllDays-error-message-3")).isTrue();

        assertThat(testPage.elementDoesNotExistById("activitiesClassEndTimeAllDays-error-message-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesClassEndTimeAllDays-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesClassEndTimeAllDays-error-message-3")).isTrue();

        testPage.enter("activitiesClassStartTimeAllDaysMinute", "55");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-class-commute-time.title"));
        testPage.clickContinue();

        //activities-ed-program-dates
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-dates.title"));

        testPage.goBack();
        testPage.goBack();
        testPage.enter("activitiesClassEndTimeAllDaysMinute", "75");
        testPage.clickContinue();

        assertThat(testPage.elementDoesNotExistById("activitiesClassStartTimeAllDays-error-message-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesClassStartTimeAllDays-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesClassStartTimeAllDays-error-message-3")).isTrue();

        assertThat(testPage.getElementText("activitiesClassEndTimeAllDays-error-message-1")
                .equals(getEnMessage("errors.validate.minute")));
        assertThat(testPage.elementDoesNotExistById("activitiesClassEndTimeAllDays-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesClassEndTimeAllDays-error-message-3")).isTrue();

        testPage.enter("activitiesClassEndTimeAllDaysMinute", "12");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-class-commute-time.title"));
        testPage.clickContinue();

        //activities-ed-program-dates
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-dates.title"));
    }

    @Test
    void missingParentPartnerJobTimeRaisesError() {
        testPage.navigateToFlowScreen("gcc/activities-partner-add-job");
        saveSubmission(getSessionSubmissionTestBuilder().withValidSubmissionUpTo2ParentActivities().withParentPartnerDetails().with(
                        "parentHasPartner", "true")
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

        assertThat(testPage.getElementText("activitiesJobStartTimeAllDays-error-message-1")
                .equals(getEnMessage("errors.validate.time-hour")));
        assertThat(testPage.getElementText("activitiesJobStartTimeAllDays-error-message-2")
                .equals(getEnMessage("errors.validate.time-ampm")));
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeAllDays-error-message-3")).isTrue();

        assertThat(testPage.getElementText("activitiesJobEndTimeAllDays-error-message-1")
                .equals(getEnMessage("errors.validate.time-hour")));
        assertThat(testPage.getElementText("activitiesJobEndTimeAllDays-error-message-2")
                .equals(getEnMessage("errors.validate.time-ampm")));
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeAllDays-error-message-3")).isTrue();

        testPage.selectFromDropdown("activitiesJobStartTimeAllDaysHour", "9");
        testPage.enter("activitiesJobStartTimeAllDaysMinute", "00");
        testPage.selectFromDropdown("activitiesJobStartTimeAllDaysAmPm", "PM");

        testPage.clickContinue();

        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeAllDays-error-message-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeAllDays-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeAllDays-error-message-3")).isTrue();

        assertThat(testPage.getElementText("activitiesJobEndTimeAllDays-error-message-1")
                .equals(getEnMessage("errors.validate.time-hour")));
        assertThat(testPage.getElementText("activitiesJobEndTimeAllDays-error-message-2")
                .equals(getEnMessage("errors.validate.time-ampm")));
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeAllDays-error-message-3")).isTrue();

        testPage.selectFromDropdown("activitiesJobEndTimeAllDaysHour", "1");
        testPage.enter("activitiesJobEndTimeAllDaysMinute", "00");
        testPage.selectFromDropdown("activitiesJobEndTimeAllDaysAmPm", "PM");

        testPage.clickContinue();

        //activities-partner-commute-time
        testPage.selectFromDropdown("activitiesJobCommuteTime", getEnMessage("general.hours.1.5.hours"));
        testPage.clickContinue();
    }

    @Test
    void invalidParentPartnerJobTimeRaisesError() {
        testPage.navigateToFlowScreen("gcc/activities-partner-add-job");
        saveSubmission(getSessionSubmissionTestBuilder().withValidSubmissionUpTo2ParentActivities().withParentPartnerDetails().with(
                        "parentHasPartner", "true")
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
        testPage.enter("activitiesJobStartTimeAllDaysMinute", "78");
        testPage.selectFromDropdown("activitiesJobStartTimeAllDaysAmPm", "PM");

        testPage.selectFromDropdown("activitiesJobEndTimeAllDaysHour", "1");
        testPage.enter("activitiesJobEndTimeAllDaysMinute", "00");
        testPage.selectFromDropdown("activitiesJobEndTimeAllDaysAmPm", "PM");

        testPage.clickContinue();

        assertThat(testPage.getElementText("activitiesJobStartTimeAllDays-error-message-1")
                .equals(getEnMessage("errors.validate.minute")));
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeAllDays-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeAllDays-error-message-3")).isTrue();

        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeAllDays-error-message-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeAllDays-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeAllDays-error-message-3")).isTrue();

        testPage.enter("activitiesJobStartTimeAllDaysMinute", "22");
        testPage.clickContinue();
        //activities-partner-commute-time
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-commute-time.title"));

        testPage.goBack();

        testPage.enter("activitiesJobEndTimeAllDaysMinute", "-23");
        testPage.clickContinue();

        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeAllDays-error-message-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeAllDays-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobStartTimeAllDays-error-message-3")).isTrue();

        assertThat(testPage.getElementText("activitiesJobEndTimeAllDays-error-message-1")
                .equals(getEnMessage("errors.validate.minute")));
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeAllDays-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("activitiesJobEndTimeAllDays-error-message-3")).isTrue();

        testPage.enter("activitiesJobEndTimeAllDaysMinute", "23");
        testPage.clickContinue();

        //activities-partner-commute-time
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-commute-time.title"));
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

        assertThat(testPage.getElementText("partnerClassStartTimeAllDays-error-message-1")
                .equals(getEnMessage("errors.validate.time-hour")));
        assertThat(testPage.getElementText("partnerClassStartTimeAllDays-error-message-2")
                .equals(getEnMessage("errors.validate.time-ampm")));
        assertThat(testPage.elementDoesNotExistById("partnerClassStartTimeAllDays-error-message-3")).isTrue();

        assertThat(testPage.getElementText("partnerClassEndTimeAllDays-error-message-1")
                .equals(getEnMessage("errors.validate.time-hour")));
        assertThat(testPage.getElementText("partnerClassEndTimeAllDays-error-message-2")
                .equals(getEnMessage("errors.validate.time-ampm")));
        assertThat(testPage.elementDoesNotExistById("partnerClassEndTimeAllDays-error-message-3")).isTrue();

        testPage.selectFromDropdown("partnerClassStartTimeAllDaysHour", "11");
        testPage.enter("partnerClassStartTimeAllDaysMinute", "00");
        testPage.selectFromDropdown("partnerClassStartTimeAllDaysAmPm", "AM");

        testPage.clickContinue();
        assertThat(testPage.elementDoesNotExistById("partnerClassStartTimeAllDays-error-message-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("partnerClassStartTimeAllDays-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("partnerClassStartTimeAllDays-error-message-3")).isTrue();

        assertThat(testPage.getElementText("partnerClassEndTimeAllDays-error-message-1")
                .equals(getEnMessage("errors.validate.time-hour")));
        assertThat(testPage.getElementText("partnerClassEndTimeAllDays-error-message-2")
                .equals(getEnMessage("errors.validate.time-ampm")));
        assertThat(testPage.elementDoesNotExistById("partnerClassEndTimeAllDays-error-message-3")).isTrue();

        testPage.selectFromDropdown("partnerClassEndTimeAllDaysHour", "4");
        testPage.enter("partnerClassEndTimeAllDaysMinute", "00");
        testPage.selectFromDropdown("partnerClassEndTimeAllDaysAmPm", "PM");

        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-class-commute-time.title"));
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

        assertThat(testPage.getElementText("partnerClassStartTimeAllDays-error-message-1")
                .equals(getEnMessage("errors.validate.minute")));
        assertThat(testPage.elementDoesNotExistById("partnerClassStartTimeAllDays-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("partnerClassStartTimeAllDays-error-message-3")).isTrue();

        assertThat(testPage.elementDoesNotExistById("partnerClassEndTimeAllDays-error-message-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("partnerClassEndTimeAllDays-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("partnerClassEndTimeAllDays-error-message-3")).isTrue();

        testPage.clickContinue();

        testPage.enter("partnerClassStartTimeAllDaysMinute", "55");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-class-commute-time.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-dates.title"));

        testPage.goBack();
        testPage.goBack();

        testPage.enter("partnerClassEndTimeAllDaysMinute", "-22");
        testPage.clickContinue();
        assertThat(testPage.elementDoesNotExistById("partnerClassStartTimeAllDays-error-message-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("partnerClassStartTimeAllDays-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("partnerClassStartTimeAllDays-error-message-3")).isTrue();

        assertThat(testPage.getElementText("partnerClassEndTimeAllDays-error-message-1")
                .equals(getEnMessage("errors.validate.minute")));
        assertThat(testPage.elementDoesNotExistById("partnerClassEndTimeAllDays-error-message-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("partnerClassEndTimeAllDays-error-message-3")).isTrue();

        testPage.enter("partnerClassEndTimeAllDaysMinute", "22");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-partner-class-commute-time.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-ed-program-dates.title"));
    }

    void addParentSchool() {
        // activities-parent-intro
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withValidSubmissionUpTo2ParentActivities()
                .build());

        testPage.clickContinue();

        // activities-parent-type
        testPage.clickElementById("activitiesParentChildcareReason-SCHOOL-label");
        testPage.clickContinue();

        //activities-add-ed-program
        testPage.clickContinue();

        //parent-info-bachelors
        testPage.clickYes();

        //activities-ed-program-type
        testPage.clickElementById("educationType-highSchoolOrGed-label");

        // activities-ed-program
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

    void addParentPartnerSchool() {
        // activities-parent-intro
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");
        saveSubmission(getSessionSubmissionTestBuilder().withValidSubmissionUpTo2ParentActivities().withParentPartnerDetails().with(
                "parentHasPartner", "true")
                .build());

        testPage.clickContinue();
        // activities-parent-type
        testPage.clickElementById("activitiesParentChildcareReason-TANF_TRAINING-label");
        testPage.clickElementById("activitiesParentPartnerChildcareReason-SCHOOL-label");
        testPage.clickContinue();

        //activities-add-ed-program
        testPage.clickContinue();

        //parent-info-bachelors
        testPage.clickElementById("partnerHasBachelorsDegree-skip");

        //
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
