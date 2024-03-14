package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ParentPartnerQualifies implements Condition {

  @Override
  public Boolean run(Submission submission) {
    var inputData = (Map<String, Object>) submission.getInputData();

    var parentSpouseLiveTogether = inputData.getOrDefault("parentSpouseLiveTogether", "No").equals("Yes");
    if (!parentSpouseLiveTogether) {
      return false;
    }

    var parentSpouseShareChildren = inputData.getOrDefault("parentSpouseShareChildren", "No").equals("Yes");
    var parentSpouseIsStepParent = inputData.getOrDefault("parentSpouseIsStepParent", "No").equals("Yes");
    return parentSpouseIsStepParent || parentSpouseShareChildren;
  }
}