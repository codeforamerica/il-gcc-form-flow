package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CurrentProviderHasResponded implements Condition {

    @Value("${il-gcc.enable-multiple-providers}")
    private boolean enableMultipleProviders;

    @Override
    public Boolean run(Submission submission) {
        // TODO: Get the current provider that was selected on multiple-providers and confirmed on
        // confirm-provider, and if that provider has a recorded response, this value is true
        boolean currentProviderHasResponded = false;
        return enableMultipleProviders && currentProviderHasResponded;
    }
}
