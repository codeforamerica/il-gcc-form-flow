package org.ilgcc.app.email;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.templates.DailyNewApplicationsProviderEmailTemplate;
import org.ilgcc.app.email.templates.FamilyConfirmationEmailTemplate;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendDailyNewApplicationProviderEmail {

    private SendEmailJob sendEmailJob;

    private MessageSource messageSource;

    @Autowired
    public SendDailyNewApplicationProviderEmail(SendEmailJob sendEmailJob, MessageSource messageSource) {
        this.sendEmailJob = sendEmailJob;
        this.messageSource = messageSource;
    }

    public void send(Map<String, Object> emailData) {
        List<String> recipients = (List) emailData.get("recipients");

        log.info("SendDailyNewApplicationsProviderEmail enqueuing %s emails for %s and processing org: %s", recipients.size(), emailData.get("currentEmailDate"), emailData.get("processingOrgName"));

        recipients.forEach(recipient -> {
            ILGCCEmail email = new ILGCCEmail(recipient, emailTemplate(emailData));
            sendEmail(email);
        });
    }

    protected ILGCCEmailTemplate emailTemplate(Map<String, Object> emailData) {
        return new DailyNewApplicationsProviderEmailTemplate(emailData,
                messageSource).createTemplate();
    }

    protected void sendEmail(ILGCCEmail email) {
        sendEmailJob.enqueueSendEmailJob(email);
    }
}
