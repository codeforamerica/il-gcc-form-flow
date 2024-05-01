package org.ilgcc.app.submission.conditions;

import static java.util.Collections.emptyList;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LearningIsOneReasonForChildcareNeed implements Condition {

  @Override
  public Boolean run(Submission submission) {
    List<String> reasonsForChildcareNeed = (List<String>) submission.getInputData()
        .getOrDefault("activitiesParentChildcareReason[]", emptyList());
    return reasonsForChildcareNeed.contains("SCHOOL");
  }

  @Override
  public Boolean run(Submission submission, String uuid) {
    return run(submission);
  }
}
