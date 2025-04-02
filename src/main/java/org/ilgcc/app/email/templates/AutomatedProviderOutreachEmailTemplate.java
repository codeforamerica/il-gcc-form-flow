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
public class AutomatedProviderOutreachEmailTemplate {

    private Map<String, Object> emailData;
    private MessageSource messageSource;
    private Locale locale;

    public AutomatedProviderOutreachEmailTemplate(Map<String, Object> emailData, MessageSource messageSource, Locale locale) {
       this.emailData = emailData;
       this.messageSource = messageSource;
       this.locale = locale;

   }
   public ILGCCEmailTemplate createTemplate(){
       return new ILGCCEmailTemplate(senderEmail(), setSubject(), new Content("text/html", setBodyCopy(emailData)), EmailType.AUTOMATED_PROVIDER_OUTREACH_EMAIL);
   }

    private Email senderEmail() {
        return  new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale));
    }

    private String setSubject() {
        return messageSource.getMessage("email.automated-provider-outreach.subject", null,
                locale);
    }

    private String setBodyCopy(Map<String, Object> emailData) {
        String p1 = messageSource.getMessage("email.automated-provider-outreach.p1", null,
                locale);
        String p2 = messageSource.getMessage("email.automated-provider-outreach.p2", new Object[]{emailData.get("confirmationCode")}, locale);
        String p3 = messageSource.getMessage("email.automated-provider-outreach.p3", new Object[]{emailData.get("shareableLink")}, locale);
        String p4 = messageSource.getMessage("email.automated-provider-outreach.p4",
                null, locale);
        String p5 = messageSource.getMessage("email.automated-provider-outreach.p5",
                new Object[]{emailData.get("ccrrName")},
                locale);
        String p6 = messageSource.getMessage("email.automated-provider-outreach.p6", null, locale);
        String p7 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p8 = messageSource.getMessage("email.general.footer.cfa", null, locale);
        return p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8;
    }

}
