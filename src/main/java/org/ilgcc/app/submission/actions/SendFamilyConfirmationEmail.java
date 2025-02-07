package org.ilgcc.app.submission.actions;

import com.sendgrid.helpers.mail.objects.Content;
import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.EmailConstants;
import org.ilgcc.app.email.EmailConstants.EmailType;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendFamilyConfirmationEmail implements Action {

    private final SendEmailJob sendEmailJob;
    private final MessageSource messageSource;
    private final SubmissionRepositoryService submissionRepositoryService;
    private final String EMAIL_SUBJECT_KEY = "email.family-confirmation.subject";
    private final String EMAIL_SENDER_NAME_KEY = "email.sender-name";
    private final String RECIPIENT_EMAIL_INPUT_NAME = "parentContactEmail";

    public SendFamilyConfirmationEmail(SendEmailJob sendEmailJob, MessageSource messageSource,
            SubmissionRepositoryService submissionRepositoryService) {
        this.sendEmailJob = sendEmailJob;
        this.messageSource = messageSource;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission familySubmission) {
        sendEmail(familySubmission, EmailType.FAMILY_CONFIRMATION_EMAIL);
    }

    public void sendEmail(Submission familySubmission, EmailType emailType) {
        String confirmationEmailSent = (String) familySubmission.getInputData()
                .getOrDefault(emailType.getSentVariable(), "false");
        if (confirmationEmailSent.equals("true")) {
            log.warn("Family confirmation email has already been sent for submission with ID: {}", familySubmission.getId());
            return;
        }

        String familyEmail = familySubmission.getInputData().get(RECIPIENT_EMAIL_INPUT_NAME).toString();
        if (familyEmail == null || familyEmail.isEmpty()) {
            log.warn("Family email was empty when attempting to send family confirmation email for submission with ID: {}",
                    familySubmission.getId());
            return;
        }

        String familySubmissionShortCode = familySubmission.getShortCode();

        Locale locale =
                familySubmission.getInputData().getOrDefault("languageRead", "English").equals("Spanish") ? Locale.forLanguageTag(
                        "es") : Locale.ENGLISH;
        String subject = messageSource.getMessage(EMAIL_SUBJECT_KEY, new Object[]{familySubmissionShortCode},
                locale);

        String senderName = messageSource.getMessage(EMAIL_SENDER_NAME_KEY, null, locale);

        sendEmailJob.enqueueSendEmailJob(familyEmail, senderName, subject,
                emailType.getDescription(),
                createFamilyConfirmationEmailBody(familySubmission, familySubmissionShortCode, locale), familySubmission);
        familySubmission.getInputData().put(EmailType.FAMILY_CONFIRMATION_EMAIL.getSentVariable(), "true");
        submissionRepositoryService.save(familySubmission);
    }


    private Content createFamilyConfirmationEmailBody(Submission familySubmission, String confirmationCode, Locale locale) {
        String parentFirstName = familySubmission.getInputData().get("parentFirstName").toString();
        String emailLink = familySubmission.getInputData().get("emailLink").toString();
        String ccrAndR = familySubmission.getInputData().get("ccrrName").toString();
        String submittedDate = SubmissionUtilities.getFormattedSubmittedAtDate(familySubmission);

        String p1 = messageSource.getMessage("email.family-confirmation.p1", new Object[]{parentFirstName}, locale);
        String p2 = messageSource.getMessage("email.family-confirmation.p2", null, locale);
        String p3 = messageSource.getMessage("email.family-confirmation.p3", new Object[]{emailLink}, locale);
        String p4 = messageSource.getMessage("email.family-confirmation.p4", new Object[]{ccrAndR}, locale);
        String p5 = messageSource.getMessage("email.family-confirmation.p5", new Object[]{confirmationCode, submittedDate},
                locale);
        String p6 = messageSource.getMessage("email.family-confirmation.p6", null, locale);
        String p7 = messageSource.getMessage("email.family-confirmation.p7", null, locale);
        String p8 = messageSource.getMessage("email.family-confirmation.p8", null, locale);
        String p9 = messageSource.getMessage("email.footer", null, locale);
        return new Content("text/html", p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9);
    }
}
