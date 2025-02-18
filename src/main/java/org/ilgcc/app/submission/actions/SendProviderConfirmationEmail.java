package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getCombinedDataForEmails;

import com.sendgrid.helpers.mail.objects.Content;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.context.MessageSource;

@Slf4j
public class SendProviderConfirmationEmail extends Mailer {

    protected static String EMAIL_SENT_STATUS_INPUT_NAME = "providerConfirmationEmailSent";
    protected static String RECIPIENT_EMAIL_INPUT_NAME = "providerResponseContactEmail";

    public SendProviderConfirmationEmail(SendEmailJob sendEmailJob,
            SubmissionRepositoryService submissionRepositoryService,
            MessageSource messageSource) {
        super(sendEmailJob, submissionRepositoryService, messageSource, EMAIL_SENT_STATUS_INPUT_NAME, RECIPIENT_EMAIL_INPUT_NAME);
    }

    @Override
    public void run(Submission submission) {
        if (!skipEmailSend(submission)) {
            Locale locale =
                    submission.getInputData().getOrDefault("languageRead", "English").equals("Spanish") ? Locale.forLanguageTag(
                            "es") : Locale.ENGLISH;
            Optional<Map<String, String>> emailData = getEmailData(submission);
            String subject = emailData.isPresent() ? setSubject(emailData.get(), locale) : "";
            Content body = emailData.isPresent() ? new Content("text/html", setBodyCopy(emailData.get(), locale)) : new Content();

            ILGCCEmail email = ILGCCEmail.createProviderConfirmationEmail(getSenderName(locale), getRecipientEmail(submission),
                    subject, body,
                    submission.getId());
            sendEmail(email, submission);
        }
    }

    protected Optional<Map<String, String>> getEmailData(Submission providerSubmission) {
        Optional<Submission> familySubmission = getFamilyApplication(providerSubmission);
        if (familySubmission.isPresent()) {
            return Optional.of(getCombinedDataForEmails(providerSubmission, familySubmission.get()));
        } else {
            log.warn("Could not send Email. No family submission is associated with the familSubmissionID: {}",
                    providerSubmission.getId());
            return Optional.empty();
        }
    }

    protected String setSubject(Map<String, String> emailData, Locale locale) {
        return messageSource.getMessage("email.family-confirmation.subject", new Object[]{emailData.get("confirmationCode")},
                locale);
    }

    protected String setBodyCopy(Map<String, String> emailData, Locale locale) {
        String p1 = messageSource.getMessage("email.provider-confirmation.p1", null, locale);
        String p2 = messageSource.getMessage("email.provider-confirmation.p2", new Object[]{emailData.get("ccrrName")},
                locale);
        String p3 = messageSource.getMessage("email.provider-confirmation.p3",
                new Object[]{emailData.get("childrenInitials"), emailData.get("ccapStartDate")}, locale);
        String p4 = messageSource.getMessage("email.provider-confirmation.p4",
                new Object[]{emailData.get("confirmationCode")}, locale);
        String p5 = messageSource.getMessage("email.provider-confirmation.p5",
                new Object[]{emailData.get("ccrrName"), emailData.get("ccrrPhoneNumber")},
                locale);
        String p6 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p7 = messageSource.getMessage("email.general.footer.cfa", null, locale);
        return p1 + p2 + p3 + p4 + p5 + p6 + p7;
    }
}



