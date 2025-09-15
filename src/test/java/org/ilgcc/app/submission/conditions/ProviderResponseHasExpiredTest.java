package org.ilgcc.app.submission.conditions;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.SubmissionRepositoryService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class ProviderResponseHasExpiredTest {

  @Autowired
  SubmissionRepositoryService submissionRepositoryService;
  @Autowired
  SubmissionRepository submissionRepository;
  private Submission familySubmission;
  private Submission providerSubmission;
  ProviderResponseHasExpired providerResponseHasExpired;
  private static final Map<String, Object> provider1 = new HashMap<>();

  @BeforeEach
  void setUp() {
    provider1.put("uuid", "daycareone-1");
    provider1.put("iterationIsComplete", true);
    provider1.put("providerFirstName", "FirstName");
    provider1.put("providerLastName", "LastName");
  }

  @AfterEach
  protected void tearDown() {
    submissionRepository.deleteAll();
  }
  @Test
  void shouldReturnFalseIfResponseHasNotExpired() {
    familySubmission = new SubmissionTestBuilder()
        .withParentBasicInfo()
        .withChild("First", "Child", "true")
        .withChild("Second", "Child", "true")
        .withProvider("DayCareOne", "1")
        .with("providers", List.of(provider1))
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
    submissionRepositoryService.save(providerSubmission);
    providerResponseHasExpired = new ProviderResponseHasExpired(submissionRepositoryService);
    assertThat(providerResponseHasExpired.run(providerSubmission)).isFalse();
  }

  @Test
  void shouldReturnTrueIfResponseHasExpired() {
    provider1.put("providerApplicationResponseStatus", SubmissionStatus.EXPIRED);

    familySubmission = new SubmissionTestBuilder()
        .withParentBasicInfo()
        .withChild("First", "Child", "true")
        .withChild("Second", "Child", "true")
        .with("providers", List.of(provider1))
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

    submissionRepositoryService.save(providerSubmission);

    providerResponseHasExpired = new ProviderResponseHasExpired(submissionRepositoryService);
    assertThat(providerResponseHasExpired.run(providerSubmission)).isTrue();
  }
}