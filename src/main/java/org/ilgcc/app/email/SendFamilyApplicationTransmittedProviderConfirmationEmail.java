package org.ilgcc.app.email;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getCombinedDataForEmails;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.ILGCCEmail.EmailType;
import org.ilgcc.app.email.templates.FamilyApplicationTransmittedProviderConfirmationTemplate;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendFamilyApplicationTransmittedProviderConfirmationEmail extends SendEmail {

    @Autowired
    public SendFamilyApplicationTransmittedProviderConfirmationEmail(SendEmailJob sendEmailJob, MessageSource messageSource,
            SubmissionRepositoryService submissionRepositoryService) {
        super(sendEmailJob, messageSource, submissionRepositoryService, "applicationTransmittedConfirmationEmailSent",
                "providerResponseContactEmail");
    }

    @Override
    protected ILGCCEmailTemplate emailTemplate(Map<String, Object> emailData) {
        return new FamilyApplicationTransmittedProviderConfirmationTemplate(emailData, messageSource, locale).createTemplate();
    }

    @Override
    protected Optional<Map<String, Object>> getEmailData(Submission providerSubmission, Map<String, Object> subflowData) {
        Optional<Submission> familySubmission = getFamilyApplication(providerSubmission);
        if (familySubmission.isPresent()) {
            String currentProviderUuid = (String) providerSubmission.getInputData().getOrDefault("currentProviderUuid", "");
            Map<String, Object> currentProvider = SubmissionUtilities.getCurrentProvider(familySubmission.get().getInputData(),
                    currentProviderUuid);
            // todo: Remove when productizing ENABLE_MULTIPLE_PROVIDERS. Having no current provider would be an error when
            //  ENABLE_MULTIPLE_PROVIDERS feature flag is on.
            if (!currentProvider.isEmpty()) {
                return Optional.of(getCombinedDataForEmails(providerSubmission, familySubmission.get(), currentProvider));
            } else {
                return Optional.of(getCombinedDataForEmails(providerSubmission, familySubmission.get()));
            }

        } else {
            log.warn(
                    "{}: Skipping email send because there is no family submission associated with the provider submission with ID : {}",
                    EmailType.FAMILY_APPLICATION_TRANSMITTED_PROVIDER_CONFIRMATION_EMAIL.getDescription(), providerSubmission.getId());
            return Optional.empty();
        }
    }

}

