package org.ilgcc.app.email;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getFamilySubmissionDataForEmails;

import formflow.library.data.Submission;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.templates.FamilyConfirmationEmailTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendFamilyConfirmationNoProviderEmail extends SendEmail {

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
