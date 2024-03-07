package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
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
    var parentHomeStreetAddress1 = (String) inputData.get("parentHomeStreetAddress1");
    var parentHomeStreetAddress2 = (String) inputData.get("parentHomeStreetAddress2");
    var parentHomeCity = (String) inputData.get("parentHomeCity");
    var parentHomeState = (String) inputData.get("parentHomeState");
    var parentHomeZipCode = (String) inputData.get("parentHomeZipCode");

    List<String> addressLines = new ArrayList<>();
    addressLines.add(parentHomeStreetAddress1);
    addressLines.add(parentHomeStreetAddress2);
    addressLines.add("%s, %s".formatted(parentHomeCity, parentHomeState));
    addressLines.add(parentHomeZipCode);

    inputData.put("addressLines", addressLines);

    // TODO redirect based on - selected homeless (mailing), mailing address different than home (mailing), mailing is the same as home (home)
    inputData.put("redirectPage", "parent-home-address");
  }
}
