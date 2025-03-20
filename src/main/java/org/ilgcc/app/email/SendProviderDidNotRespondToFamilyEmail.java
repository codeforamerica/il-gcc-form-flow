package org.ilgcc.app.email;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getFamilySubmissionDataForEmails;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.templates.ProviderDidNotRespondToFamilyEmailTemplate;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendProviderDidNotRespondToFamilyEmail extends SendEmail {

    public SendProviderDidNotRespondToFamilyEmail(Submission submission) {
        super(submission);
        this.emailSentStatusInputName = "providerDidNotRespondEmailSent";
        this.recipientEmailInputName = "parentContactEmail";
    }

    @Override
    protected ILGCCEmailTemplate emailTemplate(Map<String, Object> emailData){
        return new ProviderDidNotRespondToFamilyEmailTemplate(emailData, messageSource,
                locale).createTemplate();
    }

    @Override
    protected Optional<Map<String, Object>> getEmailData(Submission familySubmission) {
        return Optional.of(getFamilySubmissionDataForEmails(familySubmission));
    }
}
