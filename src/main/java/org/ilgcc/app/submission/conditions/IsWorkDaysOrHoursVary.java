package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

@Component
public class IsWorkDaysOrHoursVary implements Condition {

  @Override
  public Boolean run(Submission submission, String uuid) {
    var jobs = (List<Map<String, Object>>) submission.getInputData().getOrDefault("jobs", emptyList());
    for (var job : jobs) {
      if (job.get("uuid").equals(uuid)) {
        var activitiesWorkVary = job.getOrDefault("activitiesWorkVary", "false");
        return activitiesWorkVary.equals("false");
      }
    }
    return true;
  }
}
