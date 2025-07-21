package org.ilgcc.app.pdf;

import static org.ilgcc.app.utils.FileNameUtility.getCCMSFileNameForAdditionalProviderPDF;
import static org.ilgcc.app.utils.FileNameUtility.getCCMSFileNameForApplicationPDF;
import static org.ilgcc.app.utils.SchedulePreparerUtility.getRelatedChildrenSchedulesForEachProvider;
import static org.ilgcc.app.utils.SubmissionUtilities.formatToStringFromLocalDate;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.PDFFormFiller;
import formflow.library.pdf.PdfField;
import formflow.library.pdf.PdfFieldMapper;
import formflow.library.pdf.PdfService;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.ilgcc.app.pdf.helpers.FamilyIntendedProviderPreparerHelper;
import org.ilgcc.app.pdf.helpers.ProviderApplicationPreparerHelper;
import org.ilgcc.app.pdf.helpers.ProviderHouseholdMemberPreparerHelper;
import org.ilgcc.app.pdf.helpers.ProviderLanguagesPreparerHelper;
import org.ilgcc.app.pdf.helpers.ProviderRegistrationPreparer;
import org.ilgcc.app.pdf.helpers.ProviderSSNPreparerHelper;
import org.ilgcc.app.pdf.helpers.ProviderTypePreparerHelper;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MultiProviderPDFService {

    Submission submission;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    PdfService pdfService;

    @Autowired
    FamilyIntendedProviderPreparerHelper familyIntendedProviderPreparerHelper;

    @Autowired
    PdfFieldMapper pdfFieldMapper;

    @Autowired
    PDFFormFiller pdfFormFiller;

    @Autowired
    NeedChildcareChildrenPreparer needChildcareChildrenPreparer;

    @Autowired
    ProviderApplicationPreparerHelper providerApplicationPreparerHelper;

    @Autowired
    ProviderHouseholdMemberPreparerHelper providerHouseholdMemberPreparer;

    @Autowired
    ProviderLanguagesPreparerHelper providerLanguagesPreparerHelper;

    @Autowired
    ProviderSSNPreparerHelper providerSSNPreparerHelper;

    @Autowired
    ProviderTypePreparerHelper providerTypePreparerHelper;

    @Autowired
    ProviderRegistrationPreparer providerRegistrationPreparer;


    private final Boolean enableMutipleProviders;

    public MultiProviderPDFService(Submission submission,
            @Value("${il-gcc.enable-multiple-providers}") boolean enableMultipleProviders) {
        this.submission = submission;
        this.enableMutipleProviders = enableMultipleProviders;
    }

    public Map<String, byte[]> generatePDFs(Submission submission) throws IOException {
        Map<String, byte[]> allFiles = new HashMap<>();

        allFiles.put(getCCMSFileNameForApplicationPDF(submission), pdfService.getFilledOutPDF(submission));

        if (enableMutipleProviders) {
            allFiles.putAll(generateAdditionalProviderPDF(submission));
        }

        return allFiles;
    }

    Map<String, byte[]> generateAdditionalProviderPDF(Submission familySubmission) throws IOException {
        Map<String, byte[]> additionalPDFs = new HashMap<>();

        List<Map<String, Object>> providers = SubmissionUtilities.getProviders(familySubmission.getInputData());

        Map<String, List<Map<String, Object>>> mergedChildrenAndSchedules =
                getRelatedChildrenSchedulesForEachProvider(familySubmission.getInputData());

        if (mergedChildrenAndSchedules.isEmpty()) {
            return additionalPDFs;
        }

        List<String> providerSchedulesByUuid = mergedChildrenAndSchedules.keySet().stream().toList();

        if (providerSchedulesByUuid.size() > 1) {
            for (int i = 1; i < providerSchedulesByUuid.size(); i++) {
                Map<String, Object> currentProvider = SubmissionUtilities.getCurrentProvider(familySubmission.getInputData(),
                        providerSchedulesByUuid.get(i));

                Map<String, SubmissionField> submissionFields = new HashMap<>();


                List<Map<String, Object>> listOfChildcareSchedulesForCurrentProvider =
                        mergedChildrenAndSchedules.get(providerSchedulesByUuid.get(i));

                for (int j = 0; j < listOfChildcareSchedulesForCurrentProvider.size(); j++) {

                    submissionFields.putAll(
                            needChildcareChildrenPreparer.prepareChildCareSchedule(
                                    listOfChildcareSchedulesForCurrentProvider.get(j),
                                    j + 1));
                }

                Optional<Submission> providerSubmissionOptional = Optional.empty();

                if (currentProvider.containsKey("providerResponseSubmissionId")) {
                    UUID providerUUID = UUID.fromString(currentProvider.get(
                            "providerResponseSubmissionId").toString());
                    providerSubmissionOptional =
                            submissionRepositoryService.findById(providerUUID);
                }

                if (providerSubmissionOptional.isPresent()) {
                    Submission providerSubmission = providerSubmissionOptional.get();
                    if (ProviderSubmissionUtilities.isProviderRegistering(providerSubmission)) {
                        submissionFields.putAll(mapProviderRegistrationData(providerSubmission.getInputData()));
                    }
                    submissionFields.putAll(
                            providerApplicationPreparerHelper.prepareSubmissionFields(
                                    providerSubmission.getInputData()));
                    submissionFields.put("providerSignatureDate",
                            new SingleField("providerSignatureDate",
                                    providerSignatureDate(providerSubmission.getSubmittedAt()), null));
                } else {
                    submissionFields.putAll(familyIntendedProviderPreparerHelper.prepareSubmissionFields(familySubmission,
                            currentProvider));
                }

                String parentFirstName = (String) familySubmission.getInputData().getOrDefault("parentFirstName", "");
                String parentLastName = (String) familySubmission.getInputData().getOrDefault("parentLastName", "");
                submissionFields.put("parentFullName",
                        new SingleField("parentFullName", String.format("%s, %s", parentLastName, parentFirstName), null));

                submissionFields.put("clientResponseConfirmationCode",
                        new SingleField("clientResponseConfirmationCode", familySubmission.getShortCode(), null));

                DateTimeFormatter receivedTimestampFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy, h:mm a zzz");
                ZoneId chicagoTimeZone = ZoneId.of("America/Chicago");

                Optional<OffsetDateTime> submittedAt = Optional.ofNullable(familySubmission.getSubmittedAt());
                submittedAt.ifPresent(offsetDateTime -> {
                    offsetDateTime.atZoneSameInstant(chicagoTimeZone);
                    String formattedSubmittedAtDate = offsetDateTime.atZoneSameInstant(chicagoTimeZone).format(receivedTimestampFormat);
                    submissionFields.put("receivedTimestamp", new SingleField("receivedTimestamp", formattedSubmittedAtDate, null));
                });

                additionalPDFs.put(getCCMSFileNameForAdditionalProviderPDF(familySubmission.getId(), i, providers.size()),
                        getFilledOutPDF("src/main/resources/pdfs/IL-CCAP-Form-Additional-Provider.pdf",
                                submissionFields.values().stream().toList()));
            }
        }

        return additionalPDFs;
    }


    byte[] getFilledOutPDF(String pathToPDFResource, List<SubmissionField> submissionFields) throws IOException {
        List<PdfField> pdfFields = pdfFieldMapper.map(submissionFields, "gcc");
        File file = pdfFormFiller.fill(pathToPDFResource, pdfFields);
        byte[] pdfByteArray = Files.readAllBytes(file.toPath());
        file.delete();
        return pdfByteArray;
    }

    public byte[] zipped(Map<String, byte[]> multiplePDFs) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Map.Entry<String, byte[]> entry : multiplePDFs.entrySet()) {
                String fileName = entry.getKey();
                byte[] fileContent = entry.getValue();

                ZipEntry zipEntry = new ZipEntry(fileName);
                zos.putNextEntry(zipEntry);
                zos.write(fileContent);
                zos.closeEntry();
            }
            zos.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return baos.toByteArray();
    }

    public Map<String, SubmissionField> mapProviderRegistrationData(Map<String, Object> registeringProviderInputData) {
        Map<String, SubmissionField> providerSubmissionFields = new HashMap<>();
        providerSubmissionFields.putAll(providerRegistrationPreparer.prepareSubmissionFields(registeringProviderInputData));
        providerSubmissionFields.putAll(providerHouseholdMemberPreparer.prepareSubmissionFields(registeringProviderInputData));
        providerSubmissionFields.putAll(providerLanguagesPreparerHelper.prepareSubmissionFields(registeringProviderInputData));
        providerSubmissionFields.putAll(providerTypePreparerHelper.prepareSubmissionFields(registeringProviderInputData));
        providerSubmissionFields.putAll(providerSSNPreparerHelper.prepareSubmissionFields(registeringProviderInputData));

        return providerSubmissionFields;
    }


    private String providerSignatureDate(OffsetDateTime submittedAt) {
        if (submittedAt != null) {
            Optional<LocalDate> providerSignatureDate = Optional.of(LocalDate.from(submittedAt));
            return formatToStringFromLocalDate(providerSignatureDate);
        }
        return "";
    }

}