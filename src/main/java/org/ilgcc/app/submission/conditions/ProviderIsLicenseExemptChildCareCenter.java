package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.List;
import org.ilgcc.app.utils.enums.ProviderType;
import org.springframework.stereotype.Component;

@Component
public class ProviderIsLicenseExemptChildCareCenter implements Condition {

    final static List<String> providerTypesRequired = List.of(ProviderType.LICENSE_EXEMPT_CHILD_CARE_CENTER.name());


    @Override
    public Boolean run(Submission submission) {
        return displayScreen(submission);
    }

    private Boolean displayScreen(Submission submission) {
        String providerType = (String) submission.getInputData().getOrDefault("providerType", "");
        return providerTypesRequired.contains(providerType);
    }
}
