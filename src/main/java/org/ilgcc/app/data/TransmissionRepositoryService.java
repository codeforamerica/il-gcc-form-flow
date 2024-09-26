package org.ilgcc.app.data;

import formflow.library.data.Submission;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TransmissionRepositoryService {
    
    TransmissionRepository transmissionRepository;
    
    public TransmissionRepositoryService(TransmissionRepository transmissionRepository) {
        this.transmissionRepository = transmissionRepository;
    }

    public List<Transmission> findAllBySubmissionId(Submission submission) {
        return this.transmissionRepository.findAllBySubmissionId(submission);
    }
}
