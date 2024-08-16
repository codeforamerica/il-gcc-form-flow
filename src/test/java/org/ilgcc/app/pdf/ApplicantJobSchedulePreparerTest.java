package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.enums.CommuteTimeType;
import org.junit.jupiter.api.Test;

public class ApplicantJobSchedulePreparerTest {

    ApplicantJobSchedulePreparer preparer = new ApplicantJobSchedulePreparer();

    private Submission submission;

    @Test
    public void withTheSameScheduleEveryDay() {
        submission = new SubmissionTestBuilder()
            .withRegularWorkSchedule(List.of("Monday", "Thursday","Sunday"))
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("applicantEmployerScheduleMondayStart_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleMondayStart", "10:00", 1));
        assertThat(result.get("applicantEmployerScheduleMondayStartAmPm_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleMondayStartAmPm", "AM", 1));
        assertThat(result.get("applicantEmployerScheduleMondayEnd_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleMondayEnd", "03:45", 1));
        assertThat(result.get("applicantEmployerScheduleMondayEndAmPm_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleMondayEndAmPm", "PM", 1));

        assertThat(result.get("applicantEmployerScheduleTuesdayStart_1")).isEqualTo(null);
        assertThat(result.get("applicantEmployerScheduleTuesdayStartAmPm_1")).isEqualTo(null);
        assertThat(result.get("applicantEmployerScheduleTuesdayEnd_1")).isEqualTo(null);
        assertThat(result.get("applicantEmployerScheduleTuesdayEndAmPm_1")).isEqualTo(null);

        assertThat(result.get("applicantEmployerScheduleThursdayStart_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleThursdayStart", "10:00", 1));
        assertThat(result.get("applicantEmployerScheduleThursdayStartAmPm_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleThursdayStartAmPm", "AM", 1));
        assertThat(result.get("applicantEmployerScheduleThursdayEnd_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleThursdayEnd", "03:45", 1));
        assertThat(result.get("applicantEmployerScheduleThursdayEndAmPm_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleThursdayEndAmPm", "PM", 1));

        assertThat(result.get("applicantEmployerScheduleSundayStart_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleSundayStart", "10:00", 1));
        assertThat(result.get("applicantEmployerScheduleSundayStartAmPm_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleSundayStartAmPm", "AM", 1));
        assertThat(result.get("applicantEmployerScheduleSundayEnd_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleSundayEnd", "03:45", 1));
        assertThat(result.get("applicantEmployerScheduleSundayEndAmPm_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleSundayEndAmPm", "PM", 1));
    }

    @Test
    public void withDifferentScheduleEveryDay() {
        submission = new SubmissionTestBuilder()
            .withWorkScheduleByDay("Monday","10","0", "AM", "3","45","PM")
            .withWorkScheduleByDay("Wednesday","8", "0", "AM", "12", "45", "PM")
            .withWorkScheduleByDay("Friday","12", "0","PM", "7","0","PM")
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("applicantEmployerScheduleMondayStart_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleMondayStart", "10:00", 1));
        assertThat(result.get("applicantEmployerScheduleMondayStartAmPm_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleMondayStartAmPm", "AM", 1));
        assertThat(result.get("applicantEmployerScheduleMondayEnd_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleMondayEnd", "03:45", 1));
        assertThat(result.get("applicantEmployerScheduleMondayEndAmPm_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleMondayEndAmPm", "PM", 1));

        assertThat(result.get("applicantEmployerScheduleTuesdayStart_1")).isEqualTo(null);
        assertThat(result.get("applicantEmployerScheduleTuesdayStartAmPm_1")).isEqualTo(null);
        assertThat(result.get("applicantEmployerScheduleTuesdayEnd_1")).isEqualTo(null);
        assertThat(result.get("applicantEmployerScheduleTuesdayEndAmPm_1")).isEqualTo(null);

        assertThat(result.get("applicantEmployerScheduleWednesdayStart_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleWednesdayStart", "08:00", 1));
        assertThat(result.get("applicantEmployerScheduleWednesdayStartAmPm_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleWednesdayStartAmPm", "AM", 1));
        assertThat(result.get("applicantEmployerScheduleWednesdayEnd_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleWednesdayEnd", "12:45", 1));
        assertThat(result.get("applicantEmployerScheduleWednesdayEndAmPm_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleWednesdayEndAmPm", "PM", 1));

        assertThat(result.get("applicantEmployerScheduleFridayStart_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleFridayStart", "12:00", 1));
        assertThat(result.get("applicantEmployerScheduleFridayStartAmPm_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleFridayStartAmPm", "PM", 1));
        assertThat(result.get("applicantEmployerScheduleFridayEnd_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleFridayEnd", "07:00", 1));
        assertThat(result.get("applicantEmployerScheduleFridayEndAmPm_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleFridayEndAmPm", "PM", 1));
    }

    @Test
    public void withTwoJobs() {
        submission = new SubmissionTestBuilder()
            .withRegularWorkSchedule(List.of("Monday"))
            .withRegularWorkScheduleAddHour(List.of("Monday", "Wednesday"),"8","0", "AM", "12", "45", "PM")
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("applicantEmployerScheduleMondayStart_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleMondayStart", "10:00", 1));
        assertThat(result.get("applicantEmployerScheduleMondayStartAmPm_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleMondayStartAmPm", "AM", 1));
        assertThat(result.get("applicantEmployerScheduleMondayEnd_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleMondayEnd", "03:45", 1));
        assertThat(result.get("applicantEmployerScheduleMondayEndAmPm_1")).isEqualTo(
            new SingleField("applicantEmployerScheduleMondayEndAmPm", "PM", 1));

        assertThat(result.get("applicantEmployerScheduleMondayStart_2")).isEqualTo(
            new SingleField("applicantEmployerScheduleMondayStart", "08:00", 2));
        assertThat(result.get("applicantEmployerScheduleMondayStartAmPm_2")).isEqualTo(
            new SingleField("applicantEmployerScheduleMondayStartAmPm", "AM", 2));
        assertThat(result.get("applicantEmployerScheduleMondayEnd_2")).isEqualTo(
            new SingleField("applicantEmployerScheduleMondayEnd", "12:45", 2));
        assertThat(result.get("applicantEmployerScheduleMondayEndAmPm_2")).isEqualTo(
            new SingleField("applicantEmployerScheduleMondayEndAmPm", "PM", 2));

        assertThat(result.get("applicantEmployerScheduleWednesdayStart_1")).isEqualTo(null);
        assertThat(result.get("applicantEmployerScheduleWednesdayStartAmPm_1")).isEqualTo(null);
        assertThat(result.get("applicantEmployerScheduleWednesdayEnd_1")).isEqualTo(null);
        assertThat(result.get("applicantEmployerScheduleWednesdayEndAmPm_1")).isEqualTo(null);

        assertThat(result.get("applicantEmployerScheduleWednesdayStart_2")).isEqualTo(
            new SingleField("applicantEmployerScheduleWednesdayStart", "08:00", 2));
        assertThat(result.get("applicantEmployerScheduleWednesdayStartAmPm_2")).isEqualTo(
            new SingleField("applicantEmployerScheduleWednesdayStartAmPm", "AM", 2));
        assertThat(result.get("applicantEmployerScheduleWednesdayEnd_2")).isEqualTo(
            new SingleField("applicantEmployerScheduleWednesdayEnd", "12:45", 2));
        assertThat(result.get("applicantEmployerScheduleWednesdayEndAmPm_2")).isEqualTo(
            new SingleField("applicantEmployerScheduleWednesdayEndAmPm", "PM", 2));
    }

    @Test
    public void withCommute(){
        submission = new SubmissionTestBuilder()
            .regularWorkScheduleWithCommuteTime(CommuteTimeType.HOUR_THIRTY.name())
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("applicantEmployerTravelTimeHours_1")).isEqualTo(
            new SingleField("applicantEmployerTravelTimeHours", "1", 1));
        assertThat(result.get("applicantEmployerTravelTimeMins_1")).isEqualTo(
            new SingleField("applicantEmployerTravelTimeMins", "30", 1));
        assertThat(result.get("applicantEmployerTravelTimeHours_2")).isEqualTo(null);
        assertThat(result.get("applicantEmployerTravelTimeMins_2")).isEqualTo(null);
    }

    @Test
    public void withMultipleCommutes(){
        submission = new SubmissionTestBuilder()
            .regularWorkScheduleWithCommuteTime(CommuteTimeType.TWO_HOURS.name())
            .regularWorkScheduleWithCommuteTime(CommuteTimeType.HOUR_THIRTY.name())
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("applicantEmployerTravelTimeHours_1")).isEqualTo(
            new SingleField("applicantEmployerTravelTimeHours", "2", 1));
        assertThat(result.get("applicantEmployerTravelTimeMins_1")).isEqualTo(
            new SingleField("applicantEmployerTravelTimeMins", "0", 1));
        assertThat(result.get("applicantEmployerTravelTimeHours_2")).isEqualTo(
            new SingleField("applicantEmployerTravelTimeHours", "1", 2));
        assertThat(result.get("applicantEmployerTravelTimeMins_2")).isEqualTo(
            new SingleField("applicantEmployerTravelTimeMins", "30", 2));
    }

}
