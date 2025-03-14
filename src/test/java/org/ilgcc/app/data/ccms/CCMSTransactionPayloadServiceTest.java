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
import java.util.Optional;
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

    private Submission familySubmission;
    private Submission providerSubmission;

    private final UserFile testConvertedJpegPdf = new UserFile();
    private final UserFile testConvertedPngPdf = new UserFile();
    private final UserFile testProviderUploadedPdf1 = new UserFile();
    private final UserFile testProviderUploadedPdf2 = new UserFile();
    private final Path testFilledCcapPdfPath = Path.of("src/test/resources/output/test_filled_ccap.pdf");
    private final Path testConvertedJpegPath = Path.of("src/test/resources/test-image-jpeg-converted.pdf");
    private final Path testConvertedPngPath = Path.of("src/test/resources/test-image-png-converted.pdf");
    
    @BeforeEach
    public void setUp() throws IOException {
        providerSubmission = Submission.builder()
                .id(UUID.randomUUID())
                .submittedAt(OffsetDateTime.now())
                .build();
        
        Map<String, Object> familyInputData = new HashMap<>();
        familyInputData.put("organizationId", "testOrganizationId");
        familyInputData.put("parentFirstName", "Tésty");
        familyInputData.put("parentLastName", "McTéstersün");
        familyInputData.put("parentBirthDate", "12/17/1987");
        familyInputData.put("providerResponseSubmissionId", providerSubmission.getId());
        
        familySubmission = Submission.builder()
                .id(UUID.randomUUID())
                .shortCode("shortCode")
                .submittedAt(OffsetDateTime.now())
                .inputData(familyInputData)
                .build();
        
        testConvertedJpegPdf.setMimeType("application/pdf");
        testConvertedJpegPdf.setFileId(UUID.randomUUID());
        testConvertedJpegPdf.setRepositoryPath("testConvertedJpegPath");
        testConvertedJpegPdf.setSubmission(familySubmission);
        
        testConvertedPngPdf.setMimeType("application/pdf");
        testConvertedPngPdf.setFileId(UUID.randomUUID());
        testConvertedPngPdf.setRepositoryPath("testConvertedPngPath");
        testConvertedPngPdf.setSubmission(familySubmission);
        
        testProviderUploadedPdf1.setMimeType("application/pdf");
        testProviderUploadedPdf1.setFileId(UUID.randomUUID());
        testProviderUploadedPdf1.setRepositoryPath("testProviderUploadedPdf1Path");
        testProviderUploadedPdf1.setSubmission(providerSubmission);
        
        testProviderUploadedPdf2.setMimeType("application/pdf");
        testProviderUploadedPdf2.setFileId(UUID.randomUUID());
        testProviderUploadedPdf2.setRepositoryPath("testProviderUploadedPdf2Path");
        testProviderUploadedPdf2.setSubmission(providerSubmission);

        CloudFile testConvertedJpegCloudFile = new CloudFile(Files.size(testConvertedJpegPath),
                Files.readAllBytes(testConvertedJpegPath), Map.of());
        CloudFile testConvertedPngCloudFile = new CloudFile(Files.size(testConvertedPngPath),
                Files.readAllBytes(testConvertedPngPath), Map.of());
        CloudFile providerPdfCloudFile1 = new CloudFile(100L, "testBase64String1".getBytes(), Map.of());
        CloudFile providerPdfCloudFile2 = new CloudFile(100L, "testBase64String2".getBytes(), Map.of());

        when(pdfService.getFilledOutPDF(familySubmission)).thenReturn(Files.readAllBytes(testFilledCcapPdfPath));
        when(userFileRepositoryService.findAllOrderByOriginalName(familySubmission, "application/pdf")).thenReturn(
                List.of(testConvertedPngPdf, testConvertedJpegPdf));
        when(submissionRepositoryService.findById(providerSubmission.getId())).thenReturn(Optional.ofNullable(providerSubmission));
        when(userFileRepositoryService.findAllOrderByOriginalName(providerSubmission, "application/pdf")).thenReturn(
                List.of(testProviderUploadedPdf1, testProviderUploadedPdf2));
        when(cloudFileRepository.get(testConvertedJpegPdf.getRepositoryPath())).thenReturn(testConvertedJpegCloudFile);
        when(cloudFileRepository.get(testConvertedPngPdf.getRepositoryPath())).thenReturn(testConvertedPngCloudFile);
        when(cloudFileRepository.get(testProviderUploadedPdf1.getRepositoryPath())).thenReturn(providerPdfCloudFile1);
        when(cloudFileRepository.get(testProviderUploadedPdf2.getRepositoryPath())).thenReturn(providerPdfCloudFile2);
        when(submissionRepositoryService.save(familySubmission)).thenReturn(familySubmission);
    }

    @Test
    void shouldCreateACCMSTransactionWithTheCorrectDataAndFields() throws IOException {
        List<TransactionFile> testFiles = List.of(
                // The indexes here matter unfortunately as the file names are checked in the loop below
                new TransactionFile(
                        String.format("%s-CCAP-Application-Form.pdf", familySubmission.getId()),
                        TransactionFile.FileTypeId.APPLICATION_PDF.getValue(),
                        Base64.getEncoder().encodeToString(Files.readAllBytes(testFilledCcapPdfPath))),
                new TransactionFile(
                        String.format("%s-Supporting-Document-%d-of-%d.pdf", familySubmission.getId(), 1, 4),
                        FileTypeId.UPLOADED_DOCUMENT.getValue(),
                        Base64.getEncoder().encodeToString("testBase64String1".getBytes())),
                new TransactionFile(
                        String.format("%s-Supporting-Document-%d-of-%d.pdf", familySubmission.getId(), 2, 4),
                        FileTypeId.UPLOADED_DOCUMENT.getValue(),
                        Base64.getEncoder().encodeToString("testBase64String2".getBytes())),
                new TransactionFile(
                        String.format("%s-Supporting-Document-%d-of-%d.pdf", familySubmission.getId(), 3, 4),
                        FileTypeId.UPLOADED_DOCUMENT.getValue(),
                        Base64.getEncoder().encodeToString(Files.readAllBytes(testConvertedPngPath))),
                new TransactionFile(
                        String.format("%s-Supporting-Document-%d-of-%d.pdf", familySubmission.getId(), 4, 4),
                        FileTypeId.UPLOADED_DOCUMENT.getValue(),
                        Base64.getEncoder().encodeToString(Files.readAllBytes(testConvertedJpegPath)))
        );

        CCMSTransaction ccmsTransaction = ccmsTransactionPayloadService.generateSubmissionTransactionPayload(familySubmission);
        assertThat(ccmsTransaction).isNotNull();
        assertThat(ccmsTransaction.getTransmissionType()).isEqualTo("application");
        assertThat(ccmsTransaction.getSubmissionId()).isEqualTo(familySubmission.getId());
        assertThat(ccmsTransaction.getClientConfirmationCode()).isEqualTo(familySubmission.getShortCode());
        assertThat(ccmsTransaction.getSubmissionOrgId()).isEqualTo("testOrganizationId");
        assertThat(ccmsTransaction.getSubmissionFirstName()).isEqualTo("Testy");
        assertThat(ccmsTransaction.getSubmissionLastName()).isEqualTo("McTestersun");
        assertThat(ccmsTransaction.getSubmissionDOB()).isEqualTo("12/17/1987");
        assertThat(ccmsTransaction.getProviderId()).isEqualTo(List.of(""));
        assertThat(ccmsTransaction.getFiles().size()).isEqualTo(5);
        assertThat(ccmsTransaction.getWebSubmissionTimestamp()).isEqualTo(
                DateUtilities.formatDateToYearMonthDayHourCSTWithOffset(familySubmission.getSubmittedAt()));

        List<TransactionFile> actualFiles = ccmsTransaction.getFiles();
        for (TransactionFile expectedFile : testFiles) {
            boolean expectedFilesContainsCurrentFile = actualFiles.stream().anyMatch(actualFile ->
                    actualFile.getFileName().equals(expectedFile.getFileName()) &&
                            actualFile.getFileType().equals(expectedFile.getFileType()) &&
                            actualFile.getFilePayload().equals(expectedFile.getFilePayload())
            );

            assertThat(expectedFilesContainsCurrentFile)
                    .as("The two lists did not have a matching file for %s. Because of the naming convention the indexes need to match as well.", expectedFile.getFileName()) // This message displays if the assertion in the loop fails
                    .isTrue();
        }
    }
}