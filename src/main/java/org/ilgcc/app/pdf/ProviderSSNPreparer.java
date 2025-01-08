package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProviderSSNPreparer implements SubmissionFieldPreparer {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;
    @Autowired
    private ProviderSubmissionUtilities providerSubmissionUtilities;

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission familySubmission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();
        Optional<Submission> providerSubmission = providerSubmissionUtilities.getProviderSubmissionFromId(
                submissionRepositoryService, familySubmission);
        if (providerSubmission.isEmpty()) {
            return results;
        }
        var providerInputData = providerSubmission.get().getInputData();
        if (providerInputData.containsKey("providerIdentityCheckSSN")) {
                results.put("providerSSN",
                    new SingleField("providerSSN", (String) providerInputData.getOrDefault("providerIdentityCheckSSN", ""),
                        null));
        }else if(providerInputData.containsKey("providerTaxIdSSN")){
                results.put("providerSSN",
                    new SingleField("providerSSN", (String) providerInputData.getOrDefault("providerTaxIdSSN", ""),
                        null));
        }else{
            return results;
        }
        return results;
    }
}
