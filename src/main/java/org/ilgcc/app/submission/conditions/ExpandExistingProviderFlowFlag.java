package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This is a false-Condition to skip the pages that have not yet been implemented.
 */
@Component
public class ExpandExistingProviderFlowFlag implements Condition {

    private Boolean expandExistingProviderFlow;

    public ExpandExistingProviderFlowFlag(
            @Value("${il-gcc.dts.expand-existing-provider-flow}") Boolean expandExistingProviderFlow) {
        this.expandExistingProviderFlow = expandExistingProviderFlow;
    }

    @Override
    public Boolean run(Submission submission) {
        return expandExistingProviderFlow;
    }
}
