package org.ilgcc.app.email;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getCombinedDataForEmails;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.ILGCCEmail.EmailType;
import org.ilgcc.app.email.templates.ProviderAgreesToCareFamilyConfirmationEmailTemplate;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendProviderAgreesToCareFamilyConfirmationEmail extends SendEmail {

    @Autowired
    public SendProviderAgreesToCareFamilyConfirmationEmail(SendEmailJob sendEmailJob,
            MessageSource messageSource,
            SubmissionRepositoryService submissionRepositoryService) {
        super(sendEmailJob, messageSource, submissionRepositoryService, "providerResponseFamilyConfirmationEmailSent",
                "parentContactEmail");
    }

    @Override
    protected ILGCCEmailTemplate emailTemplate(Map<String, Object> emailData) {
        return new ProviderAgreesToCareFamilyConfirmationEmailTemplate(emailData, messageSource,
                locale).createTemplate();
    }

    @Override
    protected Optional<Map<String, Object>> getEmailData(Submission providerSubmission) {
        Optional<Submission> familySubmission = getFamilyApplication(providerSubmission);
        if (familySubmission.isPresent()) {
            return Optional.of(getCombinedDataForEmails(providerSubmission, familySubmission.get()));
        } else {
            log.warn(
                    "{}: Skipping email send because there is no family submission associated with the provider submission with ID : {}",
                    EmailType.PROVIDER_AGREES_TO_CARE_FAMILY_EMAIL.getDescription(), providerSubmission.getId());
            return Optional.empty();
        }
    }

    @Override
    protected Boolean skipEmailSend(Map<String, Object> inputData) {
        boolean emailSent = inputData.getOrDefault(emailSentStatusInputName, "false")
                .equals("true");
        boolean providerAgreedToCare = inputData.getOrDefault("providerResponseAgreeToCare", "false")
                .equals("true");

        return emailSent || !providerAgreedToCare;
    }
}
