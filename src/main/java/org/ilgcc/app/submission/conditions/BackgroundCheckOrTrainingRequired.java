package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.List;
import org.ilgcc.app.utils.enums.ProviderType;
import org.springframework.stereotype.Component;

@Component
public class BackgroundCheckOrTrainingRequired implements Condition {

    final static List providerTypesRequired = List.of(
            ProviderType.LICENSE_EXEMPT_CHILD_CARE_CENTER.name(),
            ProviderType.LICENSE_EXEMPT_RELATIVE_IN_PROVIDER_HOME.name(),
            ProviderType.LICENSE_EXEMPT_NONRELATIVE_IN_PROVIDER_HOME.name(),
            ProviderType.LICENSE_EXEMPT_RELATIVE_IN_CHILDS_HOME.name(),
            ProviderType.LICENSE_EXEMPT_NONRELATIVE_IN_CHILDS_HOME.name()
    );

    @Override
    public Boolean run(Submission submission) {
        String providerType = (String) submission.getInputData().getOrDefault("providerType", "");
        return providerTypesRequired.contains(providerType);
    }
}
