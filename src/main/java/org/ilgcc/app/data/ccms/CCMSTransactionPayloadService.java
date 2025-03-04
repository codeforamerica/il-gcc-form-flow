package org.ilgcc.app.data.ccms;

import static org.ilgcc.app.utils.FileNameUtility.getCCMSFileNameForApplicationPDF;

import formflow.library.data.Submission;
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
    
    public CCMSTransactionPayloadService(CloudFileRepository cloudFileRepository,
            UserFileRepositoryService userFileRepositoryService, PdfService pdfService) {
        this.cloudFileRepository = cloudFileRepository;
        this.userFileRepositoryService = userFileRepositoryService;
        this.pdfService = pdfService;
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
        
        List<UserFile> userFiles = userFileRepositoryService.findAllOrderByOriginalName(familySubmission, "application/pdf");
        for (int i = 0; i < userFiles.size(); i++) {
            UserFile userFile = userFiles.get(i);
            CloudFile cloudFile = cloudFileRepository.get(userFile.getRepositoryPath());
            transactionFiles.add(
                    new TransactionFile(
                            FileNameUtility.getCCMSFileNameForUploadedDocument(familySubmission, i + 1, userFiles.size()),
                            FileTypeId.UPLOADED_DOCUMENT.getValue(),
                            Base64.getEncoder().encodeToString(cloudFile.getFileBytes())
                    ));
        }

        return transactionFiles;
    }
}
