package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HasConfirmedProviderPhoneNumber implements Condition {

    private final boolean enableProviderMessaging;

    public HasConfirmedProviderPhoneNumber(@Value("${il-gcc.enable-provider-messaging}") boolean enableProviderMessaging) {
        this.enableProviderMessaging = enableProviderMessaging;
    }
    @Override
    public Boolean run(Submission submission) {
        return enableProviderMessaging && confirmedPhoneNumber(submission);
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
