package org.ilgcc.app.submission.actions;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.email.SendProviderAgreesToCareFamilyConfirmationEmail;
import org.ilgcc.app.email.SendProviderDeclinesCareFamilyConfirmationEmail;
import org.ilgcc.app.email.SendProviderRespondedConfirmationEmail;
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
class SendProviderAndFamilyEmailsTest {
  SendProviderAndFamilyEmails sendProviderAndFamilyEmails;
  Map<String, Object> providerIsActive = new HashMap<>();
  Map<String, Object> providerIsNotActive = new HashMap<>();

  private Submission familySubmission;
  Submission providerSubmission;

  @MockitoSpyBean
  SendProviderRespondedConfirmationEmail sendProviderRespondedConfirmationEmail;

  @MockitoSpyBean
  SendProviderAgreesToCareFamilyConfirmationEmail sendProviderAgreesToCareFamilyConfirmationEmail;

  @MockitoSpyBean
  SendProviderDeclinesCareFamilyConfirmationEmail sendProviderDeclinesCareFamilyConfirmationEmail;

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
  void emailsSentWhenReturningProviderAndTheSubmissionIsActive() {
    providerSubmission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("familySubmissionId", familySubmission.getId().toString())
        .with("providerPaidCcap", true)
        .with("currentProviderUuid", "activeDayCareProviderUuid")
        .build();
    submissionRepositoryService.save(providerSubmission);

    sendProviderAndFamilyEmails = new SendProviderAndFamilyEmails(sendProviderRespondedConfirmationEmail, sendProviderAgreesToCareFamilyConfirmationEmail,sendProviderDeclinesCareFamilyConfirmationEmail, submissionRepositoryService);
    sendProviderAndFamilyEmails.run(providerSubmission);
    verify(sendProviderRespondedConfirmationEmail).send(providerSubmission);
    verify(sendProviderAgreesToCareFamilyConfirmationEmail).send(providerSubmission);
    verify(sendProviderDeclinesCareFamilyConfirmationEmail).send(providerSubmission);
  }

  @Test
  void emailsNotSentWhenProviderSubmissionHasExpired() {
    providerSubmission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("familySubmissionId", familySubmission.getId().toString())
        .with("providerPaidCcap", true)
        .with("currentProviderUuid", "inactiveDayCareProviderUuid")
        .build();
    submissionRepositoryService.save(providerSubmission);

    sendProviderAndFamilyEmails = new SendProviderAndFamilyEmails(sendProviderRespondedConfirmationEmail, sendProviderAgreesToCareFamilyConfirmationEmail,sendProviderDeclinesCareFamilyConfirmationEmail, submissionRepositoryService);
    sendProviderAndFamilyEmails.run(providerSubmission);
    verifyNoInteractions(sendProviderRespondedConfirmationEmail);
    verifyNoInteractions(sendProviderAgreesToCareFamilyConfirmationEmail);
    verifyNoInteractions(sendProviderDeclinesCareFamilyConfirmationEmail);
  }
  @Test
  void emailsNotSentWhenIsANewProvider() {
    providerSubmission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("familySubmissionId", familySubmission.getId().toString())
        .with("providerPaidCcap", false)
        .with("currentProviderUuid", "activeDayCareProviderUuid")
        .build();
    submissionRepositoryService.save(providerSubmission);

    sendProviderAndFamilyEmails = new SendProviderAndFamilyEmails(sendProviderRespondedConfirmationEmail, sendProviderAgreesToCareFamilyConfirmationEmail,sendProviderDeclinesCareFamilyConfirmationEmail, submissionRepositoryService);
    sendProviderAndFamilyEmails.run(providerSubmission);
    verifyNoInteractions(sendProviderRespondedConfirmationEmail);
    verifyNoInteractions(sendProviderAgreesToCareFamilyConfirmationEmail);
    verifyNoInteractions(sendProviderDeclinesCareFamilyConfirmationEmail);
  }
}