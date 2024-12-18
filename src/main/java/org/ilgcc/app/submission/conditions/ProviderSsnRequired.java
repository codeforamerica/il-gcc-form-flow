package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProviderSsnRequired implements Condition {
    // Licensed Child Care Home
    // Licensed Group Child Care Home
    // License-exempt Relative (In provider's)
    // License-exempt Non-Relative (In providers Home)
    // License-exempt Relative (In childs home)
    // License-exempt Non-Relative (In childs home)

    @Value("${il-gcc.allow-provider-registration-flow}")
    private boolean enableProviderRegistration;

    @Override
    public Boolean run(Submission submission) {
        return enableProviderRegistration && displayScreen(submission);
    }

    private Boolean displayScreen(Submission submission){
        // suggested implementation: check if the provider type is in the list
        return true;
    }
}
