package org.ilgcc.app.email;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.context.MessageSource;

@Slf4j
public abstract class SendEmail {

    protected String emailSentStatusInputName;
    protected String recipientEmailInputName;

    protected Locale locale;

    protected SendEmailJob sendEmailJob;

    protected MessageSource messageSource;

    protected SubmissionRepositoryService submissionRepositoryService;


    public SendEmail(SendEmailJob sendEmailJob, MessageSource messageSource,
            SubmissionRepositoryService submissionRepositoryService, String emailSentStatusInputName,
            String recipientEmailInputName) {
        this.sendEmailJob = sendEmailJob;
        this.messageSource = messageSource;
        this.submissionRepositoryService = submissionRepositoryService;
        this.emailSentStatusInputName = emailSentStatusInputName;
        this.recipientEmailInputName = recipientEmailInputName;
    }

    public void send(Submission submission) {
        send(submission, null, null);
    }

    public void send(Submission submission, int offsetDelaySeconds) {
        send(submission, null, null, offsetDelaySeconds);
    }

    public void send(Submission submission, String subflowName, String subflowUuid) {
        send(submission, subflowName, subflowUuid, 0);
    }

    public void send(Submission submission, String subflowName, String subflowUuid, int offsetDelaySeconds) {

        Map<String, Object> subflowData = null;
        if (subflowName != null && subflowUuid != null) {
            subflowData = submission.getSubflowEntryByUuid(subflowName, subflowUuid);
            if (subflowData == null) {
                String errorMsg =
                        "subflow " + subflowName + " not found in submission " + submission.getId() + " for subflowUuid "
                                + subflowUuid;
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
        }

        if (!skipEmailSend(subflowData != null ? subflowData : submission.getInputData())) {
            Optional<Map<String, Object>> emailData = getEmailData(submission, subflowData);

            if (emailData.isEmpty()) {
                return;
            }

            locale = emailData.get().get("familyPreferredLanguage").equals("Spanish") ? Locale.forLanguageTag(
                    "es") : Locale.ENGLISH;

            if (!getRecipientEmail(emailData.get()).isBlank()) {
                ILGCCEmail email = new ILGCCEmail(getRecipientEmail(emailData.get()), emailTemplate(emailData.get()),
                        submission.getId());
                sendEmail(email, submission, subflowName, subflowData, offsetDelaySeconds);
            } else {
                log.debug(
                        "{}: Skipping email send because because there is no {} associated with the submission: {}",
                        getClass().getSimpleName(), recipientEmailInputName, submission.getId());
            }
        }
    }

    protected abstract ILGCCEmailTemplate emailTemplate(Map<String, Object> emailData);

    protected Optional<Map<String, Object>> getEmailData(Submission familySubmission) {
        return getEmailData(familySubmission, null);
    }

    protected Optional<Map<String, Object>> getEmailData(Submission familySubmission, Map<String, Object> subflowData) {
        return Optional.empty();
    }

    protected Boolean skipEmailSend(Map<String, Object> inputData) {
        return inputData.getOrDefault(emailSentStatusInputName, "false").equals("true");
    }

    protected String getRecipientEmail(Map<String, Object> emailData) {
        return emailData.getOrDefault(recipientEmailInputName, "").toString();
    }

    protected void sendEmail(ILGCCEmail email, Submission submission, String subflowName, Map<String, Object> subflowData,
            int offsetDelaySeconds) {
        log.info("{}: About to enqueue the Send Email Job for submissionId: {}",
                email.getEmailType(), submission.getId());
        sendEmailJob.enqueueSendSubmissionEmailJob(email, offsetDelaySeconds);
        updateEmailStatus(submission, subflowName, subflowData);
    }

    private void updateEmailStatus(Submission submission, String subflowName, Map<String, Object> subflowData) {
        if (subflowData == null) {
            submission.getInputData().put(emailSentStatusInputName, "true");
        } else {
            Map<String, Object> data = new HashMap<>() {{
                put(emailSentStatusInputName, "true");
            }};
            submission.mergeFormDataWithSubflowIterationData(subflowName, subflowData, data);
        }
        submissionRepositoryService.save(submission);
    }

    protected Optional<Submission> getFamilyApplication(Submission providerSubmission) {
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionId.isEmpty()) {
            return Optional.empty();
        }

        return submissionRepositoryService.findById(familySubmissionId.get());
    }
}
