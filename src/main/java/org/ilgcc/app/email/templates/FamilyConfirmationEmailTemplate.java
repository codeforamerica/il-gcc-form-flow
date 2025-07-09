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

    public ILGCCEmailTemplate createTemplate() {
        return new ILGCCEmailTemplate(senderEmail(), setSubject(emailData), new Content("text/html", setBodyCopy(emailData)),
                EmailType.FAMILY_CONFIRMATION_EMAIL);
    }

    private Email senderEmail() {
        return new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale));
    }

    private String setSubject(Map<String, Object> emailData) {
        return messageSource.getMessage("email.general.subject.confirmation-code",
                new Object[]{emailData.get("confirmationCode")},
                locale);
    }

    private String setBodyCopy(Map<String, Object> emailData) {
        String p1 = messageSource.getMessage("email.family-confirmation.p1", new Object[]{emailData.get("parentFirstName")},
                locale);
        String p2 = messageSource.getMessage("email.family-confirmation.p2", null, locale);

        String p3;
        String p4;

        if ((boolean) emailData.get("hasMutipleProviders")) {
            p3 = messageSource.getMessage("email.family-confirmation.p3.multiple-providers", null, locale);
            p4 = messageSource.getMessage("email.family-confirmation.p4.multiple-providers",
                    new Object[]{emailData.get("shareableLink")}, locale);
        } else {
            p3 = messageSource.getMessage("email.family-confirmation.p3.single-provider", null, locale);
            p4 = messageSource.getMessage("email.family-confirmation.p4.single-provider",
                    new Object[]{emailData.get("shareableLink")}, locale);
        }

        String p5 = messageSource.getMessage("email.family-confirmation.p5", null, locale);
        String p6 = messageSource.getMessage("email.family-confirmation.p6",
                new Object[]{emailData.get("ccrrName"), emailData.get("ccrrPhoneNumber")}, locale);
        String p7 = messageSource.getMessage("email.family-confirmation.p7",
                new Object[]{emailData.get("confirmationCode"), emailData.get("submittedDate")},
                locale);
        String p8 = messageSource.getMessage("email.family-confirmation.p8", null, locale);
        String p9 = messageSource.getMessage("email.family-confirmation.p9", null, locale);
        String p10 = messageSource.getMessage("email.family-confirmation.p10", null, locale);
        String p11 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p12 = messageSource.getMessage("email.general.footer.cfa", null, locale);
        return p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12;
    }

}
