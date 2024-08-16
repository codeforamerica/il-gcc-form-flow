package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

public class PartnerEducationSchedulePreparerTest {

    PartnerEducationSchedulePreparer preparer = new PartnerEducationSchedulePreparer();

    private Submission submission;

    @Test
    public void withTheSameScheduleEveryDay() {
        submission = new SubmissionTestBuilder()
            .withRegularSchoolSchedule("partnerClass", "partnerClassWeeklySchedule[]", List.of("Monday", "Thursday","Sunday"))
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("partnerEducationScheduleMondayStart")).isEqualTo(
            new SingleField("partnerEducationScheduleMondayStart", "10:00", null));
        assertThat(result.get("partnerEducationScheduleMondayStartAmPm")).isEqualTo(
            new SingleField("partnerEducationScheduleMondayStartAmPm", "AM", null));
        assertThat(result.get("partnerEducationScheduleMondayEnd")).isEqualTo(
            new SingleField("partnerEducationScheduleMondayEnd", "03:45", null));
        assertThat(result.get("partnerEducationScheduleMondayEndAmPm")).isEqualTo(
            new SingleField("partnerEducationScheduleMondayEndAmPm", "PM", null));

        assertThat(result.get("partnerEducationScheduleTuesdayStart")).isEqualTo(null);
        assertThat(result.get("partnerEducationScheduleTuesdayStartAmPm")).isEqualTo(null);
        assertThat(result.get("partnerEducationScheduleTuesdayEnd")).isEqualTo(null);
        assertThat(result.get("partnerEducationScheduleTuesdayEndAmPm")).isEqualTo(null);

        assertThat(result.get("partnerEducationScheduleThursdayStart")).isEqualTo(
            new SingleField("partnerEducationScheduleThursdayStart", "10:00", null));
        assertThat(result.get("partnerEducationScheduleThursdayStartAmPm")).isEqualTo(
            new SingleField("partnerEducationScheduleThursdayStartAmPm", "AM", null));
        assertThat(result.get("partnerEducationScheduleThursdayEnd")).isEqualTo(
            new SingleField("partnerEducationScheduleThursdayEnd", "03:45", null));
        assertThat(result.get("partnerEducationScheduleThursdayEndAmPm")).isEqualTo(
            new SingleField("partnerEducationScheduleThursdayEndAmPm", "PM", null));

        assertThat(result.get("partnerEducationScheduleSundayStart")).isEqualTo(
            new SingleField("partnerEducationScheduleSundayStart", "10:00", null));
        assertThat(result.get("partnerEducationScheduleSundayStartAmPm")).isEqualTo(
            new SingleField("partnerEducationScheduleSundayStartAmPm", "AM", null));
        assertThat(result.get("partnerEducationScheduleSundayEnd")).isEqualTo(
            new SingleField("partnerEducationScheduleSundayEnd", "03:45", null));
        assertThat(result.get("partnerEducationScheduleSundayEndAmPm")).isEqualTo(
            new SingleField("partnerEducationScheduleSundayEndAmPm", "PM", null));
    }

    @Test
    public void withDifferentScheduleEveryDay() {
        submission = new SubmissionTestBuilder()
            .withSchoolScheduleByDay("partnerClass","Monday","10", "0", "AM", "3", "45", "PM")
            .withSchoolScheduleByDay("partnerClass","Wednesday","8", "0", "AM", "12", "45", "PM")
            .withSchoolScheduleByDay("partnerClass","Friday","12", "0", "PM", "7", "0", "PM")
            .with("partnerClassWeeklySchedule[]", List.of("Monday", "Wednesday", "Friday"))
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("partnerEducationScheduleMondayStart")).isEqualTo(
            new SingleField("partnerEducationScheduleMondayStart", "10:00", null));
        assertThat(result.get("partnerEducationScheduleMondayStartAmPm")).isEqualTo(
            new SingleField("partnerEducationScheduleMondayStartAmPm", "AM", null));
        assertThat(result.get("partnerEducationScheduleMondayEnd")).isEqualTo(
            new SingleField("partnerEducationScheduleMondayEnd", "03:45", null));
        assertThat(result.get("partnerEducationScheduleMondayEndAmPm")).isEqualTo(
            new SingleField("partnerEducationScheduleMondayEndAmPm", "PM", null));

        assertThat(result.get("partnerEducationScheduleTuesdayStart")).isEqualTo(null);
        assertThat(result.get("partnerEducationScheduleTuesdayStartAmPm")).isEqualTo(null);
        assertThat(result.get("partnerEducationScheduleTuesdayEnd")).isEqualTo(null);
        assertThat(result.get("partnerEducationScheduleTuesdayEndAmPm")).isEqualTo(null);

        assertThat(result.get("partnerEducationScheduleWednesdayStart")).isEqualTo(
            new SingleField("partnerEducationScheduleWednesdayStart", "08:00", null));
        assertThat(result.get("partnerEducationScheduleWednesdayStartAmPm")).isEqualTo(
            new SingleField("partnerEducationScheduleWednesdayStartAmPm", "AM", null));
        assertThat(result.get("partnerEducationScheduleWednesdayEnd")).isEqualTo(
            new SingleField("partnerEducationScheduleWednesdayEnd", "12:45", null));
        assertThat(result.get("partnerEducationScheduleWednesdayEndAmPm")).isEqualTo(
            new SingleField("partnerEducationScheduleWednesdayEndAmPm", "PM", null));

        assertThat(result.get("partnerEducationScheduleFridayStart")).isEqualTo(
            new SingleField("partnerEducationScheduleFridayStart", "12:00", null));
        assertThat(result.get("partnerEducationScheduleFridayStartAmPm")).isEqualTo(
            new SingleField("partnerEducationScheduleFridayStartAmPm", "PM", null));
        assertThat(result.get("partnerEducationScheduleFridayEnd")).isEqualTo(
            new SingleField("partnerEducationScheduleFridayEnd", "07:00", null));
        assertThat(result.get("partnerEducationScheduleFridayEndAmPm")).isEqualTo(
            new SingleField("partnerEducationScheduleFridayEndAmPm", "PM", null));
    }
}
