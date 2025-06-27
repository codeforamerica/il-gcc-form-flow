package org.ilgcc.app.submission.conditions;

import static java.util.Collections.emptyList;
import static org.ilgcc.app.utils.SchedulePreparerUtility.relatedSubflowIterationData;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class DisplaySchedulesSameScreen extends EnableMultipleProviders implements Condition {

    @Override
    public Boolean run(Submission submission, String subflowUuid, String repeatForIterationUuid) {

        List<Map<String, Object>> childcareSchedules = (List) submission.getInputData().get("childcareSchedules");

        //should only run if 2 or more childcare schedules have been entered.
        if (childcareSchedules.size() >= 2) {
            Map<String, Object> currentChildcareSchedule = submission.getSubflowEntryByUuid("childcareSchedules", subflowUuid);

            Map<String, Object> currentProviderSchedule = relatedSubflowIterationData(currentChildcareSchedule, "providerSchedules",
        repeatForIterationUuid);
            String currentProviderUuidOrNoProvider = (String) currentProviderSchedule.get("repeatForValue");
            return super.run(submission) && (getAnyChildcareSchedulesWithTheSameProvider(childcareSchedules, currentProviderUuidOrNoProvider, currentChildcareSchedule).size() == 1);
        }
        return false;
    }

    private List<Map<String, Object>> getAnyChildcareSchedulesWithTheSameProvider(List<Map<String, Object>> childcareSchedules, String currentProviderUuidOrNoProvider, Map<String, Object> currentChildcareSchedule) {
        List<Map <String, Object>> remainingChildcareSchedules = childcareSchedules.stream()
            .filter(childcareSchedule -> !childcareSchedule.equals(currentChildcareSchedule))
            .toList();
        return remainingChildcareSchedules.stream().filter(childcareSchedule -> childcareScheduleIncludesThisProvider(childcareSchedule, currentProviderUuidOrNoProvider)).toList();
    }

    private boolean childcareScheduleIncludesThisProvider(Map<String, Object> childcareSchedule, String providerUuidOrNoProvider) {
        List<Map<String, Object>> providerSchedules = (List<Map<String, Object>>) Optional.ofNullable(childcareSchedule.get("providerSchedules")).orElse(emptyList());
        return providerSchedules.stream().anyMatch(providerSchedule -> providerUuidOrNoProvider.equals(providerSchedule.getOrDefault("repeatForValue", "")));
    }
}
