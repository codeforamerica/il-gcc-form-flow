package org.ilgcc.jobs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.Transaction;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.ilgcc.app.data.ccms.CCMSApiClient;
import org.ilgcc.app.data.ccms.CCMSTransactionLookup;
import org.jobrunr.jobs.annotations.Job;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EnqueueTransactionWorkItemLookupJob {
    
    private final CCMSApiClient ccmsApiClient;
    private final TransactionRepositoryService transactionRepositoryService;

    public EnqueueTransactionWorkItemLookupJob(CCMSApiClient ccmsApiClient, TransactionRepositoryService transactionRepositoryService) {
        this.ccmsApiClient = ccmsApiClient;
        this.transactionRepositoryService = transactionRepositoryService;
    }

    @Job(name = "Lookup Work Item ID for Transaction", retries = 3)
    public void lookupWorkItemIDForTransaction(Transaction transaction) throws JsonProcessingException {
        log.info("Looking up work item ID for transaction {}", transaction.getTransactionId());
        JsonNode jsonNode = ccmsApiClient.sendRequest("/fetch", new CCMSTransactionLookup(transaction.getTransactionId()));
        if (jsonNode.has("workItem")) {
            transactionRepositoryService.setWorkItemId(transaction.getTransactionId(), jsonNode.get("workItem").asText());
            log.info("Successfully updated transaction {} with work item ID {}", transaction.getTransactionId(), jsonNode.get("workItem").asText());
        }
    }
}
