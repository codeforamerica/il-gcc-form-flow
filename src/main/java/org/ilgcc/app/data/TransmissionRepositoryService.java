package org.ilgcc.app.data;

import formflow.library.data.Submission;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TransmissionRepositoryService {
    
    TransmissionRepository transmissionRepository;
    
    public TransmissionRepositoryService(TransmissionRepository transmissionRepository) {
        this.transmissionRepository = transmissionRepository;
    }
    
//    public void save(Transmission transmission) {
//        this.transmissionRepository.save(transmission);
//    }

//    public List<Transmission> findAllBySubmission(Submission submission) {
//        return this.transmissionRepository.findAllBySubmission(submission);
//    }
}
