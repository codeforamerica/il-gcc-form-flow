package org.ilgcc.app.data;

import java.util.List;
import java.util.UUID;
import org.ilgcc.app.utils.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFileTransactionRepository extends JpaRepository<UserFileTransaction, UUID> {

    List<UserFileTransaction> findByTransactionTransactionIdAndTransactionStatus(UUID transactionId, TransactionStatus status);
    
    List<UserFileTransaction> findBySubmissionIdAndTransactionStatus(UUID submissionId, TransactionStatus status);

    @Query("SELECT f FROM UserFileTransaction f WHERE f.transactionStatus in ('FAILED','REQUESTED')")
    List<UserFileTransaction> findIncompleteStatusByTransaction(UUID transactionId);
}
