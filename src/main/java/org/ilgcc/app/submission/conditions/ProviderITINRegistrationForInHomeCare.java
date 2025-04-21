package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.utils.enums.ProviderType.LICENSED_DAY_CARE_HOME;
import static org.ilgcc.app.utils.enums.ProviderType.LICENSED_GROUP_CHILD_CARE_HOME;
import static org.ilgcc.app.utils.enums.ProviderType.LICENSE_EXEMPT_NONRELATIVE_IN_CHILDS_HOME;
import static org.ilgcc.app.utils.enums.ProviderType.LICENSE_EXEMPT_NONRELATIVE_IN_PROVIDER_HOME;
import static org.ilgcc.app.utils.enums.ProviderType.LICENSE_EXEMPT_RELATIVE_IN_CHILDS_HOME;
import static org.ilgcc.app.utils.enums.ProviderType.LICENSE_EXEMPT_RELATIVE_IN_PROVIDER_HOME;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProviderITINRegistrationForInHomeCare implements Condition {
    // registration-home-provider-ssn when itin flag is off
    // registration-home-provider-tax-id when itin flag is on

    final static List providerTypesRequired = List.of(
            LICENSED_DAY_CARE_HOME.name(),
            LICENSED_GROUP_CHILD_CARE_HOME.name(),
            LICENSE_EXEMPT_NONRELATIVE_IN_CHILDS_HOME.name(),
            LICENSE_EXEMPT_NONRELATIVE_IN_PROVIDER_HOME.name(),
            LICENSE_EXEMPT_RELATIVE_IN_CHILDS_HOME.name(),
            LICENSE_EXEMPT_RELATIVE_IN_PROVIDER_HOME.name()
    );

    @Value("${il-gcc.allow-provider-registration-flow}")
    private boolean enableProviderRegistration;

    @Value("${il-gcc.enable-provider-registration-with-itin}")
    private boolean enableProviderRegistrationWithITIN;

    @Override
    public Boolean run(Submission submission) {
        return enableProviderRegistrationWithITIN && enableProviderRegistration && displayScreen(submission);
    }

    private Boolean displayScreen(Submission submission) {
        String providerType = (String) submission.getInputData().getOrDefault("providerType", "");
        return providerTypesRequired.contains(providerType);
    }
}
