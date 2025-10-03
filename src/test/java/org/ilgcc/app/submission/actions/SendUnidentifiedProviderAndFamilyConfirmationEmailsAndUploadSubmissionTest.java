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
import org.ilgcc.app.email.SendProviderNotIdentifiedFamilyEmail;
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
  Map<String, Object> providerWithStatusNotExpired = new HashMap<>();
  Map<String, Object> providerWithStatusExpired = new HashMap<>();

  private Submission familySubmission;
  Submission providerSubmission;

  @MockitoSpyBean
  SubmissionSenderService submissionSenderService;

  @MockitoSpyBean
  SendProviderNotIdentifiedFamilyEmail sendProviderNotIdentifiedFamilyEmail;

  @MockitoSpyBean
  SendUnidentifiedProviderConfirmationEmail sendUnidentifiedProviderConfirmationEmail;

  @Autowired
  SubmissionRepositoryService submissionRepositoryService;

  @BeforeEach
  void setUp() {
    providerWithStatusNotExpired.put("uuid", "activeDayCareProviderUuid");
    providerWithStatusNotExpired.put("iterationIsComplete", true);
    providerWithStatusNotExpired.put("providerFirstName", "FirstName");
    providerWithStatusNotExpired.put("providerLastName", "LastName");

    providerWithStatusExpired.put("providerApplicationResponseStatus", SubmissionStatus.EXPIRED.toString());
    providerWithStatusExpired.put("uuid", "inactiveDayCareProviderUuid");
    providerWithStatusExpired.put("iterationIsComplete", true);
    providerWithStatusExpired.put("providerFirstName", "FirstName");
    providerWithStatusExpired.put("providerLastName", "LastName");

    familySubmission = new SubmissionTestBuilder()
        .withFlow("gcc")
        .withParentDetails()
        .withChild("First", "Child", "true")
        .withChild("Second", "Child", "true")
        .withSubmittedAtDate(OffsetDateTime.now())
        .with("providers", List.of(providerWithStatusNotExpired, providerWithStatusExpired))
        .withMultipleChildcareSchedulesForProvider(List.of("first-child"), providerWithStatusNotExpired.get("uuid").toString())
        .withMultipleChildcareSchedulesForProvider(List.of("second-child"), providerWithStatusExpired.get("uuid").toString())
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

    sendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission = new SendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission(submissionSenderService, sendProviderNotIdentifiedFamilyEmail,sendUnidentifiedProviderConfirmationEmail, submissionRepositoryService);
    sendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission.run(providerSubmission);
    verify(submissionSenderService).sendProviderSubmissionInstantly(providerSubmission, Optional.of(sendProviderNotIdentifiedFamilyEmail));
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

    sendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission = new SendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission(submissionSenderService, sendProviderNotIdentifiedFamilyEmail, sendUnidentifiedProviderConfirmationEmail, submissionRepositoryService);
    sendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission.run(providerSubmission);
    verifyNoInteractions(submissionSenderService);
    verifyNoInteractions(sendUnidentifiedProviderConfirmationEmail);
  }
}