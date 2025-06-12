package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnableMultipleProviders implements Condition {

    @Value("${il-gcc.enable-multiple-providers}")
    private boolean enableMultipleProviders;

    @Override
    public Boolean run(Submission submission) {
        return enableMultipleProviders;
    }
    
    @Override
    public Boolean run(Submission submission, String uuid) {
        return enableMultipleProviders;
    }

    @Override
    public Boolean run(Submission submission, String uuid, String repeatsForUui) {
        return enableMultipleProviders;
    }
}
