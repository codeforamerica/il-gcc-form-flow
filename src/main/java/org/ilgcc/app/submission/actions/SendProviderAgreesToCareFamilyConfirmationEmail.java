package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.formatListIntoReadableString;
import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getChildrenInitialsListFromApplication;

import com.sendgrid.helpers.mail.objects.Content;
import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
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
        //Check if provider agrees to care
        String providerAgreesToCare = (String) formSubmission.getFormData().getOrDefault("providerResponseAgreeToCare", "false");
        if (providerAgreesToCare.equals("false")){
            return;
        }
        //Check that the provider response to the family email was sent.
        String providerResponseFamilyConfirmationEmailSent = (String) providerSubmission.getInputData()
                .getOrDefault("providerResponseFamilyConfirmationEmailSent", "false");
        if (providerResponseFamilyConfirmationEmailSent.equals("true")) {
            log.warn("Provider agrees to care confirmation email has already been sent for submission with ID: {}", providerSubmission.getId());
            return;
        }
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionId.isEmpty()) {
            log.warn("No family submission is associated with the provider submission with ID: {}", providerSubmission.getId());
            return;
        }
        Optional<Submission> familySubmissionOptional = submissionRepositoryService.findById(familySubmissionId.get());

        if (familySubmissionOptional.isEmpty()) {
            log.warn("No family submission is associated with the familSubmissionID: {}", providerSubmission.getId());
            return;
        }
        Submission familySubmission = familySubmissionOptional.get();
        String familyEmailAddress = (String) familySubmission.getInputData().getOrDefault("parentContactEmail", "");
        if (familyEmailAddress.isEmpty()) {
            log.warn("No parentContactEmail was found to send confirmation email about the provider agreeing to care for the provider submission with ID: {}",
                providerSubmission.getId());
            return;
        }

        String familySubmissionConfirmationId = familySubmission.getShortCode();

        Locale locale =
                familySubmission.getInputData().getOrDefault("languageRead", "English").equals("Spanish") ? Locale.forLanguageTag(
                        "es") : Locale.ENGLISH;

        String senderName = messageSource.getMessage("email.general.sender-name", null, locale);

        String subject = messageSource.getMessage("email.response-email-for-family.provider-agrees.subject", null, locale);
        Content body = createProviderResponseConfirmationEmailBody(providerSubmission, familySubmission, familySubmissionConfirmationId, locale);

        sendEmailJob.enqueueSendEmailJob(familyEmailAddress, senderName, subject,
                ILGCCEmail.EmailType.PROVIDER_AGREES_TO_CARE_FAMILY_EMAIL.getDescription(),
                body, providerSubmission);
        providerSubmission.getInputData().put("providerResponseFamilyConfirmationEmailSent", "true");
        submissionRepositoryService.save(providerSubmission);
    }

    private Content createProviderResponseConfirmationEmailBody(Submission providerSubmission, Submission familySubmission,String confirmationCode, Locale locale) {
        String providerName = ProviderSubmissionUtilities.getProviderResponseName(providerSubmission);
        String ccrrName = familySubmission.getInputData().get("ccrrName").toString();
        String ccrrPhoneNumber = (String) familySubmission.getInputData().getOrDefault("ccrrPhoneNumber", "");

        String p1 = messageSource.getMessage("email.response-email-for-family.provider-agrees.p1", null, locale);
        String p2 = providerName.isBlank() ? messageSource.getMessage("email.response-email-for-family.provider-agrees.p2-no-provider-name", new Object[]{ccrrName}, locale) : messageSource.getMessage("email.response-email-for-family.provider-agrees.p2-has-provider-name", new Object[]{providerName, ccrrName}, locale);
        String p3 = messageSource.getMessage("email.response-email-for-family.provider-agrees.p3",
            new Object[]{formatListIntoReadableString(getChildrenInitialsListFromApplication(familySubmission), messageSource.getMessage("general.and", null, locale)),
                ProviderSubmissionUtilities.getCCAPStartDateFromProviderOrFamilyChildcareStartDate(familySubmission, providerSubmission)
            }, locale);
        String p4 = messageSource.getMessage("email.response-email-for-family.provider-agrees.p4", new Object[]{confirmationCode}, locale);
        String p5 = messageSource.getMessage("email.response-email-for-family.provider-agrees.p5", new Object[]{ccrrName, ccrrPhoneNumber}, locale);
        String p6 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p7 = messageSource.getMessage("email.general.footer.cfa", null, locale);
        return new Content("text/html", p1 + p2 + p3 + p4 + p5 + p6 + p7);
    }
}
