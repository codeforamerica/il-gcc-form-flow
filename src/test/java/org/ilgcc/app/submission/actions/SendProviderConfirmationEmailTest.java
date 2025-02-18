package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.jobs.SendEmailJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;


@ActiveProfiles("test")
@SpringBootTest
public class SendProviderConfirmationEmailTest {

    @MockitoSpyBean
    SendEmailJob sendEmailJob;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    MessageSource messageSource;

    private Submission providerSubmission;

    private Submission familySubmission;

    private SendProviderConfirmationEmail action;


    @BeforeEach
    void setUp() {
        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("parentPreferredName", "FirstName").withChild("First", "Child", "Yes").withChild("Second", "Child", "Yes")
                .withSubmittedAtDate(OffsetDateTime.now())
                .withCCRR()
                .withShortCode("ABC123")
                .with("providerResponseAgreeToCare", "true")
                .build();

        submissionRepositoryService.save(familySubmission);

        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .with("familySubmissionId", familySubmission.getId().toString())
                .with("providerResponseContactEmail", "provideremail@test.com")
                .with("providerResponseFirstName", "ProviderFirst")
                .with("providerResponseLastName", "ProviderLast")
                .with("providerResponseBusinessName", "BusinessName")
                .with("providerCareStartDate", "01/10/2025")
                .build();

        submissionRepositoryService.save(providerSubmission);

        action = new SendProviderConfirmationEmail(sendEmailJob, submissionRepositoryService, messageSource);
    }

    @Test
    void correctlySetsEmailData() {
        Optional<Map<String, String>> emailDataOptional = action.getEmailData(providerSubmission);

        assertThat(emailDataOptional.isPresent()).isTrue();

        Map<String, String> emailData = emailDataOptional.get();

        assertThat(emailData.get("confirmationCode")).isEqualTo("ABC123");
        assertThat(emailData.get("childrenInitials")).isEqualTo("F.C. and S.C.");
        assertThat(emailData.get("providerName")).isEqualTo("BusinessName");
        assertThat(emailData.get("ccrrName")).isEqualTo("Sample Test CCRRR");
        assertThat(emailData.get("ccrrPhoneNumber")).isEqualTo("(603) 555-1244");
        assertThat(emailData.get("ccapStartDate")).isEqualTo("January 10, 2025");
    }

    @Test
    void correctlySetsEmailSubject() {
        Optional<Map<String, String>> emailDataOptional = action.getEmailData(providerSubmission);
        Map<String, String> emailData = emailDataOptional.get();

        assertThat(action.setSubject(emailData, Locale.ENGLISH)).isEqualTo("Your CCAP confirmation code: ABC123");
    }

    @Test
    void correctlySetsEmailBody() {
        Optional<Map<String, String>> emailDataOptional = action.getEmailData(providerSubmission);
        Map<String, String> emailData = emailDataOptional.get();

        String emailCopy = action.setBodyCopy(emailData, Locale.ENGLISH);

        assertThat(emailCopy).contains(
                "Thank you for completing a family's Child Care Assistance Program (CCAP) application. Your response has been submitted to Sample Test CCRRR for processing.");
        assertThat(emailCopy).contains(
                "<strong>Response:</strong> You agreed to care for F.C. and S.C. with a start date of January 10, 2025 or pending approval.");
        assertThat(emailCopy).contains("ABC123");
        assertThat(emailCopy).contains(
                "<strong>Next steps:You will receive a letter or email about the status of the family's application within 13-15 business days. If you want an update on the application, call Sample Test CCRRR at");
        assertThat(emailCopy).contains("(603) 555-1244");
    }

    @Test
    void correctlySetsEmailSender() {
        assertThat(action.getSenderName(Locale.ENGLISH)).isEqualTo(
                "Child Care Assistance Program Applications - Code for America on behalf of the Illinois Department of Human Services");
    }

    @Test
    void correctlyUpdatesEmailSendStatus() {
        assertThat(providerSubmission.getInputData().containsKey("providerConfirmationEmailSent")).isFalse();
        action.run(providerSubmission);

        assertThat(providerSubmission.getInputData().containsKey("providerConfirmationEmailSent")).isTrue();
        assertThat(providerSubmission.getInputData().get("providerConfirmationEmailSent")).isEqualTo("true");
    }

    @Test
    void correctlySkipsEmailSendWhenEmailStatusIsTrue() {
        assertThat(action.skipEmailSend(providerSubmission)).isFalse();

        providerSubmission.getInputData().put("providerConfirmationEmailSent", "true");
        assertThat(action.skipEmailSend(providerSubmission)).isTrue();
    }

    @Test
    void correctlyEnqueuesSendEmailJob() {
        action.run(providerSubmission);
        verify(sendEmailJob).enqueueSendEmailJob(any(ILGCCEmail.class));
    }

}