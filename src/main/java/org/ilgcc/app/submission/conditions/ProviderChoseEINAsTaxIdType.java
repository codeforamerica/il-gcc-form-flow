package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class ProviderChoseEINAsTaxIdType implements Condition {

  @Override
  public Boolean run(Submission submission) {
    return submission.getInputData().containsKey("providerTaxIdType") && submission.getInputData().get("providerTaxIdType").equals("FEIN");
  }
}
