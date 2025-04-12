package org.ilgcc.app.data;

import formflow.library.data.Submission;
import jakarta.transaction.Transactional;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class TransactionRepositoryService {

    TransactionRepository transactionRepository;

    public TransactionRepositoryService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction getByTransactionId(UUID transactionId) {
        return transactionRepository.findByTransactionId(transactionId);
    }

    public Transaction getByWorkItemId(String workItemId) {
        return transactionRepository.findByWorkItemId(workItemId);
    }

    public Transaction getBySubmissionId(UUID submissionId) {
        return transactionRepository.findBySubmissionId(submissionId);
    }

    public List<Transaction> getTransactionsWithoutWorkItemIds() {
        return transactionRepository.findByWorkItemIdIsNull();
    }

    public Set<Submission> findSubmissionsWithoutTransactions(OffsetDateTime sinceDate) {
        return transactionRepository.findSubmissionsWithoutTransactions(sinceDate);
    }

    public List<ResourceOrganizationTransaction> findSubmissionsTransmittedSince(OffsetDateTime sinceDate) {

        List<Object[]> rows = transactionRepository.findSubmissionsTransmittedSince(sinceDate);

        List<ResourceOrganizationTransaction> transactions = new ArrayList<>();

        rows.stream()
                .forEach(t -> {
                    String organizationId = (String) t[0];
                    OffsetDateTime createdAt = ((Timestamp) t[1]).toLocalDateTime().atOffset(ZoneOffset.UTC);
                    String shortCode = (String) t[2];
                    String workItemId = (String) t[3];
                    transactions.add(new ResourceOrganizationTransaction(organizationId, createdAt, shortCode, workItemId));
                });

        return transactions;
    }

    public Map<String, List<ResourceOrganizationTransaction>> findSubmissionsSentByResourceOrganizationSince(OffsetDateTime sinceDate){
        return findSubmissionsTransmittedSince(sinceDate).stream()
                .collect(Collectors.groupingBy(ResourceOrganizationTransaction::getOrganizationId));
    }

    public Transaction createTransaction(UUID transactionId, UUID submissionId, String workItemId) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setSubmissionId(submissionId);
        transaction.setWorkItemId(workItemId);
        return transactionRepository.save(transaction);
    }

    public Transaction setWorkItemId(UUID transactionId, String workItemId) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId);
        transaction.setWorkItemId(workItemId);
        return transactionRepository.save(transaction);
    }
}
