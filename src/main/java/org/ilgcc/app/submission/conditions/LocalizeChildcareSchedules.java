package org.ilgcc.app.submission.conditions;


import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LocalizeChildcareSchedules implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    MessageSource messageSource;

    @Override
    public void run(Submission submission) {
        List<Map<String, Object>> childcareSchedules = (List<Map<String, Object>>) submission.getInputData().get("childcareSchedules");

        childcareSchedules.forEach(childCareSchedule -> {
            List<Map<String,Object>> providerSchedulesTemp = (List) childCareSchedule.getOrDefault("providerSchedules",
                    Collections.EMPTY_LIST);
            providerSchedulesTemp.forEach(childSchedule -> {
                childSchedule.put("childCareScheduleLocalized", localizeChildCareHours(childSchedule));
            });
        });

        submissionRepositoryService.save(submission);
    }

    private String localizeChildCareHours(Map<String, Object> childSchedule) {
        Map<String, String> childCareHours = (Map<String, String>) childSchedule.getOrDefault("childCareHours", Collections.EMPTY_LIST);

        List<String> dateString = new ArrayList<>();
        childCareHours.forEach((key, val) -> {
            String dayKey = String.format("general.week.%s", key);
            dateString.add(String.format("<li>%s %s</li>", messageSource.getMessage(dayKey, null,
                    LocaleContextHolder.getLocale()), val));
        });

        return String.join("", dateString);
    }
}
