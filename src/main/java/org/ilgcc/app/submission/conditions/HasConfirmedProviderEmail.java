package org.ilgcc.app.submission.conditions;

import formflow.library.data.Submission;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class HasConfirmedProviderEmail extends BasicCondition {

    @Override
    public Boolean run(Submission submission) {
        return run(submission, "hasConfirmedIntendedProviderEmail", "true");
    }

    @Override
    public Boolean run(Submission submission, String subflowUuid) {
        Optional<Map<String, Object>> subflow = Optional.of(submission.getSubflowEntryByUuid("contactProviders", subflowUuid));
        return "true".equals(subflow.get().get("hasConfirmedIntendedProviderEmail"));
    }
}
