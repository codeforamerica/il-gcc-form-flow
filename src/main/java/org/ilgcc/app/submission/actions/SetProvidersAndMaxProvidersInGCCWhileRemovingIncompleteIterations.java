package org.ilgcc.app.submission.actions;

import static java.util.Collections.emptyList;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetProvidersAndMaxProvidersInGCCWhileRemovingIncompleteIterations implements Action {

    private final SubmissionRepositoryService submissionRepositoryService;

    public SetProvidersAndMaxProvidersInGCCWhileRemovingIncompleteIterations(
            SubmissionRepositoryService submissionRepositoryService) {
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission submission) {
        Map<String, Object> familyInputData = submission.getInputData();
        var subflowData = (List<Map<String, Object>>) submission.getInputData().getOrDefault("providers", emptyList());
        if (!subflowData.isEmpty()) {
            log.info("Removing incomplete provider iterations from submission {}", submission.getId());
            subflowData.removeIf(providerIteration -> !(boolean) providerIteration.getOrDefault("iterationIsComplete", false));
            submissionRepositoryService.save(submission);
        }

        List<Map<String, Object>> careProviders = (List<Map<String, Object>>) familyInputData.getOrDefault("providers",
                emptyList());
        boolean maxProvidersReached = hasMaxProvidersBeenReached(familyInputData, careProviders);
        submission.getInputData().put("maxProvidersReached", maxProvidersReached);
        submission.getInputData().put("careProviders", careProviders);
    }


    private boolean hasMaxProvidersBeenReached(Map<String, Object> familyInputData, List<Map<String, Object>> careProviders) {
        if (familyInputData.getOrDefault("choseProviderForEveryChildInNeedOfCare", "false").equals("true")) {
            return careProviders.size() > 2;
        } else {
            return careProviders.size() > 1;
        }
    }
}
