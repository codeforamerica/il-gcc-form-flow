package org.ilgcc.app.email;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getCombinedDataForEmails;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.ILGCCEmail.EmailType;
import org.ilgcc.app.email.templates.UnidentifiedProviderConfirmationEmailTemplate;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendUnidentifiedProviderConfirmationEmail extends SendEmail {

    @Autowired
    public SendUnidentifiedProviderConfirmationEmail(SendEmailJob sendEmailJob, MessageSource messageSource,
            SubmissionRepositoryService submissionRepositoryService) {
        super(sendEmailJob, messageSource, submissionRepositoryService, "providerConfirmationEmailSent",
                "providerResponseContactEmail");
    }

    @Override
    protected ILGCCEmailTemplate emailTemplate(Map<String, Object> emailData) {
        return new UnidentifiedProviderConfirmationEmailTemplate(emailData, messageSource, Locale.ENGLISH).createTemplate();
    }

    @Override
    protected Optional<Map<String, Object>> getEmailData(Submission providerSubmission, Map<String, Object> subflowData) {
        Optional<Submission> familySubmission = getFamilyApplication(providerSubmission);
        if (familySubmission.isPresent()) {
            return Optional.of(getCombinedDataForEmails(providerSubmission, familySubmission.get()));
        } else {
            log.warn(
                    "{}: Skipping email send because there is no family submission associated with the provider submission with ID : {}",
                    EmailType.UNIDENTIFIED_PROVIDER_CONFIRMATION_EMAIL.getDescription(), providerSubmission.getId());
            return Optional.empty();
        }
    }

    @Override
    protected Boolean skipEmailSend(Map<String, Object> inputData) {
        return inputData.getOrDefault("providerConfirmationEmailSent", "false").equals("true");
    }
}


