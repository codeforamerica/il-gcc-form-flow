package org.ilgcc.app.email;

import com.sendgrid.helpers.mail.objects.Content;
import lombok.Getter;
import lombok.Setter;
import org.ilgcc.app.email.ILGCCEmail.EmailType;

@Getter
@Setter
public class ILGCCEmailTemplate {

    private String subject;
    private Content body;
    private EmailType emailType;


    public ILGCCEmailTemplate(String subject, Content body, EmailType emailType) {
        this.subject = subject;
        this.body = body;
        this.emailType = emailType;
    }
}
