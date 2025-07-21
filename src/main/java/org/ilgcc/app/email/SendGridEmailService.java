package org.ilgcc.app.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import java.io.IOException;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SendGridEmailService {

    private final SendGrid sendGrid = new SendGrid(System.getenv("SENDGRID_API_KEY"));

    public Response sendEmail(ILGCCEmail ilgccEmail) throws IOException {
        // Defensive copies
        Email fromEmail = new Email(ilgccEmail.getSenderEmail().getEmail(), ilgccEmail.getSenderEmail().getName());
        String subject = ilgccEmail.getSubject();
        Content content = new Content("text/html", ilgccEmail.getBody().getValue());

        // Mail
        Mail mail = new Mail();
        mail.setFrom(fromEmail);
        mail.setSubject(subject);
        mail.addContent(content);

        // Defensive copy of recipient list
        List<Email> recipients = new ArrayList<>(ilgccEmail.getRecipientEmails());
        for (Email recipient : recipients) {
            Personalization personalization = new Personalization();
            personalization.addTo(new Email(recipient.getEmail(), recipient.getName()));
            mail.addPersonalization(personalization);
        }

        // Send request
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        return sendGrid.api(request);
    }
}
