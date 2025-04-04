package org.ilgcc.app.data;

import formflow.library.data.Submission;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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

    public Set<Submission> findSubmissionsWithoutTransactions(Instant lastRun){
        return transactionRepository.findSubmissionsWithoutTransactions(lastRun);
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
