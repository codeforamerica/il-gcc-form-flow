package org.ilgcc.app.utils;

import static java.lang.Integer.parseInt;
import static java.util.Collections.emptyList;

import formflow.library.data.Submission;
import formflow.library.inputs.FieldNameMarkers;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ActivitySchedules.LocalTimeRange;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class SubmissionUtilities {

    public static final DateTimeFormatter MM_DD_YYYY = DateTimeFormatter.ofPattern("M/d/uuuu")
            .withZone(ZoneId.of("America/Chicago"));
    public static final DateTimeFormatter YYYY_MM_DD_DASHES = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(ZoneId.of("America/Chicago"));
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_AMPM_DASHES = DateTimeFormatter.ofPattern("yyyy-MM-dd-hh'.'mm-a")
            .withZone(ZoneId.of("America/Chicago"));
    public static final DateTimeFormatter MMMM_DD_COMMA_YYYY = DateTimeFormatter.ofPattern("MMMM dd, yyy")
            .withZone(ZoneId.of("America/Chicago"));
    public static final DateTimeFormatter MM_DD_YYYY_DASHES = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            .withZone(ZoneId.of("America/Chicago"));
    public static final String PROGRAM_SCHEDULE = "programSchedule";

    /**
     * Formats the date portion of {@code submittedAt} to look like "February 7, 2023".
     *
     * @param submission the submission from which the {@code submittedAt} is to be formatted
     * @return the string formatted date
     */
    public static String getFormattedSubmittedAtDate(Submission submission) {
        return MMMM_DD_COMMA_YYYY.format(submission.getSubmittedAt());
    }

    /**
     * @param submission submission containing input data to use
     * @return the string Yes/No
     */
    public static String getProgramSchedule(Submission submission) {
        return submission.getInputData().get(PROGRAM_SCHEDULE).toString();
    }

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

    public static Map<String, String> addressObject(Map<String, Object> inputData, String addressPrefix, String useSuggestedKey) {
        Map<String, String> addressLines = new HashMap<>();

        var useSuggestedMailingAddress = inputData.getOrDefault(useSuggestedKey, "false").equals("true");

        String addressStreet1Key = addressPrefix + (useSuggestedMailingAddress ? "StreetAddress1_validated"
                : "StreetAddress1");
        String addressStreet2Key = useSuggestedMailingAddress ? "" : addressPrefix + "StreetAddress2";
        String cityKey = addressPrefix + (useSuggestedMailingAddress ? "City_validated"
                : "City");
        String stateKey = addressPrefix + (useSuggestedMailingAddress ? "State_validated"
                : "State");
        String zipCodeKey = addressPrefix + (useSuggestedMailingAddress ? "ZipCode_validated"
                : "ZipCode");

        addressLines.put("address1", inputData.getOrDefault(addressStreet1Key, "").toString());
        addressLines.put("address2", inputData.getOrDefault(addressStreet2Key, "").toString());
        addressLines.put("city", inputData.getOrDefault(cityKey, "").toString());
        addressLines.put("state", inputData.getOrDefault(stateKey, "").toString());
        addressLines.put("zipCode", inputData.getOrDefault(zipCodeKey, "").toString());

        return addressLines;
    }

    public static String applicantFirstName(Map<String, Object> inputData) {
        var applicantPreferredName = inputData.getOrDefault("parentPreferredName", "").toString();
        if (!applicantPreferredName.isBlank()) {
            return applicantPreferredName;
        } else {
            var firstName = inputData.get("parentFirstName");
            if (firstName != null) {
                return firstName.toString();
            } else {
                log.error("parentFirstName is null which is impossible because it is a required field.");
                return null;
            }
        }
    }

    public static String getApplicantNameLastToFirst(Submission submission) {
        return submission.getInputData().get("parentLastName") + " " + submission.getInputData().get("parentFirstName");
    }

    /**
     * Mixpanel helper method
     */
    public static String getMixpanelValue(Map<String, Object> inputData, String inputName) {
        String value = inputData == null ? "not_set" : (String) inputData.getOrDefault(inputName, "not_set");

        if (inputName.equals("parentHasPartner")) {
            if (!"not_set".equals(value)) {
                value = "true".equals(value) ? "dual_parent" : "single_parent";
            }
        }
        return value;
    }

    public static String getMixpanelValue(Submission submission, String subflow) {
        if (submission == null || !submission.getInputData().containsKey(subflow)) {
            return "not_set";
        }

        return String.valueOf(((List<Map<String, Object>>) submission.getInputData().get(subflow)).stream()
                .filter(iter -> (boolean) iter.getOrDefault("iterationIsComplete", false))
                .toList().size());
    }

    public static Optional<LocalDate> getDateInput(Submission submission, String inputName) {
        String year = (String) submission.getInputData().get("%sYear".formatted(inputName));
        String month = (String) submission.getInputData().get("%sMonth".formatted(inputName));
        String day = (String) submission.getInputData().get("%sDay".formatted(inputName));
        if (year == null && month == null && day == null || (year + month + day).isBlank()) {
            return Optional.empty();
        } else if (year == null || month == null || day == null) {
            throw new IllegalArgumentException("Date must be complete if specified");
        }
        return Optional.of(LocalDate.of(parseInt(year), parseInt(month), parseInt(day)));
    }

    public static String getDateInputWithDayOptional(Submission submission, String inputName) {
        String year = (String) submission.getInputData().get("%sYear".formatted(inputName));
        String month = (String) submission.getInputData().get("%sMonth".formatted(inputName));
        String day = (String) submission.getInputData().get("%sDay".formatted(inputName));
        if (year == null && month == null && day == null || (year + month + day).isBlank()) {
            return "";
        } else if (year == null || month == null) {
            throw new IllegalArgumentException("Date must be complete if specified");
        } else if (day == null || day.isEmpty()) {
            return month + "/" + year;
        } else {
            return formatToStringFromLocalDate(Optional.of(LocalDate.of(parseInt(year), parseInt(month), parseInt(day))));
        }
    }


    public static Optional<LocalTime> getTimeInput(Map<String, Object> inputData, String inputName) {
        String rawValue = (String) inputData.getOrDefault(inputName, "");
        if (rawValue.isBlank()) {
            return Optional.empty();
        } else {
            LocalTime time = LocalTime.parse((String) inputData.get(inputName));
            return Optional.of(time);
        }
    }

    public static Optional<LocalTimeRange> getTimeRangeInput(Map<String, Object> inputData, String inputName, String suffix) {
        Optional<LocalTime> start = getTimeInput(inputData, "%sStartTime%s".formatted(inputName, suffix));
        Optional<LocalTime> end = getTimeInput(inputData, "%sEndTime%s".formatted(inputName, suffix));
        if (start.isEmpty() && end.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new LocalTimeRange(start.orElseThrow(), end.orElseThrow()));
        }
    }

    public static String formatToStringFromLocalDate(Optional<LocalDate> date) {
        return date.map(MM_DD_YYYY_DASHES::format).orElse("");
    }

    public static String selectedYes(String selected) {
        if (selected.equals("Yes")) {
            return "true";
        } else {
            return "false";
        }
    }

    public static List<Map<String, Object>> getChildren(Submission submission) {
        return (List<Map<String, Object>>) submission.getInputData().getOrDefault("children", emptyList());
    }

    public static List<Map<String, Object>> getChildrenNeedingAssistance(Submission submission) {
        return getChildren(submission).stream().filter(
                        child -> child.getOrDefault("needFinancialAssistanceForChild", "No").equals("Yes"))
                .toList();
    }

    public static List<Map<String, Object>> firstFourChildrenNeedingAssistance(Submission submission) {
        int childrenNeedingAssistance = getChildrenNeedingAssistance(submission).size();
        if (childrenNeedingAssistance < 4) {
            return getChildrenNeedingAssistance(submission).subList(0, childrenNeedingAssistance);
        } else {
            return getChildrenNeedingAssistance(submission).subList(0, 4);
        }
    }

    public static List<Map<String, Object>> getAdditionalChildrenNeedingAssistance(Submission submission) {
        int childrenNeedingAssistance = getChildrenNeedingAssistance(submission).size();
        return getChildrenNeedingAssistance(submission).subList(4, childrenNeedingAssistance);
    }

    public static String generatePdfPath(Submission submission) {
        return String.format("%s/%s.pdf", submission.getId(), submission.getId());
    }

    public static String getDashFormattedSubmittedAtDate(Submission submission) {
        return YYYY_MM_DD_DASHES.format(submission.getSubmittedAt());
    }

    public static String getDashFormattedSubmittedAtDateWithTime(Submission submission) {
        return YYYY_MM_DD_HH_MM_AMPM_DASHES.format(submission.getSubmittedAt());
    }

    public static String convertToAbsoluteURLForEmailAndText(UriComponentsBuilder builder, String shortCode, String utmMedium) {
        return builder
                .path("providerresponse/submit/" + shortCode + (utmMedium != null ? "?utm_medium=" + utmMedium : ""))
                .build()
                .toUriString();
    }

    public static String convertToAbsoluteURLForEmailAndText(String shortCode, String utmMedium, String baseUrl) {
        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromUriString(baseUrl);
        return convertToAbsoluteURLForEmailAndText(builder, shortCode, utmMedium);
    }

    public static String getProviderName(Map<String, Object> inputData) {
        if (inputData.containsKey("dayCareChoice")) {
            String dayCareChoice = (String) inputData.get("dayCareChoice");
            return ChildCareProvider.valueOf(dayCareChoice).getDisplayName();
        } else if (inputData.containsKey("familyIntendedProviderName")) {
            return (String) inputData.get("familyIntendedProviderName");
        } else {
            return "";
        }

    }

    public static boolean hasNotChosenProvider(Submission submission) {
        return submission.getInputData().containsKey("hasChosenProvider") && submission.getInputData().get("hasChosenProvider")
                .equals("false");
    }

    public static boolean hasProviderResponse(Submission submission) {
        return submission.getInputData().containsKey("providerResponseSubmissionId") && !submission.getInputData()
                .get("providerResponseSubmissionId").toString().isEmpty();
    }
}
