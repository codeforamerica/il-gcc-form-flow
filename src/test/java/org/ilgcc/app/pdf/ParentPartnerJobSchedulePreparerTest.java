package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.TimeOption;
import org.ilgcc.app.utils.enums.CommuteTimeType;
import org.junit.jupiter.api.Test;

public class ParentPartnerJobSchedulePreparerTest {

    ParentPartnerJobSchedulePreparer preparer = new ParentPartnerJobSchedulePreparer();

    private Submission submission;

    @Test
    public void withTheSameScheduleEveryDay() {
        submission = new SubmissionTestBuilder()
            .withPartnerRegularWorkSchedule(List.of("Monday", "Thursday","Sunday"))
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("partnerEmployerScheduleMondayStart_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleMondayStart", "10:00", 1));
        assertThat(result.get("partnerEmployerScheduleMondayStartAmPm_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleMondayStartAmPm", "AM", 1));
        assertThat(result.get("partnerEmployerScheduleMondayEnd_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleMondayEnd", "03:45", 1));
        assertThat(result.get("partnerEmployerScheduleMondayEndAmPm_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleMondayEndAmPm", "PM", 1));

        assertThat(result.get("partnerEmployerScheduleTuesdayStart_1")).isEqualTo(null);
        assertThat(result.get("partnerEmployerScheduleTuesdayStartAmPm_1")).isEqualTo(null);
        assertThat(result.get("partnerEmployerScheduleTuesdayEnd_1")).isEqualTo(null);
        assertThat(result.get("partnerEmployerScheduleTuesdayEndAmPm_1")).isEqualTo(null);

        assertThat(result.get("partnerEmployerScheduleThursdayStart_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleThursdayStart", "10:00", 1));
        assertThat(result.get("partnerEmployerScheduleThursdayStartAmPm_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleThursdayStartAmPm", "AM", 1));
        assertThat(result.get("partnerEmployerScheduleThursdayEnd_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleThursdayEnd", "03:45", 1));
        assertThat(result.get("partnerEmployerScheduleThursdayEndAmPm_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleThursdayEndAmPm", "PM", 1));

        assertThat(result.get("partnerEmployerScheduleSundayStart_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleSundayStart", "10:00", 1));
        assertThat(result.get("partnerEmployerScheduleSundayStartAmPm_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleSundayStartAmPm", "AM", 1));
        assertThat(result.get("partnerEmployerScheduleSundayEnd_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleSundayEnd", "03:45", 1));
        assertThat(result.get("partnerEmployerScheduleSundayEndAmPm_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleSundayEndAmPm", "PM", 1));
    }

    @Test
    public void withDifferentScheduleEveryDay() {
        submission = new SubmissionTestBuilder()
            .withPartnerWorkScheduleByDay("Monday",TimeOption.TIME10AM, TimeOption.TIME345PM)
            .withPartnerWorkScheduleByDay("Wednesday",TimeOption.TIME8AM, TimeOption.TIME113PM)
            .withPartnerWorkScheduleByDay("Friday",TimeOption.TIME12PM, TimeOption.TIME7PM)
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("partnerEmployerScheduleMondayStart_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleMondayStart", "10:00", 1));
        assertThat(result.get("partnerEmployerScheduleMondayStartAmPm_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleMondayStartAmPm", "AM", 1));
        assertThat(result.get("partnerEmployerScheduleMondayEnd_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleMondayEnd", "03:45", 1));
        assertThat(result.get("partnerEmployerScheduleMondayEndAmPm_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleMondayEndAmPm", "PM", 1));

        assertThat(result.get("partnerEmployerScheduleTuesdayStart_1")).isEqualTo(null);
        assertThat(result.get("partnerEmployerScheduleTuesdayStartAmPm_1")).isEqualTo(null);
        assertThat(result.get("partnerEmployerScheduleTuesdayEnd_1")).isEqualTo(null);
        assertThat(result.get("partnerEmployerScheduleTuesdayEndAmPm_1")).isEqualTo(null);

        assertThat(result.get("partnerEmployerScheduleWednesdayStart_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleWednesdayStart", "08:00", 1));
        assertThat(result.get("partnerEmployerScheduleWednesdayStartAmPm_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleWednesdayStartAmPm", "AM", 1));
        assertThat(result.get("partnerEmployerScheduleWednesdayEnd_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleWednesdayEnd", "01:13", 1));
        assertThat(result.get("partnerEmployerScheduleWednesdayEndAmPm_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleWednesdayEndAmPm", "PM", 1));

        assertThat(result.get("partnerEmployerScheduleFridayStart_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleFridayStart", "12:00", 1));
        assertThat(result.get("partnerEmployerScheduleFridayStartAmPm_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleFridayStartAmPm", "PM", 1));
        assertThat(result.get("partnerEmployerScheduleFridayEnd_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleFridayEnd", "07:00", 1));
        assertThat(result.get("partnerEmployerScheduleFridayEndAmPm_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleFridayEndAmPm", "PM", 1));
    }

    @Test
    public void withTwoJobs() {
        submission = new SubmissionTestBuilder()
            .withPartnerRegularWorkSchedule(List.of("Monday"))
            .withPartnerRegularWorkScheduleAddHour(List.of("Monday", "Wednesday"), TimeOption.TIME8AM, TimeOption.TIME113PM)
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("partnerEmployerScheduleMondayStart_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleMondayStart", "10:00", 1));
        assertThat(result.get("partnerEmployerScheduleMondayStartAmPm_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleMondayStartAmPm", "AM", 1));
        assertThat(result.get("partnerEmployerScheduleMondayEnd_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleMondayEnd", "03:45", 1));
        assertThat(result.get("partnerEmployerScheduleMondayEndAmPm_1")).isEqualTo(
            new SingleField("partnerEmployerScheduleMondayEndAmPm", "PM", 1));

        assertThat(result.get("partnerEmployerScheduleMondayStart_2")).isEqualTo(
            new SingleField("partnerEmployerScheduleMondayStart", "08:00", 2));
        assertThat(result.get("partnerEmployerScheduleMondayStartAmPm_2")).isEqualTo(
            new SingleField("partnerEmployerScheduleMondayStartAmPm", "AM", 2));
        assertThat(result.get("partnerEmployerScheduleMondayEnd_2")).isEqualTo(
            new SingleField("partnerEmployerScheduleMondayEnd", "01:13", 2));
        assertThat(result.get("partnerEmployerScheduleMondayEndAmPm_2")).isEqualTo(
            new SingleField("partnerEmployerScheduleMondayEndAmPm", "PM", 2));

        assertThat(result.get("partnerEmployerScheduleWednesdayStart_1")).isEqualTo(null);
        assertThat(result.get("partnerEmployerScheduleWednesdayStartAmPm_1")).isEqualTo(null);
        assertThat(result.get("partnerEmployerScheduleWednesdayEnd_1")).isEqualTo(null);
        assertThat(result.get("partnerEmployerScheduleWednesdayEndAmPm_1")).isEqualTo(null);

        assertThat(result.get("partnerEmployerScheduleWednesdayStart_2")).isEqualTo(
            new SingleField("partnerEmployerScheduleWednesdayStart", "08:00", 2));
        assertThat(result.get("partnerEmployerScheduleWednesdayStartAmPm_2")).isEqualTo(
            new SingleField("partnerEmployerScheduleWednesdayStartAmPm", "AM", 2));
        assertThat(result.get("partnerEmployerScheduleWednesdayEnd_2")).isEqualTo(
            new SingleField("partnerEmployerScheduleWednesdayEnd", "01:13", 2));
        assertThat(result.get("partnerEmployerScheduleWednesdayEndAmPm_2")).isEqualTo(
            new SingleField("partnerEmployerScheduleWednesdayEndAmPm", "PM", 2));
    }

    @Test
    public void withCommute(){
        submission = new SubmissionTestBuilder()
            .regularPartnerScheduleWithCommuteTime(CommuteTimeType.HOUR_THIRTY.name())
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("partnerEmployerTravelTimeHours_1")).isEqualTo(
            new SingleField("partnerEmployerTravelTimeHours", "1", 1));
        assertThat(result.get("partnerEmployerTravelTimeMins_1")).isEqualTo(
            new SingleField("partnerEmployerTravelTimeMins", "30", 1));
        assertThat(result.get("partnerEmployerTravelTimeHours_2")).isEqualTo(null);
        assertThat(result.get("partnerEmployerTravelTimeMins_2")).isEqualTo(null);
    }

    @Test
    public void withMultipleCommutes(){
        submission = new SubmissionTestBuilder()
            .regularPartnerScheduleWithCommuteTime(CommuteTimeType.THREE_HOURS.name())
            .regularPartnerScheduleWithCommuteTime(CommuteTimeType.NO_MINUTES.name())
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("partnerEmployerTravelTimeHours_1")).isEqualTo(
            new SingleField("partnerEmployerTravelTimeHours", "3", 1));
        assertThat(result.get("partnerEmployerTravelTimeMins_1")).isEqualTo(
            new SingleField("partnerEmployerTravelTimeMins", "0", 1));
        assertThat(result.get("partnerEmployerTravelTimeHours_2")).isEqualTo(
            new SingleField("partnerEmployerTravelTimeHours", "0", 2));
        assertThat(result.get("partnerEmployerTravelTimeMins_2")).isEqualTo(
            new SingleField("partnerEmployerTravelTimeMins", "0", 2));
    }
}
