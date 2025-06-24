package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class ProviderIsCareProgram implements Condition {
   
    @Override
    public Boolean run(Submission submission) {
        return submission.getInputData().getOrDefault("providerType", "")
                .toString()
                .equals("Care Program");
    }
}
