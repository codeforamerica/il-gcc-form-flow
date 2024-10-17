package org.ilgcc.app.data;

import formflow.library.data.Submission;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransmissionRepository extends JpaRepository<Transmission, UUID> {
    @Query(value =
            "SELECT s FROM Submission s " +
            "LEFT JOIN Transmission t ON t.submissionId = s " +
            "WHERE s.submittedAt IS NOT NULL " +
            "AND s.flow = 'gcc' " +        
            "AND t.transmissionId IS NULL " +
            "ORDER BY s.updatedAt ASC")

    List<Submission> findSubmissionsWithoutTransmission();

    List<Transmission> findAllBySubmissionId(Submission submission);
}
