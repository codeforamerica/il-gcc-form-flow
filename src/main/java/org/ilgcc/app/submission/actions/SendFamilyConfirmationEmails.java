package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendFamilyConfirmationEmail;
import org.ilgcc.app.email.SendFamilyConfirmationNoProviderEmail;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendFamilyConfirmationEmails implements Action {

    protected final SendEmailJob sendEmailJob;
    protected static MessageSource messageSource;
    protected final SubmissionRepositoryService submissionRepositoryService;


    public SendFamilyConfirmationEmails(SendEmailJob sendEmailJob, MessageSource messageSource,
            SubmissionRepositoryService submissionRepositoryService) {
        this.sendEmailJob = sendEmailJob;
        this.messageSource = messageSource;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission familySubmission) {
        boolean hasProvider = familySubmission.getInputData().getOrDefault("hasChosenProvider", "false").equals("true");
        if (hasProvider) {
            new SendFamilyConfirmationEmail(familySubmission);
        } else {
            new SendFamilyConfirmationNoProviderEmail(familySubmission);
        }

    }
}
