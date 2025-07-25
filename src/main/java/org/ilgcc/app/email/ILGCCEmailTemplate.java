package org.ilgcc.app.email;

import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

public final class ILGCCEmailTemplate {

    private final Email senderEmail;
    private final String subject;
    private final Content body;
    private final ILGCCEmail.EmailType emailType;

    public ILGCCEmailTemplate(Email senderEmail, String subject, Content body, ILGCCEmail.EmailType emailType) {
        this.senderEmail = new Email(senderEmail.getEmail(), senderEmail.getName());
        this.subject = subject;
        this.body = new Content(body.getType(), body.getValue());
        this.emailType = emailType;
    }

    public Email getSenderEmail() {
        return new Email(senderEmail.getEmail(), senderEmail.getName());
    }

    public String getSubject() {
        return subject;
    }

    public Content getBody() {
        return new Content(body.getType(), body.getValue());
    }

    public ILGCCEmail.EmailType getEmailType() {
        return emailType;
    }
}
