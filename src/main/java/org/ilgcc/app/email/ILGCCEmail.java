package org.ilgcc.app.email;

import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

public class ILGCCEmail implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String FROM_ADDRESS = "noreply@getchildcareil.org";
    public static final String EMAIL_SENDER_KEY = "email.general.sender-name";

    private final Email senderEmail;
    private final String subject;
    private final Content body;
    private final EmailType emailType;
    private final UUID submissionId;
    private final List<Email> recipientEmails;
    private final String orgId;

    public ILGCCEmail(@NotNull List<String> recipientAddresses, ILGCCEmailTemplate emailTemplate, String orgId) {
        this.senderEmail = new Email(emailTemplate.getSenderEmail().getEmail(), emailTemplate.getSenderEmail().getName());
        this.subject = emailTemplate.getSubject();
        this.body = new Content(emailTemplate.getBody().getType(), emailTemplate.getBody().getValue());
        this.emailType = emailTemplate.getEmailType();
        this.submissionId = null;
        this.orgId = orgId;
        this.recipientEmails = recipientAddresses.stream().map(Email::new).toList();
    }

    public ILGCCEmail(@NotNull String recipientAddress, ILGCCEmailTemplate emailTemplate, UUID submissionId) {
        this.senderEmail = emailTemplate.getSenderEmail();
        this.subject = emailTemplate.getSubject();
        this.body = emailTemplate.getBody();
        this.emailType = emailTemplate.getEmailType();
        this.submissionId = submissionId;
        this.orgId = null;
        this.recipientEmails = List.of(new Email(recipientAddress));
    }

    public Email getSenderEmail() {
        return senderEmail;
    }

    public String getSubject() {
        return subject;
    }

    public Content getBody() {
        return body;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public UUID getSubmissionId() {
        return submissionId;
    }

    public List<Email> getRecipientEmails() {
        return recipientEmails;
    }

    public String getOrgId() {
        return orgId;
    }

    @Getter
    public enum EmailType {
        FAMILY_CONFIRMATION_EMAIL("Family Confirmation Email"), FAMILY_CONFIRMATION_EMAIL_NO_PROVIDER(
                "No Provider Family Confirmation Email"), PROVIDER_AGREES_TO_CARE_FAMILY_EMAIL(
                "Provider Agrees to Care Family Email"), PROVIDER_CONFIRMATION_EMAIL(
                "Provider Confirmation Email"), UNIDENTIFIED_PROVIDER_CONFIRMATION_EMAIL(
                "Unidentified Provider Confirmation Email"), PROVIDER_DECLINES_CARE_FAMILY_EMAIL(
                "Provider Declines Care Family Email"), PROVIDER_DID_NOT_RESPOND_FAMILY_EMAIL(
                "Provider Did Not Respond Family Email"), NEW_PROVIDER_AGREES_TO_CARE_FAMILY_EMAIL(
                "New Provider Agrees to Care Family Email"), NEW_PROVIDER_CONFIRMATION_EMAIL(
                "New Provider Confirmation Email"), AUTOMATED_PROVIDER_OUTREACH_EMAIL(
                "Automated Provider Outreach Email"), DAILY_NEW_APPLICATIONS_PROVIDER_EMAIL(
                "Automated Email going to Processing Organizations Daily"), FAMILY_APPLICATION_TRANSMITTED_CONFIRMATION_EMAIL(
                "Family Application Transmitted Email");
        private final String description;

        EmailType(String description) {
            this.description = description;
        }

    }
}
