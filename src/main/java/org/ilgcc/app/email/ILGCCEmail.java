package org.ilgcc.app.email;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
    private List<Email> recipientEmails = new ArrayList<>();
    private String orgId;

    public ILGCCEmail(@NotNull String recipientAddress, ILGCCEmailTemplate emailTemplate, UUID submissionId) {
        this.senderEmail = emailTemplate.getSenderEmail();
        this.recipientEmails.add(new Email(recipientAddress));
        this.subject = emailTemplate.getSubject();
        this.body = emailTemplate.getBody();
        this.emailType = emailTemplate.getEmailType();
        this.submissionId = submissionId;
    }

    public ILGCCEmail(@NotNull List<String> recipientAddresses, ILGCCEmailTemplate emailTemplate, String orgId) {
        this.senderEmail = emailTemplate.getSenderEmail();
        for (String recipientAddress : recipientAddresses) {
            this.recipientEmails.add(new Email(recipientAddress));
        }
        this.subject = emailTemplate.getSubject();
        this.body = emailTemplate.getBody();
        this.emailType = emailTemplate.getEmailType();
        this.orgId = orgId;
    }

    @Getter
    public enum EmailType {
        FAMILY_CONFIRMATION_EMAIL("Family Confirmation Email"),
        FAMILY_CONFIRMATION_EMAIL_NO_PROVIDER("No Provider Family Confirmation Email"),
        PROVIDER_AGREES_TO_CARE_FAMILY_EMAIL("Provider Agrees to Care Family Email"),
        PROVIDER_CONFIRMATION_EMAIL("Provider Confirmation Email"),
        UNIDENTIFIED_PROVIDER_CONFIRMATION_EMAIL("Unidentified Provider Confirmation Email"),
        PROVIDER_DECLINES_CARE_FAMILY_EMAIL("Provider Declines Care Family Email"),
        PROVIDER_DID_NOT_RESPOND_FAMILY_EMAIL("Provider Did Not Respond Family Email"),
        NEW_PROVIDER_AGREES_TO_CARE_FAMILY_EMAIL("New Provider Agrees to Care Family Email"),
        NEW_PROVIDER_CONFIRMATION_EMAIL("New Provider Confirmation Email"),
        AUTOMATED_PROVIDER_OUTREACH_EMAIL("Automated Provider Outreach Email"),
        DAILY_NEW_APPLICATIONS_PROVIDER_EMAIL("Automated Email going to Processing Organizations Daily");

        private final String description;

        EmailType(String description) {
            this.description = description;
        }

    }

}
