package org.ilgcc.app.utils;

import formflow.library.data.Submission;
import formflow.library.inputs.FieldNameMarkers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressUtilities {

    public static final String suggestedAddressKey = "useSuggested%sAddress";
    private static final String streetAddress1Key = "StreetAddress1";
    private static final String streetAddress2Key = "StreetAddress2";
    private static final String cityKey = "City";
    private static final String stateKey = "State";
    private static final String zipCodeKey = "ZipCode";

    public static final List<String> addressKeys = List.of(streetAddress1Key, streetAddress2Key, cityKey, stateKey, zipCodeKey);

    /**
     * @param inputData a JSON object of user inputs
     * @return true or false
     */
    public static boolean parentIsExperiencingHomelessness(Map<String, Object> inputData) {
        return inputData.getOrDefault("parentHomeExperiencingHomelessness[]", "no").equals(List.of("yes"));
    }

    /**
     * @param inputData a JSON object of user inputs
     * @return true or false
     */
    public static boolean parentMailingAddressIsHomeAddress(Map<String, Object> inputData) {
        return inputData.getOrDefault("parentMailingAddressSameAsHomeAddress[]", "no").equals(List.of("yes"));
    }

    public static boolean hasAddressSuggestion(Submission submission, String inputName) {
        return submission.getInputData().get(FieldNameMarkers.UNVALIDATED_FIELD_MARKER_VALIDATE_ADDRESS + inputName)
                .equals("true") && submission.getInputData()
                .containsKey(inputName + "StreetAddress1" + FieldNameMarkers.UNVALIDATED_FIELD_MARKER_VALIDATED);
    }

    public static String useSuggestedAddressKey(String suggestedAddressKey, String addressGroupInputPrefix) {
        return String.format(suggestedAddressKey, capitalize(addressGroupInputPrefix));

    }

    public static Boolean hasValidAddressSuggestion(Submission submission, String addressPrefix){
        // check if there is the validate marker
        // compare the validated data against the unvalidated data
      return false;
    }

    private Boolean hasMatchingState(String validatedState, String inputtedState){
        return validatedState.equals(inputtedState);
    }

    private Boolean hasMatchingCity(String validatedCity, String inputtedCity){
        return validatedCity.equals(inputtedCity);
    }

    private Boolean hasMatchingStreetAddress(String validatedStreetAddress, String inputtedStreetAddress1, String inputtedStreetAddress2){
        // check that both addresses exist
        // concatenate inputtedStreet addresses
        // cinoare agaubst validated street address
        return false;
    }

    private Boolean hasMatchingZipCode(String validatedZip, String inputtedZip){
        if(validatedZip.isBlank() && inputtedZip.isBlank()){
            return true;
        }
        final String truncatedZip = inputtedZip.substring(0, 5);

        return validatedZip.equals(truncatedZip);
    }

    public static Map<String, String> getAddress(Map<String, Object> inputData, String addressPrefix) {
        Map<String, String> addressLines = new HashMap<>();

        String suggestedAddressKey = String.format("useSuggested%sAddress", capitalize(addressPrefix));

        var useSmartyValidatedAddress = inputData.getOrDefault(suggestedAddressKey, "false").equals("true");

        String addressStreet1Key = addressPrefix + (useSmartyValidatedAddress ? "StreetAddress1_validated"
                : "StreetAddress1");
        String addressStreet2Key = useSmartyValidatedAddress ? "" : addressPrefix + "StreetAddress2";
        String cityKey = addressPrefix + (useSmartyValidatedAddress ? "City_validated"
                : "City");
        String stateKey = addressPrefix + (useSmartyValidatedAddress ? "State_validated"
                : "State");
        String zipCodeKey = addressPrefix + (useSmartyValidatedAddress ? "ZipCode_validated"
                : "ZipCode");

        addressLines.put("address1", inputData.getOrDefault(addressStreet1Key, "").toString());
        addressLines.put("address2", inputData.getOrDefault(addressStreet2Key, "").toString());
        addressLines.put("city", inputData.getOrDefault(cityKey, "").toString());
        addressLines.put("state", inputData.getOrDefault(stateKey, "").toString());
        addressLines.put("zipCode", inputData.getOrDefault(zipCodeKey, "").toString());

        return addressLines;
    }

    private static String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }


}
