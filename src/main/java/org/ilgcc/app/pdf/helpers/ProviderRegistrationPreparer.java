package org.ilgcc.app.pdf.helpers;

import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProviderRegistrationPreparer extends InputDataPreparerHelper {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Map<String, Object> providerInputData) {
        Map<String, SubmissionField> results = new HashMap<>();

        List<String> providerFields = new ArrayList<>(
                Arrays.asList("providerConviction", "providerConvictionExplanation", "providerIdentityCheckDateOfBirthDate"));

        for (String fieldName : providerFields) {
            results.put(fieldName, new SingleField(fieldName, providerInputData.getOrDefault(fieldName, "").toString(), null));
        }

        results.put("providerLicenseNumber", new SingleField("providerLicenseNumber", providerLicense(providerInputData), null));

        // Only registering providers can override the ccap start date and they are required to have a date
        results.put("childcareStartDate",
                new SingleField("childcareStartDate", providerInputData.get("providerCareStartDate").toString(),
                        null));

        return results;
    }

    private String providerLicense(Map<String, Object> providerInputData) {
        String providerHasStateLicense = (String) providerInputData.getOrDefault("providerCurrentlyLicensed", "false");
        String providerLicenseNumber = (String) providerInputData.getOrDefault("providerLicenseNumber", "");
        String providerLicenseState = (String) providerInputData.getOrDefault("providerLicenseState", "");
        if (providerHasStateLicense.equalsIgnoreCase("true")) {
            if (providerLicenseState.isEmpty()) {
                return providerLicenseNumber;
            } else {
                return String.format("%s (%s)", providerLicenseNumber, providerLicenseState);
            }
        }
        return "";
    }
}
