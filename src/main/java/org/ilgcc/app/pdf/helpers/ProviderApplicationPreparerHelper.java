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
                        "providerResponseContactPhoneNumber", "providerResponseContactEmail", "providerResponseProviderNumber",
                        "providerTaxIdFEIN", "providerResponseServiceStreetAddress1", "providerResponseServiceStreetAddress2",
                        "providerResponseServiceCity", "providerResponseServiceState", "providerResponseServiceZipCode",
                        "providerMailingStreetAddress1", "providerMailingStreetAddress2", "providerMailingCity",
                        "providerMailingState", "providerMailingZipCode"));

        for (String fieldName : providerFields) {
            results.put(fieldName, new SingleField(fieldName, providerInputData.getOrDefault(fieldName, "").toString(), null));
        }

        results.put("providerResponse", new SingleField("providerResponse", providerResponse(providerInputData), null));

        return results;
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
