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
            "SELECT s.* " +
                    "FROM submissions s " +
                    "LEFT JOIN transactions t ON t.submission_id = s.id " +
                    "WHERE s.submitted_at IS NOT NULL " +
                    "AND ( " +
                    "s.input_data->>'providerResponseStatus' = 'ACTIVE' " +
                    "OR s.input_data->>'providerResponseStatus' IS NULL " +
                    ") " +
                    "AND s.input_data->>'providerApplicationResponseExpirationDate' IS NOT NULL " +
                    "AND TO_TIMESTAMP((s.input_data->>'providerApplicationResponseExpirationDate')::double precision)::timestamptz <= (now() AT TIME ZONE 'UTC') " +
                    "AND s.flow = 'gcc' " +
                    "AND t.transaction_id IS NULL " +
                    "ORDER BY s.created_at ASC",
            nativeQuery = true)
    Set<Submission> findExpiringSubmissionsWithoutTransactions();

    @Query(value = """
            SELECT 
                s.input_data->>'organizationId' AS organization_id, 
                (t.created_at AT TIME ZONE 'UTC' AT TIME ZONE 'America/Chicago') AS created_at,
                s.short_code, 
                t.work_item_id
            FROM transactions t
            LEFT JOIN submissions s ON t.submission_id = s.id
            WHERE DATE_TRUNC('day', t.created_at AT TIME ZONE 'UTC' AT TIME ZONE 'America/Chicago') = DATE_TRUNC('day', CAST(:date AS TIMESTAMP))
              AND s.flow = 'gcc'
            ORDER BY t.created_at ASC
            """, nativeQuery = true)
    List<Object[]> findTransactionsOnDate(@Param("date") OffsetDateTime date);

    Transaction findByTransactionId(UUID transactionId);

    Transaction findByWorkItemId(String workItemId);

    Transaction findBySubmissionId(UUID submissionId);

    List<Transaction> findByWorkItemIdIsNull();
}
