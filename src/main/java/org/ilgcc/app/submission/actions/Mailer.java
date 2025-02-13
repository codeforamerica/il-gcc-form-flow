package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Locale;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

abstract class Mailer implements Action {

    static MessageSource messageSource;

    static SubmissionRepositoryService submissionRepositoryService;

    static SendEmailJob sendEmailJob;

    protected static String EMAIL_SENT_STATUS_INPUT_NAME;
    protected static String RECIPIENT_EMAIL_INPUT_NAME;

    public Mailer(SendEmailJob sendEmailJob, SubmissionRepositoryService submissionRepositoryService,
            MessageSource messageSource) {
        this.sendEmailJob = sendEmailJob;
        this.submissionRepositoryService = submissionRepositoryService;
        this.messageSource = messageSource;
    }


    protected static String getSenderName(Locale locale) {
        return messageSource.getMessage("email.sender-name", null, locale);
    }

    protected static String getRecipientEmail(Submission submission) {
        return submission.getInputData().getOrDefault(RECIPIENT_EMAIL_INPUT_NAME, "").toString();
    }

    protected void sendEmail(ILGCCEmail email, Submission submission) {
        sendEmailJob.enqueueSendEmailJob(email);
        updateEmailStatus(submission);
    }

    protected Boolean skipEmailSend(Submission submission) {
        return submission.getInputData().getOrDefault(EMAIL_SENT_STATUS_INPUT_NAME, "false").equals("true");
    }

    private void updateEmailStatus(Submission submission) {
        submission.getInputData().putIfAbsent(EMAIL_SENT_STATUS_INPUT_NAME, "true");
        submissionRepositoryService.save(submission);
    }
}
