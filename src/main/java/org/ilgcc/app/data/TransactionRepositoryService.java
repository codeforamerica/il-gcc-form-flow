package org.ilgcc.app.data;

import jakarta.transaction.Transactional;
import java.util.List;
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
    
    public Transaction createTransaction(UUID transactionId, UUID submissionId) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setSubmissionId(submissionId);
        return transactionRepository.save(transaction);
    }
    
    public Transaction setWorkItemId(UUID transactionId, String workItemId) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId);
        transaction.setWorkItemId(workItemId);
        return transactionRepository.save(transaction);
    }
}
