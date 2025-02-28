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
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendProviderAgreesToCareFamilyConfirmationEmail implements Action {

    protected static String EMAIL_SENT_STATUS_INPUT_NAME = "providerResponseFamilyConfirmationEmailSent";
    protected static String RECIPIENT_EMAIL_INPUT_NAME = "parentContactEmail";

    protected static Locale locale;
    private static MessageSource messageSource;
    private final SubmissionRepositoryService submissionRepositoryService;
    private final SendEmailJob sendEmailJob;


    public SendProviderAgreesToCareFamilyConfirmationEmail(SendEmailJob sendEmailJob, MessageSource messageSource,
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

            locale = emailData.get().get("familyPreferredLanguage").equals("Spanish") ? Locale.forLanguageTag(
                    "es") : Locale.ENGLISH;

            ILGCCEmail email = ILGCCEmail.createProviderAgreesToCareFamilyConfirmationEmail(getSenderName(locale),
                    getRecipientEmail(emailData.get()),
                    setSubject(emailData.get(), locale), new Content("text/html", setBodyCopy(emailData.get(), locale)),
                    providerSubmission.getId());
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
            log.warn(
                    "SendProviderAgreesToCareFamilyConfirmationEmail: Skipping email send because there is no family submission associated with the provider submission with ID : {}",
                    providerSubmission.getId());
            return Optional.empty();
        }
    }

    protected static String getSenderName(Locale locale) {
        return messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale);
    }

    protected static String getRecipientEmail(Map<String, Object> emailData) {
        String recipientEmail = emailData.get(RECIPIENT_EMAIL_INPUT_NAME).toString();

        if (recipientEmail.isBlank()) {
            log.warn(
                    "SendProviderAgreesToCareFamilyConfirmationEmail: Skipping email send because there is no email associated with the submission: {}",
                    emailData.get("confirmationCode"));
        }

        return recipientEmail;
    }

    protected String setSubject(Map<String, Object> emailData, Locale locale) {
        return messageSource.getMessage("email.response-email-for-family.provider-agrees.subject", null, locale);
    }

    protected String setBodyCopy(Map<String, Object> emailData, Locale locale) {
        String p1 = messageSource.getMessage("email.response-email-for-family.provider-agrees.p1", null, locale);

        String providerName = emailData.get("providerName").toString();
        String p2 = providerName.isEmpty() ? messageSource.getMessage(
                "email.response-email-for-family.provider-agrees.p2-no-provider-name", new Object[]{emailData.get("ccrrName")},
                locale)
                : messageSource.getMessage("email.response-email-for-family.provider-agrees.p2-has-provider-name",
                        new Object[]{providerName, emailData.get("ccrrName")}, locale);
        String p3 = messageSource.getMessage("email.response-email-for-family.provider-agrees.p3",
                new Object[]{formatListIntoReadableString((List<String>) emailData.get("childrenInitialsList"),
                        messageSource.getMessage("general.and", null, locale)), emailData.get("ccapStartDate")}, locale);
        String p4 = messageSource.getMessage("email.response-email-for-family.provider-agrees.p4",
                new Object[]{emailData.get("confirmationCode")},
                locale);
        String p5 = messageSource.getMessage("email.response-email-for-family.provider-agrees.p5",
                new Object[]{emailData.get("ccrrName"), emailData.get("ccrrPhoneNumber")}, locale);
        String p6 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p7 = messageSource.getMessage("email.general.footer.cfa", null, locale);
        return p1 + p2 + p3 + p4 + p5 + p6 + p7;
    }

    protected void sendEmail(ILGCCEmail email, Submission submission) {
        log.info("SendProviderAgreesToCareFamilyConfirmationEmail: About to enqueue the Send Email Job for submissionId: {}",
                submission.getId());
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
            return Optional.empty();
        }

        return submissionRepositoryService.findById(familySubmissionId.get());
    }
}
