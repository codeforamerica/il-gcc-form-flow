package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.emptyList;

@Component
public class PartnerIsWorking implements Condition {

  @Override
  public Boolean run(Submission submission) {
    List<String> reasonsForChildcareNeed = (List<String>) submission.getInputData()
        .getOrDefault("activitiesParentPartnerChildcareReason[]", emptyList());
    return reasonsForChildcareNeed.contains("WORKING");
  }
}