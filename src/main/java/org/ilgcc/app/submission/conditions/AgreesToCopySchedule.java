package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.utils.SchedulePreparerUtility.relatedSubflowIterationData;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AgreesToCopySchedule implements Condition {

    @Override
    public Boolean run(Submission submission, String subflowUuid, String repeatForIterationUuid) {
        Map<String, Object> childcareSubflow = submission.getSubflowEntryByUuid("childcareSchedules", subflowUuid);
        Map<String, Object> currentProviderSchedule = relatedSubflowIterationData(childcareSubflow, "providerSchedules",
                repeatForIterationUuid);
        return clientAgreesToCopySchedule(currentProviderSchedule);
    }

    private boolean clientAgreesToCopySchedule(Map<String, Object> currentProviderSchedule) {
        return currentProviderSchedule.get("sameSchedule").equals("true");
    }
}
