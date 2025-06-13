package org.ilgcc.app.pdf;

import static org.ilgcc.app.utils.FileNameUtility.getCCMSFileNameForAdditionalProviderPDF;
import static org.ilgcc.app.utils.FileNameUtility.getCCMSFileNameForApplicationPDF;
import static org.ilgcc.app.utils.SchedulePreparerUtility.getRelatedChildrenSchedulesForProvider;

import formflow.library.data.Submission;
import formflow.library.pdf.PDFFormFiller;
import formflow.library.pdf.PdfField;
import formflow.library.pdf.PdfFieldMapper;
import formflow.library.pdf.PdfService;
import formflow.library.pdf.SubmissionField;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.ilgcc.app.pdf.helpers.FamilyIntendedProviderPreparerHelper;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MultiProviderPDFService {

    Submission submission;

    @Autowired
    PdfService pdfService;

    @Autowired
    FamilyIntendedProviderPreparerHelper familyIntendedProviderPreparerHelper;

    @Autowired
    PdfFieldMapper pdfFieldMapper;

    @Autowired
    PDFFormFiller pdfFormFiller;

    @Autowired
    NeedChildcareForChildren needChildcareForChildrenPreparer;

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

        List<Map<String, Object>> providers = SubmissionUtilities.providersList(familySubmission);
        if (providers.size() > 1) {
            for (int i = 1; i < providers.size(); i++) {
                Map<String, Object> currentProvider = providers.get(i);
                // TODO: Add logic for providers responding to the application

                Map<String, SubmissionField> submissionFields = familyIntendedProviderPreparerHelper.prepareSubmissionFields(
                        currentProvider);

                List<Map<String, Object>> mergedChildrenAndSchedules =
                        getRelatedChildrenSchedulesForProvider(familySubmission.getInputData(),
                                currentProvider.get(
                                        "familyIntendedProviderName").toString());

                if (!mergedChildrenAndSchedules.isEmpty()) {
                    for (int j = 0; j < mergedChildrenAndSchedules.size(); j++) {
                        submissionFields.putAll(needChildcareForChildrenPreparer.prepareChildCareSchedule(mergedChildrenAndSchedules.get(j),
                                j));
                    }
                }
                additionalPDFs.put(getCCMSFileNameForAdditionalProviderPDF(submission.getId(), i, providers.size() - 1),
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

}