package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnableProviderMessagingFlagOn implements Condition {

    @Value("${il-gcc.enable-provider-messaging}")
    private boolean enableProviderMessaging;

    @Override
    public Boolean run(Submission submission) {
        return enableProviderMessaging;
    }
}
