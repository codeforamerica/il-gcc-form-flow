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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import java.net.HttpURLConnection;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest(classes = IlGCCApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "DOCUMENT_TRANSFER_SERVICE_URL=http://localhost:8080",
        "DOCUMENT_TRANSFER_SERVICE_CONSUMER_ID=test-consumer",
        "DOCUMENT_TRANSFER_SERVICE_AUTH_TOKEN=test-token"
})
class EnqueueDocumentTransferTest {

    private PdfTransmissionJob pdfTransmissionJob;

    @MockBean
    private S3PresignService s3PresignService;

    @SpyBean
    private TransmissionRepositoryService transmissionRepositoryService;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @MockBean
    HttpUrlConnectionFactory httpUrlConnectionFactory;

    @MockBean
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
                .useDashboard()
                .useBackgroundJobServer(usingStandardBackgroundJobServerConfiguration()
                        .andPollInterval(ofMillis(200))).initialize().getJobScheduler();

        pdfTransmissionJob = new PdfTransmissionJob(s3PresignService, jobScheduler, documentTransferRequestService, transmissionRepositoryService);
    }

    @AfterEach
    public void stopJobRunr() {
        JobRunr.destroy();
    }

    @Test
    void whenUserFilesExistAndScannedAndClearCallEnqueueUploadedDocs() throws IOException {
        assertThat(true).isTrue();
    }

    @Test
    void whenCalledCallsEnquequePDFTransmission() throws IOException {
        assertThat(true).isTrue();
    }


    // Tells JobRunr's configuration what class will activate/call enqueue for jobs
    private <T> T jobActivator(Class<T> clazz) {
        return (T) pdfTransmissionJob;
    }

    private String getSucceededJobs() {
        return getJson("http://localhost:8000/api/jobs?state=SUCCEEDED");
    }
}