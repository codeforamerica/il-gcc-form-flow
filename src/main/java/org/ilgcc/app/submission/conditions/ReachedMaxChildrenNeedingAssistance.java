package org.ilgcc.app.submission.conditions;

import static java.util.Collections.emptyList;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
public class ReachedMaxChildrenNeedingAssistance implements Condition {

    @Override
    public Boolean run(Submission submission, String uuid) {
        var children = (List<Map<String, Object>>) submission.getInputData().getOrDefault("children", emptyList());
        for (var child : children) {
            if (child.get("uuid").equals(uuid)) {
                if (child.getOrDefault("needFinancialAssistanceForChild", "false").equals("true")) {
                    var childrenNeededAssistance = SubmissionUtilities.getChildrenNeedingAssistance(submission);

                    return childrenNeededAssistance.size() > 4;
                }
            }
        }
        return false;
    }
}
