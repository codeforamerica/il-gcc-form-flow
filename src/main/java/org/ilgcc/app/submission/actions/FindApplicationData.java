package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.setChildData;
import static org.ilgcc.app.utils.SchedulePreparerUtility.getRelatedChildrenSchedulesForProvider;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FindApplicationData implements Action {

    @Value("${il-gcc.enable-multiple-providers}")
    private boolean enableMultipleProviders;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    MessageSource messageSource;


    @Override
    public void run(Submission providerSubmission) {
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionId.isPresent()) {
            Optional<Submission> familySubmission = submissionRepositoryService.findById(familySubmissionId.get());
            providerSubmission.getInputData()
                    .put("clientResponse", ProviderSubmissionUtilities.getFamilySubmission(familySubmission));

            List<Map<String, Object>> childData;

            if (enableMultipleProviders) {
                childData = new ArrayList<>();
                Map<String, List<Map<String, Object>>> mergedChildrenAndSchedules =
                        getRelatedChildrenSchedulesForProvider(familySubmission.get().getInputData());

                String currentProviderUuid = (String) providerSubmission.getInputData().getOrDefault("currentProviderUuid",
                        "");

                if (!currentProviderUuid.isBlank()) {
                    List<Map<String, Object>> childrenData = (mergedChildrenAndSchedules.get(currentProviderUuid));
                    childrenData.forEach(child -> {
                        childData.add(setChildData(child));
                    });
                }
            } else {
                childData = ProviderSubmissionUtilities.getChildrenData(
                        familySubmission.get().getInputData());
            }

            List<Map<String, String>> childDataToDisplay = new ArrayList<>();

            childData.forEach(child -> {
                Map<String, String> newChild = new HashMap<>();
                newChild.put("childName", child.get("childName").toString());
                newChild.put("childStartDate", child.getOrDefault("childStartDate", "n/a").toString());
                newChild.put("childAge", localizeChildAge(child));
                newChild.put("childCareHours", localizeChildCareHours(child));

                childDataToDisplay.add(newChild);
            });

            providerSubmission.getInputData().put("clientResponseChildren", childDataToDisplay);
            submissionRepositoryService.save(providerSubmission);
        }
    }

    private String localizeChildAge(Map<String, Object> child) {
        Map<String, String> childAge = (Map<String, String>) child.get("childAge");
        if (childAge.containsKey("months")) {
            return messageSource.getMessage("provider-response-response.time.months",
                    new Object[]{String.valueOf(childAge.get("months"))},
                    LocaleContextHolder.getLocale());
        } else if (childAge.containsKey("years")) {
            return String.valueOf(childAge.get("years"));
        } else {
            return "n/a";
        }
    }

    private String localizeChildCareHours(Map<String, Object> child) {
        Map<String, String> childCareHours = (Map<String, String>) child.get("childCareHours");

        List<String> dateString = new ArrayList<>();
        childCareHours.forEach((key, val) -> {
            String dayKey = String.format("general.week.%s", key);
            dateString.add(String.format("<li>%s %s</li>", messageSource.getMessage(dayKey, null,
                    LocaleContextHolder.getLocale()), val));
        });

        return String.join("", dateString);
    }
}
