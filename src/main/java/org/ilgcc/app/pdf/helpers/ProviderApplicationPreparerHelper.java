package org.ilgcc.app.pdf.helpers;

import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.enums.ProviderDenialReason;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProviderApplicationPreparerHelper extends InputDataPreparerHelper {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Map<String, Object> providerInputData) {
        Map<String, SubmissionField> results = new HashMap<>();

        List<String> providerFields = new ArrayList<>(
                Arrays.asList("providerResponseFirstName", "providerResponseLastName", "providerResponseBusinessName",
                        "providerResponseContactPhoneNumber", "providerResponseContactEmail", "providerConviction",
                        "providerConvictionExplanation", "providerIdentityCheckDateOfBirthDate", "providerResponseProviderNumber",
                        "providerTaxIdFEIN", "providerResponseServiceStreetAddress1", "providerResponseServiceStreetAddress2",
                        "providerResponseServiceCity", "providerResponseServiceState", "providerResponseServiceZipCode",
                        "providerMailingStreetAddress1", "providerMailingStreetAddress2", "providerMailingCity",
                        "providerMailingState", "providerMailingZipCode", "childcareStartDate"));

        for (String fieldName : providerFields) {
            results.put(fieldName, new SingleField(fieldName, providerInputData.getOrDefault(fieldName, "").toString(), null));
        }

        Map<String, String> client = (Map<String, String>) providerInputData.getOrDefault("clientResponse",
                new HashMap<String, String>());
        results.put("clientResponseConfirmationCode",
                new SingleField("clientResponseConfirmationCode", client.getOrDefault("clientResponseConfirmationCode", ""),
                        null));

        results.put("providerLicenseNumber", new SingleField("providerLicenseNumber", providerLicense(providerInputData), null));
        results.put("providerSignature", new SingleField("providerSignature", providerSignature(providerInputData), null));
        results.put("providerResponse", new SingleField("providerResponse", providerResponse(providerInputData), null));

        return results;
    }


    private String providerSignature(Map<String, Object> providerInputData) {
        String providerSignature = (String) providerInputData.getOrDefault("providerSignedName", "");
        if (!providerSignature.isEmpty()) {
            return providerSignature;
        }
        String firstname = (String) providerInputData.getOrDefault("providerResponseFirstName", "");
        String lastName = (String) providerInputData.getOrDefault("providerResponseLastName", "");
        String businessName = (String) providerInputData.getOrDefault("providerResponseBusinessName", "");

        if (businessName.isEmpty()) {
            return String.format("%s %s", firstname, lastName);
        } else {
            return String.format("%s %s, %s", firstname, lastName, businessName);
        }
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

    private String providerResponse(Map<String, Object> providerInputData) {
        boolean returningProvider = providerInputData.getOrDefault("providerPaidCcap", "true").toString().equals("true");
        boolean hasFEIN = providerInputData.containsKey("providerTaxIdFEIN");
        boolean hasProviderNumber = providerInputData.containsKey("providerResponseProviderNumber");
        if ("false".equals(providerInputData.get("providerResponseAgreeToCare"))) {
            if (providerInputData.get("providerResponseDenyCareReason") != null && !providerInputData.get(
                    "providerResponseDenyCareReason").toString().isEmpty()) {
                return ProviderDenialReason.valueOf(providerInputData.get("providerResponseDenyCareReason").toString())
                        .getPdfValue();
            } else {
                return ProviderDenialReason.NO_REASON_SELECTED.getPdfValue();
            }
        }

        if (returningProvider && !hasFEIN && !hasProviderNumber) {
            return "Unable to identify provider - no response to care arrangement";
        }

        return "";
    }
}
