package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.formatListIntoReadableString;
import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getCombinedDataForEmails;

import com.sendgrid.helpers.mail.objects.Content;
import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendProviderConfirmationEmail implements Action {

    protected static String EMAIL_SENT_STATUS_INPUT_NAME = "providerConfirmationEmailSent";
    protected static String RECIPIENT_EMAIL_INPUT_NAME = "providerResponseContactEmail";

    protected static Locale locale;

    protected static MessageSource messageSource;

    protected final SubmissionRepositoryService submissionRepositoryService;

    protected final SendEmailJob sendEmailJob;

    public SendProviderConfirmationEmail(SendEmailJob sendEmailJob, MessageSource messageSource,
            SubmissionRepositoryService submissionRepositoryService) {
        this.sendEmailJob = sendEmailJob;
        this.messageSource = messageSource;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission providerSubmission) {
        if (!skipEmailSend(providerSubmission)) {
            Optional<Map<String, Object>> emailData = getEmailData(providerSubmission);

            if (emailData.isEmpty()) {
                return;
            }

            locale = LocaleContextHolder.getLocale();

            ILGCCEmail email = ILGCCEmail.createProviderConfirmationEmail(getSenderName(locale),
                    getRecipientEmail(providerSubmission), setSubject(emailData.get(), locale),
                    new Content("text/html", setBodyCopy(emailData.get(), locale)), providerSubmission.getId());
            sendEmail(email, providerSubmission);
        }
    }

    protected Boolean skipEmailSend(Submission submission) {
        boolean emailSent = submission.getInputData().getOrDefault(EMAIL_SENT_STATUS_INPUT_NAME, "false").equals("true");
        boolean providerAgreedToCare = submission.getInputData().getOrDefault("providerResponseAgreeToCare", "false")
                .equals("true");

        return emailSent || !providerAgreedToCare;
    }

    protected Optional<Map<String, Object>> getEmailData(Submission providerSubmission) {
        Optional<Submission> familySubmission = getFamilyApplication(providerSubmission);
        if (familySubmission.isPresent()) {
            return Optional.of(getCombinedDataForEmails(providerSubmission, familySubmission.get()));
        } else {
            log.warn("Could not send Email. No family submission is associated with the providerSubmissionId: {}",
                    providerSubmission.getId());
            return Optional.empty();
        }
    }

    protected static String getSenderName(Locale locale) {
        return messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale);
    }

    protected static String getRecipientEmail(Submission submission) {
        return submission.getInputData().getOrDefault(RECIPIENT_EMAIL_INPUT_NAME, "").toString();
    }

    protected String setSubject(Map<String, Object> emailData, Locale locale) {
        return messageSource.getMessage("email.family-confirmation.subject", new Object[]{emailData.get("confirmationCode")},
                locale);
    }

    protected String setBodyCopy(Map<String, Object> emailData, Locale locale) {
        String p1 = messageSource.getMessage("email.provider-confirmation.p1", null, locale);
        String p2 = messageSource.getMessage("email.provider-confirmation.p2", new Object[]{emailData.get("ccrrName")}, locale);
        String p3 = messageSource.getMessage("email.provider-confirmation.p3", new Object[]{
                formatListIntoReadableString((List<String>) emailData.get("childrenInitialsList"),
                        messageSource.getMessage("general.and", null, locale)), emailData.get("ccapStartDate")}, locale);
        String p4 = messageSource.getMessage("email.provider-confirmation.p4", new Object[]{emailData.get("confirmationCode")},
                locale);
        String p5 = messageSource.getMessage("email.provider-confirmation.p5",
                new Object[]{emailData.get("ccrrName"), emailData.get("ccrrPhoneNumber")}, locale);
        String p6 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p7 = messageSource.getMessage("email.general.footer.cfa", null, locale);
        return p1 + p2 + p3 + p4 + p5 + p6 + p7;
    }

    protected void sendEmail(ILGCCEmail email, Submission submission) {
        sendEmailJob.enqueueSendEmailJob(email);
        updateEmailStatus(submission);
    }

    private void updateEmailStatus(Submission submission) {
        submission.getInputData().putIfAbsent(EMAIL_SENT_STATUS_INPUT_NAME, "true");
        submissionRepositoryService.save(submission);
    }

    private Optional<Submission> getFamilyApplication(Submission providerSubmission) {
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionId.isEmpty()) {
            log.warn("No family submission is associated with the provider submission with ID: {}", providerSubmission.getId());
            return Optional.empty();
        }

        return submissionRepositoryService.findById(familySubmissionId.get());
    }
}


