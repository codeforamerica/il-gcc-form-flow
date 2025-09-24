package org.ilgcc.jobs;

import static java.util.Collections.emptyList;
import static org.ilgcc.app.utils.SubmissionUtilities.haveAllProvidersResponded;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.ilgcc.app.email.SendProviderDidNotRespondToFamilyEmail;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProviderResponseRecurringJob {

    private final TransactionRepositoryService transactionRepositoryService;
    private final SubmissionRepositoryService submissionRepositoryService;
    private final CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransaction;

    private final SendProviderDidNotRespondToFamilyEmail sendProviderDidNotRespondToFamilyEmail;

    @Value("${il-gcc.no-provider-response-delay-seconds}")
    private int offsetDelaySeconds;

    public ProviderResponseRecurringJob(
            TransactionRepositoryService transactionRepositoryService,
            SubmissionRepositoryService submissionRepositoryService,
            CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransaction,
            SendProviderDidNotRespondToFamilyEmail sendProviderDidNotRespondToFamilyEmail) {
        this.transactionRepositoryService = transactionRepositoryService;
        this.submissionRepositoryService = submissionRepositoryService;
        this.ccmsSubmissionPayloadTransaction = ccmsSubmissionPayloadTransaction;
        this.sendProviderDidNotRespondToFamilyEmail = sendProviderDidNotRespondToFamilyEmail;
    }

    @Recurring(id = "no-provider-response-job", cron = "0 * * * *")
    @Job(name = "No provider response job")
    public void runNoProviderResponseJob() {

        log.info("Running No Provider Response Job for expired submissions.");

        Set<Submission> expiringSubmissionsToSend = transactionRepositoryService.findExpiringSubmissionsWithoutTransactions();

        log.info(String.format("Running the 'No provider response job' for %s expired submissions",
                expiringSubmissionsToSend.size()));

        if (expiringSubmissionsToSend.isEmpty()) {
            return;
        } else {
            // Every time the job runs, we can reset this back to the starting point of however long we'd
            // like to stagger everything. ie, if the stagger is 10 seconds, the first time is just 10
            AtomicInteger totalOffsetDelaySeconds = new AtomicInteger(this.offsetDelaySeconds);

            for (Submission expiredFamilySubmission : expiringSubmissionsToSend) {
                if (!hasProviderResponse(expiredFamilySubmission)) {
                    log.info("No provider response found for family submission {}.", expiredFamilySubmission.getId());

                    ccmsSubmissionPayloadTransaction.enqueueCCMSTransactionPayloadWithSecondsOffset(
                            expiredFamilySubmission.getId(), totalOffsetDelaySeconds.get());

                    // After we send a submission, stagger the next job by the offset
                    totalOffsetDelaySeconds.addAndGet(this.offsetDelaySeconds);

                    if (expiredFamilySubmission.getInputData().containsKey("providers")) {
                        List<Map<String, Object>> providersSubflowData = (List<Map<String, Object>>)
                                expiredFamilySubmission.getInputData().getOrDefault("providers", emptyList());
                        providersSubflowData.forEach(provider -> {
                            try {
                                if (!SubmissionStatus.RESPONDED.name().equals(provider.get("providerApplicationResponseStatus"))) {
                                    log.info("Sending did not respond email for provider ID: {} for family submission ID: {}",
                                            provider.get("uuid"), expiredFamilySubmission.getId());
                                    provider.put("providerApplicationResponseStatus", SubmissionStatus.EXPIRED.name());
                                    sendProviderDidNotRespondToFamilyEmail.send(expiredFamilySubmission, "providers",
                                            provider.get("uuid").toString(), totalOffsetDelaySeconds.get());

                                    // After we send an email, stagger the next job by the offset
                                    totalOffsetDelaySeconds.addAndGet(this.offsetDelaySeconds);
                                }
                            } catch (Exception e) {
                                log.warn("Unable to send ProviderDidNotRespondToFamilyEmail for family {} and provider {}",
                                        expiredFamilySubmission.getId(), provider.get("uuid"), e);
                            }
                        });
                    } else {
                        try {
                            sendProviderDidNotRespondToFamilyEmail.send(expiredFamilySubmission, totalOffsetDelaySeconds.get());
                        } catch (Exception e) {
                            log.warn("Unable to send ProviderDidNotRespondToFamilyEmail for family {}",
                                    expiredFamilySubmission.getId(), e);
                        }
                    }

                    updateProviderStatus(expiredFamilySubmission);
                } else {
                    log.warn(
                            String.format(
                                    "ProviderResponseRecurringJob: The Family and Provider Applications were submitted but they do not have a corresponding transaction. Check familySubmission: %s",
                                    expiredFamilySubmission.getId()));
                }
            }
        }
    }

    private void updateProviderStatus(Submission familySubmission) {
        familySubmission.getInputData().put("providerApplicationResponseStatus", SubmissionStatus.EXPIRED.name());
        submissionRepositoryService.save(familySubmission);
    }

    private boolean hasProviderResponse(Submission familySubmission) {
        // TODO: the else of this code can be removed when ENABLE_MULTIPLE_PROVIDERS is productized
        if (familySubmission.getInputData().containsKey("providers")) {
            return haveAllProvidersResponded(familySubmission);
        } else {
            // No sense refactoring this for single provider and risking it being broken, since we'll be removing this
            String providerResponseSubmissionId = (String) familySubmission.getInputData().get("providerResponseSubmissionId");

            if (providerResponseSubmissionId != null) {
                Optional<Submission> providerSubmission = submissionRepositoryService.findById(
                        UUID.fromString(providerResponseSubmissionId));
                return providerSubmission.isPresent() && providerSubmission.get().getSubmittedAt() != null;
            }
            return false;
        }
    }
}
