package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
public class SendFamilyConfirmationEmailTest {

    @MockitoSpyBean
    SendEmailJob sendEmailJob;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    MessageSource messageSource;

    private Submission familySubmission;

    private SendFamilyConfirmationEmail action;

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

        action = new SendFamilyConfirmationEmail(sendEmailJob, messageSource, submissionRepositoryService);
    }

    @Test
    void correctlySetsEmailRecipient(){
        Optional<Map<String, Object>> emailDataOptional = action.getEmailData(familySubmission);
        Map<String, Object> emailData = emailDataOptional.get();

        assertThat(action.getRecipientEmail(emailData)).isEqualTo("familyemail@test.com");
    }

    @Test
    void correctlySetsEmailData() {
        Optional<Map<String, Object>> emailDataOptional = action.getEmailData(familySubmission);

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
    void correctlySetsEmailSubject() {
        Optional<Map<String, Object>> emailDataOptional = action.getEmailData(familySubmission);
        Map<String, Object> emailData = emailDataOptional.get();

        assertThat(action.setSubject(emailData, Locale.ENGLISH)).isEqualTo("Your CCAP confirmation code: ABC123");
    }

    @Test
    void correctlySetsEmailBody() {
        Optional<Map<String, Object>> emailDataOptional = action.getEmailData(familySubmission);
        Map<String, Object> emailData = emailDataOptional.get();

        String emailCopy = action.setBodyCopy(emailData, locale);

        assertThat(emailCopy).contains(
                messageSource.getMessage("email.family-confirmation.p1", new Object[]{"FirstName"}, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.p2", null, locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.family-confirmation.p3", new Object[]{"tempEmailLink"},
                        locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.p4",
                new Object[]{"Sample Test CCRR", "(603) 555-1244"}, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.p5",
                new Object[]{"ABC123", "October 10, 2022"}, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.p6", null, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.p7", null, locale));
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
        assertThat(familySubmission.getInputData().containsKey("familyConfirmationEmailSent")).isFalse();
        action.run(familySubmission);

        assertThat(familySubmission.getInputData().containsKey("familyConfirmationEmailSent")).isTrue();
        assertThat(familySubmission.getInputData().get("familyConfirmationEmailSent")).isEqualTo("true");
    }

    @Test
    void correctlySkipsEmailSendWhenEmailStatusIsTrue() {
        assertThat(action.skipEmailSend(familySubmission)).isFalse();

        familySubmission.getInputData().put("familyConfirmationEmailSent", "true");
        assertThat(action.skipEmailSend(familySubmission)).isTrue();
    }

    @Test
    void correctlyEnqueuesSendEmailJob() {
        action.run(familySubmission);
        verify(sendEmailJob).enqueueSendEmailJob(any(ILGCCEmail.class));
    }

}