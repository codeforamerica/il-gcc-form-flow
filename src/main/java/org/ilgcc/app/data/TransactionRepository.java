package org.ilgcc.app.data;

import formflow.library.data.Submission;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    
    Transaction findByTransactionId(UUID transactionId);
    
    Transaction findByWorkItemId(String workItemId);
    
    Transaction findBySubmissionId(UUID submissionId);
    
    List<Transaction> findByWorkItemIdIsNull();

    @Query(value =
            "SELECT s FROM Submission s " +
                    "LEFT JOIN Transaction t ON t.submissionId = s " +
                    "WHERE s.submittedAt IS NOT NULL " +
                    "AND s.flow = 'gcc' " +
                    "AND t.transactionId IS NULL " +
                    "ORDER BY s.updatedAt ASC")
    List<Submission> findSubmissionsWithoutTransaction();
}
