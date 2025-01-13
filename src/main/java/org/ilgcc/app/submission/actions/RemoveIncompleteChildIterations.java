package org.ilgcc.app.submission.actions;

import static java.util.Collections.emptyList;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.DateUtilities;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoveIncompleteChildIterations implements Action {

    static final String INPUT_NAME = "earliestChildcareStartDate";
    private final SubmissionRepositoryService submissionRepositoryService;

    public RemoveIncompleteChildIterations(SubmissionRepositoryService submissionRepositoryService) {
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission submission) {
        var subflowData = (List<Map<String, Object>>) submission.getInputData().getOrDefault("children", emptyList());
        if (!subflowData.isEmpty()) {
            log.info("Removing incomplete child iterations from submission {}", submission.getId());
            subflowData.removeIf(childIteration -> !(boolean) childIteration.getOrDefault("iterationIsComplete", false));
            submissionRepositoryService.save(submission);
        }
        updateEarliestCCAPStartDate(submission);
    }

    protected void updateEarliestCCAPStartDate(Submission submission) {
        String earliestCCAPDate = findEarliestCCAPDate(submission);
        if (earliestCCAPDate != null) {
            submission.getInputData().put(INPUT_NAME, earliestCCAPDate);
            submissionRepositoryService.save(submission);
        }
    }

    private String findEarliestCCAPDate(Submission submission) {
        List<Map<String, Object>> childrenNeedingAssistance = SubmissionUtilities.getChildrenNeedingAssistance(submission);

        if (childrenNeedingAssistance.isEmpty()) {
            return "";
        } else if (childrenNeedingAssistance.size() > 1) {
            return earliestDateOfMultipleChildren(childrenNeedingAssistance);
        } else {
            return (String) childrenNeedingAssistance.get(0).get("ccapStartDate");
        }
    }

    private String earliestDateOfMultipleChildren(List<Map<String, Object>> children) {
        String earliestChildCareDate = "";
        for (var child : children.stream().toList()) {
            Object ccapStartDate = child.get("ccapStartDate");
            if (ccapStartDate != null) {
                earliestChildCareDate = DateUtilities.getEarliestDate(earliestChildCareDate, ccapStartDate.toString());
            }
        }

        return earliestChildCareDate;
    }
}

