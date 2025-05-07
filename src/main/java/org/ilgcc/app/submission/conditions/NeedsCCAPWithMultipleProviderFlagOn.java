package org.ilgcc.app.submission.conditions;

import static java.util.Collections.emptyList;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NeedsCCAPWithMultipleProviderFlagOn implements Condition {

    @Value("${il-gcc.enable-multiple-providers}")
    private boolean enableMultipleProviders;
    
    @Override
    public Boolean run(Submission submission, String uuid) {
        var children = (List<Map<String, Object>>) submission.getInputData().getOrDefault("children", emptyList());
        for(var child : children) {
            if(child.get("uuid").equals(uuid)) {
                return enableMultipleProviders && child.getOrDefault("needFinancialAssistanceForChild", "false").equals("true");
            }
        }

        return false;
    }
}
