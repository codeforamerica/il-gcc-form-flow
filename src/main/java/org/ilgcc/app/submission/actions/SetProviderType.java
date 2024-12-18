package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.enums.ProviderType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetProviderType implements Action {

    @Override
    public void run(Submission submission) {
        var providerInputData = submission.getInputData();

        String providerCurrentlyLicensed = (String) providerInputData.getOrDefault("providerCurrentlyLicensed", "");
        String providerLicensedCareLocation = (String) providerInputData.getOrDefault("providerLicensedCareLocation", "");
        String providerLicenseExemptType = (String) providerInputData.getOrDefault("providerLicenseExemptType", "");

        if (providerCurrentlyLicensed.equals("true") && !providerLicensedCareLocation.isBlank()) {
            switch (providerLicensedCareLocation) {
                case "childCareCenter":
                    submission.getInputData().put("providerType", ProviderType.LICENSED_CHILD_CARE_CENTER);
                    break;
                case "childCareHome":
                    submission.getInputData().put("providerType", ProviderType.LICENSED_CHILD_CARE_HOME);
                    break;
                case "groupChildCareHome":
                    submission.getInputData().put("providerType", ProviderType.LICENSED_GROUP_CHILD_CARE_HOME);
                    break;
                default:
                    break;
            }
        }

        if (providerCurrentlyLicensed.equals("false") && !providerLicenseExemptType.isBlank()) {
            if (providerLicenseExemptType.equals("License-exempt")) {
                submission.getInputData().put("providerType", ProviderType.LICENSE_EXEMPT_CHILD_CARE_CENTER);
            }

            if (providerLicenseExemptType.equals("Self")) {
                String providerLicenseExemptCareLocation = (String) providerInputData.getOrDefault(
                        "providerLicenseExemptCareLocation", "");
                String providerLicenseExemptRelationship = (String) providerInputData.getOrDefault(
                        "providerLicenseExemptRelationship", "");

                if (providerLicenseExemptCareLocation.equals("Providers home")) {
                    if (providerLicenseExemptRelationship.equals("Relative")) {
                        submission.getInputData().put("providerType", ProviderType.LICENSE_EXEMPT_RELATIVE_IN_PROVIDER_HOME);
                    }
                    if (providerLicenseExemptRelationship.equals("Not related")) {
                        submission.getInputData().put("providerType", ProviderType.LICENSE_EXEMPT_NONRELATIVE_IN_PROVIDER_HOME);
                    }
                }
                if (providerLicenseExemptCareLocation.equals("Childs home")) {
                    if (providerLicenseExemptRelationship.equals("Relative")) {
                        submission.getInputData().put("providerType", ProviderType.LICENSE_EXEMPT_RELATIVE_IN_CHILDS_HOME);
                    }

                    if (providerLicenseExemptRelationship.equals("Not related")) {
                        submission.getInputData().put("providerType", ProviderType.LICENSE_EXEMPT_RELATIVE_IN_CHILDS_HOME);
                    }
                }
            }
        }
    }
}
