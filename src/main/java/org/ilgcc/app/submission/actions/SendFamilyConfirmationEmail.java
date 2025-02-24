package org.ilgcc.app.submission.actions;

import com.sendgrid.helpers.mail.objects.Content;
import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.ILGCCEmail;
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

    public SendFamilyConfirmationEmail(SendEmailJob sendEmailJob, MessageSource messageSource, SubmissionRepositoryService submissionRepositoryService) {
        this.sendEmailJob = sendEmailJob;
        this.messageSource = messageSource;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission familySubmission) {
        String familyConfirmationEmailSent = (String) familySubmission.getInputData()
                .getOrDefault("familyConfirmationEmailSent", "false");
        if (familyConfirmationEmailSent.equals("true")) {
            log.warn("Family confirmation email has already been sent for submission with ID: {}", familySubmission.getId());
            return;
        }
        String familyEmail = familySubmission.getInputData().get("parentContactEmail").toString();
        if (familyEmail == null || familyEmail.isEmpty()) {
            log.warn("Family email was empty when attempting to send family confirmation email for submission with ID: {}",
                    familySubmission.getId());
            return;
        }

        String familySubmissionShortCode = familySubmission.getShortCode();

        Locale locale =
                familySubmission.getInputData().getOrDefault("languageRead", "English").equals("Spanish") ? Locale.forLanguageTag(
                        "es") : Locale.ENGLISH;
        String subject = messageSource.getMessage("email.family-confirmation.subject", new Object[]{familySubmissionShortCode},
                locale);

        String senderName = messageSource.getMessage("email.general.sender-name", null, locale);

        sendEmailJob.enqueueSendEmailJob(familyEmail, senderName, subject,
                ILGCCEmail.EmailType.FAMILY_CONFIRMATION_EMAIL.getDescription(),
                createFamilyConfirmationEmailBody(familySubmission, familySubmissionShortCode, locale), familySubmission);
        familySubmission.getInputData().put("familyConfirmationEmailSent", "true");
        submissionRepositoryService.save(familySubmission);
    }

    private Content createFamilyConfirmationEmailBody(Submission familySubmission, String confirmationCode, Locale locale) {
        String parentFirstName = familySubmission.getInputData().get("parentFirstName").toString();
        String emailLink = familySubmission.getInputData().get("emailLink").toString();
        String ccrAndR = familySubmission.getInputData().get("ccrrName").toString();
        String submittedDate = SubmissionUtilities.getFormattedSubmittedAtDate(familySubmission);
        String ccrrPhoneNumber = (String) familySubmission.getInputData().getOrDefault("ccrrPhoneNumber", "");

        String p1 = messageSource.getMessage("email.family-confirmation.p1", new Object[]{parentFirstName}, locale);
        String p2 = messageSource.getMessage("email.family-confirmation.p2", null, locale);
        String p3 = messageSource.getMessage("email.family-confirmation.p3", new Object[]{emailLink}, locale);
        String p4 = messageSource.getMessage("email.family-confirmation.p4", new Object[]{ccrAndR, ccrrPhoneNumber}, locale);
        String p5 = messageSource.getMessage("email.family-confirmation.p5", new Object[]{confirmationCode, submittedDate},
                locale);
        String p6 = messageSource.getMessage("email.family-confirmation.p6", null, locale);
        String p7 = messageSource.getMessage("email.family-confirmation.p7", null, locale);
        String p8 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p9 = messageSource.getMessage("email.general.footer.cfa", null, locale);
        return new Content("text/html", p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9);
    }
}
