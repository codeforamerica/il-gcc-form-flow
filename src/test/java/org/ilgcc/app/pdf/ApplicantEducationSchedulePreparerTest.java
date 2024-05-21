package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

public class ApplicantEducationSchedulePreparerTest {

    ApplicantEducationSchedulePreparer preparer = new ApplicantEducationSchedulePreparer();

    private Submission submission;

    @Test
    public void withTheSameScheduleEveryDay() {
        submission = new SubmissionTestBuilder()
            .withRegularSchoolSchedule(List.of("Monday", "Thursday","Sunday"),"10:00", "15:45")
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("applicantEducationScheduleMondayStart")).isEqualTo(
            new SingleField("applicantEducationScheduleMondayStart", "10:00", null));
        assertThat(result.get("applicantEducationScheduleMondayStartAmPm")).isEqualTo(
            new SingleField("applicantEducationScheduleMondayStartAmPm", "AM", null));
        assertThat(result.get("applicantEducationScheduleMondayEnd")).isEqualTo(
            new SingleField("applicantEducationScheduleMondayEnd", "03:45", null));
        assertThat(result.get("applicantEducationScheduleMondayEndAmPm")).isEqualTo(
            new SingleField("applicantEducationScheduleMondayEndAmPm", "PM", null));

        assertThat(result.get("applicantEducationScheduleTuesdayStart")).isEqualTo(null);
        assertThat(result.get("applicantEducationScheduleTuesdayStartAmPm")).isEqualTo(null);
        assertThat(result.get("applicantEducationScheduleTuesdayEnd")).isEqualTo(null);
        assertThat(result.get("applicantEducationScheduleTuesdayEndAmPm")).isEqualTo(null);

        assertThat(result.get("applicantEducationScheduleThursdayStart")).isEqualTo(
            new SingleField("applicantEducationScheduleThursdayStart", "10:00", null));
        assertThat(result.get("applicantEducationScheduleThursdayStartAmPm")).isEqualTo(
            new SingleField("applicantEducationScheduleThursdayStartAmPm", "AM", null));
        assertThat(result.get("applicantEducationScheduleThursdayEnd")).isEqualTo(
            new SingleField("applicantEducationScheduleThursdayEnd", "03:45", null));
        assertThat(result.get("applicantEducationScheduleThursdayEndAmPm")).isEqualTo(
            new SingleField("applicantEducationScheduleThursdayEndAmPm", "PM", null));

        assertThat(result.get("applicantEducationScheduleSundayStart")).isEqualTo(
            new SingleField("applicantEducationScheduleSundayStart", "10:00", null));
        assertThat(result.get("applicantEducationScheduleSundayStartAmPm")).isEqualTo(
            new SingleField("applicantEducationScheduleSundayStartAmPm", "AM", null));
        assertThat(result.get("applicantEducationScheduleSundayEnd")).isEqualTo(
            new SingleField("applicantEducationScheduleSundayEnd", "03:45", null));
        assertThat(result.get("applicantEducationScheduleSundayEndAmPm")).isEqualTo(
            new SingleField("applicantEducationScheduleSundayEndAmPm", "PM", null));
    }

    @Test
    public void withDifferentScheduleEveryDay() {
        submission = new SubmissionTestBuilder()
            .withSchoolScheduleByDay("Monday","10:00", "15:45")
            .withSchoolScheduleByDay("Wednesday","08:00", "12:45")
            .withSchoolScheduleByDay("Friday","12:00", "19:00")
            .with("weeklySchedule[]", List.of("Monday", "Wednesday", "Friday"))
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("applicantEducationScheduleMondayStart")).isEqualTo(
            new SingleField("applicantEducationScheduleMondayStart", "10:00", null));
        assertThat(result.get("applicantEducationScheduleMondayStartAmPm")).isEqualTo(
            new SingleField("applicantEducationScheduleMondayStartAmPm", "AM", null));
        assertThat(result.get("applicantEducationScheduleMondayEnd")).isEqualTo(
            new SingleField("applicantEducationScheduleMondayEnd", "03:45", null));
        assertThat(result.get("applicantEducationScheduleMondayEndAmPm")).isEqualTo(
            new SingleField("applicantEducationScheduleMondayEndAmPm", "PM", null));

        assertThat(result.get("applicantEducationScheduleTuesdayStart")).isEqualTo(null);
        assertThat(result.get("applicantEducationScheduleTuesdayStartAmPm")).isEqualTo(null);
        assertThat(result.get("applicantEducationScheduleTuesdayEnd")).isEqualTo(null);
        assertThat(result.get("applicantEducationScheduleTuesdayEndAmPm")).isEqualTo(null);

        assertThat(result.get("applicantEducationScheduleWednesdayStart")).isEqualTo(
            new SingleField("applicantEducationScheduleWednesdayStart", "08:00", null));
        assertThat(result.get("applicantEducationScheduleWednesdayStartAmPm")).isEqualTo(
            new SingleField("applicantEducationScheduleWednesdayStartAmPm", "AM", null));
        assertThat(result.get("applicantEducationScheduleWednesdayEnd")).isEqualTo(
            new SingleField("applicantEducationScheduleWednesdayEnd", "12:45", null));
        assertThat(result.get("applicantEducationScheduleWednesdayEndAmPm")).isEqualTo(
            new SingleField("applicantEducationScheduleWednesdayEndAmPm", "PM", null));

        assertThat(result.get("applicantEducationScheduleFridayStart")).isEqualTo(
            new SingleField("applicantEducationScheduleFridayStart", "12:00", null));
        assertThat(result.get("applicantEducationScheduleFridayStartAmPm")).isEqualTo(
            new SingleField("applicantEducationScheduleFridayStartAmPm", "PM", null));
        assertThat(result.get("applicantEducationScheduleFridayEnd")).isEqualTo(
            new SingleField("applicantEducationScheduleFridayEnd", "07:00", null));
        assertThat(result.get("applicantEducationScheduleFridayEndAmPm")).isEqualTo(
            new SingleField("applicantEducationScheduleFridayEndAmPm", "PM", null));
    }
}
