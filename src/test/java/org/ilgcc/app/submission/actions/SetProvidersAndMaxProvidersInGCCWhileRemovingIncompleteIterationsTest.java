package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SetProvidersAndMaxProvidersInGCCWhileRemovingIncompleteIterationsTest {
  SubmissionRepositoryService repository = Mockito.mock(SubmissionRepositoryService.class);
  SetProvidersAndMaxProvidersInGCCWhileRemovingIncompleteIterations action = new SetProvidersAndMaxProvidersInGCCWhileRemovingIncompleteIterations(repository);

  @Test
  void shouldSetMaxProvidersReachedToTrueIfThreeProvidersHaveBeenAdded(){
    Submission submission = new SubmissionTestBuilder()
        .withProvider("FirstProvider", "1")
        .withProvider("SecondProvider", "2")
        .withProvider("ThirdProvider", "3").build();
    action.run(submission);
    assertThat(submission.getInputData().getOrDefault("maxProvidersReached", "false")).isEqualTo(true);
  }

  @Test
  void shouldSetMaxProvidersReachedToTrueIfTwoProvidersHaveBeenAddedAndParentHasNotChosenAProviderForEveryChildInNeedOfCare(){
    Submission submission = new SubmissionTestBuilder()
        .withProvider("FirstProvider", "1")
        .withProvider("SecondProvider", "2")
        .with("choseProviderForEveryChildInNeedOfCare", "false").build();
    action.run(submission);
    assertThat(submission.getInputData().getOrDefault("maxProvidersReached", "false")).isEqualTo(true);
  }
}