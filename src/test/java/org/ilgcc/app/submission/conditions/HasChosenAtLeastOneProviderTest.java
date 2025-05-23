package org.ilgcc.app.submission.conditions;

import static org.junit.jupiter.api.Assertions.*;

import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

class HasChosenAtLeastOneProviderTest {
  @Test
  void shouldReturnFalseWhenEnableMultipleProvidersIsFalse() {
    boolean mockEnableMultipleProviders = false;
    Submission submission = new SubmissionTestBuilder().with("hasChosenProvider", "true").build();
    HasChosenAtLeastOneProvider condition = new HasChosenAtLeastOneProvider(mockEnableMultipleProviders);

    assertFalse(condition.run(submission));
  }

  @Test
  void shouldReturnFalseWhenEnableMultipleProvidersIsTrueAndHasChoseProviderIsFalse() {
    boolean mockEnableMultipleProviders = true;
    Submission submission = new SubmissionTestBuilder().with("hasChosenProvider", "false").build();
    HasChosenAtLeastOneProvider condition = new HasChosenAtLeastOneProvider(mockEnableMultipleProviders);

    assertFalse(condition.run(submission));
  }

  @Test
  void shouldReturnFalseWhenEnableMultipleProvidersIsTrueAndHasChoseProviderIsTrue() {
    boolean mockEnableMultipleProviders = true;
    Submission submission = new SubmissionTestBuilder().with("hasChosenProvider", "true").build();
    HasChosenAtLeastOneProvider condition = new HasChosenAtLeastOneProvider(mockEnableMultipleProviders);
    assertTrue(condition.run(submission));
  }
}