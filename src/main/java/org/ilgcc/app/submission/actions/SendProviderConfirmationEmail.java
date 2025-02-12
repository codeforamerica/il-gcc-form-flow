package org.ilgcc.app.submission.actions;

import com.sendgrid.helpers.mail.objects.Content;
import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Locale;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.EmailConstants;
import org.ilgcc.app.email.EmailConstants.EmailType;
import org.ilgcc.app.email.EmailMessage;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendProviderConfirmationEmail extends CreateEmailMessage {

    public static String EMAIL_SENT_STATUS_INPUT_NAME = "providerConfirmationEmailSent";

    public static String RECIPIENT_EMAIL_INPUT_NAME = "providerResponseContactEmail";

    public SendProviderConfirmationEmail(SendEmailJob sendEmailJob, SubmissionRepositoryService submissionRepositoryService) {
        super(sendEmailJob, submissionRepositoryService, EmailType.FAMILY_CONFIRMATION_EMAIL_NO_PROVIDER );
    }

    @Override
    protected Content emailBody() {
        String parentFirstName = submission.getInputData().get("parentFirstName").toString();
        String emailLink = submission.getInputData().get("emailLink").toString();
        String ccrAndR = submission.getInputData().get("ccrrName").toString();
        String submittedDate = SubmissionUtilities.getFormattedSubmittedAtDate(submission);

        String p1 = messageSource.getMessage("email.family-confirmation.p1", new Object[]{parentFirstName}, locale);
        String p2 = messageSource.getMessage("email.family-confirmation.p2", null, locale);
        String p3 = messageSource.getMessage("email.family-confirmation.p3", new Object[]{emailLink}, locale);
        String p4 = messageSource.getMessage("email.family-confirmation.p4", new Object[]{ccrAndR}, locale);
        String p5 = messageSource.getMessage("email.family-confirmation.p5", new Object[]{submission.getShortCode(), submittedDate},
                locale);
        String p6 = messageSource.getMessage("email.family-confirmation.p6", null, locale);
        String p7 = messageSource.getMessage("email.family-confirmation.p7", null, locale);
        String p8 = messageSource.getMessage("email.family-confirmation.p8", null, locale);
        String p9 = messageSource.getMessage("email.footer", null, locale);
        return new Content("text/html", p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9);
    }
}
