package org.ilgcc.app.pdf.helpers;

import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ProviderSSNPreparerHelper extends InputDataPreparerHelper {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Map<String, Object> providerInputData) {
        Map<String, SubmissionField> results = new HashMap<>();

        if (!providerInputData.getOrDefault("providerITIN", "").toString().isBlank()) {
            results.put("providerSSN",
                    new SingleField("providerSSN", (String) providerInputData.get("providerITIN"),
                            null));
        } else if (providerInputData.containsKey("providerIdentityCheckSSN")) {
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
