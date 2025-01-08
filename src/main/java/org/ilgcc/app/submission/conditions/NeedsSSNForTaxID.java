package org.ilgcc.app.submission.conditions;

import static java.util.Collections.emptyList;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NeedsSSNForTaxID implements Condition {

  @Value("${il-gcc.allow-provider-registration-flow}")
  private boolean enableProviderRegistration;
  @Override
  public Boolean run(Submission submission) {
    boolean hasNotProvidedSSN = submission.getInputData().getOrDefault("providerIdentityCheckSSN", "").equals("");
    boolean hasSelectedTaxIdTypeSSN = submission.getInputData().getOrDefault("providerTaxIdType", "").equals("SSN");

    return (enableProviderRegistration && hasNotProvidedSSN && hasSelectedTaxIdTypeSSN);
  }
}
