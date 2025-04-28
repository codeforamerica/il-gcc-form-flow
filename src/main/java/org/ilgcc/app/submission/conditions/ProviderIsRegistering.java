package org.ilgcc.app.submission.conditions;

import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProviderIsRegistering extends BasicCondition {

    @Value("${il-gcc.allow-provider-registration-flow}")
    private boolean enableProviderRegistration;

    @Override
    public Boolean run(Submission submission) {
        // providerPaidCcap doesn't exist when enable provider registration flag is turned off.
        boolean providerNotPaidByCCAP = submission.getInputData().getOrDefault("providerPaidCcap", "true").toString().equals("false");

        return enableProviderRegistration && providerNotPaidByCCAP;
    }
}
