package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnableProviderRegistration implements Condition {

    @Value("${il-gcc.allow-provider-registration-flow}")
    private boolean enableProviderRegistration;

    @Override
    public Boolean run(Submission submission) {
        return false && enableProviderRegistration;
    }
}
