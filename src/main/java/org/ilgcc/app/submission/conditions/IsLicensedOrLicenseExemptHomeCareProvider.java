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
import org.springframework.stereotype.Component;

@Component
public class IsLicensedOrLicenseExemptHomeCareProvider implements Condition {
    
    private final List<String> homeCareProviderTypes = List.of(
            LICENSED_DAY_CARE_HOME.name(),
            LICENSED_GROUP_CHILD_CARE_HOME.name(),
            LICENSE_EXEMPT_NONRELATIVE_IN_CHILDS_HOME.name(),
            LICENSE_EXEMPT_NONRELATIVE_IN_PROVIDER_HOME.name(),
            LICENSE_EXEMPT_RELATIVE_IN_CHILDS_HOME.name(),
            LICENSE_EXEMPT_RELATIVE_IN_PROVIDER_HOME.name()
    ); 
    
    @Override
    public Boolean run(Submission submission) {
        return submission.getInputData().containsKey("providerType") && 
                homeCareProviderTypes.contains(submission.getInputData().get("providerType").toString());
    }
}
