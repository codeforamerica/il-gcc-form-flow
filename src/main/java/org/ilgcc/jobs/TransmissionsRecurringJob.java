package org.ilgcc.jobs;


import formflow.library.data.Submission;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.providerApplicationHasExpired;

import org.ilgcc.app.data.TransmissionRepositoryService;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.ilgcc.app.utils.enums.FileNameUtility;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransmissionsRecurringJob {

    private final S3PresignService s3PresignService;
    private final TransmissionRepositoryService transmissionRepositoryService;
    private final UserFileRepositoryService userFileRepositoryService;
    private final UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob;
    private final PdfService pdfService;
    private final CloudFileRepository cloudFileRepository;
    private final String waitForProviderResponseFlag;
    private final PdfTransmissionJob pdfTransmissionJob;
    private final String CONTENT_TYPE = "application/pdf";

    public TransmissionsRecurringJob(
            S3PresignService s3PresignService,
            TransmissionRepositoryService transmissionRepositoryService,
            UserFileRepositoryService userFileRepositoryService, UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob,
            PdfService pdfService,
            CloudFileRepository cloudFileRepository, PdfTransmissionJob pdfTransmissionJob,
            @Value("${il-gcc.dts.wait-for-provider-response}") String waitForProviderResponseFlag) {
        this.s3PresignService = s3PresignService;
        this.transmissionRepositoryService = transmissionRepositoryService;
        this.userFileRepositoryService = userFileRepositoryService;
        this.uploadedDocumentTransmissionJob = uploadedDocumentTransmissionJob;
        this.pdfService = pdfService;
        this.cloudFileRepository = cloudFileRepository;
        this.pdfTransmissionJob = pdfTransmissionJob;
        this.waitForProviderResponseFlag = waitForProviderResponseFlag;
    }

    @Recurring(id = "no-provider-response-job", cron = "* * * * *")
    @Job(name = "No provider response job")
    public void noProviderResponseJob() {
        List<Submission> submissionsWithoutTransmissions = transmissionRepositoryService.findSubmissionsWithoutTransmission();


//        if (waitForProviderResponseFlag.equals("false")) {
//            return;
//        } else

        if (submissionsWithoutTransmissions.isEmpty()) {
            return;
        } else {
            ZoneId chicagoTimeZone = ZoneId.of("America/Chicago");
            ZonedDateTime todaysDate = OffsetDateTime.now().atZoneSameInstant(chicagoTimeZone);
            for (Submission s : submissionsWithoutTransmissions) {
                if (!hasProviderResponse(s) && providerApplicationHasExpired(s, todaysDate)) {
                    uploadPdfSubmissionToS3(s);
                    sendUploadedDocumentsToDocumentTransferService(s);
                } else if (hasProviderResponse(s) && providerApplicationHasExpired(s, todaysDate)) {
                    throw new IllegalStateException(String.format(
                            "Weird, provider response exist but the provider response expired. Check submission: %s", s.getId()));
                }
            }
        }
    }

    private boolean hasProviderResponse(Submission submission) {
        return submission.getInputData().containsKey("providerResponseSubmissionId");
    }

    private void uploadPdfSubmissionToS3(Submission submission) {
        new EnqueuePdfDocumentTransfer(pdfService, cloudFileRepository, pdfTransmissionJob).enqueueBySubmission(submission, FileNameUtility.getFileNameForPdf(submission, "No-Provider-Response"));
    }

    private void sendUploadedDocumentsToDocumentTransferService(Submission submission) {
        new EnqueueUploadedDocumentTransfer(userFileRepositoryService, uploadedDocumentTransmissionJob, s3PresignService).enqueueBySubmission(submission);
    }
}
