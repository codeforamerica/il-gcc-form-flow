package org.ilgcc.app.pdf.helpers;

import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.enums.ProviderLanguagesOffered;
import org.springframework.stereotype.Component;

@Component
public class ProviderLanguagesPreparerHelper extends InputDataPreparerHelper {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Map<String, Object> providerInputData) {
        Map<String, SubmissionField> results = new HashMap<>();

        List<String> otherLanguages = new ArrayList<>();
        if (providerInputData.containsKey("providerLanguagesOffered[]")) {
            List<String> providerLanguagesOffered = (List<String>) providerInputData.get("providerLanguagesOffered[]");
            if (providerLanguagesOffered.isEmpty() || providerLanguagesOffered.contains("NONE")) {
                return results;
            }

            for (ProviderLanguagesOffered provider : ProviderLanguagesOffered.values()) {
                if (providerLanguagesOffered.contains(provider.getValue())) {
                    if (!provider.getProviderLanguagePdfFieldValue().equalsIgnoreCase("Other")) {
                        String pdfMapInputName = String.format("providerLanguage%s", provider.getValue());
                        results.put(pdfMapInputName, new SingleField(pdfMapInputName, "true", null));
                    } else {
                        results.put("providerLanguageOther", new SingleField("providerLanguageOther", "true", null));
                        otherLanguages.add(provider.getProviderLanguageOtherDetailPdfFieldValue());
                    }
                }
            }

            if (providerLanguagesOffered.contains("other")) {
                if (otherLanguages.isEmpty()) {
                    results.put("providerLanguageOther", new SingleField("providerLanguageOther", "true", null));
                }
                otherLanguages.add((String) providerInputData.getOrDefault("providerLanguagesOffered_other", ""));
            }

            if (!otherLanguages.isEmpty()) {
                String otherLanguagesList = String.join(", ", otherLanguages);
                results.put("providerLanguageOtherDetail",
                        new SingleField("providerLanguageOtherDetail", otherLanguagesList, null));
            }
            return results;
        }
        return results;
    }
}
