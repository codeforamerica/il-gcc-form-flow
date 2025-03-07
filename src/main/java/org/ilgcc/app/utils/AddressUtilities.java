package org.ilgcc.app.utils;

import static formflow.library.inputs.FieldNameMarkers.UNVALIDATED_FIELD_MARKER_VALIDATED;

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

    private static final List<String> addressKeys = List.of(streetAddress1Key, streetAddress2Key, cityKey, stateKey, zipCodeKey);

    /**
     * @param inputData a JSON object of user inputs
     * @return true or false
     */
    public static boolean parentIsExperiencingHomelessness(Map<String, Object> inputData,
            String experiencingHomelessnessInputName) {
        return inputData.getOrDefault(experiencingHomelessnessInputName + "[]", "no").equals(List.of("yes"));
    }

    public static boolean hasAddressSuggestion(Submission submission, String addressGroupInputPrefix) {
        return submission.getInputData().get(FieldNameMarkers.UNVALIDATED_FIELD_MARKER_VALIDATE_ADDRESS + addressGroupInputPrefix)
                .equals("true") && submission.getInputData()
                .containsKey(addressGroupInputPrefix + "StreetAddress1" + FieldNameMarkers.UNVALIDATED_FIELD_MARKER_VALIDATED);
    }

    public static String useSuggestedAddressKey(String suggestedAddressKey, String addressGroupInputPrefix) {
        return String.format(suggestedAddressKey, capitalize(addressGroupInputPrefix));
    }

    public static boolean requireAddressConfirmation(Submission submission, String addressGroupInputPrefix) {
        return !validatedAndInputtedAddressesMatch(submission.getInputData(), addressGroupInputPrefix);
    }

    public static Boolean validatedAndInputtedAddressesMatch(Map<String, Object> inputData, String addressGroupInputPrefix) {
        return hasMatchingState(inputData, addressGroupInputPrefix) && hasMatchingCity(inputData, addressGroupInputPrefix)
                && hasMatchingStreetAddress(inputData, addressGroupInputPrefix) && hasMatchingZipCode(inputData,
                addressGroupInputPrefix);
    }

    public static Boolean hasMatchingState(Map<String, Object> inputData, String addressGroupInputPrefix) {
        String validatedData = (String) inputData.getOrDefault(
                addressGroupInputPrefix + stateKey + UNVALIDATED_FIELD_MARKER_VALIDATED, "");
        String inputtedData = (String) inputData.getOrDefault(addressGroupInputPrefix + stateKey, "");
        return validatedData.equals(inputtedData);
    }

    public static Boolean hasMatchingCity(Map<String, Object> inputData, String addressGroupInputPrefix) {
        String validatedData = (String) inputData.getOrDefault(
                addressGroupInputPrefix + cityKey + UNVALIDATED_FIELD_MARKER_VALIDATED, "");
        String inputtedData = (String) inputData.getOrDefault(addressGroupInputPrefix + cityKey, "");
        return validatedData.equals(inputtedData);
    }

    public static Boolean hasMatchingStreetAddress(Map<String, Object> inputData, String addressGroupInputPrefix) {
        String validatedData = (String) inputData.getOrDefault(
                addressGroupInputPrefix + streetAddress1Key + UNVALIDATED_FIELD_MARKER_VALIDATED, "");
        String inputtedData1 = (String) inputData.getOrDefault(addressGroupInputPrefix + streetAddress1Key, "");
        String inputtedData2 = (String) inputData.getOrDefault(addressGroupInputPrefix + streetAddress2Key, "");

        return validatedData.equals(inputtedData1 + inputtedData2);
    }

    public static Boolean hasMatchingZipCode(Map<String, Object> inputData, String addressGroupInputPrefix) {
        String validatedData = (String) inputData.getOrDefault(
                addressGroupInputPrefix + zipCodeKey + UNVALIDATED_FIELD_MARKER_VALIDATED, "");
        String inputtedData = (String) inputData.getOrDefault(addressGroupInputPrefix + zipCodeKey, "");
        if (validatedData.isBlank() && inputtedData.isBlank()) {
            return true;
        }
        final String truncatedZip = validatedData.substring(0, 5);

        return inputtedData.equals(truncatedZip);
    }

    public static Map<String, String> formatAddressAsMultiline(Map<String, Object> inputData, String addressGroupInputPrefix) {
        Map<String, String> addressLines = new HashMap<>();

        addressKeys.forEach(ak -> {
                    if (inputData.containsKey(addressGroupInputPrefix + ak) && !inputData.get(addressGroupInputPrefix + ak).toString()
                            .isBlank()) {
                        addressLines.put(ak, (String) inputData.get(addressGroupInputPrefix + ak));
                    }
                }

        );

        return addressLines;
    }

    private static String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }


}
