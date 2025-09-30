package org.ilgcc.app.submission.conditions;

import static org.junit.jupiter.api.Assertions.*;

import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

class HouseholdHasAtLeastOneChildWithoutAProviderTest {
  @Test
  void shouldReturnFalseWhenChoseProviderForEveryChildInNeedOfCareIsTrue() {
    Submission submission = new SubmissionTestBuilder().with("choseProviderForEveryChildInNeedOfCare", "true").build();
    HouseholdHasAtLeastOneChildWithoutAProvider condition = new HouseholdHasAtLeastOneChildWithoutAProvider(true);

    assertFalse(condition.run(submission));
  }

  //If at least one child does not have a chose provider this should return true
  @Test
  void shouldReturnFalseChoseProviderForEveryChildInNeedOfCareIsFalse() {
    Submission submission = new SubmissionTestBuilder().with("choseProviderForEveryChildInNeedOfCare", "false").build();
    HouseholdHasAtLeastOneChildWithoutAProvider condition = new HouseholdHasAtLeastOneChildWithoutAProvider(true);
    assertTrue(condition.run(submission));
  }
}