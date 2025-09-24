package org.ilgcc.app.data;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.UserFile;
import java.util.List;
import java.util.UUID;
import org.ilgcc.app.utils.enums.TransactionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EntityScan(basePackageClasses = {
        formflow.library.data.Submission.class,
        formflow.library.data.UserFile.class,
        org.ilgcc.app.data.Transaction.class,
        org.ilgcc.app.data.UserFileTransaction.class
})
@Import(UserFileTransactionRepositoryService.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserFileTransactionRepositoryServiceTest {

    @Autowired TestEntityManager testEntityManager;
    @Autowired UserFileTransactionRepositoryService userFileTransactionRepositoryService;

    @Test
    void findByTransactionIdAndStatusReturnsMatchingRows() {
        Submission submission = new Submission();
        submission.setFlow("test");
        submission = testEntityManager.persistAndFlush(submission);

        UUID transactionId = UUID.randomUUID();
        Transaction transaction1 = new Transaction();
        transaction1.setTransactionId(transactionId);
        transaction1.setSubmissionId(submission.getId());
        transaction1.setWorkItemId("WI-1");
        testEntityManager.persist(transaction1);

        UserFile userFile1 = new UserFile();
        userFile1.setFileId(UUID.randomUUID());
        userFile1.setSubmission(submission);
        userFile1.setOriginalName("doc.pdf");
        userFile1.setRepositoryPath("test-s3-path");
        userFile1.setMimeType("application/pdf");
        userFile1.setFilesize(123.45f);
        userFile1.setVirusScanned(true);
        testEntityManager.persist(userFile1);

        UserFile userFile2 = new UserFile();
        userFile2.setFileId(UUID.randomUUID());
        userFile2.setSubmission(submission);
        userFile2.setOriginalName("another-doc.pdf");
        userFile2.setRepositoryPath("another-test-s3-path");
        userFile2.setMimeType("application/pdf");
        userFile2.setFilesize(543.21f);
        userFile2.setVirusScanned(true);
        testEntityManager.persist(userFile2);

        UserFileTransaction uftRequested = new UserFileTransaction();
        uftRequested.setUserFile(userFile1);
        uftRequested.setTransaction(transaction1);
        uftRequested.setSubmission(submission);
        uftRequested.setTransactionStatus(TransactionStatus.REQUESTED);
        testEntityManager.persist(uftRequested);

        UserFileTransaction utfRequested2 = new UserFileTransaction();
        utfRequested2.setUserFile(userFile2);
        utfRequested2.setTransaction(transaction1);
        utfRequested2.setSubmission(submission);
        utfRequested2.setTransactionStatus(TransactionStatus.REQUESTED);
        testEntityManager.persist(utfRequested2);

        testEntityManager.flush();
        testEntityManager.clear();
        
        List<UserFileTransaction> byTxRequested =
                userFileTransactionRepositoryService.findByTransactionIdAndTransactionStatus(transactionId, TransactionStatus.REQUESTED);
        
        assertThat(byTxRequested).hasSize(2);
        assertThat(byTxRequested.get(0).getTransactionStatus()).isEqualTo(TransactionStatus.REQUESTED);
        assertThat(byTxRequested.get(0).getTransaction().getTransactionId()).isEqualTo(transactionId);
        assertThat(byTxRequested.get(0).getUserFile().getFileId()).isEqualTo(userFile1.getFileId());
        assertThat(byTxRequested.get(1).getTransactionStatus()).isEqualTo(TransactionStatus.REQUESTED);
        assertThat(byTxRequested.get(1).getTransaction().getTransactionId()).isEqualTo(transactionId);
        assertThat(byTxRequested.get(1).getUserFile().getFileId()).isEqualTo(userFile2.getFileId());
    }

    @Test
    void findByTransactionIdAndStatusReturnsDoesNotReturnNonMatchingRows() {
        Submission submission = new Submission();
        submission.setFlow("test");
        submission = testEntityManager.persistAndFlush(submission);

        UUID transactionId = UUID.randomUUID();
        Transaction transaction1 = new Transaction();
        transaction1.setTransactionId(transactionId);
        transaction1.setSubmissionId(submission.getId());
        transaction1.setWorkItemId("WI-1");
        testEntityManager.persist(transaction1);

        UserFile userFile1 = new UserFile();
        userFile1.setFileId(UUID.randomUUID());
        userFile1.setSubmission(submission);
        userFile1.setOriginalName("doc.pdf");
        userFile1.setRepositoryPath("test-s3-path");
        userFile1.setMimeType("application/pdf");
        userFile1.setFilesize(123.45f);
        userFile1.setVirusScanned(true);
        testEntityManager.persist(userFile1);

        UserFile userFile2 = new UserFile();
        userFile2.setFileId(UUID.randomUUID());
        userFile2.setSubmission(submission);
        userFile2.setOriginalName("another-doc.pdf");
        userFile2.setRepositoryPath("another-test-s3-path");
        userFile2.setMimeType("application/pdf");
        userFile2.setFilesize(543.21f);
        userFile2.setVirusScanned(true);
        testEntityManager.persist(userFile2);

        UserFileTransaction uftRequested = new UserFileTransaction();
        uftRequested.setUserFile(userFile1);
        uftRequested.setTransaction(transaction1);
        uftRequested.setSubmission(submission);
        uftRequested.setTransactionStatus(TransactionStatus.REQUESTED);
        testEntityManager.persist(uftRequested);

        UserFileTransaction utfCompleted = new UserFileTransaction();
        utfCompleted.setUserFile(userFile2);
        utfCompleted.setTransaction(transaction1);
        utfCompleted.setSubmission(submission);
        utfCompleted.setTransactionStatus(TransactionStatus.COMPLETED);
        testEntityManager.persist(utfCompleted);

        testEntityManager.flush();
        testEntityManager.clear();

        List<UserFileTransaction> byTxRequested =
                userFileTransactionRepositoryService.findByTransactionIdAndTransactionStatus(transactionId, TransactionStatus.REQUESTED);

        assertThat(byTxRequested).hasSize(1);
        assertThat(byTxRequested.getFirst().getTransactionStatus()).isEqualTo(TransactionStatus.REQUESTED);
        assertThat(byTxRequested.getFirst().getTransaction().getTransactionId()).isEqualTo(transactionId);
        assertThat(byTxRequested.getFirst().getUserFile().getFileId()).isEqualTo(userFile1.getFileId());
    }

    @Test
    void findBySubmissionIdAndStatusReturnsMatchingRows() {
        Submission submission = new Submission();
        submission.setFlow("test");
        submission = testEntityManager.persistAndFlush(submission);

        UUID transactionId = UUID.randomUUID();
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setSubmissionId(submission.getId());
        transaction.setWorkItemId("WI-2");
        testEntityManager.persist(transaction);

        UserFile userFile1 = new UserFile();
        userFile1.setFileId(UUID.randomUUID());
        userFile1.setSubmission(submission);
        userFile1.setOriginalName("test.jpg");
        userFile1.setRepositoryPath("test-s3-path-scan");
        userFile1.setMimeType("image/jpeg");
        userFile1.setFilesize(42.0f);
        userFile1.setVirusScanned(true);
        testEntityManager.persist(userFile1);

        UserFile userFile2 = new UserFile();
        userFile2.setFileId(UUID.randomUUID());
        userFile2.setSubmission(submission);
        userFile2.setOriginalName("another-test.jpg");
        userFile2.setRepositoryPath("another-test-s3-path-scan");
        userFile2.setMimeType("image/jpeg");
        userFile2.setFilesize(33.0f);
        userFile2.setVirusScanned(true);
        testEntityManager.persist(userFile2);

        UserFileTransaction uftCompleted = new UserFileTransaction();
        uftCompleted.setUserFile(userFile1);
        uftCompleted.setTransaction(transaction);
        uftCompleted.setSubmission(submission);
        uftCompleted.setTransactionStatus(TransactionStatus.COMPLETED);
        testEntityManager.persist(uftCompleted);

        UserFileTransaction uftRequested = new UserFileTransaction();
        uftRequested.setUserFile(userFile2);
        uftRequested.setTransaction(transaction);
        uftRequested.setSubmission(submission);
        uftRequested.setTransactionStatus(TransactionStatus.COMPLETED);
        testEntityManager.persist(uftRequested);

        testEntityManager.flush();
        testEntityManager.clear();

        List<UserFileTransaction> bySubmissionCompleted =
                userFileTransactionRepositoryService.findBySubmissionIdAndTransactionStatus(submission.getId(), TransactionStatus.COMPLETED);

        assertThat(bySubmissionCompleted).hasSize(2);
        assertThat(bySubmissionCompleted.get(0).getSubmission().getId()).isEqualTo(submission.getId());
        assertThat(bySubmissionCompleted.get(0).getTransactionStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(bySubmissionCompleted.get(0).getTransaction().getTransactionId()).isEqualTo(transactionId);
        assertThat(bySubmissionCompleted.get(0).getUserFile().getFileId()).isEqualTo(userFile1.getFileId());
        assertThat(bySubmissionCompleted.get(1).getSubmission().getId()).isEqualTo(submission.getId());
        assertThat(bySubmissionCompleted.get(1).getTransactionStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(bySubmissionCompleted.get(1).getTransaction().getTransactionId()).isEqualTo(transactionId);
        assertThat(bySubmissionCompleted.get(1).getUserFile().getFileId()).isEqualTo(userFile2.getFileId());
    }

    @Test
    void findBySubmissionIdAndStatusDoesNotReturnNonMatchingRows() {
        Submission submission = new Submission();
        submission.setFlow("test");
        submission = testEntityManager.persistAndFlush(submission);

        UUID transactionId = UUID.randomUUID();
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setSubmissionId(submission.getId());
        transaction.setWorkItemId("WI-2");
        testEntityManager.persist(transaction);

        UserFile userFile1 = new UserFile();
        userFile1.setFileId(UUID.randomUUID());
        userFile1.setSubmission(submission);
        userFile1.setOriginalName("test.jpg");
        userFile1.setRepositoryPath("test-s3-path-scan");
        userFile1.setMimeType("image/jpeg");
        userFile1.setFilesize(42.0f);
        userFile1.setVirusScanned(true);
        testEntityManager.persist(userFile1);

        UserFile userFile2 = new UserFile();
        userFile2.setFileId(UUID.randomUUID());
        userFile2.setSubmission(submission);
        userFile2.setOriginalName("another-test.jpg");
        userFile2.setRepositoryPath("another-test-s3-path-scan");
        userFile2.setMimeType("image/jpeg");
        userFile2.setFilesize(33.0f);
        userFile2.setVirusScanned(true);
        testEntityManager.persist(userFile2);

        UserFileTransaction uftCompleted = new UserFileTransaction();
        uftCompleted.setUserFile(userFile1);
        uftCompleted.setTransaction(transaction);
        uftCompleted.setSubmission(submission);
        uftCompleted.setTransactionStatus(TransactionStatus.COMPLETED);
        testEntityManager.persist(uftCompleted);

        UserFileTransaction uftRequested = new UserFileTransaction();
        uftRequested.setUserFile(userFile2);
        uftRequested.setTransaction(transaction);
        uftRequested.setSubmission(submission);
        uftRequested.setTransactionStatus(TransactionStatus.FAILED);
        testEntityManager.persist(uftRequested);

        testEntityManager.flush();
        testEntityManager.clear();

        List<UserFileTransaction> bySubmissionCompleted =
                userFileTransactionRepositoryService.findBySubmissionIdAndTransactionStatus(submission.getId(), TransactionStatus.COMPLETED);

        assertThat(bySubmissionCompleted).hasSize(1);
        assertThat(bySubmissionCompleted.getFirst().getSubmission().getId()).isEqualTo(submission.getId());
        assertThat(bySubmissionCompleted.getFirst().getTransactionStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(bySubmissionCompleted.getFirst().getTransaction().getTransactionId()).isEqualTo(transactionId);
        assertThat(bySubmissionCompleted.getFirst().getUserFile().getFileId()).isEqualTo(userFile1.getFileId());
    }


    @Test
    void methodsReturnEmptyListWhenNoMatch() {
        List<UserFileTransaction> none1 =
                userFileTransactionRepositoryService.findByTransactionIdAndTransactionStatus(
                        UUID.randomUUID(), TransactionStatus.REQUESTED);
        List<UserFileTransaction> none2 =
                userFileTransactionRepositoryService.findBySubmissionIdAndTransactionStatus(
                        UUID.randomUUID(), TransactionStatus.COMPLETED);

        assertThat(none1).isEmpty();
        assertThat(none2).isEmpty();
    }
}
