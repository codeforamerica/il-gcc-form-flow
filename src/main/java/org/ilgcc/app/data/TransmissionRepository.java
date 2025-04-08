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
public interface TransmissionRepository extends JpaRepository<Transmission, UUID> {
    @Query(value =
            "SELECT s FROM Submission s " +
                    "LEFT JOIN Transmission t ON t.submissionId = s " +
                    "WHERE s.submittedAt IS NOT NULL " +
                    "AND s.submittedAt >= :sinceDate " +
                    "AND s.flow = 'gcc' " +
                    "AND t.transmissionId IS NULL " +
                    "ORDER BY s.updatedAt ASC")
    Set<Submission> findSubmissionsWithoutTransmissions(@Param("sinceDate") OffsetDateTime sinceDate);

    List<Transmission> findAllBySubmissionId(Submission submission);
}
