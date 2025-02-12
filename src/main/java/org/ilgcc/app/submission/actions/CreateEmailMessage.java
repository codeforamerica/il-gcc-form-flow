package org.ilgcc.app.submission.actions;

import com.sendgrid.helpers.mail.objects.Content;
import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.EmailConstants.EmailType;
import org.ilgcc.app.email.EmailMessage;
import org.ilgcc.app.utils.DateUtilities;
import org.ilgcc.jobs.SendEmailJob;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.context.MessageSource;

@Slf4j
public class CreateEmailMessage implements Action {

    public static MessageSource messageSource;

    public static Submission submission;

    public static Locale locale;
    public static String EMAIL_SENT_STATUS_INPUT_NAME;
    public static String RECIPIENT_EMAIL_INPUT_NAME;
    public static String SENDER_MESSAGE_KEY = "email.sender-name";
    public static String SUBJECT_MESSAGE_KEY = "email.family-confirmation.subject";

    public static EmailType emailType;

    private final SendEmailJob sendEmailJob;
    private final SubmissionRepositoryService submissionRepositoryService;

    public CreateEmailMessage(SendEmailJob sendEmailJob, SubmissionRepositoryService submissionRepositoryService, EmailType emailType) {
        this.sendEmailJob = sendEmailJob;
        this.submissionRepositoryService = submissionRepositoryService;
        this.emailType = emailType;
    }

    @Override
    public void run(Submission submission) {
        this.submission = submission;
        this.locale =
                submission.getInputData().getOrDefault("languageRead", "English").equals("Spanish") ? Locale.forLanguageTag(
                        "es") : Locale.ENGLISH;
        if(emailHasBeenSent()){
            log.warn("{} confirmation email has already been sent for submission with ID: {}", EMAIL_SENT_STATUS_INPUT_NAME, submission.getId());
            return;
        }
        if(recipientEmail().isEmpty()){
            log.warn("{} email field was empty when attempting to send family confirmation email for submission with ID: {}",
                    RECIPIENT_EMAIL_INPUT_NAME, submission.getId());
            return;
        }
        sendEmailJob.enqueueSendEmailJob(recipientEmail(), senderName(), emailSubject(new Object[]{submission.getShortCode()}), emailType.getDescription(), emailBody(), submission);

        submission.getInputData().put(EMAIL_SENT_STATUS_INPUT_NAME, "true");
        submissionRepositoryService.save(submission);
    }
    protected Boolean emailHasBeenSent() {
        String emailConfirmationSent = (String) submission.getInputData()
                .getOrDefault(EMAIL_SENT_STATUS_INPUT_NAME, "false");
        if (emailConfirmationSent.equals("true")) {
            return true;
        }
        return false;
    }

    protected String recipientEmail() {
        String recipientEmail = submission.getInputData().get(RECIPIENT_EMAIL_INPUT_NAME).toString();
        if (recipientEmail == null || recipientEmail.isEmpty()) {
            return "";
        } else {
            return recipientEmail;
        }
    }

    protected String emailSubject(Object[] params) {
        return messageSource.getMessage(SUBJECT_MESSAGE_KEY, params, locale);
    }

    protected String senderName(){
        return messageSource.getMessage(SENDER_MESSAGE_KEY, null, locale);
    }

    protected Content emailBody() {
        return new Content();
    }

}
