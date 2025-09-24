package org.ilgcc.app.data;

import formflow.library.data.Submission;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.ilgcc.app.utils.enums.TransactionType;
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

    public Set<Submission> findExpiringSubmissionsWithoutTransactions() {
        return transactionRepository.findExpiringSubmissionsWithoutTransactions();
    }

    public List<ResourceOrganizationTransaction> findTransactionsOnDate(OffsetDateTime date) {

        List<Object[]> rows = transactionRepository.findTransactionsOnDate(date);

        if (rows.isEmpty()) {
            return Collections.emptyList();
        }

        List<ResourceOrganizationTransaction> transactions = new ArrayList<>();

        rows.stream()
                .forEach(t -> {
                    String organizationId = (String) t[0];
                    OffsetDateTime createdAt = ((Instant) t[1]).atOffset(ZoneOffset.UTC);
                    String shortCode = (String) t[2];
                    String workItemId = (String) t[3];
                    transactions.add(new ResourceOrganizationTransaction(organizationId, createdAt, shortCode, workItemId));
                });

        return transactions;
    }

    public Optional<Map<String, List<ResourceOrganizationTransaction>>> findTransactionsByResourceOrganizationsOnDate(
            OffsetDateTime date) {

        List<ResourceOrganizationTransaction> transactions = findTransactionsOnDate(date);
        if (transactions.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(transactions.stream().filter(t -> t.getOrganizationId() != null)
                    .collect(Collectors.groupingBy(ResourceOrganizationTransaction::getOrganizationId)));
        }
    }

    public Transaction createTransaction(UUID transactionId, UUID submissionId, String workItemId, TransactionType transactionType) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setSubmissionId(submissionId);
        transaction.setWorkItemId(workItemId);
        transaction.setTransactionType(transactionType);
        return transactionRepository.save(transaction);
    }
}
