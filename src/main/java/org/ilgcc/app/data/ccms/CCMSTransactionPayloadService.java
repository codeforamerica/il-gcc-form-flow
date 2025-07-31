package org.ilgcc.app.data.ccms;

import static java.util.Collections.emptyList;
import static org.ilgcc.app.utils.FileNameUtility.getCCMSFileNameForApplicationPDF;
import static org.ilgcc.app.utils.SubmissionUtilities.isPreMultiProviderApplicationWithSingleProvider;
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
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.ccms.TransactionFile.FileTypeId;
import org.ilgcc.app.pdf.MultiProviderPDFService;
import org.ilgcc.app.utils.DateUtilities;
import org.ilgcc.app.utils.FileNameUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CCMSTransactionPayloadService {

    private final CloudFileRepository cloudFileRepository;
    private final UserFileRepositoryService userFileRepositoryService;
    private final SubmissionRepositoryService submissionRepositoryService;
    private final MultiProviderPDFService pdfService;
    private final boolean enableMultipleProviders;
    private boolean allowPdfModification;

    public CCMSTransactionPayloadService(CloudFileRepository cloudFileRepository,
            UserFileRepositoryService userFileRepositoryService,
            MultiProviderPDFService pdfService,
            SubmissionRepositoryService submissionRepositoryService,
            @Value("${il-gcc.enable-multiple-providers}") boolean enableMultipleProviders,
            @Value("${form-flow.uploads.file-conversion.allow-pdf-modification}") boolean allowPdfModification) {
        this.cloudFileRepository = cloudFileRepository;
        this.userFileRepositoryService = userFileRepositoryService;
        this.pdfService = pdfService;
        this.submissionRepositoryService = submissionRepositoryService;
        this.enableMultipleProviders = enableMultipleProviders;
        this.allowPdfModification = allowPdfModification;
    }

    public Optional<CCMSTransaction> generateSubmissionTransactionPayload(Submission familySubmission) {
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
            log.error("generateSubmissionTransactionPayload error for submission {}", familySubmission != null ? familySubmission.getId() : "NULL FAMILY SUBMISSION", e);
            return Optional.empty();
        }
    }

    private List<TransactionFile> getTransactionFiles(Submission familySubmission) {
        List<TransactionFile> transactionFiles = new ArrayList<>();

        try {
            Map<String, byte[]> filledOutPDFs = pdfService.generatePDFs(familySubmission);
            for (Map.Entry<String, byte[]> entry : filledOutPDFs.entrySet()) {
                String fileName = entry.getKey();
                byte[] fileContent = entry.getValue();

                if (fileName.equals(getCCMSFileNameForApplicationPDF(familySubmission))) {
                    TransactionFile applicationPdfJSON = new TransactionFile(fileName, FileTypeId.APPLICATION_PDF.getValue(), familySubmission.getId().toString(),
                            Base64.getEncoder().encodeToString(fileContent));
                    transactionFiles.add(applicationPdfJSON);
                } else {
                    TransactionFile additionalProviderPagesJSON = new TransactionFile(fileName, FileTypeId.UPLOADED_DOCUMENT.getValue(), "580d1866-3c15-4823-9271-f326e3690921",
                            Base64.getEncoder().encodeToString(fileContent));
                    transactionFiles.add(additionalProviderPagesJSON);
                }
            }
        } catch (IOException e) {
            log.error(
                    "There was an error when generating the application PDF for sending to the CCMS Submission Endpoint for Submission with ID {}.",
                    familySubmission.getId(), e);
        }

        List<UserFile> allFiles = new ArrayList<>();
        if (enableMultipleProviders && !isPreMultiProviderApplicationWithSingleProvider(familySubmission)) {
            List<Map<String, Object>> providers = (List<Map<String, Object>>) familySubmission.getInputData()
                    .getOrDefault("providers", emptyList());
            for (Map<String, Object> provider : providers) {
                if (provider.containsKey("providerResponseSubmissionId")) {
                    UUID providerSubmissionId = UUID.fromString(provider.get("providerResponseSubmissionId").toString());
                    submissionRepositoryService.findById(providerSubmissionId).ifPresent(
                            providerSubmission -> allFiles.addAll(findAllFiles(providerSubmission)));
                }
            }
        } else {
            if (familySubmission.getInputData().containsKey("providerResponseSubmissionId")) {
                submissionRepositoryService.findById(
                                UUID.fromString(familySubmission.getInputData().get("providerResponseSubmissionId").toString()))
                        .ifPresent(providerSubmission -> allFiles.addAll(findAllFiles(providerSubmission)));
            }
        }

        List<UserFile> userFiles = findAllFiles(familySubmission);

        allFiles.addAll(userFiles);

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


            /// testing with random uuid for now, don't try this at home kids
            transactionFiles.add(new TransactionFile(
                    FileNameUtility.getCCMSFileNameForUploadedDocument(familySubmission, i + 1, allFiles.size()),
                    FileTypeId.UPLOADED_DOCUMENT.getValue(), UUID.randomUUID().toString(), Base64.getEncoder().encodeToString(cloudFile.getFileBytes())));
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
