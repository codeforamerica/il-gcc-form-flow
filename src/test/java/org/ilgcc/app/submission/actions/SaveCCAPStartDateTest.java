package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

public class SaveCCAPStartDateTest {
    private final SaveCCAPStartDate action = new SaveCCAPStartDate();

    @Test
    public void earliestChildCareDateIsSetWithFirstChild() {
        Submission submission = new SubmissionTestBuilder()
                .withChild("first", "child", "Yes")
                .addChildCareStartDate(0, "2009", "2", "10")
                .build();

        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData().get("children");
        Map<String, Object> firstChild = children.get(0);
        action.run(submission, firstChild.get("uuid").toString());

        assertThat(firstChild.get("ccapStartDate").toString()).isEqualTo("02/10/2009");
        assertThat(submission.getInputData().get("earliestChildcareStartDate").toString()).isEqualTo("02/10/2009");
    }

    @Test
    public void earliestChildCareDateIsUpdatedWhenDateIsEarlier() {
        Submission submission = new SubmissionTestBuilder()
                .withChild("first", "child", "Yes")
                .withChild("second", "child", "Yes")
                .addChildCareStartDate(0, "2009", "2", "10")
                .addChildCareStartDate(1, "2009", "1", "10")
                .with("earliestChildcareStartDate", "02/10/2009")
                .build();

        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData().get("children");
        Map<String, Object> firstChild = children.get(0);
        Map<String, Object> secondChild = children.get(1);
        action.run(submission, firstChild.get("uuid").toString());
        action.run(submission, secondChild.get("uuid").toString());

        assertThat(firstChild.get("ccapStartDate").toString()).isEqualTo("02/10/2009");
        assertThat(secondChild.get("ccapStartDate").toString()).isEqualTo("01/10/2009");
        assertThat(submission.getInputData().get("earliestChildcareStartDate").toString()).isEqualTo("01/10/2009");
    }

    @Test
    public void earliestChildCareDateIsNotUpdatedWhenDateIsLater() {
        Submission submission = new SubmissionTestBuilder()
                .withChild("first", "child", "Yes")
                .withChild("second", "child", "Yes")
                .addChildCareStartDate(0, "2009", "2", "10")
                .addChildCareStartDate(1, "2019", "1", "10")
                .with("earliestChildcareStartDate", "02/10/2009")
                .build();

        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData().get("children");
        Map<String, Object> firstChild = children.get(0);
        Map<String, Object> secondChild = children.get(1);
        action.run(submission, firstChild.get("uuid").toString());
        action.run(submission, secondChild.get("uuid").toString());

        assertThat(firstChild.get("ccapStartDate").toString()).isEqualTo("02/10/2009");
        assertThat(secondChild.get("ccapStartDate").toString()).isEqualTo("01/10/2019");
        assertThat(submission.getInputData().get("earliestChildcareStartDate").toString()).isEqualTo("02/10/2009");
    }

}
