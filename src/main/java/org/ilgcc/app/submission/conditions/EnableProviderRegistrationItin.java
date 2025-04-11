package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnableProviderRegistrationItin implements Condition {

    private boolean enableProviderRegistrationWithItin;

    public EnableProviderRegistrationItin(@Value("${il-gcc.enable-provider-registration-with-itin}") boolean enableProviderRegistrationWithItin){
        this.enableProviderRegistrationWithItin = enableProviderRegistrationWithItin;
    }

    @Override
    public Boolean run(Submission submission) {
        return enableProviderRegistrationWithItin;
    }
}
