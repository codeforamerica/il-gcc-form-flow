package org.ilgcc.app.pdf;

import static org.ilgcc.app.utils.SubmissionUtilities.hasProviderResponse;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ProviderSSNPreparer extends ProviderSubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission familySubmission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();
        if (!hasProviderResponse(familySubmission)) {
            return results;
        }

        Optional<Submission> providerSubmission = setProviderSubmission(familySubmission);
        var providerInputData = providerSubmission.get().getInputData();
        if (providerInputData.containsKey("providerIdentityCheckSSN")) {
            results.put("providerSSN",
                    new SingleField("providerSSN", (String) providerInputData.getOrDefault("providerIdentityCheckSSN", ""),
                            null));
        } else if (providerInputData.containsKey("providerTaxIdSSN")) {
            results.put("providerSSN",
                    new SingleField("providerSSN", (String) providerInputData.getOrDefault("providerTaxIdSSN", ""),
                            null));
        } else {
            return results;
        }
        return results;
    }
}
