package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnableProviderResponseFEIN implements Condition {

    private final boolean enableProviderResponseFEIN;

    public EnableProviderResponseFEIN(
            @Value("${il-gcc.enable-provider-response-with-fein}") boolean enableProviderResponseFEIN) {
        this.enableProviderResponseFEIN = enableProviderResponseFEIN;
    }

    @Override
    public Boolean run(Submission submission) {
        return enableProviderResponseFEIN;
    }
}
