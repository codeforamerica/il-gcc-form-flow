package org.ilgcc.app.submission.conditions;

import static org.junit.jupiter.api.Assertions.*;

import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

class HasProviderForOneChildTest {

  Submission submission;
  boolean enableMultipleProviders;

  @Test
  void shouldReturnFalseIfEnableMultipleProvidersIsFalse() {
    enableMultipleProviders = false;
    HasProviderForOneChild hasProviderForOneChild = new HasProviderForOneChild(enableMultipleProviders);
    submission = new SubmissionTestBuilder()
        .withChild("First", "Child", "true")
        .with("hasChosenProvider", "true")
        .build();

    assertFalse(hasProviderForOneChild.run(submission));
  }

  @Test
  void shouldReturnFalseHasChosenProviderIsFalse() {
    enableMultipleProviders = true;
    HasProviderForOneChild hasProviderForOneChild = new HasProviderForOneChild(enableMultipleProviders);
    submission = new SubmissionTestBuilder()
        .withChild("First", "Child", "true")
        .with("hasChosenProvider", "false")
        .build();

    assertFalse(hasProviderForOneChild.run(submission));
  }

  @Test
  void shouldReturnFalseIfMoreThanOneChildNeedsAssistance() {
    enableMultipleProviders = true;
    HasProviderForOneChild hasProviderForOneChild = new HasProviderForOneChild(enableMultipleProviders);
    submission = new SubmissionTestBuilder()
        .withChild("First", "Child", "true")
        .withChild("Second", "Child", "true")
        .with("hasChosenProvider", "true")
        .build();

    assertFalse(hasProviderForOneChild.run(submission));
  }

  @Test
  void shouldReturnTrueIfEnableMultipleProvidersIsTrueAndHasChosenProviderIsTrueAndOnlyOneChildNeedsAssistance() {
    enableMultipleProviders = true;
    HasProviderForOneChild hasProviderForOneChild = new HasProviderForOneChild(enableMultipleProviders);
    submission = new SubmissionTestBuilder()
        .withChild("First", "Child", "true")
        .with("hasChosenProvider", "true")
        .build();

    assertTrue(hasProviderForOneChild.run(submission));
  }
}