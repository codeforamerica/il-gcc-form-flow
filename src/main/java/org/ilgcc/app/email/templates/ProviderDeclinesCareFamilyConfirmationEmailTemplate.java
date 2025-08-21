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
public class ProviderDeclinesCareFamilyConfirmationEmailTemplate {

    private Map<String, Object> emailData;
    private MessageSource messageSource;
    private Locale locale;

    public ProviderDeclinesCareFamilyConfirmationEmailTemplate(Map<String, Object> emailData, MessageSource messageSource,
            Locale locale) {
        this.emailData = emailData;
        this.messageSource = messageSource;
        this.locale = locale;

    }

    public ILGCCEmailTemplate createTemplate() {
        return new ILGCCEmailTemplate(senderEmail(), setSubject(emailData), new Content("text/html", setBodyCopy(emailData)),
                EmailType.PROVIDER_DECLINES_CARE_FAMILY_EMAIL);
    }

    private Email senderEmail() {
        return new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale));
    }

    private String setSubject(Map<String, Object> emailData) {
        return messageSource.getMessage("email.response-email-for-family.provider-declines.subject", new Object[]{emailData.get("confirmationCode")}, locale);
    }


    private String setBodyCopy(Map<String, Object> emailData) {
        String p1 = messageSource.getMessage("email.response-email-for-family.provider-declines.p1",
                new Object[]{emailData.get("parentFirstName")}, locale);

        String providerType = emailData.getOrDefault("providerType", "").toString();
        String programName = emailData.get("childCareProgramName").toString();
        String childCareProviderInitials = emailData.get("childCareProviderInitials").toString();

        String p2;
        if (providerType.equals("Care Program") || !programName.isBlank()) {
            p2 = messageSource.getMessage(
                    "email.response-email-for-family.provider-declines.p2-program", new Object[]{programName},
                    locale);
        } else {
            p2 = messageSource.getMessage(
                    "email.response-email-for-family.provider-declines.p2-individual", new Object[]{childCareProviderInitials},
                    locale);
        }
        String p3 = messageSource.getMessage("email.response-email-for-family.provider-declines.p3",
                new Object[]{formatListIntoReadableString((List<String>) emailData.get("childrenInitialsList"),
                        messageSource.getMessage("general.and", null, locale)), emailData.get("ccapStartDate")}, locale);
        String p4 = messageSource.getMessage("email.response-email-for-family.provider-declines.p4",
                new Object[]{emailData.get("confirmationCode")},
                locale);
        String p5 = messageSource.getMessage("email.response-email-for-family.provider-declines.p5",
                new Object[]{emailData.get("ccrrName"), emailData.get("ccrrPhoneNumber")}, locale);
        String p6 = messageSource.getMessage("email.response-email-for-family.provider-declines.p6",
                null, locale);
        String p7 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p8 = messageSource.getMessage("email.general.footer.cfa", null, locale);
        return p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8;
    }

}
