package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getFamilySubmissionDataForEmails;

import com.sendgrid.helpers.mail.objects.Content;
import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.email.ILGCCEmail.EmailType;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendFamilyConfirmationEmail implements Action {

    protected static String EMAIL_SENT_STATUS_INPUT_NAME = "familyConfirmationEmailSent";
    protected static String RECIPIENT_EMAIL_INPUT_NAME = "parentContactEmail";

    protected static Locale locale;

    private final SendEmailJob sendEmailJob;
    private static MessageSource messageSource;
    private final SubmissionRepositoryService submissionRepositoryService;

    public SendFamilyConfirmationEmail(SendEmailJob sendEmailJob, MessageSource messageSource,
            SubmissionRepositoryService submissionRepositoryService) {
        this.sendEmailJob = sendEmailJob;
        this.messageSource = messageSource;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission familySubmission) {
        if (!skipEmailSend(familySubmission)) {
            Optional<Map<String, Object>> emailData = getEmailData(familySubmission);

            if (emailData.isEmpty()) {
                return;
            }

            locale = emailData.get().get("familyPreferredLanguage").equals("Spanish") ? Locale.forLanguageTag(
                    "es") : Locale.ENGLISH;

            ILGCCEmail email = ILGCCEmail.createFamilyConfirmationEmail(getSenderName(locale),
                    getRecipientEmail(emailData.get()),
                    setSubject(emailData.get(), locale), new Content("text/html", setBodyCopy(emailData.get(), locale)),
                    familySubmission.getId());
            sendEmail(email, familySubmission);
        }
    }

    protected Boolean skipEmailSend(Submission submission) {
        return submission.getInputData().getOrDefault(EMAIL_SENT_STATUS_INPUT_NAME, "false").equals("true");
    }

    protected Optional<Map<String, Object>> getEmailData(Submission familySubmission) {
        return Optional.of(getFamilySubmissionDataForEmails(familySubmission));
    }

    protected static String getSenderName(Locale locale) {
        return messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale);
    }

    protected static String getRecipientEmail(Map<String, Object> emailData) {
        String recipientEmail = emailData.get(RECIPIENT_EMAIL_INPUT_NAME).toString();

        if (recipientEmail.isBlank()) {
            log.warn(
                    "{}: Skipping email send because there is no email associated with the submission: {}",
                    EmailType.FAMILY_CONFIRMATION_EMAIL.getDescription(), emailData.get("familySubmissionId"));
        }

        return recipientEmail;
    }

    protected String setSubject(Map<String, Object> emailData, Locale locale) {
        return messageSource.getMessage("email.family-confirmation.subject", new Object[]{emailData.get("confirmationCode")},
                locale);
    }

    protected String setBodyCopy(Map<String, Object> emailData, Locale locale) {
        String p1 = messageSource.getMessage("email.family-confirmation.p1", new Object[]{emailData.get("parentFirstName")},
                locale);
        String p2 = messageSource.getMessage("email.family-confirmation.p2", null, locale);
        String p3 = messageSource.getMessage("email.family-confirmation.p3", new Object[]{emailData.get("shareableLink")}, locale);
        String p4 = messageSource.getMessage("email.family-confirmation.p4",
                new Object[]{emailData.get("ccrrName"), emailData.get("ccrrPhoneNumber")}, locale);
        String p5 = messageSource.getMessage("email.family-confirmation.p5",
                new Object[]{emailData.get("confirmationCode"), emailData.get("submittedDate")},
                locale);
        String p6 = messageSource.getMessage("email.family-confirmation.p6", null, locale);
        String p7 = messageSource.getMessage("email.family-confirmation.p7", null, locale);
        String p8 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p9 = messageSource.getMessage("email.general.footer.cfa", null, locale);
        return p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
    }

    protected void sendEmail(ILGCCEmail email, Submission submission) {
        log.info("{}: About to enqueue the Send Email Job for submissionId: {}",  EmailType.FAMILY_CONFIRMATION_EMAIL.getDescription(), submission.getId());
        sendEmailJob.enqueueSendEmailJob(email);
        updateEmailStatus(submission);
    }

    private void updateEmailStatus(Submission submission) {
        submission.getInputData().putIfAbsent(EMAIL_SENT_STATUS_INPUT_NAME, "true");
        submissionRepositoryService.save(submission);
    }
}
