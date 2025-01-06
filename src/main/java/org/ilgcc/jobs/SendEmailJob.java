package org.ilgcc.jobs;



import com.sendgrid.helpers.mail.objects.Content;
import formflow.library.data.Submission;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendGridEmailService;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendEmailJob {
    
    private final JobScheduler jobScheduler;
    private final SendGridEmailService sendGridEmailService;

    public SendEmailJob(
            JobScheduler jobScheduler, SendGridEmailService sendGridEmailService) {
        this.jobScheduler = jobScheduler;
        this.sendGridEmailService = sendGridEmailService;
    }

    public void enqueueSendEmailJob(String recipientAddress, String subject, String emailType, Content content, Submission submission) {
        JobId jobId = jobScheduler.enqueue(() -> sendEmailRequest(recipientAddress, subject, emailType, content, submission));
        log.info("Enqueued {} email job with ID: {} for submission with ID: {}", emailType, jobId, submission.getId());
    }

    @Job(name = "Send Email Request", retries = 3)
    public void sendEmailRequest(String recipientAddress, String subject, String emailType, Content content, Submission submission)
            throws IOException {
        try {
            sendGridEmailService.sendEmail(recipientAddress, subject, content);
            log.info ("Successfully sent the {} for submission with ID {} to Sendgrid.", emailType, submission.getId());
        } catch (IOException e) {
            log.error("There was an error when attempting to send the {} for submission with ID {}: {}", emailType, submission.getId(), e.getMessage());
            throw e;
        }
    }
}