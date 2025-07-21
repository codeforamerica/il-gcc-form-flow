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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;


@ActiveProfiles("test")
@SpringBootTest
public class DailyNewApplicationsProviderEmailTemplateTest {

    @MockitoSpyBean
    SendEmailJob sendEmailJob;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    MessageSource messageSource;

    private Submission familySubmission;

    private SendAutomatedProviderOutreachEmail sendEmailClass;

    private final Locale locale = Locale.ENGLISH;

    @BeforeEach
    void setUp() {
        familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("parentFirstName", "FirstName").withChild("First", "Child", "true").withChild("Second", "Child", "true")
                .withSubmittedAtDate(OffsetDateTime.of(2022, 10, 11, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0)))
                .withCCRR()
                .with("parentContactEmail", "familyemail@test.com")
                .with("familyIntendedProviderEmail", "provideremail@test.com")
                .with("shareableLink", "tempEmailLink")
                .withShortCode("ABC123")
                .build());

        sendEmailClass = new SendAutomatedProviderOutreachEmail(sendEmailJob, messageSource, submissionRepositoryService);
    }

    @AfterEach
    void tearDown() {
        submissionRepository.deleteAll();
    }

    @Test
    void correctlySetsEmailRecipient() {
        Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmission);
        Map<String, Object> emailData = emailDataOptional.get();

        assertThat(sendEmailClass.getRecipientEmail(emailData)).isEqualTo("provideremail@test.com");
    }

    @Test
    void correctlySetsEmailData() {
        Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmission);

        assertThat(emailDataOptional.isPresent()).isTrue();

        Map<String, Object> emailData = emailDataOptional.get();

        assertThat(emailData.get("parentFirstName")).isEqualTo("FirstName");
        assertThat(emailData.get("parentContactEmail")).isEqualTo("familyemail@test.com");
        assertThat(emailData.get("familyIntendedProviderEmail")).isEqualTo("provideremail@test.com");
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

        assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
        assertThat(emailTemplate.getSubject()).isEqualTo(
                messageSource.getMessage("email.automated-provider-outreach.subject", null, locale));

        String emailCopy = emailTemplate.getBody().getValue();

        assertThat(emailCopy).contains(
                messageSource.getMessage("email.automated-provider-outreach.p1", null, locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.automated-provider-outreach.p2", new Object[]{"ABC123"}, locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.automated-provider-outreach.p3", new Object[]{"tempEmailLink"},
                        locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.automated-provider-outreach.p4", null,
                        locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.automated-provider-outreach.p5",
                new Object[]{"Sample Test CCRR"}, locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.automated-provider-outreach.p6", null,
                        locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
    }

    @Test
    void correctlyUpdatesEmailSendStatus() {
        assertThat(familySubmission.getInputData().containsKey("providerOutreachEmailSent")).isFalse();
        sendEmailClass.send(familySubmission);

        assertThat(familySubmission.getInputData().containsKey("providerOutreachEmailSent")).isTrue();
        assertThat(familySubmission.getInputData().get("providerOutreachEmailSent")).isEqualTo("true");
    }

    @Test
    void correctlySkipsEmailSendWhenEmailStatusIsTrue() {
        assertThat(sendEmailClass.skipEmailSend(familySubmission.getInputData())).isFalse();

        familySubmission.getInputData().put("providerOutreachEmailSent", "true");
        assertThat(sendEmailClass.skipEmailSend(familySubmission.getInputData())).isTrue();
    }

    @Test
    void correctlyEnqueuesSendEmailJob() {
        sendEmailClass.send(familySubmission);
        verify(sendEmailJob).enqueueSendSubmissionEmailJob(any(ILGCCEmail.class), any(Integer.class));
    }

}