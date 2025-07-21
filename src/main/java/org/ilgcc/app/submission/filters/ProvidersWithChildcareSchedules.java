package org.ilgcc.app.submission.filters;

import static org.ilgcc.app.utils.SchedulePreparerUtility.getRelatedChildrenSchedulesForProvider;

import formflow.library.config.submission.SubflowRelationshipFilter;
import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ProvidersWithChildcareSchedules implements SubflowRelationshipFilter {

    @Override
    public List<HashMap<String, Object>> filter(List<HashMap<String, Object>> providers, Submission submission) {
        Set<String> providerIdsWithSchedules = getRelatedChildrenSchedulesForProvider(submission.getInputData()).keySet();

        return providers.stream().filter(provider -> providerIdsWithSchedules.contains(provider.get("uuid").toString()))
                .collect(Collectors.toList());
    }
}
