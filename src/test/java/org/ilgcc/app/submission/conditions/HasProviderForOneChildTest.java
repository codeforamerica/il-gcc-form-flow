package org.ilgcc.app.submission.conditions;

import static org.junit.jupiter.api.Assertions.*;

import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

class HasProviderForOneChildTest {

  Submission submission;

  @Test
  void returnsFalseWhenHasChosenProviderIsFalse() {
    HasProviderForOneChild hasProviderForOneChild = new HasProviderForOneChild(true);
    submission = new SubmissionTestBuilder()
        .withChild("First", "Child", "true")
        .with("hasChosenProvider", "false")
        .build();

    assertFalse(hasProviderForOneChild.run(submission));
  }

  @Test
  void returnsFalseIfMoreThanOneChildNeedsAssistance() {
    HasProviderForOneChild hasProviderForOneChild = new HasProviderForOneChild(true);
    submission = new SubmissionTestBuilder()
        .withChild("First", "Child", "true")
        .withChild("Second", "Child", "true")
        .with("hasChosenProvider", "true")
        .build();

    assertFalse(hasProviderForOneChild.run(submission));
  }

  @Test
  void returnsTrueIfHasChosenProviderIsTrueAndOnlyOneChildNeedsAssistance() {
    HasProviderForOneChild hasProviderForOneChild = new HasProviderForOneChild(true);
    submission = new SubmissionTestBuilder()
        .withChild("First", "Child", "true")
        .with("hasChosenProvider", "true")
        .build();

    assertTrue(hasProviderForOneChild.run(submission));
  }
}