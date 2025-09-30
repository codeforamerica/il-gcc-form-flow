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
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.jobs.SendEmailJob;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;


@ActiveProfiles("test")
@SpringBootTest
public class SendFamilyConfirmationNoProviderEmailTest {

    @MockitoSpyBean
    SendEmailJob sendEmailJob;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    MessageSource messageSource;

    private Submission familySubmission;

    private SendFamilyConfirmationNoProviderEmail sendEmailClass;

    Map<String, Object> emailData;

    private final Locale locale = Locale.ENGLISH;


    @BeforeEach
    void setUp() {
        familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("parentFirstName", "FirstName").withChild("First", "Child", "true").withChild("Second", "Child", "true")
                .withSubmittedAtDate(OffsetDateTime.of(2022, 10, 11, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0)))
                .withCCRR()
                .with("parentContactEmail", "familyemail@test.com")
                .with("shareableLink", "tempEmailLink")
                .withShortCode("ABC123")
                .build());

        sendEmailClass = new SendFamilyConfirmationNoProviderEmail(sendEmailJob, messageSource, submissionRepositoryService);

        Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmission);
        emailData = emailDataOptional.get();
    }

    @AfterEach
    void tearDown() {
        submissionRepository.deleteAll();
    }

    @Test
    void correctlySetsEmailRecipient(){
        assertThat(sendEmailClass.getRecipientEmail(emailData)).isEqualTo("familyemail@test.com");
    }

    @Test
    void correctlyUpdatesEmailSendStatus() {
        assertThat(familySubmission.getInputData().containsKey("familyConfirmationEmailSent")).isFalse();
        sendEmailClass.send(familySubmission);

        assertThat(familySubmission.getInputData().containsKey("familyConfirmationEmailSent")).isTrue();
        assertThat(familySubmission.getInputData().get("familyConfirmationEmailSent")).isEqualTo("true");
    }

    @Test
    void correctlySkipsEmailSendWhenEmailStatusIsTrue() {
        assertThat(sendEmailClass.skipEmailSend(familySubmission.getInputData())).isFalse();

        familySubmission.getInputData().put("familyConfirmationEmailSent", "true");
        assertThat(sendEmailClass.skipEmailSend(familySubmission.getInputData())).isTrue();
    }

    @Test
    void correctlyEnqueuesSendEmailJob() {
        sendEmailClass.send(familySubmission);
        verify(sendEmailJob).enqueueSendSubmissionEmailJob(any(ILGCCEmail.class), any(Integer.class));
    }

    @Nested
    class whenSubmissionHasNoProviders {
        @Test
        void correctlySetsEmailData() {
            assertThat(emailData.get("parentFirstName")).isEqualTo("FirstName");
            assertThat(emailData.get("parentContactEmail")).isEqualTo("familyemail@test.com");
            assertThat(emailData.get("ccrrName")).isEqualTo("Sample Test CCRR");
            assertThat(emailData.get("ccrrPhoneNumber")).isEqualTo("(603) 555-1244");
            assertThat(emailData.get("childrenInitialsList")).isEqualTo(List.of("F.C.", "S.C."));
            assertThat(emailData.get("confirmationCode")).isEqualTo("ABC123");
            assertThat(emailData.get("submittedDate")).isEqualTo("October 10, 2022");
            assertThat(emailData.get("shareableLink")).isEqualTo("tempEmailLink");
        }

        @Test
        void correctlySetsEmailTemplateData() {
            ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

            assertThat(emailTemplate.getSenderEmail()).isEqualTo(new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
            assertThat(emailTemplate.getSubject()).isEqualTo(messageSource.getMessage("email.general.subject.confirmation-code", new Object[]{"ABC123"}, locale));

            String emailCopy = emailTemplate.getBody().getValue();

            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p1", new Object[]{"FirstName"}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.p2", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.no-provider.p3",
                    new Object[]{"ABC123", "October 10, 2022"}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.no-provider.p4", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.no-provider.p5", new Object[]{
                    "Sample Test CCRR", "(603) 555-1244"}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.no-provider.p6", null,
                    locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
        }
    }

    @Nested
    class whenNoProviderSelectedInSingleProviderFlow {

        @BeforeEach
        void setUp() {
            familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .with("parentFirstName", "FirstName").withChild("First", "Child", "true").withChild("Second", "Child", "true")
                    .withSubmittedAtDate(OffsetDateTime.of(2022, 10, 11, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0)))
                    .withCCRR()
                    .with("providers", List.of())
                    .with("parentContactEmail", "familyemail@test.com")
                    .with("shareableLink", "tempEmailLink")
                    .withShortCode("ABC123")
                    .build());

            sendEmailClass = new SendFamilyConfirmationNoProviderEmail(sendEmailJob, messageSource, submissionRepositoryService);
            Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmission);
            emailData = emailDataOptional.get();

        }

        @Test
        void correctlySetsEmailData() {
            assertThat(emailData.get("parentFirstName")).isEqualTo("FirstName");
            assertThat(emailData.get("parentContactEmail")).isEqualTo("familyemail@test.com");
            assertThat(emailData.get("ccrrName")).isEqualTo("Sample Test CCRR");
            assertThat(emailData.get("ccrrPhoneNumber")).isEqualTo("(603) 555-1244");
            assertThat(emailData.get("childrenInitialsList")).isEqualTo(List.of("F.C.", "S.C."));
            assertThat(emailData.get("confirmationCode")).isEqualTo("ABC123");
            assertThat(emailData.get("submittedDate")).isEqualTo("October 10, 2022");
            assertThat(emailData.get("shareableLink")).isEqualTo("tempEmailLink");
        }

        @Test
        void correctlySetsEmailTemplateData() {
            ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

            assertThat(emailTemplate.getSenderEmail()).isEqualTo(new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
            assertThat(emailTemplate.getSubject()).isEqualTo(messageSource.getMessage("email.general.subject.confirmation-code", new Object[]{"ABC123"}, locale));

            String emailCopy = emailTemplate.getBody().getValue();

            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p1", new Object[]{"FirstName"}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.p2", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.no-provider.p3",
                    new Object[]{"ABC123", "October 10, 2022"}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.no-provider.p4", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.no-provider.p5", new Object[]{
                    "Sample Test CCRR", "(603) 555-1244"}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.no-provider.p6", null,
                    locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
        }
    }

}