package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.enums.FileNameUtility;
import org.ilgcc.jobs.EnqueueDocumentTransfer;
import org.ilgcc.jobs.PdfTransmissionJob;
import org.ilgcc.jobs.UploadedDocumentTransmissionJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UploadProviderSubmissionToS3 implements Action {

    private final PdfService pdfService;
    private final CloudFileRepository cloudFileRepository;

    private final PdfTransmissionJob pdfTransmissionJob;

    private final EnqueueDocumentTransfer enqueueDocumentTransfer;

    private final String waitForProviderResponseFlag;
    private final SubmissionRepositoryService submissionRepositoryService;
    private final UserFileRepositoryService userFileRepositoryService;
    private final UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob;
    private final S3PresignService s3PresignService;

    public UploadProviderSubmissionToS3(PdfService pdfService, CloudFileRepository cloudFileRepository,
            PdfTransmissionJob pdfTransmissionJob,
            EnqueueDocumentTransfer enqueueDocumentTransfer,
            @Value("${il-gcc.dts.expand-existing-provider-flow}") String waitForProviderResponseFlag,
        SubmissionRepositoryService submissionRepositoryService, UserFileRepositoryService userFileRepositoryService,
        UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob, S3PresignService s3PresignService) {
        this.pdfService = pdfService;
        this.cloudFileRepository = cloudFileRepository;
        this.pdfTransmissionJob = pdfTransmissionJob;
        this.enqueueDocumentTransfer = enqueueDocumentTransfer;
        this.waitForProviderResponseFlag = waitForProviderResponseFlag;
        this.submissionRepositoryService = submissionRepositoryService;
        this.userFileRepositoryService = userFileRepositoryService;
        this.uploadedDocumentTransmissionJob = uploadedDocumentTransmissionJob;
        this.s3PresignService = s3PresignService;
    }

    @Override
    public void run(Submission providerSubmission) {
        if (waitForProviderResponseFlag.equals("true")) {
            var clientId = ProviderSubmissionUtilities.getClientId(providerSubmission);
            if(clientId !=null && clientId.isPresent()){
                Optional<Submission> familySubmission = submissionRepositoryService.findById(clientId.get());
                if(familySubmission.isPresent()){
                    Submission submission = familySubmission.get();
                    submission.getInputData().put("providerResponseSubmissionId", providerSubmission.getId().toString());
                    submissionRepositoryService.save(submission);
                    enqueueDocumentTransfer.enqueuePDFDocumentBySubmission(pdfService, cloudFileRepository, pdfTransmissionJob,
                        submission, FileNameUtility.getFileNameForPdf(submission, "Provider-Responded"));
                    enqueueDocumentTransfer.enqueueUploadedDocumentBySubmission(userFileRepositoryService,
                        uploadedDocumentTransmissionJob, s3PresignService, submission);
                }else{
                    log.error(String.format("We can not find a match for your family submission: %s", clientId.get()));
                }
            }else{
                log.error(String.format("Family Submission Id is Blank for the provider submission: %s.", providerSubmission.getId().toString()));
            }

        }
    }
}