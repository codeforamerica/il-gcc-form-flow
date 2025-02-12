package org.ilgcc.jobs;


import com.sendgrid.helpers.mail.objects.Content;
import formflow.library.data.Submission;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendGridEmailService;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendEmailJob {

    private final JobScheduler jobScheduler;
    private final SendGridEmailService sendGridEmailService;

    @Value("${il-gcc.enable-emails}")
    boolean emailsEnabled;

    public SendEmailJob(JobScheduler jobScheduler, SendGridEmailService sendGridEmailService) {
        this.jobScheduler = jobScheduler;
        this.sendGridEmailService = sendGridEmailService;
    }


    public void enqueueSendEmailJob(String recipientAddress, String senderName, String subject, String emailType, Content content,
            Submission submission) {
        if (emailsEnabled) {
            JobId jobId = jobScheduler.enqueue(() -> sendEmailRequest(recipientAddress, senderName, subject, emailType, content, submission));
            log.info("Enqueued {} email job with ID: {} for submission with ID: {}", emailType, jobId, submission.getId());
        } else {
            log.info("Emails disabled. Skipping enqueue {} email job for submission with ID: {}", emailType, submission.getId());
            log.info("Would have sent: {} with a subject of: {} from: {}", content.getValue(), subject, senderName); // Don't log recipient email for security reasons
        }
    }

    @Job(name = "Send Email Request", retries = 3)
    public void sendEmailRequest(String recipientAddress, String senderName, String subject, String emailType, Content content,
            Submission submission) throws IOException {
        try {
            sendGridEmailService.sendEmail(recipientAddress, senderName, subject, content);
            log.info("Successfully sent the {} for submission with ID {} to Sendgrid.", emailType, submission.getId());
        } catch (IOException e) {
            log.error("There was an error when attempting to send the {} for submission with ID {}: {}", emailType,
                    submission.getId(), e.getMessage());
            throw e;
        }
    }
}