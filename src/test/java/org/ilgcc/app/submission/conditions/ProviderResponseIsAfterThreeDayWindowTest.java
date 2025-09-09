package org.ilgcc.app.submission.conditions;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.OffsetDateTime;
import java.util.List;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class ProviderResponseIsAfterThreeDayWindowTest {
 @Autowired
 SubmissionRepositoryService submissionRepositoryService;

  private Submission familySubmission;
  private Submission providerSubmission;
  ProviderResponseIsAfterThreeDayWindow providerResponseIsAfterThreeDayWindow;

  @Test
  void shouldReturnFalseIfResponseIsBeforeThreeDayWindow() {
    providerResponseIsAfterThreeDayWindow = new ProviderResponseIsAfterThreeDayWindow(submissionRepositoryService);
    familySubmission = new SubmissionTestBuilder()
        .withParentBasicInfo()
        .withSubmittedAtDate(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(OffsetDateTime.now().plusDays(1)))
        .withChild("First", "Child", "true")
        .withChild("Second", "Child", "true")
        .withProvider("DayCareOne", "1")
        .withMultipleChildcareSchedulesAllBelongingToTheSameProvider(List.of("first-child", "second-child"), "daycareone-1")
        .build();

    submissionRepositoryService.save(familySubmission);

    providerSubmission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("familySubmissionId", familySubmission.getId().toString())
        .with("providerResponseFirstName", "ProviderFirst")
        .with("providerResponseLastName", "ProviderLast")
        .with("providerResponseBusinessName", "DayCare Place")
        .with("providerResponseContactPhoneNumber", "(111) 222-3333")
        .with("providerResponseContactEmail", "mail@daycareplace.org")
        .with("providerPaidCcap", "true")
        .with("currentProviderUuid", "daycareone-1")
        .build();
    assertThat(providerResponseIsAfterThreeDayWindow.run(providerSubmission)).isFalse();
  }

  @Test
  void shouldReturnTrueIfResponseIsAfterThreeDayWindow() {
    providerResponseIsAfterThreeDayWindow = new ProviderResponseIsAfterThreeDayWindow(submissionRepositoryService);
    familySubmission = new SubmissionTestBuilder()
        .withParentBasicInfo()
        .withSubmittedAtDate(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(OffsetDateTime.now().minusDays(1)))
        .withChild("First", "Child", "true")
        .withChild("Second", "Child", "true")
        .withProvider("DayCareOne", "1")
        .withMultipleChildcareSchedulesAllBelongingToTheSameProvider(List.of("first-child", "second-child"), "daycareone-1")
        .build();

    submissionRepositoryService.save(familySubmission);

    providerSubmission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("familySubmissionId", familySubmission.getId().toString())
        .with("providerResponseFirstName", "ProviderFirst")
        .with("providerResponseLastName", "ProviderLast")
        .with("providerResponseBusinessName", "DayCare Place")
        .with("providerResponseContactPhoneNumber", "(111) 222-3333")
        .with("providerResponseContactEmail", "mail@daycareplace.org")
        .with("providerPaidCcap", "true")
        .with("currentProviderUuid", "daycareone-1")
        .build();
    assertThat(providerResponseIsAfterThreeDayWindow.run(providerSubmission)).isTrue();
  }
}