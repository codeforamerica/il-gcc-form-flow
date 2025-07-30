package org.ilgcc.app.utils;

import static java.lang.Integer.parseInt;
import static java.util.Collections.emptyList;
import static org.ilgcc.app.utils.SchedulePreparerUtility.getRelatedChildrenSchedulesForEachProvider;

import formflow.library.data.Submission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.enums.SubmissionStatus;
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

    private static final String PROVIDER_HAS_STARTED_CHILDCARE = "already-started";
    private static final String PROVIDER_HAS_NOT_STARTED_CHILDCARE = "not-started";
    private static final String NO_PROVIDER = "no-provider";

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

    public static String generatePdfPath(String fileName, UUID submissionId) {
        return String.format("%s/%s", submissionId, fileName);
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

    public static boolean isNoProviderSubmission(Map<String, Object> familyInputData) {
        String hasChosenProvider = familyInputData.getOrDefault("hasChosenProvider", "").toString();

        if ("false".equals(hasChosenProvider)) {
            return true;
        }

        List<Map<String, Object>> providers = new ArrayList<>();
        if (familyInputData.containsKey("providers")) {
            providers = (List<Map<String, Object>>) familyInputData.getOrDefault("providers", emptyList());
            if (providers.isEmpty()) {
                return true;
            }
        }

        List<Map<String, Object>> childcareSchedules = (List<Map<String, Object>>) familyInputData.getOrDefault(
                "childcareSchedules", emptyList());
        if (!childcareSchedules.isEmpty()) {
            AtomicBoolean hasNotChosenAProviderOrHasNoProvidersScheduled = new AtomicBoolean(true);
            providers.forEach(provider -> {
                childcareSchedules.forEach(childcareSchedule -> {
                    if (childcareScheduleIncludesThisProvider(childcareSchedule, provider.get("uuid").toString())) {
                        hasNotChosenAProviderOrHasNoProvidersScheduled.set(false);
                    }
                });
            });
            return hasNotChosenAProviderOrHasNoProvidersScheduled.get();
        }
        return false;
    }

    public static boolean hasSelectedAProviderAndNoProvider(Map<String, Object> familyInputData) {
        Set<String> providersWithSchedules = getRelatedChildrenSchedulesForEachProvider(familyInputData).keySet();
        return providersWithSchedules.size() > 1 && providersWithSchedules.contains("NO_PROVIDER");

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

    public static boolean isSelectedAsProviderContactMethod(Map<String, Object> inputData,
            @NotBlank String providerContactMethod) {
        List<String> contactProviderMethodList = (List<String>) inputData.getOrDefault("contactProviderMethod[]", List.of());
        return contactProviderMethodList.contains(providerContactMethod);
    }

    public static boolean isSelectedAsProviderContactMethod(@NotNull Submission submission, @NotBlank String subflowUuid,
            @NotBlank String providerContactMethod) {
        Map<String, Object> subflow = submission.getSubflowEntryByUuid("contactProviders", subflowUuid);
        return subflow != null && isSelectedAsProviderContactMethod(subflow, providerContactMethod);
    }

    public static List<Map<String, Object>> getProviders(Map<String, Object> inputData) {
        if (inputData.containsKey("providers")) {
            return (List) inputData.get("providers");
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
     * Generates a suffix to append to a message string indicating the type of header based on whether the iteration is a no
     * provider iteration.  When there is a provider, is that child in care determines the suffix returned
     *
     * @param currentProvider the current provider schedule
     * @return a string representing the care status:
     * <ul>
     *     <li><strong>"already-started"</strong> if {@code childInCare} is {@code true}</li>
     *     <li><strong>"not-started"</strong> if {@code childInCare} is {@code false}</li>
     *     <li><strong>"no-provider"</strong> if {@code repeatForValue} is {@code NO_PROVIDER}</li>
     * </ul>
     */
    public static String getSuffixForMessagesWhereChildIsInCare(Map<String, Object> currentProvider) {
        if (currentProvider.containsKey("repeatForValue")) {
            if (currentProvider.get("repeatForValue").toString().equals("NO_PROVIDER")) {
                return NO_PROVIDER;
            } else {
                return currentProvider.getOrDefault("childInCare", "false").equals("true") ? PROVIDER_HAS_STARTED_CHILDCARE
                        : PROVIDER_HAS_NOT_STARTED_CHILDCARE;
            }
        } else {
            log.warn("repeatForValue is missing in providerSchedules map");
            return null;
        }
    }

    public static String generateMessageKey(String prefix, String suffix) {
        return String.format("%s.%s", prefix, suffix);
    }

    public static List<Map<String, Object>> getAnyChildcareSchedulesWithTheSameProvider(
            List<Map<String, Object>> childcareSchedules, String currentProviderUuidOrNoProvider,
            Map<String, Object> currentChildcareSchedule) {
        List<Map<String, Object>> remainingChildcareSchedules = childcareSchedules.stream()
                .filter(childcareSchedule -> !childcareSchedule.equals(currentChildcareSchedule))
                .toList();
        return remainingChildcareSchedules.stream()
                .filter(childcareSchedule -> childcareScheduleIncludesThisProvider(childcareSchedule,
                        currentProviderUuidOrNoProvider)).toList();
    }

    private static boolean childcareScheduleIncludesThisProvider(Map<String, Object> childcareSchedule,
            String providerUuidOrNoProvider) {
        List<Map<String, Object>> providerSchedules = (List<Map<String, Object>>) Optional.ofNullable(
                childcareSchedule.get("providerSchedules")).orElse(emptyList());
        return providerSchedules.stream().anyMatch(
                providerSchedule -> providerUuidOrNoProvider.equals(providerSchedule.getOrDefault("repeatForValue", "")));
    }

    public static Map<String, Object> getProviderScheduleByRepeatForValue(Map<String, Object> childcareSchedule,
            String repeatForValue) {
        List<Map<String, Object>> providerSchedules = (List<Map<String, Object>>) childcareSchedule.getOrDefault(
                "providerSchedules", emptyList());
        return providerSchedules.stream()
                .filter(providerSchedule -> providerSchedule.get("repeatForValue").equals(repeatForValue)).toList().stream()
                .findFirst().orElse(null);
    }

    public static void setCurrentProviderResponseInFamilyApplication(Submission providerSubmission, Submission familySubmission) {
        String currentProviderUuid = providerSubmission.getInputData().get("currentProviderUuid").toString();
        String providerResponseAgreeToCare = (String) providerSubmission.getInputData().get("providerResponseAgreeToCare");

        List<Map<String, Object>> providers = getProviders(familySubmission.getInputData());
        boolean allProvidersResponded = true;

        for (Map<String, Object> provider : providers) {
            if (currentProviderUuid.equals(provider.get("uuid").toString())) {
                provider.put("providerResponseSubmissionId", providerSubmission.getId().toString());
                provider.put("providerApplicationResponseStatus", SubmissionStatus.RESPONDED.name());
                provider.put("providerResponseAgreeToCare", providerResponseAgreeToCare);
                provider.put("providerResponseName", ProviderSubmissionUtilities.getProviderResponseName(providerSubmission));
            } else if (!provider.containsKey("providerApplicationResponseStatus") || !SubmissionStatus.RESPONDED.name()
                    .equals(provider.get("providerApplicationResponseStatus").toString())) {
                allProvidersResponded = false;
            }
        }

        familySubmission.getInputData().put("providers", providers);

        if (allProvidersResponded) {
            familySubmission.getInputData().put("providerApplicationResponseStatus", SubmissionStatus.RESPONDED.name());
        }
    }

    public static boolean haveAllProvidersResponded(Submission familySubmission) {
        return SubmissionStatus.RESPONDED.name().equals(familySubmission.getInputData().get("providerApplicationResponseStatus"));
    }

    public static boolean allChildcareSchedulesAreForTheSameProvider(Map<String, Object> inputData) {

        Map<String, List<Map<String, Object>>> relatedChildrenSchedulesByProvider = getRelatedChildrenSchedulesForEachProvider(
                inputData);

        List<String> providerUUIDs = relatedChildrenSchedulesByProvider.keySet().stream()
                .filter(providerUuid -> !providerUuid.equals("NO_PROVIDER")).toList();

        return providerUUIDs.size() == 1;
    }

    public static boolean isPreMultiProviderApplicationWithSingleProvider(Submission familySubmission) {
        HashMap<String, Object> inputData = (HashMap<String, Object>) familySubmission.getInputData();
        return inputData.containsKey("familyIntendedProviderName") && !inputData.containsKey("providers");
    }

    public static boolean isMultiProviderApplication(Submission familySubmission) {
        return !isPreMultiProviderApplicationWithSingleProvider(familySubmission) &&
                !allChildcareSchedulesAreForTheSameProvider(familySubmission.getInputData());
    }
}
