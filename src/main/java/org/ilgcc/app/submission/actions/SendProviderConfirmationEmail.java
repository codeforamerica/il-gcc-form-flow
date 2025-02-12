package org.ilgcc.app.submission.actions;

import com.sendgrid.helpers.mail.objects.Content;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.EmailConstants.EmailType;
import org.ilgcc.app.submission.router.CCRR;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendProviderConfirmationEmail extends CreateEmailMessage {

    protected static String EMAIL_SENT_STATUS_INPUT_NAME = "providerConfirmationEmailSent";

    protected static String RECIPIENT_EMAIL_INPUT_NAME = "providerResponseContactEmail";

    private static Submission currentFamilySubmission;


    public SendProviderConfirmationEmail(SendEmailJob sendEmailJob, SubmissionRepositoryService submissionRepositoryService) {
        super(sendEmailJob, submissionRepositoryService, EmailType.PROVIDER_CONFIRMATION_EMAIL);
    }

    @Override
    public void run(Submission submission) {
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getClientId(currentSubmission);

        if (familySubmissionId.isPresent()) {
            Optional<Submission> familySubmission = submissionRepositoryService.findById(familySubmissionId.get());
            if (familySubmission.isPresent()) {
                currentFamilySubmission = familySubmission.get();
            }
        }
        sendEmailMessage(submission);
    }

    @Override
    protected String emailSubject() {
        if (null == currentFamilySubmission) {
            log.error("Provider submission {0} does not have a corresponding family response", currentSubmission.getId());
        }
        return messageSource.getMessage(SUBJECT_MESSAGE_KEY, new Object[]{currentFamilySubmission.getShortCode()}, locale);
    }

    @Override
    protected Content emailBody() {
        String providerFirstName = (String) currentSubmission.getInputData().getOrDefault("providerResponseFirstName", "");
        String businessName = (String) currentSubmission.getInputData().getOrDefault("providerResponseBusinessName", "");
        String childrenInitials = ProviderSubmissionUtilities.formatChildInitialsAsCommaSeparatedList(currentFamilySubmission);
        Optional<CCRR> ccrr = CCRR.findCCRRByOrganizationalId(
                currentFamilySubmission.getInputData().get("organizationalId").toString());
        String submittedDate = SubmissionUtilities.getFormattedSubmittedAtDate(currentFamilySubmission);

        String ccrrName = "";
        String ccrrPhone = "";

        if (ccrr.isPresent()) {
            ccrrName = ccrr.get().getName();
            ccrrPhone = ccrr.get().getPhoneNumber();
        }

        String startDate = (String) currentSubmission.getInputData().getOrDefault("providerCareStartDay", "");

        String p1 = messageSource.getMessage("email.provider-confirmation.p1", null, locale);
        String p2 = messageSource.getMessage("email.provider-confirmation.p2", new Object[]{ccrrName}, locale);
        String p3 = messageSource.getMessage("email.provider-confirmation.p3", new Object[]{childrenInitials, startDate}, locale);
        String p4 = messageSource.getMessage("email.provider-confirmation.p4",
                new Object[]{currentFamilySubmission.getShortCode()}, locale);
        String p5 = messageSource.getMessage("email.provider-confirmation.p5",
                new Object[]{ccrrName, ccrrPhone},
                locale);
        String p6 = messageSource.getMessage("email.family-confirmation.p8", null, locale);
        String p7 = messageSource.getMessage("email.footer", null, locale);
        return new Content("text/html", p1 + p2 + p3 + p4 + p5 + p6 + p7);
    }

}
