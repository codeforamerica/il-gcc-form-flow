package org.ilgcc.jobs;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.data.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = IlGCCApplication.class)
@ActiveProfiles("test")
@Transactional
class TransactionWorkItemRecurringJobTest {
    
    @Autowired
    TransactionWorkItemRecurringJob transactionWorkItemRecurringJob;
    
    @PersistenceContext
    EntityManager entityManager;
    
    
    @BeforeEach
    void setUp() {
        // Manually insert test data using native SQL to bypass Hibernate's automatic timestamps
        entityManager.createNativeQuery(
                        "INSERT INTO transactions (transaction_id, work_item_id, submission_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?)")
                .setParameter(1, UUID.randomUUID())
                .setParameter(2, null)  // No workItemId
                .setParameter(3, UUID.randomUUID())
                .setParameter(4, Timestamp.from(Instant.now().minus(2, ChronoUnit.HOURS))) // Two hours ago should be included
                .setParameter(5, Timestamp.from(Instant.now().minus(2, ChronoUnit.HOURS)))
                .executeUpdate();

        entityManager.createNativeQuery(
                        "INSERT INTO transactions (transaction_id, work_item_id, submission_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?)")
                .setParameter(1, UUID.randomUUID())
                .setParameter(2, null)  // No workItemId
                .setParameter(3, UUID.randomUUID())
                .setParameter(4, Timestamp.from(Instant.now().minus(30, ChronoUnit.MINUTES))) // 30 Minutes ago should not be included
                .setParameter(5, Timestamp.from(Instant.now().minus(30, ChronoUnit.MINUTES)))
                .executeUpdate();

        entityManager.createNativeQuery(
                        "INSERT INTO transactions (transaction_id, work_item_id, submission_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?)")
                .setParameter(1, UUID.randomUUID())
                .setParameter(2, UUID.randomUUID())  // Has workItemId
                .setParameter(3, UUID.randomUUID())
                .setParameter(4, Timestamp.from(Instant.now().minus(2, ChronoUnit.HOURS)))
                .setParameter(5, Timestamp.from(Instant.now().minus(2, ChronoUnit.HOURS)))
                .executeUpdate();

        entityManager.createNativeQuery(
                        "INSERT INTO transactions (transaction_id, work_item_id, submission_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?)")
                .setParameter(1, UUID.randomUUID())
                .setParameter(2, null)  // No workItemId
                .setParameter(3, UUID.randomUUID())
                .setParameter(4, Timestamp.from(Instant.now().minus(1, ChronoUnit.HOURS))) // Exactly one hour ago should not be included
                .setParameter(5, Timestamp.from(Instant.now().minus(1, ChronoUnit.HOURS)))
                .executeUpdate();

        entityManager.createNativeQuery(
                        "INSERT INTO transactions (transaction_id, work_item_id, submission_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?)")
                .setParameter(1, UUID.randomUUID())
                .setParameter(2, null)  // No workItemId
                .setParameter(3, UUID.randomUUID())
                .setParameter(4, Timestamp.from(Instant.now().minus(1, ChronoUnit.HOURS).minus(5, ChronoUnit.SECONDS)))
                .setParameter(5, Timestamp.from(Instant.now().minus(1, ChronoUnit.HOURS).minus(5, ChronoUnit.SECONDS)))
                .executeUpdate(); // One hour and 5 seconds ago should be included
    }
    

    @Test
    void getTransactionsWithoutWorkItemIdsOlderThanOneHour() {
        List<Transaction> transactionsWithoutWorkItemIdsOlderThanOneHour = transactionWorkItemRecurringJob.getTransactionsWithoutWorkItemIdsOlderThanOneHour();
        assertThat(transactionsWithoutWorkItemIdsOlderThanOneHour.size()).isEqualTo(2);
        transactionsWithoutWorkItemIdsOlderThanOneHour.forEach(transaction -> {
            assertThat(transaction.getWorkItemId()).isNull();
            assertThat(transaction.getCreatedAt().before(Date.from(Instant.now().minus(1, ChronoUnit.HOURS)))).isTrue();
        });
    }
}