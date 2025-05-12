package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

class SetProvidersAndMaxProvidersInGCCTest {
  @Test
  void shouldSetMaxProvidersReachedToTrueIfThreeProvidersHaveBeenAdded(){
    Submission submission = new SubmissionTestBuilder()
        .withProvider("FirstProvider", "1")
        .withProvider("SecondProvider", "2")
        .withProvider("ThirdProvider", "3").build();
    SetProvidersAndMaxProvidersInGCC action = new SetProvidersAndMaxProvidersInGCC();
    action.run(submission);
    assertThat(submission.getInputData().getOrDefault("maxProvidersReached", "false")).isEqualTo(true);
  }

  @Test
  void shouldSetMaxProvidersReachedToTrueIfTwoProvidersHaveBeenAddedAndParentHasNotChosenAProviderForEveryChildInNeedOfCare(){
    Submission submission = new SubmissionTestBuilder()
        .withProvider("FirstProvider", "1")
        .withProvider("SecondProvider", "2")
        .with("choseProviderForEveryChildInNeedOfCare", "false").build();
    SetProvidersAndMaxProvidersInGCC action = new SetProvidersAndMaxProvidersInGCC();
    action.run(submission);
    assertThat(submission.getInputData().getOrDefault("maxProvidersReached", "false")).isEqualTo(true);
  }
}