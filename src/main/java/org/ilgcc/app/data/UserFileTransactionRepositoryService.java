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

    /**
     * @param userFileTransaction a UserFileTransaction containing the relationship between a Submission it's UserFile's and their
     *                            related Transaction's along with their status.
     * @return the saved UserFileTransaction.
     */
    public UserFileTransaction save(UserFileTransaction userFileTransaction) {
        return userFileTransactionRepository.save(userFileTransaction);
    }
    
    /**
     * @param userFileTransactions a list of UserFileTransactions containing the relationship between a Submission it's UserFile's and their
     *                            related Transaction's along with their status.
     * @return the saved list of UserFileTransactions.
     */
    public List<UserFileTransaction> saveAll(List<UserFileTransaction> userFileTransactions) {
        return userFileTransactionRepository.saveAll(userFileTransactions);
    }

    /**
     * @param transactionId The Transaction's UUID.
     * @param status The TransactionStatus to filter by.
     * @return A list of UserFileTransactions matching the given Transaction ID and TransactionStatus.
     */
    public List<UserFileTransaction> findByTransactionIdAndTransactionStatus(UUID transactionId, TransactionStatus status) {
        return userFileTransactionRepository.findByTransactionTransactionIdAndTransactionStatus(transactionId, status);
    }

    /**
     * @param submissionId The Submission's UUID.
     * @param status The TransactionStatus to filter by.
     * @return A list of UserFileTransactions matching the given Submission ID and TransactionStatus.
     */
    public List<UserFileTransaction> findBySubmissionIdAndTransactionStatus(UUID submissionId, TransactionStatus status) {
        return userFileTransactionRepository.findBySubmissionIdAndTransactionStatus(submissionId, status);
    }
}
