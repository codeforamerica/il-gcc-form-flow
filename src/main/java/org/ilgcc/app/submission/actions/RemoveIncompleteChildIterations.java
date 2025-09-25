package org.ilgcc.app.submission.actions;

import static java.util.Collections.emptyList;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.DateUtilities;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoveIncompleteChildIterations implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    static final String INPUT_NAME = "earliestChildcareStartDate";

    @Override
    public void run(Submission submission) {
        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData().getOrDefault(
                "children",
                emptyList());

        if (children.isEmpty()) {
            submission.getInputData().remove("childcareSchedules");
        } else {
            submission.getInputData().put("children", children.stream()
                    .filter(childIteration -> (boolean) childIteration.getOrDefault("iterationIsComplete", false))
                    .collect(Collectors.toList()));
            if (submission.getInputData().containsKey("childcareSchedules")) {
                List<Map<String, Object>> childcareSchedules = (List<Map<String, Object>>) submission.getInputData()
                        .get("childcareSchedules");

                Set<String> validChildUuids = children.stream()
                        .map(child -> (String) child.get("uuid"))
                        .collect(Collectors.toSet());

                childcareSchedules.removeIf(schedule -> {
                    String scheduleChildUuid = (String) schedule.get("childUuid");
                    return scheduleChildUuid == null || !validChildUuids.contains(scheduleChildUuid);
                });
            }
        }

        submissionRepositoryService.save(submission);

        // ToDo: remove as part of EnableMultipleProviders cleanup?
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
        List<Map<String, Object>> childrenNeedingAssistance = SubmissionUtilities.getChildrenNeedingAssistance(
                submission.getInputData());

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

