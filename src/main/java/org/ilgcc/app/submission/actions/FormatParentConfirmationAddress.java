package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.ilgcc.app.utils.SubmissionUtilities.parentIsExperiencingHomelessness;

@Component
public class FormatParentConfirmationAddress implements Action {

  @Override
  public void run(Submission submission) {
    Map<String, Object> inputData = submission.getInputData();
    var parentMailingStreetAddress1 = (String) inputData.get("parentMailingStreetAddress1");
    var parentMailingStreetAddress2 = (String) inputData.get("parentMailingStreetAddress2");
    var parentMailingCity = (String) inputData.get("parentMailingCity");
    var parentMailingState = (String) inputData.get("parentMailingState");
    var parentMailingZipCode = (String) inputData.get("parentMailingZipCode");
    var parentMailingUsingSmartySuggestion = (String) inputData.get("useSuggestedParentAddress");
    var parentMailingSuggestedStreetAddress1 = (String) inputData.get("parentMailingStreetAddress1_validated");
    var parentMailingSuggestedCity = (String) inputData.get("parentMailingCity_validated");
    var parentMailingSuggestedState = (String) inputData.get("parentMailingState_validated");
    var parentMailingSuggestedZipCode = (String) inputData.get("parentMailingZipCode_validated");

    if (parentAddressFieldsAreNotEmpty(parentMailingStreetAddress1, parentMailingCity, parentMailingState, parentMailingZipCode)) {
      List<String> addressLines = new ArrayList<>();

      if (parentMailingUsingSmartySuggestion.equals("true")) {
        addressLines.add(parentMailingSuggestedStreetAddress1);
        addressLines.add("%s, %s".formatted(parentMailingSuggestedCity, parentMailingSuggestedState));
        addressLines.add(parentMailingSuggestedZipCode);
      } else {
        addressLines.add(parentMailingStreetAddress1);
        addressLines.add(parentMailingStreetAddress2);
        addressLines.add("%s, %s".formatted(parentMailingCity, parentMailingState));
        addressLines.add(parentMailingZipCode);
      }

      inputData.put("addressLines", addressLines);
    }

    var parentHomeStreetAddress1 = (String) inputData.get("parentHomeStreetAddress1");
    var parentHomeStreetAddress2 = (String) inputData.get("parentHomeStreetAddress2");
    var parentHomeCity = (String) inputData.get("parentHomeCity");
    var parentHomeState = (String) inputData.get("parentHomeState");
    var parentHomeZipCode = (String) inputData.get("parentHomeZipCode");

    if (parentAddressFieldsAreNotEmpty(parentHomeStreetAddress1, parentHomeCity, parentHomeState, parentHomeZipCode) && !parentIsExperiencingHomelessness(inputData)) {
      List<String> homeAddressLines = new ArrayList<>();
      homeAddressLines.add(parentHomeStreetAddress1);
      homeAddressLines.add(parentHomeStreetAddress2);
      homeAddressLines.add("%s, %s".formatted(parentHomeCity, parentHomeState));
      homeAddressLines.add(parentHomeZipCode);

      inputData.put("homeAddressLines", homeAddressLines);
    }
  }

  private boolean parentAddressFieldsAreNotEmpty(String streetAddress1, String city, String state, String zipCode){
    return (!streetAddress1.isBlank() && !city.isBlank() && !state.isBlank() && !zipCode.isBlank());
  }
}
