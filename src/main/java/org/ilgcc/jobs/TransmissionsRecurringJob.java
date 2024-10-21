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
    private Boolean expandExistingProviderFlow;
    private final PdfTransmissionJob pdfTransmissionJob;
    private final EnqueueDocumentTransfer enqueueDocumentTransfer;

    public TransmissionsRecurringJob(S3PresignService s3PresignService,
            TransmissionRepositoryService transmissionRepositoryService, UserFileRepositoryService userFileRepositoryService,
            UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob, PdfService pdfService,
            CloudFileRepository cloudFileRepository, PdfTransmissionJob pdfTransmissionJob,
            @Value("${il-gcc.dts.expand-existing-provider-flow}") Boolean expandExistingProviderFlow,
            EnqueueDocumentTransfer enqueueDocumentTransfer) {
        this.s3PresignService = s3PresignService;
        this.transmissionRepositoryService = transmissionRepositoryService;
        this.userFileRepositoryService = userFileRepositoryService;
        this.uploadedDocumentTransmissionJob = uploadedDocumentTransmissionJob;
        this.pdfService = pdfService;
        this.cloudFileRepository = cloudFileRepository;
        this.pdfTransmissionJob = pdfTransmissionJob;
        this.expandExistingProviderFlow = expandExistingProviderFlow;
        this.enqueueDocumentTransfer = enqueueDocumentTransfer;
    }

    @Recurring(id = "no-provider-response-job", cron = "* * * * *")
    @Job(name = "No provider response job")
    public void noProviderResponseJob() {
        List<Submission> submissionsWithoutTransmissions = transmissionRepositoryService.findSubmissionsWithoutTransmission();

        ZoneId chicagoTimeZone = ZoneId.of("America/Chicago");
        ZonedDateTime todaysDate = OffsetDateTime.now().atZoneSameInstant(chicagoTimeZone);

        List<Submission> expiredSubmissionsWithNoTransmission = submissionsWithoutTransmissions.stream()
                .filter(submission -> providerApplicationHasExpired(submission, todaysDate)).toList();
        if (expiredSubmissionsWithNoTransmission.isEmpty() || !expandExistingProviderFlow) {
            return;
        } else {
            log.info(String.format("Running the 'No provider response job' for %s expired submissions",
                    expiredSubmissionsWithNoTransmission.size()));
            for (Submission submission : expiredSubmissionsWithNoTransmission) {
                if (!hasProviderResponse(submission)) {
                    enqueueDocumentTransfer.enqueuePDFDocumentBySubmission(pdfService, cloudFileRepository, pdfTransmissionJob,
                            submission, FileNameUtility.getFileNameForPdf(submission, "No-Provider-Response"));
                    enqueueDocumentTransfer.enqueueUploadedDocumentBySubmission(userFileRepositoryService,
                            uploadedDocumentTransmissionJob, s3PresignService, submission);
                } else {
                    log.error(
                            String.format("The provider response exists but the provider response expired. Check submission: %s",
                                    submission.getId()));
                }
            }
        }
    }

    private boolean hasProviderResponse(Submission submission) {
        return submission.getInputData().containsKey("providerResponseSubmissionId");
    }
}
