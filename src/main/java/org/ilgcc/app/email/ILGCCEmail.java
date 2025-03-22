package org.ilgcc.app.email;

import java.io.Serializable;
import lombok.Getter;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.util.UUID;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ILGCCEmail implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String FROM_ADDRESS = "noreply@getchildcareil.org";
    public static final String EMAIL_SENDER_KEY = "email.general.sender-name";

    private Email senderEmail;
    private String subject;
    private Content body;
    private EmailType emailType;
    private UUID submissionId;
    private Email recipientEmail;

    public ILGCCEmail(String recipientAddress, ILGCCEmailTemplate emailTemplate, UUID submissionId) {
        this.senderEmail = emailTemplate.getSenderEmail();
        this.recipientEmail = new Email(recipientAddress);
        this.subject = emailTemplate.getSubject();
        this.body = emailTemplate.getBody();
        this.emailType = emailTemplate.getEmailType();
        this.submissionId = submissionId;
    }

    @Getter
    public enum EmailType {
        FAMILY_CONFIRMATION_EMAIL("Family Confirmation Email"), FAMILY_CONFIRMATION_EMAIL_NO_PROVIDER(
                "No Provider Family Confirmation Email"), PROVIDER_AGREES_TO_CARE_FAMILY_EMAIL(
                "Provider Agrees to Care Family Email"), PROVIDER_CONFIRMATION_EMAIL(
                "Provider confirmation email"), PROVIDER_DECLINES_CARE_FAMILY_EMAIL("Provider Declines Care Family Email");

        private final String description;

        EmailType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

}
