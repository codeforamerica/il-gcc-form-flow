package org.ilgcc.app.submission.conditions;

import static java.util.Collections.emptyList;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PartnersWorkDaysOrHoursVaryDoNotVary implements Condition {

  @Override
  public Boolean run(Submission submission, String uuid) {
    var jobs = (List<Map<String, Object>>) submission.getInputData().getOrDefault("partnerJobs", emptyList());
    for (var job : jobs) {
      if (job.get("uuid").equals(uuid)) {
        var activitiesWorkVary = job.getOrDefault("activitiesWorkVary", "false");
        return activitiesWorkVary.equals("false");
      }
    }
    return true;
  }
}
