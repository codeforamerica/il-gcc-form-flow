package org.ilgcc.app.submission.actions;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import formflow.library.config.submission.Action;
@Component
@Slf4j
public class SendProviderAndFamilyEmails implements Action {

    protected final SendEmailJob sendEmailJob;
    protected static MessageSource messageSource;
    protected final SubmissionRepositoryService submissionRepositoryService;


    public SendProviderAndFamilyEmails(SendEmailJob sendEmailJob, MessageSource messageSource,
            SubmissionRepositoryService submissionRepositoryService) {
        this.sendEmailJob = sendEmailJob;
        this.messageSource = messageSource;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission submission) {
        new SendProviderConfirmationEmail(sendEmailJob,messageSource,submissionRepositoryService).run(submission);
        new SendProviderDeclinesCareFamilyConfirmationEmail(sendEmailJob,messageSource,submissionRepositoryService).run(submission);
        new SendProviderAgreesToCareFamilyConfirmationEmail(sendEmailJob,messageSource,submissionRepositoryService).run(submission);
    }
}
