package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.context.MessageSource;

@Slf4j
abstract class Mailer implements Action {

    protected static MessageSource messageSource;

    protected final SubmissionRepositoryService submissionRepositoryService;

    protected final SendEmailJob sendEmailJob;

    protected static String emailSentStatusInputName;
    protected static String recipientEmailInputName;

    public Mailer(SendEmailJob sendEmailJob, SubmissionRepositoryService submissionRepositoryService,
            MessageSource messageSource, String emailSentStatusInputName, String recipientEmailInputName) {
        this.sendEmailJob = sendEmailJob;
        this.submissionRepositoryService = submissionRepositoryService;
        this.messageSource = messageSource;
        this.emailSentStatusInputName = emailSentStatusInputName;
        this.recipientEmailInputName = recipientEmailInputName;
    }


    protected static String getSenderName(Locale locale) {
        return messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale);
    }

    protected static String getRecipientEmail(Submission submission) {
        return submission.getInputData().getOrDefault(recipientEmailInputName, "").toString();
    }

    protected void sendEmail(ILGCCEmail email, Submission submission) {
        sendEmailJob.enqueueSendEmailJob(email);
        updateEmailStatus(submission);
    }

    protected Boolean skipEmailSend(Submission submission) {
        return submission.getInputData().getOrDefault(emailSentStatusInputName, "false").equals("true");
    }

    private void updateEmailStatus(Submission submission) {
        submission.getInputData().putIfAbsent(emailSentStatusInputName, "true");
        submissionRepositoryService.save(submission);
    }

    protected Optional<Submission> getFamilyApplication(Submission providerSubmission) {
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getClientId(providerSubmission);
        if (familySubmissionId.isEmpty()) {
            log.warn("No family submission is associated with the provider submission with ID: {}",
                    providerSubmission.getId());
            return Optional.empty();
        }

        return submissionRepositoryService.findById(familySubmissionId.get());
    }
}
