package org.ilgcc.app.data;

import formflow.library.data.Submission;
import jakarta.transaction.Transactional;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.AbstractMap;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.ilgcc.app.data.TransactionRepositoryService.ResourceOrganizationTransaction.TransactionData;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class TransactionRepositoryService {

    TransactionRepository transactionRepository;

    public TransactionRepositoryService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public class ResourceOrganizationTransaction {

        private String organizationId;
        private List<TransactionData> transactions;


        public ResourceOrganizationTransaction(String organizationId, List<TransactionData> transactions) {
            this.organizationId = organizationId;
            this.transactions = transactions;
        }


        public static class TransactionData {

            private String shortCode;
            private OffsetDateTime createdAt;
            private String workItemId;

            public TransactionData(OffsetDateTime createdAt, String shortCode, String workItemId) {
                this.workItemId = workItemId;
                this.shortCode = shortCode;
                this.createdAt = createdAt;
            }

        }

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

    public List<ResourceOrganizationTransaction> findSubmissionsTransmittedPerResourceOrganization(OffsetDateTime sinceDate) {

        List<Object[]> rows = transactionRepository.findSubmissionsTransmittedSince(sinceDate);

        Map<String, List<ResourceOrganizationTransaction.TransactionData>> grouped = rows.stream()
                .map(row -> {
                    OffsetDateTime createdAt = ((Timestamp) row[0]).toLocalDateTime().atOffset(ZoneOffset.UTC);
                    String shortCode = (String) row[1];
                    String workItemId = (String) row[2];
                    String organizationId = (String) row[3];

                    return new AbstractMap.SimpleEntry<>(
                            organizationId,
                            new ResourceOrganizationTransaction.TransactionData(createdAt, shortCode, workItemId)
                    );
                })
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        return grouped.entrySet().stream()
                .map(entry -> new ResourceOrganizationTransaction(entry.getKey(), entry.getValue()))
                .toList();

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
