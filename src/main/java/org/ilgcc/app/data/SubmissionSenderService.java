package org.ilgcc.app.data;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendEmail;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.ilgcc.app.utils.FileNameUtility;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.ilgcc.jobs.CCMSSubmissionPayloadTransactionJob;
import org.ilgcc.jobs.EnqueueDocumentTransfer;
import org.ilgcc.jobs.PdfTransmissionJob;
import org.ilgcc.jobs.UploadedDocumentTransmissionJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SubmissionSenderService {

    private final PdfService pdfService;
    private final CloudFileRepository cloudFileRepository;
    private final PdfTransmissionJob pdfTransmissionJob;
    private final EnqueueDocumentTransfer enqueueDocumentTransfer;
    private final SubmissionRepositoryService submissionRepositoryService;
    private final UserFileRepositoryService userFileRepositoryService;
    private final UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob;
    private final S3PresignService s3PresignService;
    private final CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransactionJob;
    private final boolean ccmsIntegrationEnabled;
    private final boolean dtsIntegrationEnabled;
    private final boolean multipleProvidersEnabled;

    public SubmissionSenderService(PdfService pdfService,
            CloudFileRepository cloudFileRepository,
            PdfTransmissionJob pdfTransmissionJob,
            EnqueueDocumentTransfer enqueueDocumentTransfer,
            SubmissionRepositoryService submissionRepositoryService,
            UserFileRepositoryService userFileRepositoryService,
            UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob,
            S3PresignService s3PresignService,
            CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransactionJob,
            @Value("${il-gcc.ccms-integration-enabled:false}") boolean ccmsIntegrationEnabled,
            @Value("${il-gcc.dts-integration-enabled}") boolean dtsIntegrationEnabled,
            @Value("${il-gcc.enable-multiple-providers}") boolean multipleProvidersEnabled) {
        this.pdfService = pdfService;
        this.cloudFileRepository = cloudFileRepository;
        this.pdfTransmissionJob = pdfTransmissionJob;
        this.enqueueDocumentTransfer = enqueueDocumentTransfer;
        this.submissionRepositoryService = submissionRepositoryService;
        this.userFileRepositoryService = userFileRepositoryService;
        this.uploadedDocumentTransmissionJob = uploadedDocumentTransmissionJob;
        this.s3PresignService = s3PresignService;
        this.ccmsSubmissionPayloadTransactionJob = ccmsSubmissionPayloadTransactionJob;
        this.ccmsIntegrationEnabled = ccmsIntegrationEnabled;
        this.dtsIntegrationEnabled = dtsIntegrationEnabled;
        this.multipleProvidersEnabled = multipleProvidersEnabled;
    }

    public void sendProviderSubmission(Submission providerSubmission) {
       sendProviderSubmission(providerSubmission, false, Optional.empty());
    }

    public void sendProviderSubmissionInstantly(Submission providerSubmission) {
        sendProviderSubmission(providerSubmission, true, Optional.empty());
    }

    public void sendProviderSubmissionInstantly(Submission providerSubmission, Optional<SendEmail> sendEmail) {
        sendProviderSubmission(providerSubmission, true, sendEmail);
    }

    private void sendProviderSubmission(Submission providerSubmission, boolean sendToCCMSInstantly, Optional<SendEmail> sendFamilyEmail) {
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionId.isPresent()) {
            Optional<Submission> familySubmissionOptional = submissionRepositoryService.findById(familySubmissionId.get());
            if (familySubmissionOptional.isPresent()) {
                Submission familySubmission = familySubmissionOptional.get();

                log.info("Provider submitted response for family submission {}, enqueuing transfer of documents.",
                        familySubmission.getId());

                if (multipleProvidersEnabled) {
                    String currentProviderUuid = providerSubmission.getInputData().get("currentProviderUuid").toString();

                    boolean allProvidersResponded = true;
                    List<Map<String, Object>> providers = SubmissionUtilities.getProviders(familySubmission.getInputData());

                    for (int i = 0; i < providers.size(); i++) {
                        Map<String, Object> provider = providers.get(i);
                        if (currentProviderUuid.equals(provider.get("uuid").toString())) {
                            provider.put("providerResponseSubmissionId", providerSubmission.getId().toString());
                            provider.put("providerResponseStatus", SubmissionStatus.RESPONDED.name());
                            providers.set(i, provider);
                        } else if (!provider.containsKey("providerResponseStatus") || !SubmissionStatus.RESPONDED.name().equals(provider.get("providerResponseStatus").toString())) {
                            allProvidersResponded = false;
                        }
                    }

                    if (allProvidersResponded) {
                        familySubmission.getInputData().put("providerApplicationResponseStatus", SubmissionStatus.RESPONDED.name());
                    }

                } else {
                    familySubmission.getInputData().put("providerResponseSubmissionId", providerSubmission.getId().toString());
                    familySubmission.getInputData().put("providerApplicationResponseStatus", SubmissionStatus.RESPONDED.name());
                }

                submissionRepositoryService.save(familySubmission);
                sendFamilyEmail.ifPresent(email -> email.send(familySubmission));

                if (dtsIntegrationEnabled) {
                    enqueueDocumentTransfer.enqueuePDFDocumentBySubmission(pdfService, cloudFileRepository, pdfTransmissionJob,
                            familySubmission, FileNameUtility.getFileNameForPdf(familySubmission, "Provider-Responded"));
                    enqueueDocumentTransfer.enqueueUploadedDocumentBySubmission(userFileRepositoryService,
                            uploadedDocumentTransmissionJob, s3PresignService, familySubmission);
                }
                if (ccmsIntegrationEnabled) {
                    if (sendToCCMSInstantly) {
                        ccmsSubmissionPayloadTransactionJob.enqueueCCMSTransactionPayloadInstantly(familySubmission.getId());
                    } else {
                        ccmsSubmissionPayloadTransactionJob.enqueueCCMSTransactionPayloadWithDelay(familySubmission.getId());
                    }
                }
            } else {
                log.error("Could not find a family submission for id: {}", familySubmissionId.get());
            }
        } else {
            log.error("Family submission id is blank for the provider submission: {}", providerSubmission.getId());
        }
    }

    public void sendFamilySubmission(Submission familySubmission) {
        if (dtsIntegrationEnabled) {
            enqueueDocumentTransfer.enqueuePDFDocumentBySubmission(pdfService, cloudFileRepository, pdfTransmissionJob,
                    familySubmission, FileNameUtility.getFileNameForPdf(familySubmission, "Form-Family"));
        }

        if (ccmsIntegrationEnabled) {
            ccmsSubmissionPayloadTransactionJob.enqueueCCMSTransactionPayloadWithDelay(familySubmission.getId());
        }
    }
}
