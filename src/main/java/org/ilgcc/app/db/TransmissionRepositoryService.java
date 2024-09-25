package org.ilgcc.app.db;

import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TransmissionRepositoryService {
    
    private final TransmissionRepository transmissionRepository;
    
    public TransmissionRepositoryService(TransmissionRepository transmissionRepository) {
        this.transmissionRepository = transmissionRepository;
    }
    
    public void save(Transmission transmission) {
        this.transmissionRepository.save(transmission);
    }
}
