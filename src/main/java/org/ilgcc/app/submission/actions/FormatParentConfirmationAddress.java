package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class FormatParentConfirmationAddress implements Action {

  @Override
  public void run(Submission submission) {
    Map<String, Object> inputData = submission.getInputData();
    // TODO check if mailing addr is different than home addr
    var parentMailingStreetAddress1 = (String) inputData.get("parentMailingStreetAddress1");
    var parentMailingStreetAddress2 = (String) inputData.get("parentMailingStreetAddress2");
    var parentMailingCity = (String) inputData.get("parentMailingCity");
    var parentMailingState = (String) inputData.get("parentMailingState");
    var parentMailingZipCode = (String) inputData.get("parentMailingZipCode");

    List<String> addressLines = new ArrayList<>();
    addressLines.add(parentMailingStreetAddress1);
    addressLines.add(parentMailingStreetAddress2);
    addressLines.add("%s, %s".formatted(parentMailingCity, parentMailingState));
    addressLines.add(parentMailingZipCode);

    inputData.put("addressLines", addressLines);

    // TODO redirect based on - selected homeless (mailing), mailing address different than home (mailing), mailing is the same as home (home)
    if(SubmissionUtilities.parentIsExperiencingHomelessness(inputData)){
      inputData.put("redirectPage", "parent-mailing-address");
    }else{
      inputData.put("redirectPage", "parent-home-address");
    }
  }
}
