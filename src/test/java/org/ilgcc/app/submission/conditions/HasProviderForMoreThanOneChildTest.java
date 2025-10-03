package org.ilgcc.app.submission.conditions;

import static org.junit.jupiter.api.Assertions.*;

import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

class HasProviderForMoreThanOneChildTest {
  Submission submission;
  boolean enableMultipleProviders;

  @Test
  void returnsFalseWhenHasChosenProviderIsFalse() {
    HasProviderForMoreThanOneChild hasProviderForMoreThanOneChild = new HasProviderForMoreThanOneChild();
    submission = new SubmissionTestBuilder()
        .withChild("First", "Child", "true")
        .withChild("Second", "Child", "true")
        .with("hasChosenProvider", "false")
        .build();

    assertFalse(hasProviderForMoreThanOneChild.run(submission));
  }

  @Test
  void returnsFalseIfOnlyOneChildNeedsAssistance() {
    HasProviderForMoreThanOneChild hasProviderForMoreThanOneChild = new HasProviderForMoreThanOneChild();
    submission = new SubmissionTestBuilder()
        .withChild("First", "Child", "true")
        .with("hasChosenProvider", "true")
        .build();

    assertFalse(hasProviderForMoreThanOneChild.run(submission));
  }

  @Test
  void returnsTrueIfHasChosenProviderIsTrueAndMoreThanOneChildNeedsAssistance() {
    HasProviderForMoreThanOneChild hasProviderForMoreThanOneChild = new HasProviderForMoreThanOneChild();
    submission = new SubmissionTestBuilder()
        .withChild("First", "Child", "true")
        .withChild("Second", "Child", "true")
        .with("hasChosenProvider", "true")
        .build();

    assertTrue(hasProviderForMoreThanOneChild.run(submission));
  }
}