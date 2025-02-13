package org.ilgcc.app.submission.actions;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.context.MessageSource;

public class SendProviderConfirmationEmail extends Mailer {

    protected static String EMAIL_SENT_STATUS_INPUT_NAME = "providerConfirmationEmailSent";
    protected static String RECIPIENT_EMAIL_INPUT_NAME = "providerResponseContactEmail";

    public SendProviderConfirmationEmail(SendEmailJob sendEmailJob,
            SubmissionRepositoryService submissionRepositoryService,
            MessageSource messageSource) {
        super(sendEmailJob, submissionRepositoryService, messageSource);
    }

    @Override
    public void run(Submission submission) {
        if(!skipEmailSend(submission)){
            ILGCCEmail email = ILGCCEmail.createProviderConfirmationEmail();
            sendEmail(email, submission);
        }

        return;
    }


}
