package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class DisplaySchedulesStartCareScreen extends EnableMultipleProviders implements Condition {

    @Override
    public Boolean run(Submission submission, String subflowUuid) {
        Map<String, Object> childcareSubflow = submission.getSubflowEntryByUuid("childcareSchedules", subflowUuid);
        return super.run(submission) && hasSelectedOneProvider(childcareSubflow);
    }
    private boolean hasSelectedOneProvider(Map<String, Object> childcareSubflow) {
         return Optional.ofNullable(childcareSubflow.get("currentChildcareProvider[]"))
             .filter(List.class::isInstance)
             .map(list -> ((List<?>) list).stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .anyMatch(item -> !item.equals("NO_PROVIDER")))
             .orElse(false);
    }
}
