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
            "SELECT s.* FROM submissions s " +
                    "LEFT JOIN transmissions t ON t.submission_id = s.id " +
                    "WHERE s.submitted_at IS NOT NULL " +
                    "AND s.input_data->>'providerResponseStatus' = 'ACTIVE' " +
                    "AND s.input_data->>'providerApplicationResponseExpirationDate' IS NOT NULL " +
                    "AND CAST(s.input_data->>'providerApplicationResponseExpirationDate' AS TIMESTAMPTZ) <= (now() AT TIME ZONE"
                    + " 'UTC' AT TIME ZONE 'America/Chicago')" +
                    "AND s.flow = 'gcc' " +
                    "AND t.transmission_id IS NULL " +
                    "ORDER BY s.created_at ASC",
            nativeQuery = true)
    Set<Submission> findExpiringSubmissionsWithoutTransmission();

    List<Transmission> findAllBySubmissionId(Submission submission);
}
