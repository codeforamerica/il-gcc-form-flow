package org.ilgcc.app.submission.conditions;

import static org.junit.jupiter.api.Assertions.*;

import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

class HasNoChosenProvidersTest {
  Submission submission;
  boolean enableMultipleProviders;
  @Test
  void shouldReturnTrueIfEnableMultipleProvidersIsTrueAndHasChosenProviderIsFalse() {
    enableMultipleProviders = true;
    HasNoChosenProviders hasNoChosenProviders = new HasNoChosenProviders(enableMultipleProviders);
    submission = new SubmissionTestBuilder()
        .withChild("First", "Child", "true")
        .withChild("Second", "Child", "true")
        .with("hasChosenProvider", "false")
        .build();

    assertTrue(hasNoChosenProviders.run(submission));
  }
  @Test
  void shouldReturnFalseIfEnableMultipleProvidersIsFalse() {
    enableMultipleProviders = false;
    HasNoChosenProviders hasNoChosenProviders = new HasNoChosenProviders(enableMultipleProviders);
    submission = new SubmissionTestBuilder()
        .withChild("First", "Child", "true")
        .withChild("Second", "Child", "true")
        .with("hasChosenProvider", "false")
        .build();

    assertFalse(hasNoChosenProviders.run(submission));
  }
  @Test
  void shouldReturnFalseIfHasChosenProviderIsTrue() {
    enableMultipleProviders = true;
    HasNoChosenProviders hasNoChosenProviders = new HasNoChosenProviders(enableMultipleProviders);
    submission = new SubmissionTestBuilder()
        .withChild("First", "Child", "true")
        .withChild("Second", "Child", "true")
        .with("hasChosenProvider", "true")
        .build();

    assertFalse(hasNoChosenProviders.run(submission));
  }
}