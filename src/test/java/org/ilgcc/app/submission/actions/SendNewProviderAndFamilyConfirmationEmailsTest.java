package org.ilgcc.app.submission.actions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ilgcc.app.email.SendNewProviderAgreesToCareFamilyConfirmationEmail;
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
class SendNewProviderAndFamilyConfirmationEmailsTest {

  SendNewProviderAndFamilyConfirmationEmails sendNewProviderAndFamilyConfirmationEmails;
  Map<String, Object> providerStatusNotExpired = new HashMap<>();
  Map<String, Object> providerStatusExpired = new HashMap<>();

  private Submission familySubmission;
  Submission providerSubmission;

  @MockitoSpyBean
  SendNewProviderAgreesToCareFamilyConfirmationEmail sendNewProviderAgreesToCareFamilyConfirmationEmail;

  @MockitoSpyBean
  SendProviderRespondedConfirmationEmail sendProviderRespondedConfirmationEmail;

  @Autowired
  SubmissionRepositoryService submissionRepositoryService;

  @BeforeEach
  void setUp() {
    providerStatusNotExpired.put("uuid", "activeDayCareProviderUuid");
    providerStatusNotExpired.put("iterationIsComplete", true);
    providerStatusNotExpired.put("providerFirstName", "FirstName");
    providerStatusNotExpired.put("providerLastName", "LastName");

    providerStatusExpired.put("providerApplicationResponseStatus", SubmissionStatus.EXPIRED.toString());
    providerStatusExpired.put("uuid", "inactiveDayCareProviderUuid");
    providerStatusExpired.put("iterationIsComplete", true);
    providerStatusExpired.put("providerFirstName", "FirstName");
    providerStatusExpired.put("providerLastName", "LastName");

    familySubmission = new SubmissionTestBuilder()
        .withFlow("gcc")
        .withParentDetails()
        .withChild("First", "Child", "true")
        .withChild("Second", "Child", "true")
        .withSubmittedAtDate(OffsetDateTime.now())
        .with("providers", List.of(providerStatusNotExpired, providerStatusExpired))
        .withMultipleChildcareSchedulesForProvider(List.of("first-child"), providerStatusNotExpired.get("uuid").toString())
        .withMultipleChildcareSchedulesForProvider(List.of("second-child"), providerStatusExpired.get("uuid").toString())
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

    sendNewProviderAndFamilyConfirmationEmails = new SendNewProviderAndFamilyConfirmationEmails(sendNewProviderAgreesToCareFamilyConfirmationEmail, sendProviderRespondedConfirmationEmail, submissionRepositoryService);
    sendNewProviderAndFamilyConfirmationEmails.run(providerSubmission);
    verify(sendNewProviderAgreesToCareFamilyConfirmationEmail).send(providerSubmission);
    verify(sendProviderRespondedConfirmationEmail).send(providerSubmission);
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

    sendNewProviderAndFamilyConfirmationEmails = new SendNewProviderAndFamilyConfirmationEmails(sendNewProviderAgreesToCareFamilyConfirmationEmail, sendProviderRespondedConfirmationEmail, submissionRepositoryService);
    sendNewProviderAndFamilyConfirmationEmails.run(providerSubmission);
    verifyNoInteractions(sendNewProviderAgreesToCareFamilyConfirmationEmail);
    verifyNoInteractions(sendProviderRespondedConfirmationEmail);
  }
}