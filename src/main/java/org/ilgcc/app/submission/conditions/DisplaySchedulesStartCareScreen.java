package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.utils.SchedulePreparerUtility.relatedSubflowIterationData;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DisplaySchedulesStartCareScreen extends EnableMultipleProviders implements Condition {

    @Override
    public Boolean run(Submission submission, String subflowUUID) {
        return true;
    }

    @Override
    public Boolean run(Submission submission, String subflowUuid, String repeatForIterationUuid) {
        Map<String, Object> childcareSubflow = submission.getSubflowEntryByUuid("childcareSchedules", subflowUuid);
        Map<String, Object> repeatForSubflowIteration = relatedSubflowIterationData(childcareSubflow, "providerSchedules",
                repeatForIterationUuid);
        return super.run(submission) && (providerSelected(repeatForSubflowIteration));
    }

    private boolean providerSelected(Map<String, Object> repeatForSubflowIteration){
        return !repeatForSubflowIteration.get("repeatForValue").equals("NO_PROVIDER");
    }
}
