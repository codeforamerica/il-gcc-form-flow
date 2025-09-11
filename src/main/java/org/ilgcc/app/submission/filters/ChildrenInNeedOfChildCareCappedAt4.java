package org.ilgcc.app.submission.filters;

import formflow.library.config.submission.SubflowRelationshipFilter;
import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ChildrenInNeedOfChildCareCappedAt4 implements SubflowRelationshipFilter {

    @Override
    public List<HashMap<String, Object>> filter(List<HashMap<String, Object>> children, Submission submission) {
        return children.stream().filter(child -> child.getOrDefault("needFinancialAssistanceForChild", "false").equals("true"))
                .limit(4)
                .collect(Collectors.toList());
    }
}
