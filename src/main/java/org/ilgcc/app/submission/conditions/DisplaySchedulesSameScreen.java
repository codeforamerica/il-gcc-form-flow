package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.utils.SchedulePreparerUtility.relatedSubflowIterationData;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class DisplaySchedulesSameScreen extends EnableMultipleProviders implements Condition {

    @Override
    public Boolean run(Submission submission, String subflowUuid, String repeatForIterationUuid) {
        /*
          Steps:
          2. Get the current provider from the provider schedules
          3. Get all the children in need of chi
         */
        List<Map<String, Object>> childcareSchedules = (List) submission.getInputData().get("childcareSchedules");
        Boolean shouldNavigateToSameScreen = false;

        //should only run if 2 or more childcare schedules have been entered.
        if (childcareSchedules.size() >= 2) {
            Map<String, Object> currentChildcareSchedule = submission.getSubflowEntryByUuid("childcareSchedules", subflowUuid);

            Map<String, Object> currentProviderSchedule = relatedSubflowIterationData(currentChildcareSchedule, "providerSchedules",
        repeatForIterationUuid);
            String currentProviderUuidOrNoProvider = (String) currentProviderSchedule.get("repeatForValue");

        }
        return super.run(submission) && (shouldNavigateToSameScreen);
    }

    private boolean hasOnlyOneChildWithTheSameProviderSelected (List<Map<String, Object>> childcareSchedules, String currentProviderUuidOrNoProvider, Map<String, Object> currentChildcareSchedule) {
        List<Map <String, Object>> remainingChildcareSchedules = childcareSchedules.stream()
            .filter(childcareSchedule -> !childcareSchedule.equals(currentChildcareSchedule))
            .toList();
        return false;
    }
    private boolean providerSelected(Map<String, Object> currentProviderSchedule){
        return !currentProviderSchedule.get("repeatForValue").equals("NO_PROVIDER");
    }
}
