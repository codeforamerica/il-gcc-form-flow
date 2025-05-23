package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PDFFormFiller;
import formflow.library.pdf.PdfField;
import formflow.library.pdf.PdfFieldMapper;
import formflow.library.pdf.PdfService;
import formflow.library.pdf.SubmissionField;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.utils.ByteArrayMultipartFile;
import org.ilgcc.app.utils.FileNameUtility;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ILGCCAPPDFService {

    Submission submission;

    private final static String CONTENT_TYPE = "application/pdf";

    @Autowired
    PdfService pdfService;

    @Autowired
    CustomPdfService customPdfService;

    @Autowired
    ProviderSubmissionFieldPreparer providerSubmissionFieldPreparer;

    @Autowired
    PdfFieldMapper pdfFieldMapper;

    @Autowired
    PDFFormFiller pdfFormFiller;

    public ILGCCAPPDFService(Submission submission) {
        this.submission = submission;
    }
    public List<byte[]> generatePDFs(Submission submission) throws IOException {
        List<byte[]> multipleFiles = new ArrayList<>();

        multipleFiles.add(pdfService.getFilledOutPDF(submission));
        multipleFiles.addAll(generateAdditionalProviderPDF(submission));

        return multipleFiles;
    }

    List<byte[]> generateAdditionalProviderPDF(Submission submission){
        List<byte[]> multipleFiles = new ArrayList<>();

        List<Map<String, Object>> providers = SubmissionUtilities.providersList(submission);
        if (providers.size() > 1) {
            for (int i = 1; i < providers.size(); i++) {
                Map<String, Object> currentProvider = providers.get(i);
//                List<SubmissionField> submissionFields = providerSubmissionFieldPreparer.prepareSubmissionFields(submission, pdf)
                //Update with the data generated per currentProvider.
                List<SubmissionField> submissionFields = new ArrayList<>();
                multipleFiles.add(getFilledOutPDF("additionalPDF", submissionFields));
            }
        }

        return multipleFiles;
    }


    byte[] getFilledOutPDF(String pathToPDFResource, List<SubmissionField> submissionFields) throws IOException {
        List<PdfField> pdfFields = pdfFieldMapper.map(submissionFields, "gcc");
        File file = pdfFormFiller.fill(pathToPDFResource, pdfFields);
        byte[] pdfByteArray = Files.readAllBytes(file.toPath());
        file.delete();
        return pdfByteArray;
    }
}