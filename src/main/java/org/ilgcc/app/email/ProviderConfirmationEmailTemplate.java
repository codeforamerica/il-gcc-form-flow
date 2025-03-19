package org.ilgcc.app.email;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getFamilySubmissionDataForEmails;

import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import formflow.library.data.Submission;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.ilgcc.app.email.ILGCCEmail.EmailType;
import org.springframework.context.MessageSource;

@Getter
@Setter
public class ProviderConfirmationEmailTemplate {

    private Submission familySubmission;
    private MessageSource messageSource;
    private Locale locale;



   public ProviderConfirmationEmailTemplate(Submission familySubmission, MessageSource messageSource, Locale locale) {
       this.familySubmission = familySubmission;
       this.messageSource = messageSource;
       this.locale = locale;
   }

    protected Optional<Map<String, Object>> getEmailData() {
        return Optional.of(getFamilySubmissionDataForEmails(familySubmission));
    }


    protected String setSubject(Map<String, Object> emailData) {
        return messageSource.getMessage("email.family-confirmation.subject", new Object[]{emailData.get("confirmationCode")},
                locale);
    }

    protected String setBodyCopy(Map<String, Object> emailData) {
        String p1 = messageSource.getMessage("email.family-confirmation.p1", new Object[]{emailData.get("parentFirstName")},
                locale);
        String p2 = messageSource.getMessage("email.family-confirmation.p2", null, locale);
        String p3 = messageSource.getMessage("email.family-confirmation.p3", new Object[]{emailData.get("shareableLink")}, locale);
        String p4 = messageSource.getMessage("email.family-confirmation.p4",
                new Object[]{emailData.get("ccrrName"), emailData.get("ccrrPhoneNumber")}, locale);
        String p5 = messageSource.getMessage("email.family-confirmation.p5",
                new Object[]{emailData.get("confirmationCode"), emailData.get("submittedDate")},
                locale);
        String p6 = messageSource.getMessage("email.family-confirmation.p6", null, locale);
        String p7 = messageSource.getMessage("email.family-confirmation.p7", null, locale);
        String p8 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p9 = messageSource.getMessage("email.general.footer.cfa", null, locale);
        return p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
    }

}
