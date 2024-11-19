package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This is a false-Condition to skip the pages that have not yet been implemented.
 */
@Component
public class ShowNoProviderFlow implements Condition {

    private Boolean showNoProviderFlow;

    public ShowNoProviderFlow(
            @Value("${il-gcc.show-no-provider-flow}") Boolean showNoProviderFlow) {
        this.showNoProviderFlow = showNoProviderFlow;
    }

    @Override
    public Boolean run(Submission submission) {
        return showNoProviderFlow && (submission.getInputData().containsKey("hasChosenProvider") && !Boolean.parseBoolean(submission.getInputData().get("hasChosenProvider").toString()));
    }
}
