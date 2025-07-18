package org.ilgcc.app.pdf.helpers;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.HashMap;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.springframework.stereotype.Component;

@Component
public class FamilyIntendedProviderPreparerHelper extends InputDataPreparerHelper {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Map<String, Object> familyInputData) {
        Map<String, SubmissionField> results = new HashMap<>();

        if (SubmissionUtilities.isNoProviderSubmission(familyInputData)) {
            results.putAll(prepareNoProviderData());
        } else {
            String submissionStatus = (String) familyInputData.getOrDefault("providerApplicationResponseStatus", "");
            Boolean hasExpired = SubmissionStatus.EXPIRED.name().equals(submissionStatus);
            results.putAll(prepareFamilyIntendedProviderData(familyInputData, hasExpired));
        }

        return results;
    }

    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, Map<String, Object> providerData) {
        Map<String, SubmissionField> results = new HashMap<>();

        if (SubmissionUtilities.isNoProviderSubmission(submission.getInputData())) {
            results.putAll(prepareNoProviderData());
        } else {
            String submissionStatus = (String) submission.getInputData().getOrDefault("providerApplicationResponseStatus", "");
            Boolean hasExpired = SubmissionStatus.EXPIRED.name().equals(submissionStatus);
            results.putAll(prepareFamilyIntendedProviderData(providerData, hasExpired));
        }

        return results;
    }

    private Map<String, SubmissionField> prepareFamilyIntendedProviderData(Map<String, Object> inputData,
            Boolean providerApplicationExpired) {
        Map<String, SubmissionField> results = new HashMap<>();

        String familyIntendedProviderName = inputData.getOrDefault("familyIntendedProviderName", "").toString();

        String childCareProgramName = inputData.getOrDefault("childCareProgramName", "").toString();

        if (!familyIntendedProviderName.isEmpty()) {
            results.put("providerResponseBusinessName", new SingleField("providerResponseBusinessName",
                    familyIntendedProviderName, null));
        }

        if (!childCareProgramName.isEmpty()) {
            results.put("providerResponseBusinessName", new SingleField("providerResponseBusinessName",
                    childCareProgramName, null));
        }

        results.put("providerResponseFirstName", new SingleField("providerResponseFirstName",
                inputData.getOrDefault("providerFirstName", "").toString(), null));
        results.put("providerResponseLastName", new SingleField("providerResponseLastName",
                inputData.getOrDefault("providerLastName", "").toString(), null));
        results.put("providerResponseContactPhoneNumber",
                new SingleField("providerResponseContactPhoneNumber",
                        inputData.getOrDefault("familyIntendedProviderPhoneNumber", "").toString(),
                        null));
        results.put("providerResponseContactEmail",
                new SingleField("providerResponseContactEmail",
                        inputData.getOrDefault("familyIntendedProviderEmail", "").toString(), null));

        results.put("providerMailingStreetAddress1",
                new SingleField("providerMailingStreetAddress1",
                        inputData.getOrDefault("familyIntendedProviderAddress", "").toString(), null));

        results.put("providerMailingCity",
                new SingleField("providerMailingCity",
                        inputData.getOrDefault("familyIntendedProviderCity", "").toString(), null));
        results.put("providerMailingState",
                new SingleField("providerMailingState",
                        inputData.getOrDefault("familyIntendedProviderState", "").toString(), null));

        if (providerApplicationExpired) {
            results.put("providerResponse", new SingleField("providerResponse", "No response from provider", null));
        }

        return results;
    }

    private Map<String, SubmissionField> prepareNoProviderData() {
        Map<String, SubmissionField> results = new HashMap<>();
        results.put("providerResponseBusinessName",
                new SingleField("providerResponseBusinessName", "No qualified provider", null));
        results.put("providerResponseProviderNumber", new SingleField("providerResponseProviderNumber", "460328258720008", null));
        results.put("providerResponse", new SingleField("providerResponse", "No provider chosen", null));
        return results;
    }

}
