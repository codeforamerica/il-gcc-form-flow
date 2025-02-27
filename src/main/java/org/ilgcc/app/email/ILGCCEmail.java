package org.ilgcc.app.email;

import lombok.Getter;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.util.UUID;

@Getter
public class ILGCCEmail {

    public static final String FROM_ADDRESS = "contact@getchildcareil.org";
    public static final String EMAIL_SENDER_KEY = "email.general.sender-name";

    private Email senderEmail;
    private String subject;
    private Content body;
    private EmailType emailType;
    private UUID submissionId;
    private Email recipientEmail;

    public ILGCCEmail(String senderName, String recipientAddress, String subject, Content body, EmailType emailType,
            UUID submissionId) {
        this.senderEmail = new Email(FROM_ADDRESS, senderName);
        this.recipientEmail = new Email(recipientAddress);
        this.subject = subject;
        this.body = body;
        this.emailType = emailType;
        this.submissionId = submissionId;
    }


    public static ILGCCEmail createProviderConfirmationEmail(String senderName, String recipientAddress, String subject,
            Content body, UUID submissionId) {
        return new ILGCCEmail(senderName, recipientAddress, subject, body, EmailType.PROVIDER_CONFIRMATION_EMAIL, submissionId);
    }

    public static ILGCCEmail createFamilyConfirmationEmail(String senderName, String recipientAddress, String subject,
            Content body, UUID submissionId) {
        return new ILGCCEmail(senderName, recipientAddress, subject, body, EmailType.FAMILY_CONFIRMATION_EMAIL, submissionId);
    }

    public static ILGCCEmail createProviderAgreesToCareFamilyConfirmationEmail(String senderName, String recipientAddress, String subject,
            Content body, UUID submissionId) {
        return new ILGCCEmail(senderName, recipientAddress, subject, body, EmailType.PROVIDER_AGREES_TO_CARE_FAMILY_EMAIL, submissionId);
    }

    @Getter
    public enum EmailType {
        FAMILY_CONFIRMATION_EMAIL("Family Confirmation Email"), FAMILY_CONFIRMATION_EMAIL_NO_PROVIDER(
                "No Provider Family Confirmation Email"), PROVIDER_AGREES_TO_CARE_FAMILY_EMAIL(
                "Provider Agrees to Care Family Email"), PROVIDER_CONFIRMATION_EMAIL("Provider confirmation email");

        private final String description;

        EmailType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

}
