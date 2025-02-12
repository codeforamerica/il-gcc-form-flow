package org.ilgcc.app.submission.actions;

import com.sendgrid.helpers.mail.objects.Content;
import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.EmailConstants.EmailType;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.context.MessageSource;

@Slf4j
public class CreateEmailMessage implements Action {

    public static MessageSource messageSource;
    protected static Submission currentSubmission;

    protected static Locale locale;
    protected static String EMAIL_SENT_STATUS_INPUT_NAME;
    protected static String RECIPIENT_EMAIL_INPUT_NAME;
    protected static String SENDER_MESSAGE_KEY = "email.sender-name";
    protected static String SUBJECT_MESSAGE_KEY = "email.family-confirmation.subject";
    protected static EmailType emailType;
    protected final SendEmailJob sendEmailJob;
    protected final SubmissionRepositoryService submissionRepositoryService;

    public CreateEmailMessage(SendEmailJob sendEmailJob, SubmissionRepositoryService submissionRepositoryService,
            EmailType emailType) {
        this.sendEmailJob = sendEmailJob;
        this.submissionRepositoryService = submissionRepositoryService;
        this.emailType = emailType;
    }

    public void sendEmailMessage(Submission submission) {
        this.currentSubmission = submission;
        this.locale =
                submission.getInputData().getOrDefault("languageRead", "English").equals("Spanish") ? Locale.forLanguageTag(
                        "es") : Locale.ENGLISH;
        if (emailHasBeenSent()) {
            log.warn("{} confirmation email has already been sent for submission with ID: {}", EMAIL_SENT_STATUS_INPUT_NAME,
                    submission.getId());
            return;
        }
        if (recipientEmail().isEmpty()) {
            log.warn("{} email field was empty when attempting to send family confirmation email for submission with ID: {}",
                    RECIPIENT_EMAIL_INPUT_NAME, submission.getId());
            return;
        }
        sendEmailJob.enqueueSendEmailJob(recipientEmail(), senderName(), emailSubject(), emailType.getDescription(), emailBody(),
                submission);

        updateEmailSentStatus();
    }

    protected Boolean emailHasBeenSent() {
        String emailConfirmationSent = (String) currentSubmission.getInputData()
                .getOrDefault(EMAIL_SENT_STATUS_INPUT_NAME, "false");
        if (emailConfirmationSent.equals("true")) {
            return true;
        }
        return false;
    }

    protected String recipientEmail() {
        String recipientEmail = currentSubmission.getInputData().get(RECIPIENT_EMAIL_INPUT_NAME).toString();
        if (recipientEmail == null || recipientEmail.isEmpty()) {
            return "";
        } else {
            return recipientEmail;
        }
    }

    protected String emailSubject() {
        return messageSource.getMessage(SUBJECT_MESSAGE_KEY, new Object[]{currentSubmission.getShortCode()}, locale);
    }

    protected String senderName() {
        return messageSource.getMessage(SENDER_MESSAGE_KEY, null, locale);
    }

    protected Content emailBody() {
        return new Content();
    }

    protected void updateEmailSentStatus() {
        currentSubmission.getInputData().put(EMAIL_SENT_STATUS_INPUT_NAME, "true");
        submissionRepositoryService.save(currentSubmission);
    }

}
