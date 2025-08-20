package org.ilgcc.app.submission.actions;


import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getLocalizedChildCareHours;
import static org.ilgcc.app.utils.SchedulePreparerUtility.relatedSubflowIterationData;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.utils.DateUtilities;
import org.ilgcc.app.utils.SchedulePreparerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class LocalizeChildcareSchedulesAndSaveEarliestCCAPStartDate implements Action {

    static final String INPUT_NAME = "earliestChildcareStartDate";

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    MessageSource messageSource;

    @Override
    public void run(Submission submission) {
        List<Map<String, Object>> childcareSchedules = (List<Map<String, Object>>) submission.getInputData()
                .get("childcareSchedules");

        childcareSchedules.forEach(childCareSchedule -> {
            List<Map<String, Object>> providerSchedulesTemp = (List) childCareSchedule.getOrDefault("providerSchedules",
                    Collections.EMPTY_LIST);
            providerSchedulesTemp.forEach(childSchedule -> {
                childSchedule.put("childCareScheduleLocalized", localizeChildCareHours(childSchedule));
            });
        });

        saveEarliestCCAPStartDatePerProvider(submission);

        submissionRepositoryService.save(submission);
    }

    private String localizeChildCareHours(Map<String, Object> childSchedule) {
        return getLocalizedChildCareHours(childSchedule, messageSource);
    }

    private void saveEarliestCCAPStartDatePerProvider(Submission submission) {
        Map<String, List<Map<String, Object>>> providerSchedules = SchedulePreparerUtility.getRelatedChildrenSchedulesForEachProvider(
                submission.getInputData());

        providerSchedules.forEach((providerId, providerSchedule) -> {

            Map<String,
                    Object> currentProvider = relatedSubflowIterationData(submission.getInputData(), "providers",
                    providerId);
            List<Map<String, Object>> providerSchedulesForThisProvider = providerSchedules.getOrDefault(providerId,
                    Collections.EMPTY_LIST);
            String earliestCCAPDateForProvider = DateUtilities.getEarliestDate(
                    providerSchedulesForThisProvider.stream().map(s -> s.getOrDefault("ccapStartDate", "").toString())
                            .toList());
            currentProvider.put(INPUT_NAME, earliestCCAPDateForProvider);
        });

        submissionRepositoryService.save(submission);

    }
}
