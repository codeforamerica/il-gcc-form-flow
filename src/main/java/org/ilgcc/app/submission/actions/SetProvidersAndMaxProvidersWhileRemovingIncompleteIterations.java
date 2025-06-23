package org.ilgcc.app.submission.actions;

import static java.util.Collections.emptyList;
import static org.ilgcc.app.utils.SubmissionUtilities.getChildrenNeedingAssistance;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.List;
import java.util.Map;
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
    Map<String, Object> familyInputData = submission.getInputData();
    var subflowData = (List<Map<String, Object>>) submission.getInputData().getOrDefault("providers", emptyList());
    if (!subflowData.isEmpty()) {
      subflowData.removeIf(providerIteration -> !(boolean) providerIteration.getOrDefault("iterationIsComplete", false));
      submissionRepositoryService.save(submission);
    }

    List<Map<String, Object>> providers = (List<Map<String, Object>>) familyInputData.getOrDefault("providers",
        emptyList());
    submission.getInputData().put("maxProvidersAllowed", getMaxProvidersAllowed(submission));
    submission.getInputData().put("providers", providers);
  }


  private int getMaxProvidersAllowed(Submission submission) {
    // Families with only 1 child never get to see the screen that would set choseProviderForEveryChildInNeedOfCare
    if ("true".equals(submission.getInputData().get("choseProviderForEveryChildInNeedOfCare")) || getChildrenNeedingAssistance(submission.getInputData()).size() == 1) {
      return 3;
    } else {
      return 2;
    }
  }
}
