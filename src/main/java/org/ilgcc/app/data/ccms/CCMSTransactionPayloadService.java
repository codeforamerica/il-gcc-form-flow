package org.ilgcc.app.data.ccms;

import static org.ilgcc.app.utils.FileNameUtility.getCCMSFileNameForApplicationPDF;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.data.UserFile;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFile;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.ccms.TransactionFile.FileTypeId;
import org.ilgcc.app.utils.DateUtilities;
import org.ilgcc.app.utils.FileNameUtility;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CCMSTransactionPayloadService {

    private final CloudFileRepository cloudFileRepository;
    private final UserFileRepositoryService userFileRepositoryService;
    private final PdfService pdfService;
    private final SubmissionRepositoryService submissionRepositoryService;
    
    public CCMSTransactionPayloadService(CloudFileRepository cloudFileRepository,
            UserFileRepositoryService userFileRepositoryService, PdfService pdfService,
            SubmissionRepositoryService submissionRepositoryService) {
        this.cloudFileRepository = cloudFileRepository;
        this.userFileRepositoryService = userFileRepositoryService;
        this.pdfService = pdfService;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    public CCMSTransaction generateSubmissionTransactionPayload(Submission familySubmission) {
        return new CCMSTransaction(
                "application",
                familySubmission.getId(),
                familySubmission.getShortCode(),
                familySubmission.getInputData().get("organizationId").toString(),
                FileNameUtility.removeNonSpaceOrDashCharacters(familySubmission.getInputData().get("parentFirstName").toString()),
                FileNameUtility.removeNonSpaceOrDashCharacters(familySubmission.getInputData().get("parentLastName").toString()),
                familySubmission.getInputData().get("parentBirthDate").toString(),
                getTransactionFiles(familySubmission),
                DateUtilities.formatDateToYearMonthDayHourCSTWithOffset(OffsetDateTime.now()),
                DateUtilities.formatDateToYearMonthDayHourCSTWithOffset(familySubmission.getSubmittedAt()) 
        );
    }

    private List<TransactionFile> getTransactionFiles(Submission familySubmission) {
        List<TransactionFile> transactionFiles = new ArrayList<>();
        try {
            byte[] filledOutPDF = pdfService.getFilledOutPDF(familySubmission);
            TransactionFile applicationPdfJSON = new TransactionFile(
                    getCCMSFileNameForApplicationPDF(familySubmission), 
                    FileTypeId.APPLICATION_PDF.getValue(),
                    Base64.getEncoder().encodeToString(filledOutPDF));
            transactionFiles.add(applicationPdfJSON);
        } catch (IOException e) {
            log.error(
                    "There was an error when generating the application PDF for sending to the CCMS Submission Endpoint for Submission with ID {}.",
                    familySubmission.getId(), e);
        }

        List<UserFile> allFiles = new ArrayList<>();
        if (familySubmission.getInputData().containsKey("providerResponseSubmissionId")) {
            submissionRepositoryService.findById(
                            UUID.fromString(familySubmission.getInputData().get("providerResponseSubmissionId").toString()))
                    .ifPresent(providerSubmission -> allFiles.addAll(
                            userFileRepositoryService.findAllOrderByOriginalName(providerSubmission, "application/pdf")));
        }
        allFiles.addAll(userFileRepositoryService.findAllOrderByOriginalName(familySubmission, "application/pdf"));
        for (int i = 0; i < allFiles.size(); i++) {
            UserFile userFile = allFiles.get(i);
            CloudFile cloudFile = cloudFileRepository.get(userFile.getRepositoryPath());
            transactionFiles.add(
                    new TransactionFile(
                            FileNameUtility.getCCMSFileNameForUploadedDocument(familySubmission, i + 1, allFiles.size()),
                            FileTypeId.UPLOADED_DOCUMENT.getValue(),
                            Base64.getEncoder().encodeToString(cloudFile.getFileBytes())
                    ));
        }

        return transactionFiles;
    }
}
