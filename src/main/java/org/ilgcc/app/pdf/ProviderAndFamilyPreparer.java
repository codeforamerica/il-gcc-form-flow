package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ProviderAndFamilyPreparer extends ProviderSubmissionFieldPreparer {
    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission familySubmission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();
        Map<String, Object> familyInputData = familySubmission.getInputData();
        Optional<Map<String, Object>> providerInputData = setProviderInputData(familySubmission);

        results.put("childcareStartDate",
                new SingleField("childcareStartDate", childcareStartDate(providerInputData, familyInputData), null));

        return results;
    }

    private static String childcareStartDate(Optional<Map<String, Object>> providerInputData,
            Map<String, Object> familyInputData) {
        if (providerInputData.isPresent() && providerInputData.get().containsKey("providerCareStartDate")) {
           String providerCareStartDate = (String) providerInputData.get().get("providerCareStartDate");
            if(!providerCareStartDate.isBlank()){
                return (String) providerInputData.get().get("providerCareStartDate");
            }
        }

        if (familyInputData.containsKey("earliestChildcareStartDate")) {
            return (String) familyInputData.get("earliestChildcareStartDate");
        }

        return "";
    }
}
