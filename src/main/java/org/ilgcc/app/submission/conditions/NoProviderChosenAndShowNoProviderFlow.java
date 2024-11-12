package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NoProviderChosenAndShowNoProviderFlow implements Condition {
  
  @Value("${il-gcc.show-no-provider-flow}")
  private boolean showNoProviderFlow;

  @Override
  public Boolean run(Submission submission) {
    return showNoProviderFlow && submission.getInputData().getOrDefault("hasChosenProvider", "false").equals("false");
  }
}
