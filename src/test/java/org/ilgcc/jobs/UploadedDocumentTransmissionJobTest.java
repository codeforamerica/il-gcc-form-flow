package org.ilgcc.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ilgcc.app.utils.enums.TransmissionStatus.Queued;
import static org.ilgcc.app.utils.enums.TransmissionType.UPLOADED_DOCUMENT;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.data.UserFile;
import formflow.library.data.UserFileRepositoryService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.data.Transmission;
import org.ilgcc.app.data.TransmissionRepositoryService;
import org.ilgcc.app.file_transfer.DocumentTransferRequestService;
import org.ilgcc.app.file_transfer.HttpUrlConnectionFactory;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.enums.TransmissionStatus;
import org.ilgcc.app.utils.enums.TransmissionType;
import org.jobrunr.configuration.JobRunr;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


@SpringBootTest(classes = IlGCCApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "DOCUMENT_TRANSFER_SERVICE_URL=http://localhost:8080",
        "DOCUMENT_TRANSFER_SERVICE_CONSUMER_ID=test-consumer",
        "DOCUMENT_TRANSFER_SERVICE_AUTH_TOKEN=test-token"
})
class UploadedDocumentTransmissionJobTest {
    
    private UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob;

    @MockitoBean
    private S3PresignService s3PresignService;

    @Autowired
    private TransmissionRepositoryService transmissionRepositoryService;
    
    @Autowired
    SubmissionRepositoryService submissionRepositoryService;
    
    @Autowired
    UserFileRepositoryService userFileRepositoryService;
    
    @MockitoBean
    HttpUrlConnectionFactory httpUrlConnectionFactory;
    
    @MockitoBean
    HttpURLConnection httpUrlConnection;
    
    @Autowired
    DocumentTransferRequestService documentTransferRequestService;
    
    private Submission submission;
    
    private UserFile testUserFile;
    
    private Transmission uploadedDocumentTransmission;

    private final String objectPath = "testPath";

    @BeforeEach
    void setUp() {
        submission = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now())
                .build();
        submissionRepositoryService.save(submission);

        testUserFile = new UserFile(UUID.randomUUID(), submission, OffsetDateTime.now(), "testFile.pdf", "testFile.pdf", "Application/pdf", 10F, false, null, null);
        userFileRepositoryService.save(testUserFile);

        Date now = Date.from(ZonedDateTime.now(ZoneId.of("America/Chicago")).toInstant());
        uploadedDocumentTransmission =
                new Transmission(submission, testUserFile, now, Queued, UPLOADED_DOCUMENT, null);
        transmissionRepositoryService.save(uploadedDocumentTransmission);
        
        uploadedDocumentTransmissionJob = new UploadedDocumentTransmissionJob(s3PresignService, documentTransferRequestService, transmissionRepositoryService);
    }

    @AfterEach
    public void stopJobRunr() {
        JobRunr.destroy();
    }

    @Test
    void enqueuePdfTransmissionJobShouldSetTransmissionStatusToCompleteWhenSuccessful() throws IOException {
        when(s3PresignService.generatePresignedUrl(objectPath)).thenReturn("https://www.test.com");
        when(httpUrlConnectionFactory.createHttpURLConnection(any(URL.class))).thenReturn(httpUrlConnection);
        when(httpUrlConnection.getOutputStream()).thenReturn(mock(OutputStream.class));
        when(httpUrlConnection.getInputStream()).thenReturn(new ByteArrayInputStream("Mock Response".getBytes()));
        when(httpUrlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);


       
        uploadedDocumentTransmissionJob.sendUploadedDocumentTransferRequest(submission, testUserFile, "testFile.pdf", uploadedDocumentTransmission.getTransmissionId());
        
        List<Transmission> transmissions = transmissionRepositoryService.findAllBySubmissionId(submission);
        assertThat(transmissions).size().isEqualTo(1);
        Transmission transmission = transmissions.get(0);
        assertThat(transmission.getStatus()).isEqualTo(TransmissionStatus.Complete);
        assertThat(transmission.getType()).isEqualTo(TransmissionType.UPLOADED_DOCUMENT);
    }

    @Test
    void enqueuePdfTransmissionJobShouldSetTransmissionStatusToFailedWhenAnErrorOccurs() throws IOException {
        when(s3PresignService.generatePresignedUrl(objectPath)).thenReturn("https://www.test.com");
        when(httpUrlConnectionFactory.createHttpURLConnection(any(URL.class))).thenReturn(httpUrlConnection);
        when(httpUrlConnection.getOutputStream()).thenReturn(mock(OutputStream.class));
        when(httpUrlConnection.getInputStream()).thenReturn(new ByteArrayInputStream("Mock Response".getBytes()));
        when(httpUrlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
        
        assertThrows(RuntimeException.class, () -> uploadedDocumentTransmissionJob.sendUploadedDocumentTransferRequest(submission, testUserFile, "testFile.pdf", uploadedDocumentTransmission.getTransmissionId()));
    }
}