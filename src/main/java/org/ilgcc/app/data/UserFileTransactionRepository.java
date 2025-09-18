package org.ilgcc.app.data;

import java.util.List;
import java.util.UUID;
import org.ilgcc.app.utils.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFileTransactionRepository extends JpaRepository<UserFileTransaction, UUID> {

    List<UserFileTransaction> findByTransaction_TransactionIdAndTransactionStatus(UUID transactionId, TransactionStatus status);
    
    List<UserFileTransaction> findBySubmission_IdAndTransactionStatus(UUID submissionId, TransactionStatus status);
}
