package org.ilgcc.app.data.ccms;

import static java.util.Collections.emptyList;
import static org.ilgcc.app.utils.FileNameUtility.getCCMSFileNameForApplicationPDF;
import static org.ilgcc.app.utils.constants.MediaTypes.PDF_CONTENT_TYPE;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.data.UserFile;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFile;
import formflow.library.file.CloudFileRepository;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.ccms.TransactionFile.FileTypeId;
import org.ilgcc.app.pdf.MultiProviderPDFService;
import org.ilgcc.app.utils.ByteArrayMultipartFile;
import org.ilgcc.app.utils.DateUtilities;
import org.ilgcc.app.utils.FileNameUtility;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class CCMSTransactionPayloadService {
    
    private final CloudFileRepository cloudFileRepository;
    private final UserFileRepositoryService userFileRepositoryService;
    private final SubmissionRepositoryService submissionRepositoryService;
    private final MultiProviderPDFService pdfService;
    private final boolean allowPdfModification;

    public CCMSTransactionPayloadService(CloudFileRepository cloudFileRepository,
            UserFileRepositoryService userFileRepositoryService,
            MultiProviderPDFService pdfService,
            SubmissionRepositoryService submissionRepositoryService,
            @Value("${form-flow.uploads.file-conversion.allow-pdf-modification}") boolean allowPdfModification) {
        this.cloudFileRepository = cloudFileRepository;
        this.userFileRepositoryService = userFileRepositoryService;
        this.pdfService = pdfService;
        this.submissionRepositoryService = submissionRepositoryService;
        this.allowPdfModification = allowPdfModification;
    }

    public Optional<CCMSTransaction> generateSubmissionTransactionPayload(Submission familySubmission) {
        if (familySubmission == null) {
            log.error("generateSubmissionTransactionPayload error: familySubmission is null");
            return Optional.empty();
        }
        try {
            return Optional.of(new CCMSTransaction(
                    "application",
                    familySubmission.getId(),
                    familySubmission.getShortCode(),
                    familySubmission.getInputData().get("organizationId").toString(),
                    FileNameUtility.removeNonSpaceOrDashCharacters(
                            familySubmission.getInputData().get("parentFirstName").toString()),
                    FileNameUtility.removeNonSpaceOrDashCharacters(
                            familySubmission.getInputData().get("parentLastName").toString()),
                    familySubmission.getInputData().get("parentBirthDate").toString(),
                    getTransactionFiles(familySubmission),
                    DateUtilities.formatDateToYearMonthDayHourCSTWithOffset(OffsetDateTime.now()),
                    DateUtilities.formatDateToYearMonthDayHourCSTWithOffset(familySubmission.getSubmittedAt())
            ));
        } catch (Exception e) {
            log.error("generateSubmissionTransactionPayload error for submission {}", familySubmission.getId(), e);
            return Optional.empty();
        }
    }

    private List<TransactionFile> getTransactionFiles(Submission familySubmission) {
        List<TransactionFile> transactionFiles = new ArrayList<>();
        
        // Preload existing files for this Submission in case this is a retry
        List<UserFile> existing = userFileRepositoryService.findAllOrderByOriginalName(familySubmission);
        Map<String, UserFile> existingFilesByName = existing.stream()
                .collect(Collectors.toMap(UserFile::getOriginalName, uf -> uf, (a, b) -> a));
        
        try {
            Map<String, byte[]> pdfs = pdfService.generatePDFs(familySubmission);

            for (Map.Entry<String, byte[]> entry : pdfs.entrySet()) {
                String pdfFileName = entry.getKey();
                byte[] pdfBytes = entry.getValue();
                
                UserFile pdfUserFile = existingFilesByName.get(pdfFileName);
                if (pdfUserFile == null) {
                    // Only save to S3 and create UserFile if it doesn't already exist
                    String s3Path = SubmissionUtilities.generatePdfPath(pdfFileName, familySubmission.getId());
                    MultipartFile multipartFile =
                            new ByteArrayMultipartFile(pdfBytes, pdfFileName, PDF_CONTENT_TYPE);
                    cloudFileRepository.upload(s3Path, multipartFile);
                    pdfUserFile = userFileRepositoryService.save(
                            UserFile.builder()
                                    .fileId(UUID.randomUUID())
                                    .submission(familySubmission)
                                    .originalName(pdfFileName)
                                    .repositoryPath(s3Path)
                                    .filesize((float) pdfBytes.length)
                                    .mimeType(PDF_CONTENT_TYPE)
                                    .virusScanned(true)
                                    .build()
                    );

                    existingFilesByName.put(pdfFileName, pdfUserFile);
                } else {
                    log.info("Reusing existing PDF '{}' for submission {} when regenerating CCMS transaction for retry.", pdfFileName, familySubmission.getId());
                }

                String fileType = pdfFileName.equals(getCCMSFileNameForApplicationPDF(familySubmission))
                        ? FileTypeId.APPLICATION_PDF.getValue()
                        : FileTypeId.UPLOADED_DOCUMENT.getValue();

                transactionFiles.add(new TransactionFile(
                        pdfFileName,
                        fileType,
                        Base64.getEncoder().encodeToString(pdfBytes),
                        pdfUserFile
                ));
            }
        } catch (IOException e) {
            log.error("Error uploading submission {} PDFs to S3. Could not complete sending the submission to CCMS.",
                    familySubmission, e);
            throw new RuntimeException(
                    String.format("Failed to upload PDF files for Submission %s to S3. Could not complete CCMS submission.",
                            familySubmission.getId()), e
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while uploading PDF files to S3 for submission {}. Could not complete CCMS submission.",
                    familySubmission, e);
            throw new RuntimeException(
                    String.format("Failed to upload PDF files for Submission %s to S3. Could not complete CCMS submission.",
                            familySubmission.getId()), e
            );
        }
        
        List<UserFile> allFiles = new ArrayList<>();

        List<Map<String, Object>> providers = (List<Map<String, Object>>) familySubmission.getInputData()
                .getOrDefault("providers", emptyList());
        for (Map<String, Object> provider : providers) {
            if (provider.containsKey("providerResponseSubmissionId")) {
                UUID providerSubmissionId = UUID.fromString(provider.get("providerResponseSubmissionId").toString());
                submissionRepositoryService.findById(providerSubmissionId).ifPresent(
                        providerSubmission -> allFiles.addAll(findAllFiles(providerSubmission)));
            }
        }

        List<UserFile> usersUploadedFiles = findAllFiles(familySubmission).stream()
                .filter(userFile -> !userFile.getOriginalName().contains("CCAP-Application-Form"))
                .toList();
        
        allFiles.addAll(usersUploadedFiles);

        for (int i = 0; i < allFiles.size(); i++) {
            UserFile userFile = allFiles.get(i);
            CloudFile cloudFile;
            try {
                cloudFile = cloudFileRepository.get(userFile.getRepositoryPath());
            } catch (Exception e) {
                log.error(
                        "There was an error when attempting to send uploaded file with id: {} in submission with id: {} to CCMS. It's possible the file had a virus, or could not be scanned.",
                        userFile.getFileId(), familySubmission.getId(), e);
                continue;
            }

            if (cloudFile == null) {
                log.error(
                        "There was an error when attempting to send uploaded file with id: {} in submission with id: {} to CCMS. It's possible the file had a virus, or could not be scanned.",
                        userFile.getFileId(), familySubmission.getId());
                continue;
            }
            TransactionFile transactionFile = new TransactionFile(
                    FileNameUtility.getCCMSFileNameForUploadedDocument(familySubmission, i + 1, allFiles.size()),
                    FileTypeId.UPLOADED_DOCUMENT.getValue(), Base64.getEncoder().encodeToString(cloudFile.getFileBytes()), userFile);
            transactionFiles.add(transactionFile);
        }

        return transactionFiles;
    }

    private List<UserFile> findAllFiles(Submission submission) {
        if (allowPdfModification) {
            return userFileRepositoryService.findAllConvertedOrderByOriginalName(submission, PDF_CONTENT_TYPE);
        } else {
            return userFileRepositoryService.findAllOrderByOriginalName(submission, PDF_CONTENT_TYPE);
        }
    }
}
