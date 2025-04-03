package org.ilgcc.app.data;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    
    Transaction findByTransactionId(UUID transactionId);
    
    Transaction findByWorkItemId(String workItemId);
    
    Transaction findBySubmissionId(UUID submissionId);
    
    List<Transaction> findByWorkItemIdIsNull();
}
