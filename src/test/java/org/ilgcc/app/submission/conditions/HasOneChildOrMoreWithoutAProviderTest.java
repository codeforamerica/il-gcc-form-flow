package org.ilgcc.app.submission.conditions;

import static org.junit.jupiter.api.Assertions.*;

import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

class HasOneChildOrMoreWithoutAProviderTest {

  @Test
  void shouldReturnFalseWhenEnableMultipleProvidersIsFalse() {
    boolean mockEnableMultipleProviders = false;
    String mockContactProviderUUID = "mockContactProviderUUID";
    Submission submission = new SubmissionTestBuilder().with("choseProviderForEveryChildInNeedOfCare", "false").build();
    HasOneChildOrMoreWithoutAProvider condition = new HasOneChildOrMoreWithoutAProvider(mockEnableMultipleProviders);

    assertFalse(condition.run(submission, mockContactProviderUUID));
  }
  //If all the children have a chosen provider this should return false
  @Test
  void shouldReturnFalseWhenEnableMultipleProvidersIsTrueAndChoseProviderForEveryChildInNeedOfCareIsTrue() {
    boolean mockEnableMultipleProviders = true;
    String mockContactProviderUUID = "mockContactProviderUUID";
    Submission submission = new SubmissionTestBuilder().with("choseProviderForEveryChildInNeedOfCare", "true").build();
    HasOneChildOrMoreWithoutAProvider condition = new HasOneChildOrMoreWithoutAProvider(mockEnableMultipleProviders);

    assertFalse(condition.run(submission, mockContactProviderUUID));
  }

  //If at least one child does not have a chose provider this should return true
  @Test
  void shouldReturnFalseWhenEnableMultipleProvidersIsTrueAndChoseProviderForEveryChildInNeedOfCareIsFalse() {
    boolean mockEnableMultipleProviders = true;
    String mockContactProviderUUID = "mockContactProviderUUID";
    Submission submission = new SubmissionTestBuilder().with("choseProviderForEveryChildInNeedOfCare", "false").build();
    HasOneChildOrMoreWithoutAProvider condition = new HasOneChildOrMoreWithoutAProvider(mockEnableMultipleProviders);
    assertTrue(condition.run(submission, mockContactProviderUUID));
  }
}