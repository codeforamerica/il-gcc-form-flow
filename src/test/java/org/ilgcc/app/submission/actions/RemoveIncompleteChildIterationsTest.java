package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = IlGCCApplication.class)
@ActiveProfiles("test")
public class RemoveIncompleteChildIterationsTest {

    SubmissionRepositoryService repository = Mockito.mock(SubmissionRepositoryService.class);
    private final RemoveIncompleteChildIterations action = new RemoveIncompleteChildIterations(repository);

    @Test
    public void removesEarliestDateWhenNoChildren() {
        Submission submission = new SubmissionTestBuilder()
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("earliestChildcareStartDate").toString()).isEqualTo("");
    }

    @Test
    public void setsEarliestDateWithMultipleChildren() {
        Submission submission = new SubmissionTestBuilder()
                .withChild("first", "child", "true")
                .withChild("second", "child", "true")
                .withChild("third", "child", "true")
                .addChildCareStartDate(0, "2009", "2", "10")
                .addChildCareStartDate(1, "2009", "1", "10")
                .addChildCareStartDate(2, "2020", "1", "10")
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("earliestChildcareStartDate").toString()).isEqualTo("01/10/2009");
    }

    @Test
    public void setsEarliestDateWithSingle() {
        Submission submission = new SubmissionTestBuilder()
                .withChild("first", "child", "true")
                .addChildCareStartDate(0, "2020", "2", "10")
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("earliestChildcareStartDate").toString()).isEqualTo("02/10/2020");
    }

}
