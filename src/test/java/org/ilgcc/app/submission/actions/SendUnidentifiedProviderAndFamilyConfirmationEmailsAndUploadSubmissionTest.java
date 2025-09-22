package org.ilgcc.app.submission.actions;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.data.SubmissionSenderService;
import org.ilgcc.app.email.SendProviderDidNotRespondToFamilyEmail;
import org.ilgcc.app.email.SendUnidentifiedProviderConfirmationEmail;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@ActiveProfiles("test")
@SpringBootTest
class SendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmissionTest {

  SendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission sendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission;
  Map<String, Object> providerIsActive = new HashMap<>();
  Map<String, Object> providerIsNotActive = new HashMap<>();

  private Submission familySubmission;
  Submission providerSubmission;

  @MockitoSpyBean
  SubmissionSenderService submissionSenderService;

  @MockitoSpyBean
  SendProviderDidNotRespondToFamilyEmail sendProviderDidNotRespondToFamilyEmail;

  @MockitoSpyBean
  SendUnidentifiedProviderConfirmationEmail sendUnidentifiedProviderConfirmationEmail;

  @Autowired
  SubmissionRepositoryService submissionRepositoryService;

  @BeforeEach
  void setUp() {
    providerIsActive.put("uuid", "activeDayCareProviderUuid");
    providerIsActive.put("iterationIsComplete", true);
    providerIsActive.put("providerFirstName", "FirstName");
    providerIsActive.put("providerLastName", "LastName");

    providerIsNotActive.put("providerApplicationResponseStatus", SubmissionStatus.EXPIRED.toString());
    providerIsNotActive.put("uuid", "inactiveDayCareProviderUuid");
    providerIsNotActive.put("iterationIsComplete", true);
    providerIsNotActive.put("providerFirstName", "FirstName");
    providerIsNotActive.put("providerLastName", "LastName");

    familySubmission = new SubmissionTestBuilder()
        .withFlow("gcc")
        .withParentDetails()
        .withChild("First", "Child", "true")
        .withChild("Second", "Child", "true")
        .withSubmittedAtDate(OffsetDateTime.now())
        .with("providers", List.of(providerIsActive, providerIsNotActive))
        .withMultipleChildcareSchedulesForProvider(List.of("first-child"), providerIsActive.get("uuid").toString())
        .withMultipleChildcareSchedulesForProvider(List.of("second-child"), providerIsNotActive.get("uuid").toString())
        .withShortCode("shortCodeTest")
        .build();

    submissionRepositoryService.save(familySubmission);

  }

  @Test
  void emailsSentWhenNewProviderIsActive() {
    providerSubmission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("familySubmissionId", familySubmission.getId().toString())
        .with("providerPaidCcap", false)
        .with("currentProviderUuid", "activeDayCareProviderUuid")
        .build();
    submissionRepositoryService.save(providerSubmission);

    sendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission = new SendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission(submissionSenderService, sendProviderDidNotRespondToFamilyEmail,sendUnidentifiedProviderConfirmationEmail, submissionRepositoryService);
    sendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission.run(providerSubmission);
    verify(submissionSenderService).sendProviderSubmissionInstantly(providerSubmission, Optional.of(sendProviderDidNotRespondToFamilyEmail));
    verify(sendUnidentifiedProviderConfirmationEmail).send(providerSubmission);
  }

  @Test
  void emailsNotSentWhenNewProviderSubmissionHasExpired() {
    providerSubmission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("familySubmissionId", familySubmission.getId().toString())
        .with("providerPaidCcap", false)
        .with("currentProviderUuid", "inactiveDayCareProviderUuid")
        .build();
    submissionRepositoryService.save(providerSubmission);

    sendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission = new SendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission(submissionSenderService, sendProviderDidNotRespondToFamilyEmail, sendUnidentifiedProviderConfirmationEmail, submissionRepositoryService);
    sendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission.run(providerSubmission);
    verifyNoInteractions(submissionSenderService);
    verifyNoInteractions(sendUnidentifiedProviderConfirmationEmail);
  }
}