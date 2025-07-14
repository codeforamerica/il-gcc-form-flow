package org.ilgcc.app.email.templates;

import static org.ilgcc.app.email.ILGCCEmail.FROM_ADDRESS;

import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.util.LinkedHashSet;
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
public class AllProvidersRespondedFamilyConfirmationEmailTemplate {

    private Map<String, Object> emailData;
    private MessageSource messageSource;
    private Locale locale;

    public AllProvidersRespondedFamilyConfirmationEmailTemplate(Map<String, Object> emailData, MessageSource messageSource,
            Locale locale) {
        this.emailData = emailData;
        this.messageSource = messageSource;
        this.locale = locale;

    }

    public ILGCCEmailTemplate createTemplate() {
        return new ILGCCEmailTemplate(senderEmail(), setSubject(), new Content("text/html", setBodyCopy(emailData)),
                EmailType.ALL_PROVIDERS_RESPONDED_FAMILY_CONFIRMATION_EMAIL);
    }

    private Email senderEmail() {
        return new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale));
    }

    private String setSubject() {
        return messageSource.getMessage("email.all-providers-responded-family-confirmation-email.subject", null,
                locale);
    }

    private String setBodyCopy(Map<String, Object> emailData) {
        String p1 = messageSource.getMessage("email.all-providers-responded-family-confirmation-email.p1",
                new Object[]{emailData.get("parentFirstName")},
                locale);
        String p2 = messageSource.getMessage("email.all-providers-responded-family-confirmation-email.p2",
                new Object[]{emailData.get("ccrrName")}, locale);
//        String allProviderResponses = getAllProviderResponses(emailData, messageSource, locale);
        String p3 = messageSource.getMessage("email.all-providers-responded-family-confirmation-email.p3",
                new Object[]{emailData.get("confirmationCode")},
                locale);
        String p4 = messageSource.getMessage("email.all-providers-responded-family-confirmation-email.p4",
                new Object[]{emailData.get("ccrrName"), emailData.get("ccrrPhoneNumber")}, locale);
        String p5 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p6 = messageSource.getMessage("email.general.footer.cfa", null, locale);
        return p1 + p2 + p3 + p4 + p5 + p6;
    }

    private String getAllProviderResponses(Map<String, Object> emailData, MessageSource messageSource, Locale locale) {

//      ML -> P1  -> P1 -> ML, TT getRelatedChildrenSchedulesForProvider
//          TT - >p1
        String providerResponses = "";
        LinkedHashSet<Map<String, Object>> providerData = (LinkedHashSet<Map<String, Object>>) emailData.getOrDefault(
                "providersData", new LinkedHashSet<Map<String, Object>>());
        if (!providerData.isEmpty()) {
            for (Map<String, Object> provider : providerData) {
                //create list elements based on whether provider agrees to care.  The fallback else statement will be that the provider did not respond in three days because of where this code will be located.
                if (provider.getOrDefault("providerResponseAgreeToCare", "").equals("true")) {
                    providerResponses = String.format("%s%s", providerResponses,
                            messageSource.getMessage("email.all-providers-responded-family-confirmation-email.li-agreed-to-care",
                                    new Object[]{provider.get("providerResponseName"), provider.get("childInitials"),
                                            provider.get("ccapStartDate")}, locale));
                } else if (provider.getOrDefault("providerResponseAgreeToCare", "").equals("false")) {
                    providerResponses = String.format("%s%s", providerResponses,
                            messageSource.getMessage(
                                    "email.all-providers-responded-family-confirmation-email.li-did-not-agree-to-care",
                                    new Object[]{provider.get("providerResponseName"), provider.get("childInitials")}, locale));
                } else {
                    providerResponses = String.format("%s%s", providerResponses,
                            messageSource.getMessage(
                                    "email.all-providers-responded-family-confirmation-email.li-did-not-complete-application-in-three-days",
                                    new Object[]{provider.get("providerResponseName")}, locale));
                }
            }
        }
        return String.format("<ul>%s</ul>", providerResponses);
    }
}
