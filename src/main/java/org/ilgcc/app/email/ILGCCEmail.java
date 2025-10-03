package org.ilgcc.app.email;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonIgnore
    public ILGCCEmail(@NotNull List<String> recipientAddresses, ILGCCEmailTemplate emailTemplate, String orgId) {
        this(
                new Email(emailTemplate.getSenderEmail().getEmail(), emailTemplate.getSenderEmail().getName()),
                emailTemplate.getSubject(),
                new Content(emailTemplate.getBody().getType(), emailTemplate.getBody().getValue()),
                emailTemplate.getEmailType(),
                null,
                recipientAddresses.stream().map(Email::new).toList(),
                orgId
        );
    }

    @JsonIgnore
    public ILGCCEmail(@NotNull String recipientAddress, ILGCCEmailTemplate emailTemplate, UUID submissionId) {
        this(
                emailTemplate.getSenderEmail(),
                emailTemplate.getSubject(),
                emailTemplate.getBody(),
                emailTemplate.getEmailType(),
                submissionId,
                List.of(new Email(recipientAddress)),
                null
        );
    }

    @JsonCreator
    public ILGCCEmail(
            @JsonProperty("senderEmail") Email senderEmail,
            @JsonProperty("subject") String subject,
            @JsonProperty("body") Content body,
            @JsonProperty("emailType") EmailType emailType,
            @JsonProperty("submissionId") UUID submissionId,
            @JsonProperty("recipientEmails") List<Email> recipientEmails,
            @JsonProperty("orgId") String orgId
    ) {
        this.senderEmail = senderEmail;
        this.subject = subject;
        this.body = body;
        this.emailType = emailType;
        this.submissionId = submissionId;
        // Defensive copy for immutability:
        this.recipientEmails = recipientEmails == null ? List.of() : List.copyOf(recipientEmails);
        this.orgId = orgId;
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
        FAMILY_CONFIRMATION_EMAIL("Family Confirmation Email"),
        FAMILY_CONFIRMATION_EMAIL_NO_PROVIDER("No Provider Family Confirmation Email"),
        PROVIDER_AGREES_TO_CARE_FAMILY_EMAIL("Provider Agrees to Care Family Email"),
        PROVIDER_RESPONDED_CONFIRMATION_EMAIL("Provider Responded Confirmation Email"),
        UNIDENTIFIED_PROVIDER_CONFIRMATION_EMAIL("Unidentified Provider Confirmation Email"),
        PROVIDER_DECLINES_CARE_FAMILY_EMAIL("Provider Declines Care Family Email"),
        PROVIDER_DID_NOT_RESPOND_FAMILY_EMAIL("Provider Did Not Respond Family Email"),
        NEW_PROVIDER_AGREES_TO_CARE_FAMILY_EMAIL("New Provider Agrees to Care Family Email"),
        AUTOMATED_PROVIDER_OUTREACH_EMAIL("Automated Provider Outreach Email"),
        FAMILY_APPLICATION_TRANSMITTED_PROVIDER_CONFIRMATION_EMAIL("Family Application Transmitted Email to Provider"),
        DAILY_NEW_APPLICATIONS_PROVIDER_EMAIL("Automated Email going to Processing Organizations Daily"),
        FAMILY_APPLICATION_TRANSMITTED_CONFIRMATION_EMAIL("Family Application Transmitted Email");

        private final String description;

        EmailType(String description) {
            this.description = description;
        }
    }
}
