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
public class RemoveIncompleteJobIterations implements Action {
    
    private final SubmissionRepositoryService submissionRepositoryService;

    public RemoveIncompleteJobIterations(SubmissionRepositoryService submissionRepositoryService) {
        this.submissionRepositoryService = submissionRepositoryService;
    }
    
    @Override
    public void run(Submission submission) {
        var subflowData = (List<Map<String, Object>>) submission.getInputData().getOrDefault("jobs", emptyList());
        if (!subflowData.isEmpty()) {
            log.info("Removing incomplete job iterations from submission {}", submission.getId());
            subflowData.removeIf(job -> !(boolean) job.getOrDefault("iterationIsComplete", false));
            submissionRepositoryService.save(submission);
        }
    }
}

