package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class HasConfirmedProviderPhoneNumber implements Condition {

    @Override
    public Boolean run(Submission submission) {
        return confirmedPhoneNumber(submission);
    }

    private boolean confirmedPhoneNumber(Submission submission) {
        return "true".equals(submission.getInputData().get("hasConfirmedIntendedProviderPhoneNumber"));
    }

    @Override
    public Boolean run(Submission submission, String subflowUuid) {
        Optional<Map<String, Object>> subflow = Optional.of(submission.getSubflowEntryByUuid("contactProviders", subflowUuid));
        return "true".equals(subflow.get().get("hasConfirmedIntendedProviderPhoneNumber"));
    }
}
