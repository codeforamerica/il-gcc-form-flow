package org.ilgcc.app.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class SendGridEmailService {

    @Autowired
    MessageSource messageSource;

    private final SendGrid sendGrid = new SendGrid(System.getenv("SENDGRID_API_KEY"));

    public Response sendEmail(String recipientAddress, String senderName, String subject, Content content) throws IOException {
        Email senderEmail = new Email(ILGCCEmail.FROM_ADDRESS, senderName);
        Email recipientEmail = new Email(recipientAddress);
        Mail mail = new Mail(senderEmail, subject, recipientEmail, content);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        return sendGrid.api(request);
    }

    public Response sendEmail(ILGCCEmail ilgccEmail) throws IOException {
        Mail mail = new Mail(ilgccEmail.getSenderEmail(), ilgccEmail.getSubject(), ilgccEmail.getRecipientEmail(),
                ilgccEmail.getBody());
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        return sendGrid.api(request);
    }
}
