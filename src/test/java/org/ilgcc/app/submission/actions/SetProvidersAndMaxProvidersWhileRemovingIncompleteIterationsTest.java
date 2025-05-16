package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.List;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SetProvidersAndMaxProvidersWhileRemovingIncompleteIterationsTest {

    SubmissionRepositoryService repository = Mockito.mock(SubmissionRepositoryService.class);
    SetProvidersAndMaxProvidersWhileRemovingIncompleteIterations action = new SetProvidersAndMaxProvidersWhileRemovingIncompleteIterations(
            repository);

    @Test
    void shouldSetMaxProvidersAllowedToThree() {
        Submission submission = new SubmissionTestBuilder()
                .withProvider("FirstProvider", "1")
                .withProvider("SecondProvider", "2")
                .withProvider("ThirdProvider", "3")
                .with("choseProviderForEveryChildInNeedOfCare", "true").build();
        action.run(submission);
        assertThat(submission.getInputData().get("maxProvidersAllowed")).isEqualTo(3);
        assertThat(((List) submission.getInputData().get("careProviders")).size()).isEqualTo(3);
    }

    @Test
    void shouldSetMaxProvidersAllowedToTwo() {
        Submission submission = new SubmissionTestBuilder()
                .withProvider("FirstProvider", "1")
                .withProvider("SecondProvider", "2")
                .with("choseProviderForEveryChildInNeedOfCare", "false").build();
        action.run(submission);
        assertThat(submission.getInputData().get("maxProvidersAllowed")).isEqualTo(2);
        assertThat(((List) submission.getInputData().get("careProviders")).size()).isEqualTo(2);
    }
}