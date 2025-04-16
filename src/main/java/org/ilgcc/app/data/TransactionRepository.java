package org.ilgcc.app.data;

import formflow.library.data.Submission;
import java.time.OffsetDateTime;
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
    Set<Submission> findSubmissionsWithoutTransactions(@Param("sinceDate") OffsetDateTime sinceDate);

    @Query(value = """
            SELECT 
                s.input_data->>'organizationId' AS organization_id, 
                t.created_at, 
                s.short_code, 
                t.work_item_id
            FROM transactions t
            LEFT JOIN submissions s ON t.submission_id = s.id
            WHERE t.created_at >= :startDate
              AND t.created_at < :endDate
              AND s.flow = 'gcc'
            ORDER BY t.created_at ASC
            """, nativeQuery = true)
    List<Object[]> find24HoursOfSubmissionsTransmittedSince(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    Transaction findByTransactionId(UUID transactionId);

    Transaction findByWorkItemId(String workItemId);

    Transaction findBySubmissionId(UUID submissionId);

    List<Transaction> findByWorkItemIdIsNull();
}
