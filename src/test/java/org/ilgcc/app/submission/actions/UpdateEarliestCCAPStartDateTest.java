package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

public class UpdateEarliestCCAPStartDateTest {
    private final UpdateEarliestCCAPStartDate action = new UpdateEarliestCCAPStartDate();

    @Test
    public void removeEarliestStartDateWhenOnlyOneChild() {
        Submission submission = new SubmissionTestBuilder()
                .withChild("first", "child", "Yes")
                .addChildCareStartDate(0, "2009", "2", "10")
                .with("earliestChildcareStartDate", "02/10/2009")
                .build();

        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData().get("children");
        Map<String, Object> firstChild = children.get(0);
        action.run(submission, firstChild.get("uuid").toString());

        assertThat(submission.getInputData().get("earliestChildcareStartDate").toString()).isEqualTo("");
    }

    @Test
    public void updateEarliestStartIfDeletedIsEarliest() {
        Submission submission = new SubmissionTestBuilder()
                .withChild("first", "child", "Yes")
                .withChild("second", "child", "Yes")
                .addChildCareStartDate(0, "2009", "2", "10")
                .addChildCareStartDate(1, "2009", "1", "10")
                .with("earliestChildcareStartDate", "01/10/2009")
                .build();

        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData().get("children");
        Map<String, Object> secondChild = children.get(1);
        action.run(submission, secondChild.get("uuid").toString());

        assertThat(submission.getInputData().get("earliestChildcareStartDate").toString()).isEqualTo("02/10/2009");
    }

    @Test
    public void keepEarliestIfDeletedIsLater() {
        Submission submission = new SubmissionTestBuilder()
                .withChild("first", "child", "Yes")
                .withChild("second", "child", "Yes")
                .addChildCareStartDate(0, "2009", "2", "10")
                .addChildCareStartDate(1, "2009", "1", "10")
                .with("earliestChildcareStartDate", "01/10/2009")
                .build();

        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData().get("children");
        Map<String, Object> firstChild = children.get(0);
        action.run(submission, firstChild.get("uuid").toString());

        assertThat(submission.getInputData().get("earliestChildcareStartDate").toString()).isEqualTo("01/10/2009");
    }

}
