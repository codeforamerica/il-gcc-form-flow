package org.ilgcc.jobs;

import com.google.common.io.Files;
import formflow.library.data.Submission;
import formflow.library.data.UserFile;
import formflow.library.data.UserFileRepositoryService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.ilgcc.app.utils.enums.FileNameUtility;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnqueueUploadedDocumentTransfer {

    private final UserFileRepositoryService userFileRepositoryService;
    private final UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob;
    private final S3PresignService s3PresignService;

    public EnqueueUploadedDocumentTransfer(UserFileRepositoryService userFileRepositoryService,
            UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob, S3PresignService s3PresignService) {
        this.userFileRepositoryService = userFileRepositoryService;
        this.uploadedDocumentTransmissionJob = uploadedDocumentTransmissionJob;
        this.s3PresignService = s3PresignService;
    }

    public void enqueueBySubmission(Submission submission) {
        log.info("Sending uploaded files to document transfer service for submission with ID: {}", submission.getId());
        List<UserFile> userFiles = userFileRepositoryService.findAllBySubmission(submission);
        if (!userFiles.isEmpty()) {
            for (int i = 0; i < userFiles.size(); i++) {
                UserFile userFile = userFiles.get(i);
                String fileExtension = Files.getFileExtension(userFile.getOriginalName());
                String fileName = FileNameUtility.getFileNameForUploadedDocument(submission, i + 1, userFiles.size(),
                        fileExtension);
                CompletableFuture<Boolean> scannedAndCleanFuture = s3PresignService.isObjectScannedAndClean(
                        userFile.getRepositoryPath());
                int currentFileIndex = i;
                scannedAndCleanFuture.thenAccept(scannedAndClean -> {
                    if (scannedAndClean) {
                        log.info("Sending file {} of {} to document transfer service for submission with ID: {}",
                                currentFileIndex + 1, userFiles.size(), submission.getId());
                        uploadedDocumentTransmissionJob.enqueueUploadedDocumentTransmissionJob(submission, userFile, fileName);
                    }
                }).exceptionally(e -> {
                    log.error(
                            "There was an error when attempting to send uploaded file with id: {} in submission with id: {} to the document transfer service. It's possible the file had a virus, or could not be scanned.",
                            userFile.getFileId(), submission.getId(), e);
                    return null;
                });
            }
        }

    }
}

