package org.ilgcc.app.db;

import formflow.library.data.Submission;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransmissionRepository extends JpaRepository<Transmission, UUID> {
    
    List<Transmission> findAllBySubmission(Submission submission);
    
    long countBySubmission(Submission submission);
}
