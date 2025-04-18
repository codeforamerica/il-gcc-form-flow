package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.utils.enums.ProviderType.LICENSE_EXEMPT_NONRELATIVE_IN_PROVIDER_HOME;
import static org.ilgcc.app.utils.enums.ProviderType.LICENSE_EXEMPT_RELATIVE_IN_PROVIDER_HOME;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;

import java.util.List;

public class SkipIfHomeCareProviderWithITIN implements Condition {

    private final List<String> homeCareProviderTypes = List.of(
            LICENSE_EXEMPT_NONRELATIVE_IN_PROVIDER_HOME.toString(),
            LICENSE_EXEMPT_RELATIVE_IN_PROVIDER_HOME.toString()
    );

    @Override
    public Boolean run(Submission submission) {

        return isHomeCareProvider(submission) && submission.getInputData().getOrDefault("providerITIN", "").toString().isBlank();
    }

    private Boolean isHomeCareProvider(Submission submission){
        return submission.getInputData().containsKey("providerType") &&
                homeCareProviderTypes.contains(submission.getInputData().get("providerType").toString());
    }
}