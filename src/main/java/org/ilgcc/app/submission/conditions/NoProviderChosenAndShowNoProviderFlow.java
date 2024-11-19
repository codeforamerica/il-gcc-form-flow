package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.utils.SubmissionUtilities.hasNotChosenProvider;

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
    return showNoProviderFlow && hasNotChosenProvider(submission);
  }
}
