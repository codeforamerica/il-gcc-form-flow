package org.ilgcc.jobs;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.file.CloudFileRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.ilgcc.CcmsTestConfig;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.data.Transaction;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.ilgcc.app.data.UserFileTransaction;
import org.ilgcc.app.data.UserFileTransactionRepositoryService;
import org.ilgcc.app.data.ccms.CCMSTransaction;
import org.ilgcc.app.data.ccms.CCMSTransactionPayloadService;
import org.ilgcc.app.email.SendFamilyApplicationTransmittedConfirmationEmail;
import org.ilgcc.app.email.SendFamilyApplicationTransmittedProviderConfirmationEmail;
import org.ilgcc.app.pdf.MultiProviderPDFService;
import org.ilgcc.app.utils.enums.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@SpringBootTest(classes = { IlGCCApplication.class, CcmsTestConfig.class })
class CCMSSubmissionPayloadTransactionJobTest {
    
    @Autowired
    private CCMSSubmissionPayloadTransactionJob job;
    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;
    @Autowired
    private TransactionRepositoryService transactionRepositoryService;
    @Autowired
    private UserFileTransactionRepositoryService userFileTransactionRepositoryService;
    
    @MockitoBean 
    private CCMSTransactionPayloadService payloadService;
    @MockitoBean 
    private MultiProviderPDFService pdfService;
    @MockitoBean 
    private CloudFileRepository cloudFileRepository;

    @MockitoBean 
    private SendFamilyApplicationTransmittedConfirmationEmail familyEmail;
    @MockitoBean 
    private SendFamilyApplicationTransmittedProviderConfirmationEmail providerEmail;
    
    private UUID submissionId;

    @Autowired
    Flyway flyway;
    
    @BeforeEach
    void setUp() {

        flyway.clean();
        flyway.migrate();
        
        Submission submission = Submission.builder()
                .inputData(Map.of())
                .flow("test-flow")
                .build();
        Submission updatedSubmission = submissionRepositoryService.save(submission);
        submissionId = updatedSubmission.getId();
    }

    @Test
    void sendCCMSTransaction_createsUserFileTransactionFromBackupPdfAndAssociatesItWithCorrectTransaction() throws Exception {
        // Stub PDF generation to produce one file and stub S3 upload as no-op
        byte[] pdf = "fake-pdf".getBytes();
        when(pdfService.generatePDFs(any(Submission.class))).thenReturn(Map.of("application.pdf", pdf));
        doNothing().when(cloudFileRepository).upload(anyString(), any());

        // Stub payload service to return an Optional<CCMSTransaction>
        // We don't need files from ccmsTransaction here; the backupPdfFiles path is enough to create a UFT row.
        CCMSTransaction txPayload = org.mockito.Mockito.mock(CCMSTransaction.class);
        when(txPayload.getFiles()).thenReturn(List.of());
        when(payloadService.generateSubmissionTransactionPayload(any(Submission.class))).thenReturn(Optional.of(txPayload));
        
        job.sendCCMSTransaction(submissionId);

        // We expect exactly one UserFileTransaction with status REQUESTED for this submission.
        List<UserFileTransaction> userFileTransactions = userFileTransactionRepositoryService
                .findBySubmissionIdAndTransactionStatus(submissionId, TransactionStatus.REQUESTED);
        assertThat(userFileTransactions.size()).isEqualTo(1);
        UserFileTransaction uft = userFileTransactions.getFirst();

        assertThat(uft.getSubmission().getId()).isEqualTo(submissionId);
        assertThat(uft.getTransaction()).isNotNull();
        assertThat(uft.getUserFile()).isNotNull();
        assertThat(uft.getTransactionStatus()).isEqualTo(TransactionStatus.REQUESTED);

        Transaction transaction = transactionRepositoryService.getBySubmissionId(submissionId);
        assertThat(transaction).isNotNull();
        assertThat(uft.getTransaction().getTransactionId()).isEqualTo(transaction.getTransactionId());
    }

    @Test
    void sendCCMSTransaction_createsTransactionAndSetsTransactionTypeToApplication() throws Exception {
        byte[] pdf = "fake-pdf".getBytes();
        when(pdfService.generatePDFs(any(Submission.class))).thenReturn(Map.of("application.pdf", pdf));
        doNothing().when(cloudFileRepository).upload(anyString(), any());
        
        CCMSTransaction txPayload = org.mockito.Mockito.mock(CCMSTransaction.class);
        when(txPayload.getFiles()).thenReturn(List.of());
        when(payloadService.generateSubmissionTransactionPayload(any(Submission.class))).thenReturn(Optional.of(txPayload));

        job.sendCCMSTransaction(submissionId);

        Transaction transaction = transactionRepositoryService.getBySubmissionId(submissionId);
        assertThat(transaction).isNotNull();
        assertThat(transaction.getTransactionType().toString()).isEqualTo("application");
    }
}
