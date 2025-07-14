package org.ilgcc.jobs;

import static java.util.Collections.emptyList;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.ilgcc.app.email.SendProviderDidNotRespondToFamilyEmail;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Deprecated
@Slf4j
@Service
public class ProviderResponseRecurringJob {

    private final TransactionRepositoryService transactionRepositoryService;
    private final SubmissionRepositoryService submissionRepositoryService;
    private final CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransaction;
    private final boolean isCCMSIntegrationEnabled;

    private final SendProviderDidNotRespondToFamilyEmail sendProviderDidNotRespondToFamilyEmail;

    public ProviderResponseRecurringJob(
            TransactionRepositoryService transactionRepositoryService,
            SubmissionRepositoryService submissionRepositoryService,
            CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransaction,
            @Value("${il-gcc.ccms-integration-enabled:false}") boolean isCCMSIntegrationEnabled,
            SendProviderDidNotRespondToFamilyEmail sendProviderDidNotRespondToFamilyEmail) {
        this.transactionRepositoryService = transactionRepositoryService;
        this.submissionRepositoryService = submissionRepositoryService;
        this.ccmsSubmissionPayloadTransaction = ccmsSubmissionPayloadTransaction;
        this.isCCMSIntegrationEnabled = isCCMSIntegrationEnabled;
        this.sendProviderDidNotRespondToFamilyEmail = sendProviderDidNotRespondToFamilyEmail;
    }

    @Recurring(id = "no-provider-response-job", cron = "0 * * * *")
    @Job(name = "No provider response job")
    public void runNoProviderResponseJob() {

        if (!isCCMSIntegrationEnabled) {
            // Nothing is enabled. This seems wrong!
            log.error("CCMS integration is not turned on. Why?");
            return;
        }

        log.info("Running No Provider Response Job for expired submissions.");

        Set<Submission> expiringSubmissionsToSend = transactionRepositoryService.findExpiringSubmissionsWithoutTransactions();

        log.info(String.format("Running the 'No provider response job' for %s expired submissions",
                expiringSubmissionsToSend.size()));

        if (expiringSubmissionsToSend.isEmpty()) {
            return;
        } else {
            for (Submission expiredFamilySubmission : expiringSubmissionsToSend) {
                if (!hasProviderResponse(expiredFamilySubmission)) {
                    log.info("No provider response found for family submission {}.", expiredFamilySubmission.getId());

                    ccmsSubmissionPayloadTransaction.enqueueCCMSTransactionPayloadInstantly(
                            expiredFamilySubmission.getId());

                    updateProviderStatus(expiredFamilySubmission);
                    if (expiredFamilySubmission.getInputData().containsKey("providers")) {
                        List<Map<String, Object>> providersSubflowData = (List<Map<String, Object>>)
                                expiredFamilySubmission.getInputData().getOrDefault("providers", emptyList());
                        providersSubflowData.forEach(provider -> {
                            if (!SubmissionStatus.RESPONDED.name().equals(provider.get("providerResponseStatus"))) {
                                log.info("Sending did not respond email for provider ID: {} for family submission ID: {}",
                                        provider.get("uuid"), expiredFamilySubmission.getId());
                                sendProviderDidNotRespondToFamilyEmail.send(expiredFamilySubmission, "providers",
                                        provider.get("uuid").toString());
                            }
                        });
                    } else {
                        sendProviderDidNotRespondToFamilyEmail.send(expiredFamilySubmission);
                    }


                } else {
                    log.warn(
                            String.format(
                                    "ProviderResponseRecurringJob: The Family and Provider Applications were submitted but they do not have a corresponding transmission or transaction. Check familySubmission: %s",
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
        String providerResponseSubmissionId = (String) familySubmission.getInputData().get("providerResponseSubmissionId");

        if (providerResponseSubmissionId != null) {
            Optional<Submission> providerSubmission = submissionRepositoryService.findById(
                    UUID.fromString(providerResponseSubmissionId));
            return providerSubmission.isPresent() && providerSubmission.get().getSubmittedAt() != null;
        }

        return false;
    }
}
