package org.ilgcc.app.data.ccms;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.data.UserFile;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFile;
import formflow.library.file.S3CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.ilgcc.app.data.ccms.TransactionFile.FileTypeId;
import org.ilgcc.app.utils.DateUtilities;
import org.jobrunr.spring.autoconfigure.JobRunrAutoConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = CCMSTransactionPayloadService.class)
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        FlywayAutoConfiguration.class,
        JobRunrAutoConfiguration.class
})
public class CCMSTransactionPayloadServiceTest {

    @Autowired
    private CCMSTransactionPayloadService ccmsTransactionPayloadService;

    @MockitoBean
    S3CloudFileRepository cloudFileRepository;
    
    @MockitoBean
    SubmissionRepositoryService submissionRepositoryService;

    @MockitoBean
    UserFileRepositoryService userFileRepositoryService;

    @MockitoBean
    PdfService pdfService;

    private Submission submission;

    private final UserFile testConvertedJpegPdf = new UserFile();
    private final UserFile testConvertedPngPdf = new UserFile();
    private final Path testFilledCcapPdfPath = Path.of("src/test/resources/output/test_filled_ccap.pdf");
    private final Path testConvertedJpegPath = Path.of("src/test/resources/test-image-jpeg-converted.pdf");
    private final Path testConvertedPngPath = Path.of("src/test/resources/test-image-png-converted.pdf");
    
    @BeforeEach
    public void setUp() throws IOException {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("organizationId", "testOrganizationId");
        inputData.put("parentFirstName", "Tésty");
        inputData.put("parentLastName", "McTéstersün");
        inputData.put("parentBirthDate", "12/17/1987");
        
        submission = Submission.builder()
                .id(UUID.randomUUID())
                .shortCode("shortCode")
                .submittedAt(OffsetDateTime.now())
                .inputData(inputData)
                .build();
        
        testConvertedJpegPdf.setMimeType("application/pdf");
        testConvertedJpegPdf.setFileId(UUID.randomUUID());
        testConvertedJpegPdf.setRepositoryPath("testConvertedJpegPath");
        testConvertedJpegPdf.setSubmission(submission);
        testConvertedPngPdf.setMimeType("application/pdf");
        testConvertedPngPdf.setFileId(UUID.randomUUID());
        testConvertedPngPdf.setRepositoryPath("testConvertedPngPath");
        testConvertedPngPdf.setSubmission(submission);

        CloudFile testConvertedJpegCloudFile = new CloudFile(Files.size(testConvertedJpegPath),
                Files.readAllBytes(testConvertedJpegPath), Map.of());
        CloudFile testConvertedPngCloudFile = new CloudFile(Files.size(testConvertedPngPath),
                Files.readAllBytes(testConvertedPngPath), Map.of());

        when(pdfService.getFilledOutPDF(submission)).thenReturn(Files.readAllBytes(testFilledCcapPdfPath));
        when(userFileRepositoryService.findAllOrderByOriginalName(submission, "application/pdf")).thenReturn(
                List.of(testConvertedPngPdf, testConvertedJpegPdf));
        when(cloudFileRepository.get(testConvertedJpegPdf.getRepositoryPath())).thenReturn(testConvertedJpegCloudFile);
        when(cloudFileRepository.get(testConvertedPngPdf.getRepositoryPath())).thenReturn(testConvertedPngCloudFile);
        when(submissionRepositoryService.save(submission)).thenReturn(submission);
    }

    @Test
    void shouldCreateACCMSTransactionWithTheCorrectDataAndFields() throws IOException, InterruptedException {
        List<TransactionFile> testFiles = List.of(
                new TransactionFile(
                        String.format("%s-CCAP-Application-Form.pdf", submission.getId()),
                        TransactionFile.FileTypeId.APPLICATION_PDF.getValue(),
                        Base64.getEncoder().encodeToString(Files.readAllBytes(testFilledCcapPdfPath))),
                new TransactionFile(
                        String.format("%s-Supporting-Document-%d-of-%d.pdf", submission.getId(), 2, 2),
                        FileTypeId.UPLOADED_DOCUMENT.getValue(),
                        Base64.getEncoder().encodeToString(Files.readAllBytes(testConvertedJpegPath))),
                new TransactionFile(
                        String.format("%s-Supporting-Document-%d-of-%d.pdf", submission.getId(), 1, 2),
                        FileTypeId.UPLOADED_DOCUMENT.getValue(),
                        Base64.getEncoder().encodeToString(Files.readAllBytes(testConvertedPngPath)))
        );

        CCMSTransaction ccmsTransaction = ccmsTransactionPayloadService.generateSubmissionTransactionPayload(submission);
        assertThat(ccmsTransaction).isNotNull();
        assertThat(ccmsTransaction.getTransmissionType()).isEqualTo("application");
        assertThat(ccmsTransaction.getSubmissionId()).isEqualTo(submission.getId());
        assertThat(ccmsTransaction.getClientConfirmationCode()).isEqualTo(submission.getShortCode());
        assertThat(ccmsTransaction.getSubmissionOrgId()).isEqualTo("testOrganizationId");
        assertThat(ccmsTransaction.getSubmissionFirstName()).isEqualTo("Testy");
        assertThat(ccmsTransaction.getSubmissionLastName()).isEqualTo("McTestersun");
        assertThat(ccmsTransaction.getSubmissionDOB()).isEqualTo("12/17/1987");
        assertThat(ccmsTransaction.getProviderId()).isEqualTo(List.of(""));
        assertThat(ccmsTransaction.getFiles().size()).isEqualTo(3);
        ccmsTransaction.getFiles().forEach(file -> {
            assertThat(testFiles.contains(file)).isTrue();
        });
        assertThat(ccmsTransaction.getWebSubmissionTimestamp()).isEqualTo(
                DateUtilities.formatDateToYearMonthDayHourCSTWithOffset(submission.getSubmittedAt()));
    }
}