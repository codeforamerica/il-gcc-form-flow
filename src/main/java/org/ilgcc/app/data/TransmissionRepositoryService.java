package org.ilgcc.app.data;

import formflow.library.data.Submission;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.ilgcc.app.utils.enums.TransmissionStatus;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TransmissionRepositoryService {
    
    TransmissionRepository transmissionRepository;
    
    public TransmissionRepositoryService(TransmissionRepository transmissionRepository) {
        this.transmissionRepository = transmissionRepository;
    }

    public Transmission save(Transmission transmission) {
        return transmissionRepository.save(transmission);
    }
    
    public Transmission findById(UUID id) {
        return this.transmissionRepository.findById(id).orElse(null);
    }

    public List<Transmission> findAllBySubmissionId(Submission submission) {
        return this.transmissionRepository.findAllBySubmissionId(submission);
    }
    
    public void updateStatus(Transmission transmission, TransmissionStatus status) {
        transmission.setStatus(status);
        this.transmissionRepository.save(transmission);
    }
    
    public void setFailureError(Transmission transmission, String error) {
        Map<Integer, String> errors = transmission.getErrors() == null ? new HashMap<>() : transmission.getErrors();
        int attempts = transmission.getAttempts();
        errors.put(attempts, error);
        transmission.setErrors(errors);
        attempts += 1;
        transmission.setAttempts(attempts);
        transmission.setStatus(TransmissionStatus.Failed);
        this.transmissionRepository.save(transmission);
    }
}
