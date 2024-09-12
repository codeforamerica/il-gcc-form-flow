package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.TimeOption;
import org.junit.jupiter.api.Test;

public class ApplicantEducationSchedulePreparerTest {

    ApplicantEducationSchedulePreparer preparer = new ApplicantEducationSchedulePreparer();

    private Submission submission;

    @Test
    public void withTheSameScheduleEveryDay() {
        submission = new SubmissionTestBuilder()
                .withRegularSchoolSchedule("activitiesClass", "weeklySchedule[]", List.of("Monday", "Thursday", "Sunday"))
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
                .withSchoolScheduleByDay("activitiesClass", "Monday", TimeOption.TIME10AM, TimeOption.TIME345PM)
                .withSchoolScheduleByDay("activitiesClass", "Wednesday", TimeOption.TIME8AM, TimeOption.TIME310PM)
                .withSchoolScheduleByDay("activitiesClass", "Friday", TimeOption.TIME12PM, TimeOption.TIME7PM)
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
                new SingleField("applicantEducationScheduleWednesdayEnd", "03:10", null));
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
