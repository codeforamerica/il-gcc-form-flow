package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class FamilySelectedAProvider implements Condition {

    @Override
    public Boolean run(Submission submission, String subflowUuid) {
        Map<String, Object> childcareSubflow = submission.getSubflowEntryByUuid("childcareSchedules", subflowUuid);
        List<String> selectedChildcareProviders = (List) childcareSubflow.getOrDefault("childcareProvidersForCurrentChild[]",
                Collections.EMPTY_LIST);
        return !selectedChildcareProviders.equals(List.of("NO_PROVIDER"));
    }
}
