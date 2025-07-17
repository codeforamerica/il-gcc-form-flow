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
public class SendUnidentifiedProviderConfirmationEmailTest {

    @MockitoSpyBean
    SendEmailJob sendEmailJob;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    MessageSource messageSource;

    private Submission providerSubmission;

    private SendUnidentifiedProviderConfirmationEmail sendEmailClass;

    private final Locale locale = Locale.ENGLISH;


    @BeforeEach
    void setUp() {
        Submission familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder().withFlow("gcc").with("parentPreferredName", "FirstName")
                .withChild("First", "Child", "true").withChild("Second", "Child", "true")
                .withSubmittedAtDate(OffsetDateTime.now()).withCCRR().withShortCode("ABC123").build());

        providerSubmission = submissionRepositoryService.save(new SubmissionTestBuilder().withFlow("providerresponse")
                .with("familySubmissionId", familySubmission.getId().toString())
                .with("providerResponseContactEmail", "provideremail@test.com").with("providerResponseFirstName", "ProviderFirst")
                .with("providerResponseLastName", "ProviderLast").with("providerResponseBusinessName", "BusinessName")
                .with("providerCareStartDate", "01/10/2025").with("providerResponseAgreeToCare", "true").build());

        sendEmailClass = new SendUnidentifiedProviderConfirmationEmail(sendEmailJob, messageSource, submissionRepositoryService);
    }

    @AfterEach
    void tearDown() {
        submissionRepository.deleteAll();
    }

    @Test
    void correctlySetsEmailRecipient() {
        Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);
        Map<String, Object> emailData = emailDataOptional.get();

        assertThat(sendEmailClass.getRecipientEmail(emailData)).isEqualTo("provideremail@test.com");
    }

    @Test
    void correctlySetsEmailData() {
        Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);

        assertThat(emailDataOptional.isPresent()).isTrue();

        Map<String, Object> emailData = emailDataOptional.get();

        assertThat(emailData.get("confirmationCode")).isEqualTo("ABC123");
        assertThat(emailData.get("childrenInitialsList")).isEqualTo(List.of("F.C.", "S.C."));
        assertThat(emailData.get("providerName")).isEqualTo("BusinessName");
        assertThat(emailData.get("ccrrName")).isEqualTo("Sample Test CCRR");
        assertThat(emailData.get("ccrrPhoneNumber")).isEqualTo("(603) 555-1244");
        assertThat(emailData.get("ccapStartDate")).isEqualTo("January 10, 2025");
        assertThat(emailData.get("providerResponseContactEmail")).isEqualTo("provideremail@test.com");
    }

    @Test
    void correctlySetsEmailTemplateData() {
        Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);
        ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailDataOptional.get());

        assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
        assertThat(emailTemplate.getSubject()).isEqualTo(
                messageSource.getMessage("email.unidentified-provider-confirmation.subject", null, locale));

        String emailCopy = emailTemplate.getBody().getValue();

        assertThat(emailCopy).contains(messageSource.getMessage("email.unidentified-provider-confirmation.p1", null, locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.unidentified-provider-confirmation.p2", new Object[]{"Sample Test CCRR"},
                        locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.unidentified-provider-confirmation.p3",
                new Object[]{"Sample Test CCRR", "(603) 555-1244"}, locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.unidentified-provider-confirmation.p4", new Object[]{"ABC123"}, locale));

        assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
    }


    @Test
    void correctlyUpdatesEmailSendStatus() {
        assertThat(providerSubmission.getInputData().containsKey("providerConfirmationEmailSent")).isFalse();
        sendEmailClass.send(providerSubmission);

        assertThat(providerSubmission.getInputData().containsKey("providerConfirmationEmailSent")).isTrue();
        assertThat(providerSubmission.getInputData().get("providerConfirmationEmailSent")).isEqualTo("true");
    }

    @Test
    void correctlySkipsEmailSendWhenEmailStatusIsTrue() {
        assertThat(sendEmailClass.skipEmailSend(providerSubmission.getInputData())).isFalse();

        providerSubmission.getInputData().put("providerConfirmationEmailSent", "true");
        assertThat(sendEmailClass.skipEmailSend(providerSubmission.getInputData())).isTrue();
    }

    @Test
    void correctlyEnqueuesSendEmailJob() {
        sendEmailClass.send(providerSubmission);
        verify(sendEmailJob).enqueueSendEmailJob(any(ILGCCEmail.class), any(Integer.class));
    }

}