package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.email.ILGCCEmail.EmailType;
import org.ilgcc.app.email.ILGCCEmailTemplate;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.context.MessageSource;

@Slf4j
abstract class SendEmail implements Action {

    protected static String emailSentStatusInputName;
    protected static String recipientEmailInputName;

    protected static Locale locale;

    protected final SendEmailJob sendEmailJob;
    protected static MessageSource messageSource;
    protected final SubmissionRepositoryService submissionRepositoryService;

    public SendEmail(SendEmailJob sendEmailJob, MessageSource messageSource,
            SubmissionRepositoryService submissionRepositoryService, String emailSentStatusInputName,
            String recipientEmailInputName) {
        this.sendEmailJob = sendEmailJob;
        this.messageSource = messageSource;
        this.submissionRepositoryService = submissionRepositoryService;
        this.emailSentStatusInputName = emailSentStatusInputName;
        this.recipientEmailInputName = recipientEmailInputName;

    }

    @Override
    public void run(Submission submission) {
        if (!skipEmailSend(submission)) {
            Optional<Map<String, Object>> emailData = getEmailData(submission);

            if (emailData.isEmpty()) {
                return;
            }

            locale = emailData.get().get("familyPreferredLanguage").equals("Spanish") ? Locale.forLanguageTag(
                    "es") : Locale.ENGLISH;

            ILGCCEmail email = new ILGCCEmail(getRecipientEmail(emailData.get()), emailTemplate(emailData.get()),
                    submission.getId());
            sendEmail(email, submission);
        }
    }

    protected ILGCCEmailTemplate emailTemplate(Map<String, Object> emailData) {
        return new ILGCCEmailTemplate();
    }

    protected Optional<Map<String, Object>> getEmailData(Submission familySubmission) {
        return Optional.empty();
    }

    protected Boolean skipEmailSend(Submission submission) {
        return submission.getInputData().getOrDefault(emailSentStatusInputName, "false").equals("true");
    }

    protected static String getRecipientEmail(Map<String, Object> emailData) {
        String recipientEmail = emailData.get(recipientEmailInputName).toString();

        if (recipientEmail.isBlank()) {
            log.warn(
                    "{}: Skipping email send because there is no email associated with the submission: {}",
                    EmailType.FAMILY_CONFIRMATION_EMAIL.getDescription(), emailData.get("familySubmissionId"));
        }

        return recipientEmail;
    }

    protected void sendEmail(ILGCCEmail email, Submission submission) {
        log.info("{}: About to enqueue the Send Email Job for submissionId: {}",
                email.getEmailType().getDescription(), submission.getId());
        sendEmailJob.enqueueSendEmailJob(email);
        updateEmailStatus(submission);
    }

    private void updateEmailStatus(Submission submission) {
        submission.getInputData().putIfAbsent(emailSentStatusInputName, "true");
        submissionRepositoryService.save(submission);
    }

    protected Optional<Submission> getFamilyApplication(Submission providerSubmission) {
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionId.isEmpty()) {
            return Optional.empty();
        }

        return submissionRepositoryService.findById(familySubmissionId.get());
    }
}
