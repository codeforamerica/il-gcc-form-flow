package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NeedsSSNForTaxID implements Condition {

  @Override
  public Boolean run(Submission submission) {
    boolean hasNotProvidedSSN = submission.getInputData().getOrDefault("providerIdentityCheckSSN", "").equals("");
    boolean hasSelectedTaxIdTypeSSN = submission.getInputData().getOrDefault("providerTaxIdType", "").equals("SSN");

    return (hasNotProvidedSSN && hasSelectedTaxIdTypeSSN);
  }
}
