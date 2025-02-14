package org.ilgcc.app.submission.actions;

import com.sendgrid.helpers.mail.objects.Content;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Locale;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.email.ILGCCEmail.EmailType;
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
            ILGCCEmail email = prepareEmailCopy(submission, EmailType.PROVIDER_CONFIRMATION_EMAIL);
            sendEmail(email, submission);
        }

        return;
    }

    @Override
    protected String setSubject(Submission submission, Locale locale){
        return messageSource.getMessage("email.family-confirmation.subject", null, locale);
    }

    @Override
    protected Content setBodyCopy(Submission submission, Locale locale){
        return new Content();
    }


}
