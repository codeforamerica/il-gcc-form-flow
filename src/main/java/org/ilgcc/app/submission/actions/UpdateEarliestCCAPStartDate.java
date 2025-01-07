package org.ilgcc.app.submission.actions;

import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.DateUtilities;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdateEarliestCCAPStartDate extends VerifyDate {

    static final String PREFIX = "ccapStart";
    static final String INPUT_NAME = "earliestChildcareStartDate";

    @Override
    public void run(Submission submission, String id) {
        Map<String, Object> childSubflowData = submission.getSubflowEntryByUuid("children", id);
        String childCCAPDate = DateUtilities.getFormattedDateFromMonthDateYearInputs(PREFIX, childSubflowData);
        String earliestCCAPStart = (String) submission.getInputData().get(INPUT_NAME);
        String earliestDate = getEarliestDate(childCCAPDate, earliestCCAPStart);
        if (earliestCCAPStart.equals(earliestDate)) {
            submission.getInputData().put(INPUT_NAME, findEarliestCCAPDate(submission, id));
        }
    }

    private String findEarliestCCAPDate(Submission submission, String id) {
        List<Map<String, Object>> childrenNeedingAssistance = SubmissionUtilities.getChildrenNeedingAssistance(submission);

        List<Map<String, Object>> childrenNeedingAssistanceMinusDeleted = childrenNeedingAssistance.stream().filter(
                        child -> !child.get("uuid").equals(id)).toList();

        if (childrenNeedingAssistanceMinusDeleted.isEmpty()) {
            return "";
        } else if (childrenNeedingAssistanceMinusDeleted.size() > 1) {
            return earliestDateOfMultipleChildren(childrenNeedingAssistanceMinusDeleted);
        } else {
            return (String) childrenNeedingAssistanceMinusDeleted.get(0).get("ccapStartDate");
        }
    }

    private String earliestDateOfMultipleChildren(List<Map<String, Object>> children) {
        String earliestChildCareDate = "";
        for (var child : children) {
            earliestChildCareDate = getEarliestDate(earliestChildCareDate, child.get("ccapStartDate").toString());
        };

        return earliestChildCareDate;
    }
}
