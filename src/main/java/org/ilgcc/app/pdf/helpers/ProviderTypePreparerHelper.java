package org.ilgcc.app.pdf.helpers;

import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.HashMap;
import java.util.Map;
import org.ilgcc.app.utils.enums.ProviderType;
import org.springframework.stereotype.Component;

@Component
public class ProviderTypePreparerHelper extends InputDataPreparerHelper {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Map<String, Object> providerInputData) {
        Map<String, SubmissionField> results = new HashMap<>();

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
