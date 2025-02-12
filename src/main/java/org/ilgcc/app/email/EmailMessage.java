package org.ilgcc.app.email;

import com.sendgrid.helpers.mail.objects.Content;
import formflow.library.data.Submission;
import lombok.Getter;
@Getter
// EmailMessage
public class EmailMessage {
    public static String recipientAddress;
    public static String senderName;
    public static String subject;
    public static String emailType;
    public static Content content;
    public static Submission submission;

    public EmailMessage(String recipientAddress, String senderName, String subject, String emailType, Content content,
            Submission submission){
        this.recipientAddress = recipientAddress;
        this.senderName = senderName;
        this.subject = subject;
        this.emailType = emailType;
        this.content = content;
        this.submission = submission;
    }
}
