package org.ilgcc.app.submission.actions;

import com.sendgrid.helpers.mail.objects.Content;
import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getClientId(providerSubmission);
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
        //If provider failed to provide an email
        String familyEmailAddress = (String) familySubmission.getInputData().getOrDefault("parentContactEmail", "");
        if (familyEmailAddress.isEmpty()) {
            log.warn("No parentContactEmail was found to send confirmation email about the provider agreeing to care for the provider submission with ID: {}",
                providerSubmission.getId());
            return;
        }

        String providerSubmissionShortCode = providerSubmission.getShortCode();

        Locale locale =
                providerSubmission.getInputData().getOrDefault("languageRead", "English").equals("Spanish") ? Locale.forLanguageTag(
                        "es") : Locale.ENGLISH;

        String senderName = messageSource.getMessage("provider-response.response-email.senderName", null, locale);

        String subject = messageSource.getMessage("provider-response.response-email.subject", null, locale);
        Content body = createProviderResponseConfirmationEmailBody(providerSubmission, familySubmission, providerSubmissionShortCode, locale);

        sendEmailJob.enqueueSendEmailJob(familyEmailAddress, senderName, subject,
                EmailConstants.EmailType.FAMILY_CONFIRMATION_EMAIL.getDescription(),
                body, providerSubmission);
        providerSubmission.getInputData().put("providerResponseFamilyConfirmationEmailSent", "true");
        submissionRepositoryService.save(providerSubmission);
    }

    private Content createProviderResponseConfirmationEmailBody(Submission providerSubmission, Submission familySubmission,String confirmationCode, Locale locale) {
        //how do we set provider response
        String providerName = getProviderResponseName(providerSubmission);
        String ccapStartDate = (String) familySubmission.getInputData().getOrDefault("earliestChildcareStartDate", "");
        String ccrrName = providerSubmission.getInputData().get("ccrrName").toString();
        String ccrrPhoneNumber = (String) familySubmission.getInputData().getOrDefault("ccrrPhoneNumber", "");

        String p1 = messageSource.getMessage("provider-response.response-email.p1", null, locale);
        String p2 = providerName.isBlank() ? messageSource.getMessage("provider-response.response-email.p2-no-provider-name", new Object[]{ccrrName}, locale) : messageSource.getMessage("provider-response.response-email.p2-has-provider-name", new Object[]{providerName, ccrrName}, locale);
        String p3 = messageSource.getMessage("provider-response.response-email.p3", new Object[]{getChildrenInitialsFromApplication(familySubmission), ccapStartDate}, locale);
        String p4 = messageSource.getMessage("provider-response.response-email.p4", new Object[]{confirmationCode}, locale);
        String p5 = messageSource.getMessage("provider-response.response-email.p5", new Object[]{ccrrName, ccrrPhoneNumber}, locale);
        String p6 = messageSource.getMessage("provider-response.response-email.footer.p1", null, locale);
        String p7 = messageSource.getMessage("provider-response.response-email.footer.p2", null, locale);
        return new Content("text/html", p1 + p2 + p3 + p4 + p5 + p6 + p7);
    }

    private String getProviderResponseName(Submission providerSubmission) {
        String providerResponseBusinessName  = (String) providerSubmission.getInputData().getOrDefault("providerResponseBusinessName", "");
        if (!providerResponseBusinessName.isEmpty()) {
            return providerResponseBusinessName;
        }
        return (String) providerSubmission.getInputData().getOrDefault("providerResponseFirstName", "");
    }
    private String getChildrenInitialsFromApplication(Submission familySubmission) {
        List<Map<String, Object>> children = SubmissionUtilities.getChildrenNeedingAssistance(familySubmission);
        var childrenInitials = new ArrayList<String>();
        if (children.isEmpty()) {
            return "";
        }
        for (var child : children) {
            String firstName = (String) child.get("childFirstName");
            String lastName = (String) child.get("childLastName");
            childrenInitials.add(String.format("%s.%s.", firstName.toUpperCase().charAt(0), lastName.toUpperCase().charAt(0)));
        }
        return String.join(", ", childrenInitials);
    }
}
