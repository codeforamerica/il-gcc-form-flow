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
public class FamilyApplicationTransmittedConfirmationEmailTemplate {

    private Map<String, Object> emailData;
    private MessageSource messageSource;
    private Locale locale;

    public FamilyApplicationTransmittedConfirmationEmailTemplate(Map<String, Object> emailData, MessageSource messageSource,
            Locale locale) {
        this.emailData = emailData;
        this.messageSource = messageSource;
        this.locale = locale;

    }

    public ILGCCEmailTemplate createTemplate() {
        return new ILGCCEmailTemplate(senderEmail(), setSubject(), new Content("text/html", setBodyCopy(emailData)),
                EmailType.FAMILY_APPLICATION_TRANSMITTED_CONFIRMATION_EMAIL);
    }

    private Email senderEmail() {
        return new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale));
    }

    private String setSubject() {
        return messageSource.getMessage("email.family-application-transmitted-confirmation-email.subject", new Object[]{emailData.get("confirmationCode")},
                locale);
    }

    private String setBodyCopy(Map<String, Object> emailData) {
        String p1 = messageSource.getMessage("email.family-application-transmitted-confirmation-email.p1",
                new Object[]{emailData.get("parentFirstName")},
                locale);
        String p2 = messageSource.getMessage("email.family-application-transmitted-confirmation-email.p2",
                new Object[]{emailData.get("ccrrName")}, locale);

        List<Map<String, Object>> providers = (List) emailData.getOrDefault("providersData", List.of());

        for (Map<String, Object> provider : providers) {
            p2 += getProviderResponseString(provider, messageSource, locale);

        }

        String p3 = messageSource.getMessage("email.family-application-transmitted-confirmation-email.p3",
                new Object[]{emailData.get("confirmationCode")},
                locale);
        String p4 = messageSource.getMessage("email.family-application-transmitted-confirmation-email.p4",
                new Object[]{emailData.get("ccrrName"), emailData.get("ccrrPhoneNumber")}, locale);
        String p5 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p6 = messageSource.getMessage("email.general.footer.cfa", null, locale);
        return p1 + p2 + p3 + p4 + p5 + p6;
    }

    private String getProviderResponseString(Map<String, Object> provider, MessageSource messageSource, Locale locale) {

        String providerResponses = "";
        String providerName =
                provider.get("providerType").equals("Individual") ? provider.get("childCareProviderInitials").toString() :
                        provider.getOrDefault("providerName", provider.get("childCareProgramName")).toString();

        if (provider.containsKey("providerResponseAgreeToCare")) {
            if ("true".equals(provider.get("providerResponseAgreeToCare"))) {
                providerResponses = String.format("%s%s", providerResponses,
                        messageSource.getMessage("email.family-application-transmitted-confirmation-email.li-agreed-to-care",
                                new Object[]{providerName,
                                        formatListIntoReadableString((List<String>) provider.get("childrenInitialsList"),
                                                messageSource.getMessage("general.and", null, locale)),
                                        provider.get("ccapStartDate")}, locale));
            } else {
                providerResponses = String.format("%s%s", providerResponses,
                        messageSource.getMessage(
                                "email.family-application-transmitted-confirmation-email.li-did-not-agree-to-care",
                                new Object[]{providerName,
                                        formatListIntoReadableString((List<String>) provider.get("childrenInitialsList"),
                                                messageSource.getMessage("general.and", null, locale))},
                                locale));
            }
        } else {
            providerResponses = String.format("%s%s", providerResponses,
                    messageSource.getMessage(
                            "email.family-application-transmitted-confirmation-email.li-did-not-complete-application-in-three-days",
                            new Object[]{providerName}, locale));
        }

        return String.format("<ul>%s</ul>", providerResponses);
    }
}
