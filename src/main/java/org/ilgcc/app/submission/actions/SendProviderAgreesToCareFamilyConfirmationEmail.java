package org.ilgcc.app.submission.actions;

import com.sendgrid.helpers.mail.objects.Content;
import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.EmailConstants;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendProviderAgreesToCareFamilyConfirmationEmail implements Action {

    private final SendEmailJob sendEmailJob;
    private final MessageSource messageSource;
    private final SubmissionRepositoryService submissionRepositoryService;

    public SendProviderAgreesToCareFamilyConfirmationEmail(SendEmailJob sendEmailJob, MessageSource messageSource, SubmissionRepositoryService submissionRepositoryService) {
        this.sendEmailJob = sendEmailJob;
        this.messageSource = messageSource;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(FormSubmission formSubmission, Submission providerSubmission) {
        //Check that the provider response to the family email was sent.
        String providerResponseFamilyConfirmationEmailSent = (String) providerSubmission.getInputData()
                .getOrDefault("providerResponseFamilyConfirmationEmailSent", "false");
        if (providerResponseFamilyConfirmationEmailSent.equals("true")) {
            log.warn("Provider agrees to care confirmation email has already been sent for submission with ID: {}", providerSubmission.getId());
            return;
        }
        //If provider failed to provide an email
        String providerEmail = (String) providerSubmission.getInputData().getOrDefault("providerResponseContactEmail", "");
        if (providerEmail.isEmpty()) {
            //TODO: The warning below needs to be rewritten
            log.warn("No provider email was provided to send confirmation email for provider submission with ID: {}",
                    providerSubmission.getId());
            return;
        }
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getClientId(providerSubmission);
        if (familySubmissionId.isEmpty()) {
            log.warn("No family submission is associated with the provider submission with ID: {}", providerSubmission.getId());
            return;
        }
        Optional<Submission> familySubmission = submissionRepositoryService.findById(familySubmissionId.get());
        if (familySubmission.isEmpty()) {
            log.warn("No family submission is associated with the familSubmissionID: {}", providerSubmission.getId());
            return;
        }
        String applicantsFirstName = (String) familySubmission.get().getInputData().getOrDefault("parentFirstName", "");
        String familySubmissionShortCode = familySubmission.get().getShortCode();

        Locale locale =
                providerSubmission.getInputData().getOrDefault("languageRead", "English").equals("Spanish") ? Locale.forLanguageTag(
                        "es") : Locale.ENGLISH;
        String subject = messageSource.getMessage("provider-response.response-email.subject", new Object[]{familySubmissionShortCode},
                locale);

        String senderName = messageSource.getMessage("email.sender-name", null, locale);

        sendEmailJob.enqueueSendEmailJob(providerEmail, senderName, subject,
                EmailConstants.EmailType.FAMILY_CONFIRMATION_EMAIL.getDescription(),
                createFamilyConfirmationEmailBody(providerSubmission, providerSubmissionShortCode, locale), providerSubmission);
        providerSubmission.getInputData().put("familyConfirmationEmailSent", "true");
        submissionRepositoryService.save(providerSubmission);
    }

    private Content createFamilyConfirmationEmailBody(Submission providerSubmission, String confirmationCode, Locale locale) {
        String parentFirstName = providerSubmission.getInputData().get("parentFirstName").toString();
        String emailLink = providerSubmission.getInputData().get("emailLink").toString();
        String ccrAndR = providerSubmission.getInputData().get("ccrrName").toString();
        String submittedDate = SubmissionUtilities.getFormattedSubmittedAtDate(providerSubmission);


        String p1 = messageSource.getMessage("provider-response.response-email.p1", new Object[]{ccrAndR}, locale);
        String p2 = messageSource.getMessage("provider-response.response-email.p2", new Object[]{confirmationCode, submittedDate},
                locale);
        String p3 = messageSource.getMessage("provider-response.response-email.p3", null, locale);
        String p4 = messageSource.getMessage("provider-response.response-email.p4", null, locale);
        String p5 = messageSource.getMessage("provider-response.response-email.footer.p1", null, locale);
        String p6 = messageSource.getMessage("provider-response.response-email.footer.p2", null, locale);
        return new Content("text/html", p1 + p2 + p3 + p4 + p5 + p6);
    }
}
