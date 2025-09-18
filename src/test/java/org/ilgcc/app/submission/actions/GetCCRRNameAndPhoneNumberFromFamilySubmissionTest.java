package org.ilgcc.app.submission.actions;

import static org.junit.jupiter.api.Assertions.*;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.SubmissionRepositoryService;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = IlGCCApplication.class
)
@ActiveProfiles("test")
class GetCCRRNameAndPhoneNumberFromFamilySubmissionTest {

  @Autowired
  private SubmissionRepositoryService submissionRepositoryService;
  @Autowired
  private GetCCRRNameAndPhoneNumberFromFamilySubmission getCCRRNameAndPhoneNumberFromFamilySubmission;


  @Autowired
  SubmissionRepository submissionRepository;
  private Submission familySubmission;
  private Submission providerSubmission;
  private final String CCRR_NAME_INPUT = "ccrrName";
  private final String CCRR_NAME_VALUE = "Sample Test CCRR";

  @AfterEach
  void tearDown() {
    submissionRepository.deleteAll();
  }

  @Test
  void shouldReturnCCRRNameFromFamilySubmissionIfPresent() {
    familySubmission = new SubmissionTestBuilder()
        .withFlow("gcc")
        .withCCRR()
        .build();
    submissionRepositoryService.save(familySubmission);

    providerSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("familySubmissionId", familySubmission.getId().toString())
        .withProviderSubmissionData()
        .build());

    getCCRRNameAndPhoneNumberFromFamilySubmission.run(providerSubmission);
    assertTrue(providerSubmission.getInputData().containsKey(CCRR_NAME_INPUT));
    assertEquals(CCRR_NAME_VALUE, familySubmission.getInputData().get(CCRR_NAME_INPUT));
  }

  @Test
  void shouldNotSetCCRRNameIfCCRRNameNotFoundInFamilySubmission() {
    familySubmission = new SubmissionTestBuilder()
        .withFlow("gcc")
        .build();
    submissionRepositoryService.save(familySubmission);

    providerSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("familySubmissionId", familySubmission.getId().toString())
        .withProviderSubmissionData()
        .build());

    getCCRRNameAndPhoneNumberFromFamilySubmission.run(providerSubmission);
    assertFalse(providerSubmission.getInputData().containsKey(CCRR_NAME_INPUT));
  }
}