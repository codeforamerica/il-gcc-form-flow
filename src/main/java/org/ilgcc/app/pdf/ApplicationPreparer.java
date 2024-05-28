package org.ilgcc.app.pdf;

import static org.ilgcc.app.utils.SubmissionUtilities.formatToStringFromLocalDate;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import net.bytebuddy.asm.Advice.Local;
import org.springframework.stereotype.Component;

@Component
public class ApplicationPreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();

        var inputData = submission.getInputData();

        var partnerSignature = inputData.getOrDefault("partnerSignedName", "");

        if (!partnerSignature.equals("")) {
            var partnerSignatureDate = LocalDate.from(submission.getSubmittedAt());
            results.put("partnerSignedAt", new SingleField("partnerSignedAt", formatToStringFromLocalDate(partnerSignatureDate), null));
        }

        return results;
    }
}