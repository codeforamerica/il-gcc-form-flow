package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This is a false-Condition to skip the pages that have not yet been implemented.
 */
@Component
public class AdditionalProviderScreensFlag implements Condition {

    private final String revealAdditionalProviderScreens;

    public AdditionalProviderScreensFlag(
            @Value("${il-gcc.pfc.reveal-additional-provider-screens}") String waitForProviderResponseFlag) {
        this.revealAdditionalProviderScreens = waitForProviderResponseFlag;
    }

    @Override
    public Boolean run(Submission submission) {
        return revealAdditionalProviderScreens.equalsIgnoreCase("true");
    }
}
