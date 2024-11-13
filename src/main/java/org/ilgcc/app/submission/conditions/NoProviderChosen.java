package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class NoProviderChosen implements Condition {

  @Override
  public Boolean run(Submission submission) {
    return submission.getInputData().containsKey("dayCareChoice") && submission.getInputData().get("dayCareChoice").equals("none");
  }
}
