package org.ilgcc.app.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ilgcc.app.email.ILGCCEmail.FROM_ADDRESS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

import com.sendgrid.helpers.mail.objects.Email;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.SubmissionRepositoryService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.jobs.SendEmailJob;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;


@ActiveProfiles("test")
@SpringBootTest
public class SendNewProviderAgreesToCareFamilyConfirmationEmailTest {

    @MockitoSpyBean
    SendEmailJob sendEmailJob;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    MessageSource messageSource;

    private Submission providerSubmission;

    private SendNewProviderAgreesToCareFamilyConfirmationEmail sendEmailClass;

    private final Locale locale = Locale.ENGLISH;


    @BeforeEach
    void setUp() {
        Submission familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("parentFirstName", "FirstName").withChild("First", "Child", "true").withChild("Second", "Child", "true")
                .with("parentContactEmail", "familyemail@test.com")
                .with("languageRead", "English")
                .withSubmittedAtDate(OffsetDateTime.now())
                .withCCRR()
                .withShortCode("ABC123")
                .build());

        providerSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .with("familySubmissionId", familySubmission.getId().toString())
                .with("providerResponseContactEmail", "provideremail@test.com")
                .with("providerResponseFirstName", "ProviderFirst")
                .with("providerResponseLastName", "ProviderLast")
                .with("providerResponseBusinessName", "BusinessName")
                .with("providerCareStartDate", "01/10/2025")
                .with("providerResponseAgreeToCare", "true")
                .build());

        sendEmailClass = new SendNewProviderAgreesToCareFamilyConfirmationEmail(sendEmailJob, messageSource,
                submissionRepositoryService);
    }

    @AfterEach
    void tearDown() {
        submissionRepository.deleteAll();
    }

    @Test
    void correctlySetsEmailRecipient() {
        Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);
        Map<String, Object> emailData = emailDataOptional.get();

        assertThat(sendEmailClass.getRecipientEmail(emailData)).isEqualTo("familyemail@test.com");
    }

    @Test
    void correctlySetsEmailData() {
        Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);

        assertThat(emailDataOptional.isPresent()).isTrue();

        Map<String, Object> emailData = emailDataOptional.get();

        assertThat(emailData.get("parentContactEmail")).isEqualTo("familyemail@test.com");
        assertThat(emailData.get("parentFirstName")).isEqualTo("FirstName");
        assertThat(emailData.get("ccrrName")).isEqualTo("Sample Test CCRR");
        assertThat(emailData.get("ccrrPhoneNumber")).isEqualTo("(603) 555-1244");
        assertThat(emailData.get("childrenInitialsList")).isEqualTo(List.of("F.C.", "S.C."));
        assertThat(emailData.get("confirmationCode")).isEqualTo("ABC123");
        assertThat(emailData.get("providerName")).isEqualTo("BusinessName");
        assertThat(emailData.get("ccapStartDate")).isEqualTo("January 10, 2025");
        assertThat(emailData.get("familyPreferredLanguage")).isEqualTo("English");
    }

    @Test
    void correctlySetsEmailTemplateData() {
        Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);
        ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailDataOptional.get());

        assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
        assertThat(emailTemplate.getSubject()).isEqualTo(
                messageSource.getMessage("email.response-email-for-family.provider-agrees.subject", null, locale));

        String emailCopy = emailTemplate.getBody().getValue();

        assertThat(emailCopy).contains(
                messageSource.getMessage("email.response-email-for-family.provider-agrees.p1", null, locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.response-email-for-family.provider-agrees.p2-has-provider-name",
                        new Object[]{"BusinessName", "Sample Test CCRR"},
                        locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-agrees.p3",
                new Object[]{"F.C. and S.C.", "January 10, 2025"}, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-agrees.p4",
                new Object[]{"ABC123"}, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-agrees.p5",
                new Object[]{"Sample Test CCRR", "(603) 555-1244"},
                locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.response-email-for-family.new-provider-agrees.note", null, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
    }

    @Test
    void correctlyUpdatesEmailSendStatus() {
        assertThat(providerSubmission.getInputData().containsKey("newProviderResponseFamilyConfirmationEmailSent")).isFalse();
        sendEmailClass.send(providerSubmission);

        assertThat(providerSubmission.getInputData().containsKey("newProviderResponseFamilyConfirmationEmailSent")).isTrue();
        assertThat(providerSubmission.getInputData().get("newProviderResponseFamilyConfirmationEmailSent")).isEqualTo("true");
    }

    @Test
    void correctlySkipsEmailSendWhenEmailStatusIsTrue() {
        assertThat(sendEmailClass.skipEmailSend(providerSubmission.getInputData())).isFalse();

        providerSubmission.getInputData().put("newProviderResponseFamilyConfirmationEmailSent", "true");
        assertThat(sendEmailClass.skipEmailSend(providerSubmission.getInputData())).isTrue();
    }

    @Test
    void correctlyEnqueuesSendEmailJob() {
        sendEmailClass.send(providerSubmission);
        verify(sendEmailJob).enqueueSendEmailJob(any(ILGCCEmail.class));
    }

}