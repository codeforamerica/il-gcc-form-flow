package org.ilgcc.app.submission.conditions;

import static org.junit.jupiter.api.Assertions.*;

import formflow.library.data.Submission;
import java.time.OffsetDateTime;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

class ProviderResponseIsAfterThreeDayWindowTest {
  @Test
  void shouldReturnFalseIfResponseIsBeforeThreeDayWindow() {
    //build a family submission with a submitted at time
    //add a provider
    //add a child and multi-provider schedule
    //create a provider submission
    //pass in the provider submission into ProviderResponseIsAfterThreeDayWindowTest
    Submission familySubmission = new SubmissionTestBuilder()
        .withParentBasicInfo()
        .withSubmittedAtDate(OffsetDateTime.now().minusDays(1)).build();
  }
}