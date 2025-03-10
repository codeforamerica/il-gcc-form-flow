package org.ilgcc.jobs;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.Transaction;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "il-gcc.ccms-integration-enabled", havingValue = "true")
public class TransactionWorkItemRecurringJob {

    private final TransactionRepositoryService transactionRepositoryService;
    private final EnqueueTransactionWorkItemLookupJob enqueueTransactionWorkItemLookupJob;
    private final JobScheduler jobScheduler;
    private final Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);

    public TransactionWorkItemRecurringJob(TransactionRepositoryService transactionRepositoryService,
            EnqueueTransactionWorkItemLookupJob enqueueTransactionWorkItemLookupJob, JobScheduler jobScheduler) {
        this.transactionRepositoryService = transactionRepositoryService;
        this.enqueueTransactionWorkItemLookupJob = enqueueTransactionWorkItemLookupJob;
        this.jobScheduler = jobScheduler;
    }

    @Recurring(id = "work-item-lookup-job", cron = "0 * * * *")
    @Job(name = "Transaction work item lookup job")
    public void transactionWorkItemLookupJob() {
        List<Transaction> transactions = transactionRepositoryService.getTransactionsWithoutWorkItemIds();
        log.info("Running the Transaction Work Item Lookup recurring job. Found {} transactions without work item IDs.", transactions.size());
        transactions.stream().filter(transaction -> transaction.getCreatedAt().before(Date.from(oneHourAgo)))
                .forEach(transaction -> {
                    jobScheduler.enqueue(() -> enqueueTransactionWorkItemLookupJob.lookupWorkItemIDForTransaction(transaction));
                });
    }
    
    public List<Transaction> getTransactionsWithoutWorkItemIdsOlderThanOneHour() {
        return transactionRepositoryService.getTransactionsWithoutWorkItemIds().stream()
                .filter(transaction -> transaction.getCreatedAt().before(Date.from(oneHourAgo))).toList();
    }
}
