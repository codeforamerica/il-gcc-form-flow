package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.util.Map;
import org.ilgcc.app.utils.AddressUtilities;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
public class SetEditAddressRedirect implements Action {

  @Override
  public void run(Submission submission) {
    Map<String, Object> inputData = submission.getInputData();

    if (AddressUtilities.parentMailingAddressIsHomeAddress(inputData)){
      inputData.put("parentEditAddressRedirect", "parent-home-address");
    } else
      inputData.put("parentEditAddressRedirect", "parent-mailing-address");
  }
}
