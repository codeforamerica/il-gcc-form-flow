package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnableProviderRegistrationITIN implements Condition {

    private boolean enableProviderRegistrationWithITIN;

    public EnableProviderRegistrationITIN(@Value("${il-gcc.enable-provider-registration-with-itin}") boolean enableProviderRegistrationWithITIN){
        this.enableProviderRegistrationWithITIN = enableProviderRegistrationWithITIN;
    }

    @Override
    public Boolean run(Submission submission) {
        return enableProviderRegistrationWithITIN;
    }
}
