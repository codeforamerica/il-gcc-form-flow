package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PopulateChildcareSchedulesIteration implements Action {

    @Override
    public void run(Submission submission, String subflowUuid) {
        Map<String, Object> childcareSchedulesSubflow = submission.getSubflowEntryByUuid("childcareSchedules", subflowUuid);
        if (childcareSchedulesSubflow.containsKey("childrenUuid")) {
            String childrenUuid = (String) childcareSchedulesSubflow.get("childrenUuid");
            Map<String, Object> childrenSubflow = submission.getSubflowEntryByUuid("children", childrenUuid);
            if (childrenSubflow != null) {
                Map<String, Object> childcareScheduleData = childcareSchedulesSubflow;
                childcareScheduleData.put("childFirstName", childrenSubflow.get("childFirstName"));
                submission.mergeFormDataWithSubflowIterationData("childcareSchedules", childcareSchedulesSubflow, childcareScheduleData);
            }
        }
    }
}
