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
        if (childcareSchedulesSubflow.containsKey("childUuid")) {
            String childUuid = (String) childcareSchedulesSubflow.get("childUuid");
            Map<String, Object> childSubflow = submission.getSubflowEntryByUuid("children", childUuid);
            if (childSubflow != null) {
              childcareSchedulesSubflow.put("childFirstName", childSubflow.get("childFirstName"));
                submission.mergeFormDataWithSubflowIterationData("childcareSchedules", childcareSchedulesSubflow,
                    childcareSchedulesSubflow);
            }
        }
    }
}
