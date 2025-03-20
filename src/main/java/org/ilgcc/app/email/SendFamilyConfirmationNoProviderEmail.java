package org.ilgcc.app.email;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getFamilySubmissionDataForEmails;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.templates.FamilyConfirmationEmailTemplate;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendFamilyConfirmationNoProviderEmail extends SendEmail {
    @Autowired
    SendEmailJob sendEmailJob;

    @Autowired
    MessageSource messageSource;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    public SendFamilyConfirmationNoProviderEmail(Submission submission) {
        super(submission);
        this.emailSentStatusInputName = "familyConfirmationEmailSent";
        this.recipientEmailInputName = "parentContactEmail";

    }

    @Override
    protected ILGCCEmailTemplate emailTemplate(Map<String, Object> emailData){
        return new FamilyConfirmationEmailTemplate(emailData,
                messageSource,
                locale).createTemplate();
    }

    @Override
    protected Optional<Map<String, Object>> getEmailData(Submission familySubmission) {
        return Optional.of(getFamilySubmissionDataForEmails(familySubmission));
    }

}
