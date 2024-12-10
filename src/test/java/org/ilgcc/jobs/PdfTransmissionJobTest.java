package org.ilgcc.jobs;

import static java.time.Duration.ofMillis;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.ilgcc.jobs.HttpClient.getJson;
import static org.jobrunr.server.BackgroundJobServerConfiguration.usingStandardBackgroundJobServerConfiguration;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;


@SpringBootTest(classes = IlGCCApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "DOCUMENT_TRANSFER_SERVICE_URL=http://localhost:8080",
        "DOCUMENT_TRANSFER_SERVICE_CONSUMER_ID=test-consumer",
        "DOCUMENT_TRANSFER_SERVICE_AUTH_TOKEN=test-token"
})
class PdfTransmissionJobTest {

    private PdfTransmissionJob pdfTransmissionJob;

    @MockitoBean
    private S3PresignService s3PresignService;

    @MockitoSpyBean
    private TransmissionRepositoryService transmissionRepositoryService;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @MockitoBean
    HttpUrlConnectionFactory httpUrlConnectionFactory;

    @MockitoBean
    HttpURLConnection httpUrlConnection;

    @Autowired
    DocumentTransferRequestService documentTransferRequestService;

    private static final InMemoryStorageProvider storageProvider = new InMemoryStorageProvider();

    private JobScheduler jobScheduler;

    private Submission submission;

    private final String objectPath = "testPath";
    @Autowired
    private Transmission transmission;

    @BeforeEach
    void setUp() {
        submission = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now())
                .build();
        submissionRepositoryService.save(submission);

        jobScheduler = JobRunr.configure()
                .useStorageProvider(storageProvider)
                .useJobActivator(this::jobActivator)
                .useDashboard(1337)
                .useBackgroundJobServer(usingStandardBackgroundJobServerConfiguration()
                        .andPollInterval(ofMillis(200))).initialize().getJobScheduler();

        pdfTransmissionJob = new PdfTransmissionJob(s3PresignService, jobScheduler, documentTransferRequestService, transmissionRepositoryService);
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

        pdfTransmissionJob.enqueuePdfTransmissionJob(objectPath, submission, "test");

        await().atMost(15, TimeUnit.SECONDS).untilAsserted(
                () -> assertThatJson(getSucceededJobs()).inPath("$.items[0].jobHistory[*].state")
                        .isEqualTo("['ENQUEUED', 'PROCESSING', 'SUCCEEDED']"));

        List<Transmission> transmissions = transmissionRepositoryService.findAllBySubmissionId(submission);
        assertThat(transmissions).size().isEqualTo(1);
        Transmission transmission = transmissions.get(0);
        assertThat(transmission.getStatus()).isEqualTo(TransmissionStatus.Complete);
        assertThat(transmission.getType()).isEqualTo(TransmissionType.APPLICATION_PDF);
        assertThat(transmission.getRetryAttempts()).isEqualTo(null);
    }

    @Test
    void enqueuePdfTransmissionJobShouldSetTransmissionStatusToFailedWhenAnErrorOccurs() throws IOException {
        when(s3PresignService.generatePresignedUrl(objectPath)).thenReturn("https://www.test.com");
        when(httpUrlConnectionFactory.createHttpURLConnection(any(URL.class))).thenReturn(httpUrlConnection);
        when(httpUrlConnection.getOutputStream()).thenReturn(mock(OutputStream.class));
        when(httpUrlConnection.getInputStream()).thenReturn(new ByteArrayInputStream("Mock Response".getBytes()));
        when(httpUrlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);

        assertThrows(RuntimeException.class, () -> pdfTransmissionJob.sendPdfTransferRequest("https://www.test.com", submission, objectPath, transmission.getTransmissionId()));
    }


    // Tells JobRunr's configuration what class will activate/call enqueue for jobs
    private <T> T jobActivator(Class<T> clazz) {
        return (T) pdfTransmissionJob;
    }

    private String getSucceededJobs() {
        return getJson("http://localhost:1337/api/jobs?state=SUCCEEDED");
    }
}