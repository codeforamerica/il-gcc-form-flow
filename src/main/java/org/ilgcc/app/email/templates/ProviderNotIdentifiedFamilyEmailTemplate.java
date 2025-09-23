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
public class ProviderNotIdentifiedFamilyEmailTemplate {

    private Map<String, Object> emailData;
    private MessageSource messageSource;
    private Locale locale;

    public ProviderNotIdentifiedFamilyEmailTemplate(Map<String, Object> emailData, MessageSource messageSource, Locale locale) {
        this.emailData = emailData;
        this.messageSource = messageSource;
        this.locale = locale;

    }
    public ILGCCEmailTemplate createTemplate(){
        return new ILGCCEmailTemplate(senderEmail(), setSubject(emailData), new Content("text/html", setBodyCopy(emailData)), EmailType.PROVIDER_NOT_IDENTIFIED_FAMILY_EMAIL);
    }

    private Email senderEmail() {
        return  new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale));
    }

    private String setSubject(Map<String, Object> emailData) {
        return messageSource.getMessage("email.response-email-for-family.provider-not-identified.subject", new Object[]{emailData.get("confirmationCode")}, locale);
    }

    private String setBodyCopy(Map<String, Object> emailData) {
        String p1 = messageSource.getMessage("email.response-email-for-family.provider-not-identified.p1",
                new Object[]{emailData.get("parentFirstName").toString()}, locale);

        String providerType = emailData.get("providerType").toString();
        String p2;

        if (providerType.isBlank()) {
            p2 = messageSource.getMessage("email.response-email-for-family.provider-not-identified.p2-individual",
                    new Object[]{emailData.get("familyIntendedProviderName"),toString()}, locale);
        } else {
            p2 = providerType.equals("Individual")
                    ? messageSource.getMessage("email.response-email-for-family.provider-not-identified.p2-individual",
                    new Object[]{emailData.get("childCareProviderInitials").toString()}, locale)
                    : messageSource.getMessage("email.response-email-for-family.provider-not-identified.p2-program",
                            new Object[]{emailData.get("childCareProgramName").toString()}, locale);
        }
        
        String p3 = messageSource.getMessage("email.response-email-for-family.provider-did-not-respond.p3",
                new Object[]{emailData.get("confirmationCode")},
                locale);
        String p4 = messageSource.getMessage("email.response-email-for-family.provider-did-not-respond.p4",
                new Object[]{emailData.get("ccrrName"), emailData.get("ccrrPhoneNumber")}, locale);
        String p5 = messageSource.getMessage("email.response-email-for-family.provider-did-not-respond.p5", null, locale);
        String p6 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p7 = messageSource.getMessage("email.general.footer.cfa", null, locale);
        return p1 + p2 + p3 + p4 + p5 + p6+ p7;
    }

}