package org.ilgcc.app.email.templates;

import static org.ilgcc.app.email.ILGCCEmail.FROM_ADDRESS;
import static org.ilgcc.app.utils.ProviderSubmissionUtilities.formatListIntoReadableString;

import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.util.List;
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
public class ProviderAgreesToCareFamilyConfirmationEmailTemplate {

    private Map<String, Object> emailData;
    private MessageSource messageSource;
    private Locale locale;

   public ProviderAgreesToCareFamilyConfirmationEmailTemplate(Map<String, Object> emailData, MessageSource messageSource, Locale locale) {
       this.emailData = emailData;
       this.messageSource = messageSource;
       this.locale = locale;

   }
   public ILGCCEmailTemplate createTemplate(){
       return new ILGCCEmailTemplate(senderEmail(), setSubject(), new Content("text/html", setBodyCopy(emailData)), EmailType.PROVIDER_AGREES_TO_CARE_FAMILY_EMAIL);
   }

    private Email senderEmail() {
        return  new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale));
    }

    private String setSubject() {
        return messageSource.getMessage("email.response-email-for-family.provider-agrees.subject", null, locale);
    }

    private String setBodyCopy(Map<String, Object> emailData) {
        String p1 = messageSource.getMessage("email.response-email-for-family.provider-agrees.p1", null, locale);

        String providerName = emailData.get("providerName").toString();
        String p2 = providerName.isEmpty() ? messageSource.getMessage(
                "email.response-email-for-family.provider-agrees.p2-no-provider-name", new Object[]{emailData.get("ccrrName")},
                locale)
                : messageSource.getMessage("email.response-email-for-family.provider-agrees.p2-has-provider-name",
                        new Object[]{providerName, emailData.get("ccrrName")}, locale);
        String p3 = messageSource.getMessage("email.response-email-for-family.provider-agrees.p3",
                new Object[]{formatListIntoReadableString((List<String>) emailData.get("childrenInitialsList"),
                        messageSource.getMessage("general.and", null, locale)), emailData.get("ccapStartDate")}, locale);
        String p4 = messageSource.getMessage("email.response-email-for-family.provider-agrees.p4",
                new Object[]{emailData.get("confirmationCode")},
                locale);
        String p5 = messageSource.getMessage("email.response-email-for-family.provider-agrees.p5",
                new Object[]{emailData.get("ccrrName"), emailData.get("ccrrPhoneNumber")}, locale);
        String p6 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p7 = messageSource.getMessage("email.general.footer.cfa", null, locale);
        return p1 + p2 + p3 + p4 + p5 + p6 + p7;
    }

}
