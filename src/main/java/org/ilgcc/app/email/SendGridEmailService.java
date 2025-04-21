package org.ilgcc.app.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class SendGridEmailService {

    @Autowired
    MessageSource messageSource;

    private final SendGrid sendGrid = new SendGrid(System.getenv("SENDGRID_API_KEY"));

    public Response sendEmail(ILGCCEmail ilgccEmail) throws IOException {
        Mail mail = new Mail();
        mail.setFrom(ilgccEmail.getSenderEmail());
        mail.setSubject(ilgccEmail.getSubject());
        mail.addContent(ilgccEmail.getBody());

        for (Email email : ilgccEmail.getRecipientEmails()) {
            Personalization personalization = new Personalization();
            personalization.addTo(email);
            mail.addPersonalization(personalization);
        }

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        return sendGrid.api(request);
    }
}
