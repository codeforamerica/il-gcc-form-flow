package org.ilgcc.app.submission.actions;

import com.sendgrid.helpers.mail.objects.Content;
import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.email.ILGCCEmail.EmailType;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.context.MessageSource;

@Slf4j
abstract class Mailer implements Action {

    protected static MessageSource messageSource;

    protected final SubmissionRepositoryService submissionRepositoryService;

    protected final SendEmailJob sendEmailJob;

    protected static String EMAIL_SENT_STATUS_INPUT_NAME;
    protected static String RECIPIENT_EMAIL_INPUT_NAME;

    public Mailer(SendEmailJob sendEmailJob, SubmissionRepositoryService submissionRepositoryService,
            MessageSource messageSource) {
        this.sendEmailJob = sendEmailJob;
        this.submissionRepositoryService = submissionRepositoryService;
        this.messageSource = messageSource;
    }


    protected static String setSenderName(Locale locale) {
        return messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale);
    }

    protected static String setRecipientName(Submission submission) {
        return submission.getInputData().getOrDefault(RECIPIENT_EMAIL_INPUT_NAME, "").toString();
    }

    protected String setSubject(Submission submission, Locale locale) {
        return messageSource.getMessage("email.family-confirmation.subject", null, locale);
    }

    protected Content setBodyCopy(Submission submission, Locale locale) {
        return new Content();
    }


    protected ILGCCEmail prepareEmailCopy(Submission submission, EmailType emailType) {
        Locale locale =
                submission.getInputData().getOrDefault("languageRead", "English").equals("Spanish") ? Locale.forLanguageTag(
                        "es") : Locale.ENGLISH;

        return new ILGCCEmail(setSenderName(locale), setRecipientName(submission), setSubject(submission, locale),
                setBodyCopy(submission, locale), emailType, submission.getId());

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

    protected Optional<Submission> getFamilyApplication(Submission providerSubmission) {
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getClientId(providerSubmission);
        if (familySubmissionId.isEmpty()) {
            log.warn("No family submission is associated with the provider submission with ID: {}", providerSubmission.getId());
            return Optional.empty();
        }

        return submissionRepositoryService.findById(familySubmissionId.get());
    }
}
