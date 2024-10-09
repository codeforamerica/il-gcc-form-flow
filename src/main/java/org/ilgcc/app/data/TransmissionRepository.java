package org.ilgcc.app.data;

import feign.Param;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransmissionRepository extends JpaRepository<Transmission, UUID> {
    @Query(value =
            "SELECT s.* " +
            "FROM submissions s " +
            "LEFT JOIN transmissions t ON t.submission_id = s.id " +
            "WHERE s.submitted_at IS NOT NULL " +
            "AND t.transmission_id IS NULL " +
            "ORDER BY s.updated_at ASC",
            nativeQuery = true)

    List<Submission> findSubmissionsWithoutTransmission();

    List<Transmission> findAllBySubmissionId(Submission submission);
}
