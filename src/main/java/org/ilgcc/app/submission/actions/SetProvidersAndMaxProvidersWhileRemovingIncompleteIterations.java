package org.ilgcc.app.submission.actions;

import static java.util.Collections.emptyList;
import static org.ilgcc.app.utils.SubmissionUtilities.getChildrenNeedingAssistance;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetProvidersAndMaxProvidersWhileRemovingIncompleteIterations implements Action {

    private final SubmissionRepositoryService submissionRepositoryService;

    public SetProvidersAndMaxProvidersWhileRemovingIncompleteIterations(
            SubmissionRepositoryService submissionRepositoryService) {
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission submission) {
        List<Map<String, Object>> providers = (List<Map<String, Object>>) submission.getInputData().getOrDefault(
                "providers",
                emptyList());

        if (providers.isEmpty()) {
            submission.getInputData().remove("childcareSchedules");
        } else {

            providers = providers.stream()
                    .filter(providerIteration -> (boolean) providerIteration.getOrDefault("iterationIsComplete", false))
                    .collect(Collectors.toList());

            if (submission.getInputData().containsKey("childcareSchedules")) {
                List<Map<String, Object>> childcareSchedules = (List<Map<String, Object>>) submission.getInputData()
                        .get("childcareSchedules");

                Set<String> validProviderUuids = providers.stream()
                        .map(provider -> (String) provider.get("uuid"))
                        .collect(Collectors.toSet());

                validProviderUuids.add("NO_PROVIDER");

                childcareSchedules.removeIf(schedule -> {
                    List<String> childcareProvidersForCurrentChild = (List<String>) schedule.getOrDefault(
                            "childcareProvidersForCurrentChild[]", new ArrayList<>());

                    if (childcareProvidersForCurrentChild == null || childcareProvidersForCurrentChild.isEmpty()) {
                        return true;
                    } else {
                        for (String childcareProvider : childcareProvidersForCurrentChild) {
                            if (!validProviderUuids.contains(childcareProvider)) {
                                return true;
                            }
                        }
                        return false;
                    }
                });
            }
        }
        
        submission.getInputData().put("providers", providers);
        submission.getInputData().put("maxProvidersAllowed", getMaxProvidersAllowed(submission));

        submissionRepositoryService.save(submission);
    }


    private int getMaxProvidersAllowed(Submission submission) {
        // Families with only 1 child never get to see the screen that would set choseProviderForEveryChildInNeedOfCare
        if ("true".equals(submission.getInputData().get("choseProviderForEveryChildInNeedOfCare"))
                || getChildrenNeedingAssistance(submission.getInputData()).size() == 1) {
            return 3;
        } else {
            return 2;
        }
    }
}
