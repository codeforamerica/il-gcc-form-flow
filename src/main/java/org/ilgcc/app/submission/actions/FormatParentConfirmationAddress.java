package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.ilgcc.app.utils.SubmissionUtilities.parentIsExperiencingHomelessness;
import static org.ilgcc.app.utils.SubmissionUtilities.parentMailingAddressIsHomeAddress;

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
      List<Object> addressLines = new ArrayList<>();

      if (!(parentMailingUsingSmartySuggestion==null) && parentMailingUsingSmartySuggestion.equals("true")) {
        addressLines.add(Map.entry("mailing-street-address-1", parentMailingSuggestedStreetAddress1));
        addressLines.add(Map.entry("mailing-city-and-state","%s, %s".formatted(parentMailingSuggestedCity, parentMailingSuggestedState)));
        addressLines.add(Map.entry("mailing-zip-code", parentMailingSuggestedZipCode));
      } else {
        addressLines.add(Map.entry("mailing-street-address-1", parentMailingStreetAddress1));
        addressLines.add(Map.entry("mailing-street-address-2",parentMailingStreetAddress2));
        addressLines.add(Map.entry("mailing-city-and-state", "%s, %s".formatted(parentMailingCity, parentMailingState)));
        addressLines.add(Map.entry("mailing-zip-code",parentMailingZipCode));
      }

      inputData.put("addressLines", addressLines);
    }

    var parentHomeStreetAddress1 = (String) inputData.get("parentHomeStreetAddress1");
    var parentHomeStreetAddress2 = (String) inputData.get("parentHomeStreetAddress2");
    var parentHomeCity = (String) inputData.get("parentHomeCity");
    var parentHomeState = (String) inputData.get("parentHomeState");
    var parentHomeZipCode = (String) inputData.get("parentHomeZipCode");

    if (parentAddressFieldsAreNotEmpty(parentHomeStreetAddress1, parentHomeCity, parentHomeState, parentHomeZipCode) && !parentIsExperiencingHomelessness(inputData) ) {
      List<Object> homeAddressLines = new ArrayList<>();
      if(parentMailingAddressIsHomeAddress(inputData) && !(parentMailingUsingSmartySuggestion==null) && parentMailingUsingSmartySuggestion.equals("true")){
        homeAddressLines.add(Map.entry("home-street-address-1", parentMailingSuggestedStreetAddress1));
        homeAddressLines.add(Map.entry("home-city-and-state", "%s, %s".formatted(parentMailingSuggestedCity, parentMailingSuggestedState)));
        homeAddressLines.add(Map.entry("home-zip-code", parentMailingSuggestedZipCode));
      }else{
        homeAddressLines.add(Map.entry("home-street-address-1", parentHomeStreetAddress1));
        homeAddressLines.add(Map.entry("home-street-address-2", parentHomeStreetAddress2));
        homeAddressLines.add(Map.entry("home-city-and-state", "%s, %s".formatted(parentHomeCity, parentHomeState)));
        homeAddressLines.add((Map.entry("home-zip-code", parentHomeZipCode)));
      }

      inputData.put("homeAddressLines", homeAddressLines);
    }
  }

  private boolean parentAddressFieldsAreNotEmpty(String streetAddress1, String city, String state, String zipCode){
    return (streetAddress1 != null && !streetAddress1.isBlank() 
            && city != null && !city.isBlank()
            && state != null && !state.isBlank()
            && zipCode != null && !zipCode.isBlank());
  }
}
