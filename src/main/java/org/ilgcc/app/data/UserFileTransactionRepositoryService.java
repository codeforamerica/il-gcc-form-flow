package org.ilgcc.app.data;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.ilgcc.app.utils.enums.TransactionStatus;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class UserFileTransactionRepositoryService {
    
    UserFileTransactionRepository userFileTransactionRepository;
    
    public UserFileTransactionRepositoryService(UserFileTransactionRepository userFileTransactionRepository) {
        this.userFileTransactionRepository = userFileTransactionRepository;
    }
    
    public List<UserFileTransaction> findByTransactionId_TransactionIdAndTransactionStatus(UUID transactionId, TransactionStatus status) {
        return userFileTransactionRepository.findByTransaction_TransactionIdAndTransactionStatus(transactionId, status);
    }
    
    public List<UserFileTransaction> findBySubmissionId_SubmissionIdAndTransactionStatus(UUID submissionId, TransactionStatus status) {
        return userFileTransactionRepository.findBySubmission_IdAndTransactionStatus(submissionId, status);
    }
}
