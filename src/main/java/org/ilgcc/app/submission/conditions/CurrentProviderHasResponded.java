package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.springframework.stereotype.Component;

@Component
public class CurrentProviderHasResponded implements Condition {

    @Override
    public Boolean run(Submission submission) {
        Map<String, Object> currentProvider = ProviderSubmissionUtilities.getCurrentProvider(submission);
        return currentProvider != null && SubmissionStatus.RESPONDED.name()
                .equals(currentProvider.get("providerApplicationResponseStatus"));

    }
}
