package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.utils.enums.ProviderLanguagesOffered;
import org.springframework.stereotype.Component;

@Component
public class ProviderLanguagesPreparer extends ProviderSubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission familySubmission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();
        Optional<Submission> providerSubmission = getProviderSubmission(familySubmission);
        if (providerSubmission.isEmpty()) {
            return results;
        }

        var providerInputData = providerSubmission.get().getInputData();
        List<String> otherLanguages = new ArrayList<>();
        if (providerInputData.containsKey("providerLanguagesOffered[]")) {
            List<String> providerLanguagesOffered = (List<String>) providerInputData.get("providerLanguagesOffered[]");
            if(providerLanguagesOffered.isEmpty() || providerLanguagesOffered.contains("NONE")){
                return results;
            }

            for (ProviderLanguagesOffered provider : ProviderLanguagesOffered.values()) {
                if(providerLanguagesOffered.contains(provider.getValue())){
                     if(!provider.getProviderLanguagePdfFieldValue().equalsIgnoreCase("Other")){
                            String pdfMapInputName = String.format("providerLanguage%s", provider.getValue());
                            results.put(pdfMapInputName, new SingleField(pdfMapInputName, "true", null));
                     }
                     if(provider.getProviderLanguagePdfFieldValue().equalsIgnoreCase("Other")){
                         results.put("providerLanguageOther", new SingleField("providerLanguageOther", "true", null));
                         otherLanguages.add(provider.getProviderLanguageOtherDetailPdfFieldValue());
                     }
                }
            }

            if(providerLanguagesOffered.contains("other")){
                if (otherLanguages.isEmpty()) {
                    results.put("providerLanguageOther", new SingleField("providerLanguageOther", "true", null));
                }
            }

            if(!otherLanguages.isEmpty()){
                String otherLanguagesList = String.join(", ", otherLanguages);
                results.put("providerLanguageOtherDetail", new SingleField("providerLanguageOtherDetail", otherLanguagesList, null));
            }
            return results;
        }
        return results;
    }
}
