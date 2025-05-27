package org.ilgcc.app.pdf;

import static org.ilgcc.app.utils.FileNameUtility.getCCMSFileNameForAdditionalProviderPDF;
import static org.ilgcc.app.utils.FileNameUtility.getCCMSFileNameForApplicationPDF;

import formflow.library.data.Submission;
import formflow.library.pdf.PDFFormFiller;
import formflow.library.pdf.PdfField;
import formflow.library.pdf.PdfFieldMapper;
import formflow.library.pdf.PdfService;
import formflow.library.pdf.SubmissionField;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.pdf.helpers.FamilyIntendedProviderPreparerHelper;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ILGCCAPPDFService {

    Submission submission;

    @Autowired
    PdfService pdfService;

    @Autowired
    FamilyIntendedProviderPreparerHelper familyIntendedProviderPreparerHelper;

    @Autowired
    PdfFieldMapper pdfFieldMapper;

    @Autowired
    PDFFormFiller pdfFormFiller;

    public ILGCCAPPDFService(Submission submission) {
        this.submission = submission;
    }

    public Map<String, byte[]> generatePDFs(Submission submission) throws IOException {
        Map<String, byte[]> allFiles = new HashMap<>();

        allFiles.put(getCCMSFileNameForApplicationPDF(submission), pdfService.getFilledOutPDF(submission));
        allFiles.putAll(generateAdditionalProviderPDF(submission));

        return allFiles;
    }

    Map<String, byte[]> generateAdditionalProviderPDF(Submission submission) throws IOException {
        Map<String, byte[]> additionalPDFs = new HashMap<>();

        List<Map<String, Object>> providers = SubmissionUtilities.providersList(submission);
        if (providers.size() > 1) {
            for (int i = 1; i < providers.size(); i++) {
                Map<String, Object> currentProvider = providers.get(i);
                // TODO: Add logic for providers responding to the application

                Map<String, SubmissionField> submissionFields = familyIntendedProviderPreparerHelper.prepareSubmissionFields(
                        currentProvider);
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
}