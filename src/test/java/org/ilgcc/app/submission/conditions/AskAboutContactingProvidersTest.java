package org.ilgcc.app.submission.conditions;

import static org.junit.jupiter.api.Assertions.*;

import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

class AskAboutContactingProvidersTest {

  @Test
  void returnsFalseWhenHasChoseProviderIsFalse() {
    boolean mockEnableMultipleProviders = true;
    Submission submission = new SubmissionTestBuilder().with("hasChosenProvider", "false").build();
    AskAboutContactingProviders condition = new AskAboutContactingProviders();

    assertFalse(condition.run(submission));
  }

  @Test
  void returnFalseWhenHasChoseProviderIsTrue() {
    Submission submission = new SubmissionTestBuilder().with("hasChosenProvider", "true").build();
    AskAboutContactingProviders condition = new AskAboutContactingProviders();
    assertTrue(condition.run(submission));
  }
}