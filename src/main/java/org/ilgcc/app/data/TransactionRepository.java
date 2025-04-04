package org.ilgcc.app.data;

import formflow.library.data.Submission;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query(value =
            "SELECT s FROM Submission s " +
                    "LEFT JOIN Transaction t ON t.submissionId = s.id " +
                    "WHERE s.submittedAt IS NOT NULL " +
                    "AND s.submittedAt >= :sinceDate " +
                    "AND s.flow = 'gcc' " +
                    "AND t.transactionId IS NULL " +
                    "ORDER BY s.updatedAt ASC")
    Set<Submission> findSubmissionsWithoutTransactions(@Param("sinceDate") Instant lastRun);

    Transaction findByTransactionId(UUID transactionId);

    Transaction findByWorkItemId(String workItemId);

    Transaction findBySubmissionId(UUID submissionId);

    List<Transaction> findByWorkItemIdIsNull();
}
