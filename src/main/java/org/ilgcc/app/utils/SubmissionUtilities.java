package org.ilgcc.app.utils;

import static java.lang.Integer.parseInt;
import static java.util.Collections.emptyList;

import formflow.library.data.Submission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
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

    public static final String PROVIDER_HAS_STARTED_CHILDCARE = "already-started";
    public static final String PROVIDER_HAS_NOT_STARTED_CHILDCARE = "not-started";
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


    public static String applicantFirstName(Map<String, Object> inputData) {
        var applicantPreferredName = inputData.getOrDefault("parentPreferredName", "").toString();
        if (!applicantPreferredName.isBlank()) {
            return applicantPreferredName;
        } else {
            var firstName = inputData.get("parentFirstName");
            if (firstName != null) {
                return firstName.toString();
            } else {
                log.warn("parentFirstName is null which is impossible because it is a required field.");
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
        String value = inputData == null ? "not_set" : inputData.getOrDefault(inputName, "not_set").toString();

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
        return getFormatedDateStringWithOptionalDayField(year, month, day);
    }

    public static String getDateInputWithDayOptionalFromSubflow(HashMap<String, Object> subflowInstance, String inputName) {
        String year = (String) subflowInstance.get("%sYear".formatted(inputName));
        String month = (String) subflowInstance.get("%sMonth".formatted(inputName));
        String day = (String) subflowInstance.get("%sDay".formatted(inputName));
        return getFormatedDateStringWithOptionalDayField(year, month, day);
    }

    public static String getFormatedDateStringWithOptionalDayField(String year, String month, String day) {
        if (year == null && month == null && day == null || (year + month + day).isBlank()) {
            return "";
        } else if (year == null || month == null || year.isEmpty() || month.isEmpty()) {
            throw new IllegalArgumentException("Date must be complete if specified");
        } else if (day == null || day.isEmpty()) {
            return month + "/" + year;
        } else {
            return formatToStringFromLocalDate(Optional.of(LocalDate.of(parseInt(year), parseInt(month), parseInt(day))));
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

    public static List<Map<String, Object>> getChildren(Map<String, Object> inputData) {
        return (List<Map<String, Object>>) inputData.getOrDefault("children", emptyList());
    }

    public static List<Map<String, Object>> getChildrenNeedingAssistance(Map<String, Object> inputData) {
        return getChildren(inputData).stream()
                .filter(child -> child.getOrDefault("needFinancialAssistanceForChild", "false").equals("true")).toList();
    }

    public static List<Map<String, Object>> getChildrenNotNeedingAssistance(Map<String, Object> inputData) {
        return getChildren(inputData).stream()
                .filter(child -> child.getOrDefault("needFinancialAssistanceForChild", "false").equals("false")).toList();
    }

    public static Map<String, List<Map<String, Object>>> getChildrenByStatus(Submission submission) {
        Map<String, List<Map<String, Object>>> children = new HashMap<>();
        children.put("needFinancialAssistance", getChildrenNeedingAssistance(submission.getInputData()));
        children.put("notNeedingFinancialAssistance", getChildrenNotNeedingAssistance(submission.getInputData()));
        return children;
    }

    public static List<Map<String, Object>> firstFourChildrenNeedingAssistance(Submission submission) {
        int childrenNeedingAssistance = getChildrenNeedingAssistance(submission.getInputData()).size();
        if (childrenNeedingAssistance < 4) {
            return getChildrenNeedingAssistance(submission.getInputData()).subList(0, childrenNeedingAssistance);
        } else {
            return getChildrenNeedingAssistance(submission.getInputData()).subList(0, 4);
        }
    }

    public static List<Map<String, Object>> getAdditionalChildrenNeedingAssistance(Submission submission) {
        int childrenNeedingAssistance = getChildrenNeedingAssistance(submission.getInputData()).size();
        return getChildrenNeedingAssistance(submission.getInputData()).subList(4, childrenNeedingAssistance);
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

    public static String convertToAbsoluteURLForEmailAndText(String shortCode, String baseUrl) {
        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromUriString(baseUrl);
        return builder.path("s/" + shortCode).build().toUriString();
    }

    public static String getProviderName(Map<String, Object> inputData) {
        if (inputData.containsKey("familyIntendedProviderName")) {
            return (String) inputData.get("familyIntendedProviderName");
        } else {
            return "";
        }

    }

    public static boolean hasNotChosenProvider(Submission submission) {
        return "false".equals(submission.getInputData().get("hasChosenProvider"));
    }

    public static boolean hasChosenProvider(Submission submission) {
        return "true".equals(submission.getInputData().get("hasChosenProvider"));
    }

    public static boolean hasProviderResponse(Submission submission) {
        return getProviderSubmissionId(submission).isPresent();
    }

    public static Optional<UUID> getProviderSubmissionId(Submission familySubmission) {
        if (familySubmission.getInputData().containsKey("providerResponseSubmissionId")) {
            return Optional.of(UUID.fromString(familySubmission.getInputData().get("providerResponseSubmissionId").toString()));
        }

        return Optional.empty();
    }

    public static boolean isSelectedAsProviderContactMethod(Map<String, Object> inputData, @NotBlank String providerContactMethod) {
        List<String> contactProviderMethodList = (List<String>) inputData.getOrDefault("contactProviderMethod[]", List.of());
        return contactProviderMethodList.contains(providerContactMethod);
    }

    public static boolean isSelectedAsProviderContactMethod(@NotNull Submission submission, @NotBlank String subflowUuid, @NotBlank String providerContactMethod) {
        Map<String, Object> subflow = submission.getSubflowEntryByUuid("contactProviders", subflowUuid);
        return subflow != null && isSelectedAsProviderContactMethod(subflow, providerContactMethod);
    }

    public static List<Map<String, Object>> getProviders(Map<String, Object> inputData){
        if (inputData.containsKey("providers")) {
           return(List) inputData.get("providers");
        }
        return emptyList();
    }

    public static Map<String, Object> getCurrentProvider(Map<String, Object> inputData, String uuid) {
        List<Map<String, Object>> providers = getProviders(inputData);

        Optional<Map<String, Object>> providerOptional = providers.stream()
                .filter(provider -> provider.get("uuid").equals(uuid)).findFirst();

        if (providerOptional.isPresent()) {
            return providerOptional.get();
        }

        if (providerOptional.isEmpty() && !uuid.equals("NO_PROVIDER")) {
            log.debug(String.format("There is a child care schedule associated with the providerUUID (%s) but no provider with "
                    + "that uuid", uuid));
        }
        return new HashMap<>();

    }

    /**
     * This method generates a suffix that can be added to the end of a message string to indicate if a providerSchedule includes whether
     * the provider has already started care.
     * @param currentProvider - The current providerSchedule
     * @return TRUE: a String <strong>already-started</strong>
     * FALSE: A String <strong>not-started</strong>
     */
    public static String getSuffixForMessagesWhereChildIsInCare(Map<String, Object> currentProvider) {
        if (currentProvider.containsKey("repeatForValue")) {
            if (currentProvider.get("repeatForValue").toString().equals("NO_PROVIDER")) {
                return "no-provider";
            }else {
                return currentProvider.getOrDefault("childInCare", "false").equals("true") ? PROVIDER_HAS_STARTED_CHILDCARE : PROVIDER_HAS_NOT_STARTED_CHILDCARE;
            }
        }else {
            log.warn("repeatForValue is missing in providerSchedules map");
            return null;
        }
    }

    public static String generateMessageKey(String prefix, String suffix) {
        return String.format("%s.%s", prefix, suffix);
    }
}
