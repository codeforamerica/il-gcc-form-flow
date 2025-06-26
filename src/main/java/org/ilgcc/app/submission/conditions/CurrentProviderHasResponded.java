package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CurrentProviderHasResponded implements Condition {

    @Value("${il-gcc.enable-multiple-providers}")
    private boolean enableMultipleProviders;

    @Override
    public Boolean run(Submission submission) {
        ProviderSubmissionUtilities.getCurrentProvider(submission);

        Map<String, Object> currentProvider = ProviderSubmissionUtilities.getCurrentProvider(submission);
        String providerResponseStatus = currentProvider != null ? (String) currentProvider.get("providerResponseStatus") : null;
        boolean currentProviderHasResponded = SubmissionStatus.RESPONDED.name().equals(providerResponseStatus);
        return enableMultipleProviders && currentProviderHasResponded;
    }
}
