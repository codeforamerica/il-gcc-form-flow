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
public class UnidentifiedProviderConfirmationEmailTemplate {

    private Map<String, Object> emailData;
    private MessageSource messageSource;
    private Locale locale;

   public UnidentifiedProviderConfirmationEmailTemplate(Map<String, Object> emailData, MessageSource messageSource, Locale locale) {
       this.emailData = emailData;
       this.messageSource = messageSource;
       this.locale = locale;

   }
   public ILGCCEmailTemplate createTemplate(){
       return new ILGCCEmailTemplate(senderEmail(), setSubject(), new Content("text/html", setBodyCopy(emailData)), EmailType.UNIDENTIFIED_PROVIDER_CONFIRMATION_EMAIL);
   }

    private Email senderEmail() {
        return  new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale));
    }

    private String setSubject() {
        return messageSource.getMessage("email.unidentified-provider-confirmation.subject", new Object[]{emailData.get("confirmationCode")}, locale);
    }

    private String setBodyCopy(Map<String, Object> emailData) {
        String p1 = messageSource.getMessage("email.unidentified-provider-confirmation.p1", null, locale);
        String p2 = messageSource.getMessage("email.unidentified-provider-confirmation.p2",
                new Object[]{emailData.get("ccrrName")}, locale);
        String p3 = messageSource.getMessage("email.unidentified-provider-confirmation.p3",
                new Object[]{emailData.get("ccrrName"), emailData.get("ccrrPhoneNumber")}, locale);
        String p4 = messageSource.getMessage("email.unidentified-provider-confirmation.p4", new Object[]{emailData.get("confirmationCode")},
                locale);
        String p5 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p6 = messageSource.getMessage("email.general.footer.cfa", null, locale);

        return p1 + p2 + p3 + p4 + p5 + p6 ;
    }

}
