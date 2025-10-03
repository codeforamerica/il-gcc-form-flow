package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.utils.SchedulePreparerUtility.relatedSubflowIterationData;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;


import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
public class DisplaySchedulesSameScreen implements Condition {

    @Override
    public Boolean run(Submission submission, String subflowUuid, String repeatForIterationUuid) {

        List<Map<String, Object>> childcareSchedules = (List) submission.getInputData().get("childcareSchedules");

        //should only run if 2 or more childcare schedules have been entered.
        if (childcareSchedules.size() >= 2) {
            Map<String, Object> currentChildcareSchedule = submission.getSubflowEntryByUuid("childcareSchedules", subflowUuid);

            Map<String, Object> currentProviderSchedule = relatedSubflowIterationData(currentChildcareSchedule,
                    "providerSchedules",
                    repeatForIterationUuid);
            String currentProviderUuidOrNoProvider = (String) currentProviderSchedule.get("repeatForValue");
            List<Map<String, Object>> childcareSchedulesWithTheSameProvider = SubmissionUtilities.getRemainingChildcareSchedulesWithTheSameProvider(
                    childcareSchedules, currentProviderUuidOrNoProvider, currentChildcareSchedule);
            return (!childcareSchedulesWithTheSameProvider.isEmpty() &&
                    hasOnlyOneProviderScheduleForTheSameProvider(childcareSchedulesWithTheSameProvider,
                            currentProviderUuidOrNoProvider));
        }
        return false;
    }

    private static boolean hasOnlyOneProviderScheduleForTheSameProvider(
            List<Map<String, Object>> childcareSchedulesWithSameProvider, String currentProviderUuidOrNoProvider) {
        return childcareSchedulesWithSameProvider.stream().noneMatch(childcareSchedule -> {
            Map<String, Object> providerSchedule = SubmissionUtilities.getProviderScheduleByRepeatForValue(childcareSchedule,
                    currentProviderUuidOrNoProvider);
            if (providerSchedule == null) {
                return true;
            } else {
                return providerSchedule.getOrDefault("sameSchedule", "").equals("false");
            }
        });
    }
}
