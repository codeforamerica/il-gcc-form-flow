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
public class RemoveIncompletePartnerJobIterations implements Action {
    
    private final SubmissionRepositoryService submissionRepositoryService;

    public RemoveIncompletePartnerJobIterations(SubmissionRepositoryService submissionRepositoryService) {
        this.submissionRepositoryService = submissionRepositoryService;
    }
    
    @Override
    public void run(Submission submission) {
        var subflowData = (List<Map<String, Object>>) submission.getInputData().getOrDefault("partnerJobs", emptyList());
        if (!subflowData.isEmpty()) {
            log.info("Removing incomplete partner job iterations from submission {}", submission.getId());
            subflowData.removeIf(partnerJob -> !(boolean) partnerJob.getOrDefault("iterationIsComplete", false));
            submissionRepositoryService.save(submission);
        }
    }
}

