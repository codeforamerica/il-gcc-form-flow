package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.enums.ProviderType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetProviderType implements Action {

    @Override
    public void run(Submission submission) {
        Map<String, Object> providerInputData = submission.getInputData();

        if ("true".equals(providerInputData.get("providerCurrentlyLicensed"))) {
            submission.getInputData().put("providerType", licensedProviderType(providerInputData));
        } else if ("false".equals(providerInputData.get("providerCurrentlyLicensed"))) {
            submission.getInputData().put("providerType", unLicensedProviderType(providerInputData));
        }
    }

    private String licensedProviderType(Map<String, Object> inputData) {
        String providerLicensedCareLocation = (String) inputData.getOrDefault("providerLicensedCareLocation", "");
        switch (providerLicensedCareLocation) {
            case "childCareCenter":
                return ProviderType.LICENSED_DAY_CARE_CENTER.name();
              
            case "childCareHome":
                return ProviderType.LICENSED_DAY_CARE_HOME.name();
            case "groupChildCareHome":
                return ProviderType.LICENSED_GROUP_CHILD_CARE_HOME.name();
            default:
                return "";
        }
    }

    private String unLicensedProviderType(Map<String, Object> inputData) {
        if ("License-exempt".equals(inputData.get("providerLicenseExemptType"))) {
            return ProviderType.LICENSE_EXEMPT_CHILD_CARE_CENTER.name();
        } else if ("Self".equals(inputData.get("providerLicenseExemptType"))) {
            String providerLicenseExemptRelationship = (String) inputData.getOrDefault(
                    "providerLicenseExemptRelationship", "");

            if ("Providers home".equals(inputData.get("providerLicenseExemptCareLocation"))) {
                if (providerLicenseExemptRelationship.equals("Relative")) {
                    return ProviderType.LICENSE_EXEMPT_RELATIVE_IN_PROVIDER_HOME.name();
                } else if (providerLicenseExemptRelationship.equals("Not related")) {
                    return ProviderType.LICENSE_EXEMPT_NONRELATIVE_IN_PROVIDER_HOME.name();
                }
            } else if ("Childs home".equals(inputData.get("providerLicenseExemptCareLocation"))) {
                if (providerLicenseExemptRelationship.equals("Relative")) {
                    return ProviderType.LICENSE_EXEMPT_RELATIVE_IN_CHILDS_HOME.name();
                } else if (providerLicenseExemptRelationship.equals("Not related")) {
                    return ProviderType.LICENSE_EXEMPT_NONRELATIVE_IN_CHILDS_HOME.name();
                }
            }
        }
        return "";
    }
}