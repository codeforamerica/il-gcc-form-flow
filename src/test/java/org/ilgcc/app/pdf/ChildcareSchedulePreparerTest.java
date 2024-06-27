package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ChildcareSchedulePreparerTest {

    ChildcareSchedulePreparer preparer = new ChildcareSchedulePreparer();

    private Submission submission;

    @Test
    public void scheduleTwoChildrenWithOverlap() {
        submission = new SubmissionTestBuilder()
                .withChild("First", "Child", "Yes")
                .withConstantChildcareSchedule(0)
                .withChild("Second", "Child", "Yes")
                .withVaryingChildcareSchedule(1)
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        // Asserting the first child
        assertThat(result.get("childCareScheduleMondayStart_1")).isEqualTo(new SingleField("childCareScheduleMondayStart", "09:00", 1));
        assertThat(result.get("childCareScheduleMondayStartAmPm_1")).isEqualTo(new SingleField("childCareScheduleMondayStartAmPm", "AM", 1));

        assertThat(result.get("childCareScheduleTuesdayStart_1")).isEqualTo(null);
        assertThat(result.get("childCareScheduleTuesdayStartAmPm_1")).isEqualTo(null);

        assertThat(result.get("childCareScheduleWednesdayStart_1")).isEqualTo(new SingleField("childCareScheduleWednesdayStart", "09:00", 1));
        assertThat(result.get("childCareScheduleWednesdayStartAmPm_1")).isEqualTo(new SingleField("childCareScheduleWednesdayStartAmPm", "AM", 1));
        assertThat(result.get("childCareScheduleMondayEnd_1")).isEqualTo(new SingleField("childCareScheduleMondayEnd", "05:00", 1));
        assertThat(result.get("childCareScheduleMondayEndAmPm_1")).isEqualTo(new SingleField("childCareScheduleMondayEndAmPm", "PM", 1));
        assertThat(result.get("childCareScheduleWednesdayEnd_1")).isEqualTo(new SingleField("childCareScheduleWednesdayEnd", "05:00", 1));
        assertThat(result.get("childCareScheduleWednesdayEndAmPm_1")).isEqualTo(new SingleField("childCareScheduleWednesdayEndAmPm", "PM", 1));

        // Asserting the second child
        assertThat(result.get("childCareScheduleMondayStart_2")).isEqualTo(null);
        assertThat(result.get("childCareScheduleMondayStartAmPm_2")).isEqualTo(null);

        assertThat(result.get("childCareScheduleTuesdayStart_2")).isEqualTo(new SingleField("childCareScheduleTuesdayStart", "09:00", 2));
        assertThat(result.get("childCareScheduleTuesdayStartAmPm_2")).isEqualTo(new SingleField("childCareScheduleTuesdayStartAmPm", "AM", 2));
        assertThat(result.get("childCareScheduleWednesdayStart_2")).isEqualTo(new SingleField("childCareScheduleWednesdayStart", "01:00", 2));
        assertThat(result.get("childCareScheduleWednesdayStartAmPm_2")).isEqualTo(new SingleField("childCareScheduleWednesdayStartAmPm", "PM", 2));
        assertThat(result.get("childCareScheduleTuesdayEnd_2")).isEqualTo(new SingleField("childCareScheduleTuesdayEnd", "12:00", 2));
        assertThat(result.get("childCareScheduleTuesdayEndAmPm_2")).isEqualTo(new SingleField("childCareScheduleTuesdayEndAmPm", "PM", 2));
        assertThat(result.get("childCareScheduleWednesdayEnd_2")).isEqualTo(new SingleField("childCareScheduleWednesdayEnd", "03:00", 2));
        assertThat(result.get("childCareScheduleWednesdayEndAmPm_2")).isEqualTo(new SingleField("childCareScheduleWednesdayEndAmPm", "PM", 2));

    }
}
