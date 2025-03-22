package org.ilgcc.app.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ilgcc.app.email.ILGCCEmail.FROM_ADDRESS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

import com.sendgrid.helpers.mail.objects.Email;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
public class SendFamilyConfirmationNoProviderEmailTest {

    @MockitoSpyBean
    SendEmailJob sendEmailJob;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    MessageSource messageSource;

    private Submission familySubmission;

    private SendFamilyConfirmationNoProviderEmail sendEmailClass;

    private Locale locale = Locale.ENGLISH;


    @BeforeEach
    void setUp() {
        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("parentFirstName", "FirstName").withChild("First", "Child", "true").withChild("Second", "Child", "true")
                .withSubmittedAtDate(OffsetDateTime.of(2022, 10, 11, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0)))
                .withCCRR()
                .with("parentContactEmail", "familyemail@test.com")
                .with("shareableLink", "tempEmailLink")
                .withShortCode("ABC123")
                .build();

        submissionRepositoryService.save(familySubmission);

        sendEmailClass = new SendFamilyConfirmationNoProviderEmail(sendEmailJob, messageSource, submissionRepositoryService);
    }

    @Test
    void correctlySetsEmailRecipient(){
        Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmission);
        Map<String, Object> emailData = emailDataOptional.get();

        assertThat(sendEmailClass.getRecipientEmail(emailData)).isEqualTo("familyemail@test.com");
    }

    @Test
    void correctlySetsEmailData() {
        Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmission);

        assertThat(emailDataOptional.isPresent()).isTrue();

        Map<String, Object> emailData = emailDataOptional.get();

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
        Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmission);
        ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailDataOptional.get());

        assertThat(emailTemplate.getSenderEmail()).isEqualTo(new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
        assertThat(emailTemplate.getSubject()).isEqualTo(messageSource.getMessage("email.family-confirmation.subject", new Object[]{"ABC123"}, locale));

        String emailCopy = emailTemplate.getBody().getValue();

        assertThat(emailCopy).contains(
                messageSource.getMessage("email.family-confirmation.hi", new Object[]{"FirstName"}, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.you-completed-the-online-application", null, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.sent-for-review",
                new Object[]{"ABC123", "October 10, 2022"}, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.review-without-a-child-care-provider", null, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.a-staff-member", new Object[]{"Sample Test CCRR", "(603) 555-1244"}, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.you-will-receive", null, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
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
        assertThat(sendEmailClass.skipEmailSend(familySubmission)).isFalse();

        familySubmission.getInputData().put("familyConfirmationEmailSent", "true");
        assertThat(sendEmailClass.skipEmailSend(familySubmission)).isTrue();
    }

    @Test
    void correctlyEnqueuesSendEmailJob() {
        sendEmailClass.send(familySubmission);
        verify(sendEmailJob).enqueueSendEmailJob(any(ILGCCEmail.class));
    }

}