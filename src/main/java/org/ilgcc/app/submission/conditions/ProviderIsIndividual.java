package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ProviderIsIndividual implements Condition {
   
    @Override
    public Boolean run(Submission submission, String iterationId) {
        Map<String, Object> subflowEntry = submission.getSubflowEntryByUuid("providers", iterationId);
        return subflowEntry.getOrDefault("providerType", "")
                .toString()
                .equals("Individual");
    }
}
