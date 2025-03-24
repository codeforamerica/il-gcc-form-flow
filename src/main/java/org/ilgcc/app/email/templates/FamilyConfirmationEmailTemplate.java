package org.ilgcc.app.email.templates;

import static org.ilgcc.app.email.ILGCCEmail.FROM_ADDRESS;

import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.util.Locale;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.email.ILGCCEmail.EmailType;
import org.ilgcc.app.email.ILGCCEmailTemplate;
import org.springframework.context.MessageSource;

@Getter
@Setter
public class FamilyConfirmationEmailTemplate {

    private Map<String, Object> emailData;
    private MessageSource messageSource;
    private Locale locale;

    public FamilyConfirmationEmailTemplate(Map<String, Object> emailData, MessageSource messageSource, Locale locale) {
       this.emailData = emailData;
       this.messageSource = messageSource;
       this.locale = locale;

   }
   public ILGCCEmailTemplate createTemplate(){
       return new ILGCCEmailTemplate(senderEmail(), setSubject(emailData), new Content("text/html", setBodyCopy(emailData)), EmailType.FAMILY_CONFIRMATION_EMAIL);
   }

    private Email senderEmail() {
        return  new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale));
    }

    private String setSubject(Map<String, Object> emailData) {
        return messageSource.getMessage("email.family-confirmation.subject", new Object[]{emailData.get("confirmationCode")},
                locale);
    }

    private String setBodyCopy(Map<String, Object> emailData) {
        String p1 = messageSource.getMessage("email.family-confirmation.hi", new Object[]{emailData.get("parentFirstName")},
                locale);
        String p2 = messageSource.getMessage("email.family-confirmation.you-completed-the-online-application", null, locale);
        String p3 = messageSource.getMessage("email.family-confirmation.you-need-to-email-or-text", new Object[]{emailData.get("shareableLink")}, locale);
        String p4 = messageSource.getMessage("email.family-confirmation.you-will-recieve-mail",
                new Object[]{emailData.get("ccrrName"), emailData.get("ccrrPhoneNumber")}, locale);
        String p5 = messageSource.getMessage("email.family-confirmation.pending-review",
                new Object[]{emailData.get("confirmationCode"), emailData.get("submittedDate")},
                locale);
        String p6 = messageSource.getMessage("email.family-confirmation.what-happens", null, locale);
        String p7 = messageSource.getMessage("email.family-confirmation.what-are-the-next", null, locale);
        String p8 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p9 = messageSource.getMessage("email.general.footer.cfa", null, locale);
        return p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
    }

}
