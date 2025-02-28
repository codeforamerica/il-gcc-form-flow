package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.OffsetDateTime;
import java.util.List;
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
public class SendProviderAgreesToCareFamilyConfirmationEmailTest {

    @MockitoSpyBean
    SendEmailJob sendEmailJob;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    MessageSource messageSource;

    private Submission providerSubmission;

    private Submission familySubmission;

    private SendProviderAgreesToCareFamilyConfirmationEmail action;

    private Locale locale = Locale.ENGLISH;


    @BeforeEach
    void setUp() {
        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("parentPreferredName", "FirstName").withChild("First", "Child", "Yes").withChild("Second", "Child", "Yes")
                .with("parentContactEmail", "familyemail@test.com")
                .with("languageRead", "English")
                .withSubmittedAtDate(OffsetDateTime.now())
                .withCCRR()
                .withShortCode("ABC123")
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
                .with("providerResponseAgreeToCare", "true")
                .build();

        submissionRepositoryService.save(providerSubmission);

        action = new SendProviderAgreesToCareFamilyConfirmationEmail(sendEmailJob, messageSource, submissionRepositoryService);
    }

    @Test
    void correctlySetsEmailRecipient(){
        Optional<Map<String, Object>> emailDataOptional = action.getEmailData(providerSubmission);
        Map<String, Object> emailData = emailDataOptional.get();

        assertThat(action.getRecipientEmail(emailData)).isEqualTo("familyemail@test.com");
    }

    @Test
    void correctlySetsEmailData() {
        Optional<Map<String, Object>> emailDataOptional = action.getEmailData(providerSubmission);

        assertThat(emailDataOptional.isPresent()).isTrue();

        Map<String, Object> emailData = emailDataOptional.get();

        assertThat(emailData.get("confirmationCode")).isEqualTo("ABC123");
        assertThat(emailData.get("parentContactEmail")).isEqualTo("familyemail@test.com");
        assertThat(emailData.get("childrenInitialsList")).isEqualTo(List.of("F.C.", "S.C."));
        assertThat(emailData.get("providerName")).isEqualTo("BusinessName");
        assertThat(emailData.get("ccrrName")).isEqualTo("Sample Test CCRR");
        assertThat(emailData.get("ccrrPhoneNumber")).isEqualTo("(603) 555-1244");
        assertThat(emailData.get("ccapStartDate")).isEqualTo("January 10, 2025");
        assertThat(emailData.get("familyPreferredLanguage")).isEqualTo("English");
    }

    @Test
    void correctlySetsEmailSubject() {
        Optional<Map<String, Object>> emailDataOptional = action.getEmailData(providerSubmission);
        Map<String, Object> emailData = emailDataOptional.get();

        assertThat(action.setSubject(emailData, Locale.ENGLISH)).isEqualTo(messageSource.getMessage("email.response-email-for-family.provider-agrees.subject", null, locale));
    }

    @Test
    void correctlySetsEmailBody() {
        Optional<Map<String, Object>> emailDataOptional = action.getEmailData(providerSubmission);
        Map<String, Object> emailData = emailDataOptional.get();

        String emailCopy = action.setBodyCopy(emailData, locale);

        assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-agrees.p1", null, locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.response-email-for-family.provider-agrees.p2-has-provider-name", new Object[]{"BusinessName", "Sample Test CCRR"},
                        locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-agrees.p3",
                new Object[]{"F.C. and S.C.", "January 10, 2025"}, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-agrees.p4",
                new Object[]{"ABC123"}, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-agrees.p5",
                new Object[]{"Sample Test CCRR", "(603) 555-1244"},
                locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
    }

    @Test
    void correctlySetsEmailSender() {
        assertThat(action.getSenderName(Locale.ENGLISH)).isEqualTo(
                "Child Care Assistance Program Applications - Code for America on behalf of the Illinois Department of Human Services");
    }

    @Test
    void correctlyUpdatesEmailSendStatus() {
        assertThat(providerSubmission.getInputData().containsKey("providerResponseFamilyConfirmationEmailSent")).isFalse();
        action.run(providerSubmission);

        assertThat(providerSubmission.getInputData().containsKey("providerResponseFamilyConfirmationEmailSent")).isTrue();
        assertThat(providerSubmission.getInputData().get("providerResponseFamilyConfirmationEmailSent")).isEqualTo("true");
    }

    @Test
    void correctlySkipsEmailSendWhenEmailStatusIsTrue() {
        assertThat(action.skipEmailSend(providerSubmission)).isFalse();

        providerSubmission.getInputData().put("providerResponseFamilyConfirmationEmailSent", "true");
        assertThat(action.skipEmailSend(providerSubmission)).isTrue();
    }

    @Test
    void correctlyEnqueuesSendEmailJob() {
        action.run(providerSubmission);
        verify(sendEmailJob).enqueueSendEmailJob(any(ILGCCEmail.class));
    }

}