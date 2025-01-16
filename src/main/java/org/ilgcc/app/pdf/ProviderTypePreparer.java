package org.ilgcc.app.pdf;

import static org.ilgcc.app.utils.SubmissionUtilities.hasProviderResponse;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.utils.enums.ProviderType;
import org.springframework.stereotype.Component;

@Component
public class ProviderTypePreparer extends ProviderSubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission familySubmission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();
        if (!hasProviderResponse(familySubmission)) {
            return results;
        }

        Optional<Submission> providerSubmission = getProviderSubmission(familySubmission);

        var providerInputData = providerSubmission.get().getInputData();

        if (providerInputData.containsKey("providerType")) {
            String providerTypeName = (String) providerInputData.get("providerType");
            String providerTypePDFValue = ProviderType.setPdfFieldNameFromName(providerTypeName);
            if(!providerTypePDFValue.isBlank()){
                results.put("providerType", new SingleField("providerType", providerTypePDFValue, null));
            }
        }
        return results;
    }
}
