package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class HouseholdPaysChildSupport implements Condition {

  @Override
  public Boolean run(Submission submission) {
    return submission.getInputData().getOrDefault("doesAnyoneInHouseholdPayChildSupport", "false").equals("true");
  }

}
