package org.ilgcc.app.email;

import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.Getter;
import lombok.Setter;
import org.ilgcc.app.email.ILGCCEmail.EmailType;

@Getter
@Setter
public class ILGCCEmailTemplate {

    private Email senderEmail;
    private String subject;
    private Content body;
    private EmailType emailType;

    public  ILGCCEmailTemplate(){}

    public ILGCCEmailTemplate(Email senderEmail, String subject, Content body, EmailType emailType) {
        this.senderEmail = senderEmail;
        this.subject = subject;
        this.body = body;
        this.emailType = emailType;
    }
}
