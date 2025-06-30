package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getLocalizedChildCareHours;
import static org.ilgcc.app.utils.SchedulePreparerUtility.relatedSubflowIterationData;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class LocalizeSameScreenProviderSchedules implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    MessageSource messageSource;
  @Autowired
  private LocalizeChildcareSchedules localizeChildcareSchedules;


    @Override
    public void run(Submission submission, String subflowUuid, String repeatForIterationUuid) {
        List<Map<String, Object>> childcareSchedules = (List<Map<String, Object>>) submission.getInputData().get("childcareSchedules");

        Map<String, Object> currentChildcareSchedule = submission.getSubflowEntryByUuid("childcareSchedules", subflowUuid);

        Map<String, Object> currentProviderSchedule = relatedSubflowIterationData(currentChildcareSchedule, "providerSchedules",
            repeatForIterationUuid);
        String currentProviderUuidOrNoProvider = (String) currentProviderSchedule.get("repeatForValue");

        List<Map<String, Object>> childCareSchedulesWithMatchingProvider = SubmissionUtilities.getAnyChildcareSchedulesWithTheSameProvider(childcareSchedules,currentProviderUuidOrNoProvider, currentChildcareSchedule);

        childCareSchedulesWithMatchingProvider.forEach(childCareScheduleMatch -> {
            List<Map<String, Object>> providerSchedules = (List<Map<String, Object>>) childCareScheduleMatch.getOrDefault("providerSchedules", Collections.emptyList());
            providerSchedules.stream()
                .filter(childProviderSchedule -> childProviderSchedule.get("repeatForValue").equals(currentProviderUuidOrNoProvider))
                .forEach(childProviderScheduleMatch -> {
                    currentProviderSchedule.put("matchingChild", submission.getSubflowEntryByUuid("children",
                        (String) childCareScheduleMatch.get("childUuid")));
                    currentProviderSchedule.put("matchingProviderScheduleLocalized", getLocalizedChildCareHours(childProviderScheduleMatch, messageSource));
                    currentProviderSchedule.put("matchingProviderSchedule", childProviderScheduleMatch);
                });
        });
        submissionRepositoryService.save(submission);
    }
}
