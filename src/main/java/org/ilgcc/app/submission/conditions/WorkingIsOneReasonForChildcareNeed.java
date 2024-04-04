package org.ilgcc.app.submission.conditions;

import static java.util.Collections.emptyList;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class WorkingIsOneReasonForChildcareNeed implements Condition {

  @Override
  public Boolean run(Submission submission) {
    List<String> reasonsForChildcareNeed = (List<String>) submission.getInputData()
        .getOrDefault("activitiesParentChildcareReason[]", emptyList());
    return reasonsForChildcareNeed.contains("WORKING");
  }
}
