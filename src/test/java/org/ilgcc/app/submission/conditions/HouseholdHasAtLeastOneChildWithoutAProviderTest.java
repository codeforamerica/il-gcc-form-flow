package org.ilgcc.app.submission.conditions;

import static org.junit.jupiter.api.Assertions.*;

import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

class HouseholdHasAtLeastOneChildWithoutAProviderTest {

  @Test
  void shouldReturnFalseWhenEnableMultipleProvidersIsFalse() {
    boolean mockEnableMultipleProviders = false;
    Submission submission = new SubmissionTestBuilder().with("choseProviderForEveryChildInNeedOfCare", "false").build();
    HouseholdHasAtLeastOneChildWithoutAProvider condition = new HouseholdHasAtLeastOneChildWithoutAProvider(mockEnableMultipleProviders);

    assertFalse(condition.run(submission));
  }
  //If all the children have a chosen provider this should return false
  @Test
  void shouldReturnFalseWhenEnableMultipleProvidersIsTrueAndChoseProviderForEveryChildInNeedOfCareIsTrue() {
    boolean mockEnableMultipleProviders = true;
    Submission submission = new SubmissionTestBuilder().with("choseProviderForEveryChildInNeedOfCare", "true").build();
    HouseholdHasAtLeastOneChildWithoutAProvider condition = new HouseholdHasAtLeastOneChildWithoutAProvider(mockEnableMultipleProviders);

    assertFalse(condition.run(submission));
  }

  //If at least one child does not have a chose provider this should return true
  @Test
  void shouldReturnFalseWhenEnableMultipleProvidersIsTrueAndChoseProviderForEveryChildInNeedOfCareIsFalse() {
    boolean mockEnableMultipleProviders = true;
    Submission submission = new SubmissionTestBuilder().with("choseProviderForEveryChildInNeedOfCare", "false").build();
    HouseholdHasAtLeastOneChildWithoutAProvider condition = new HouseholdHasAtLeastOneChildWithoutAProvider(mockEnableMultipleProviders);
    assertTrue(condition.run(submission));
  }
}