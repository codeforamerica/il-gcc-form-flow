package org.ilgcc.app.utils;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.ilgcc.app.utils.SubmissionUtilities.MM_DD_YYYY;

import formflow.library.data.Submission;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

@Component
@Slf4j
public class ProviderSubmissionUtilities {

    private final static Map<String, Integer> DAY_OF_WEEK_WITH_BUSINESS_DAYS_OFFSET = Map.of(
            "MONDAY", 3, "TUESDAY", 3, "WEDNESDAY", 5, "THURSDAY", 5, "FRIDAY", 5, "SATURDAY", 4, "SUNDAY", 3);

    private final static Map<String, Integer> DAY_OF_WEEK_WITH_BUSINESS_DAYS_DECREMENT = Map.of(
            "MONDAY", 5, "TUESDAY", 5, "WEDNESDAY", 5, "THURSDAY", 3, "FRIDAY", 3, "SATURDAY", 3, "SUNDAY", 4);


    private final static List<DayOfWeek> WEEKENDS = List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    // From https://cms.illinois.gov/personnel/employeeresources/stateholidays.html
    private final static List<LocalDate> HOLIDAYS = List.of(
            LocalDate.of(2024, 12, 25),
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 1, 20),
            LocalDate.of(2025, 2, 12),
            LocalDate.of(2025, 2, 17),
            LocalDate.of(2025, 5, 26),
            LocalDate.of(2025, 6, 19),
            LocalDate.of(2025, 7, 4),
            LocalDate.of(2025, 9, 1),
            LocalDate.of(2025, 10, 13),
            LocalDate.of(2025, 11, 11),
            LocalDate.of(2025, 11, 27),
            LocalDate.of(2025, 11, 28),
            LocalDate.of(2025, 12, 25),
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 1, 19),
            LocalDate.of(2026, 2, 12),
            LocalDate.of(2026, 2, 16),
            LocalDate.of(2026, 5, 25),
            LocalDate.of(2026, 6, 19),
            LocalDate.of(2026, 7, 3),
            LocalDate.of(2026, 9, 7),
            LocalDate.of(2026, 10, 12),
            LocalDate.of(2026, 11, 3),
            LocalDate.of(2026, 11, 11),
            LocalDate.of(2026, 11, 26),
            LocalDate.of(2026, 11, 27),
            LocalDate.of(2026, 12, 25)
    );

    public static Optional<UUID> getFamilySubmissionId(Submission providerSubmission) {
        if (providerSubmission.getInputData().containsKey("familySubmissionId")) {
            String familySubmissionId = (String) providerSubmission.getInputData().get("familySubmissionId");
            return Optional.of(UUID.fromString(familySubmissionId));
        }
        return Optional.empty();
    }

    public static Optional<String> getFamilySubmissionShortCode(Submission providerSubmission) {
        if (providerSubmission.getInputData().containsKey("providerResponseFamilyShortCode")) {
            String providerResponseFamilyShortCode = (String) providerSubmission.getInputData().get(
                    "providerResponseFamilyShortCode");
            return Optional.of(providerResponseFamilyShortCode);
        }

        return Optional.empty();
    }

    public static Map<String, String> getFamilyConfirmationCodeAndParentName(Optional<Submission> familySubmission) {
        Map<String, String> applicationData = new HashMap<>();

        if (familySubmission.isPresent()) {
            Map<String, Object> applicantInputs = familySubmission.get().getInputData();
            applicationData.put("clientResponseConfirmationCode", familySubmission.get().getShortCode());
            applicationData.put("clientResponseParentName", getApplicantName(applicantInputs));
        }

        return applicationData;
    }

    private static String getApplicantName(Map<String, Object> applicantInputData) {
        String firstName = SubmissionUtilities.applicantFirstName(applicantInputData);
        String lastName = (String) applicantInputData.get("parentLastName");

        return String.format("%s %s", firstName, lastName);
    }

    public static Map<String, Object> getCombinedDataForEmails(Submission providerSubmission, Submission familySubmission) {
        return getCombinedDataForEmails(providerSubmission, familySubmission, null);
    }

    public static Map<String, Object> getCombinedDataForEmails(Submission providerSubmission, Submission familySubmission,
            Map<String, Object> subflowData) {
        Map<String, Object> applicationData = new HashMap<>();

        applicationData.putAll(getFamilySubmissionDataForEmails(familySubmission, subflowData));
        applicationData.putAll(getProviderSubmissionDataForEmails(providerSubmission));

        String earliestDate = (String) familySubmission.getInputData()
                .getOrDefault("earliestChildcareStartDate", "");
        applicationData.put("ccapStartDate",
                ProviderSubmissionUtilities.getCCAPStartDateFromProviderOrFamilyChildcareStartDate(earliestDate,
                        Optional.of(providerSubmission)));

        return applicationData;
    }

    public static Map<String, Object> getProviderSubmissionDataForEmails(Submission providerSubmission) {
        Map<String, Object> providerApplicationData = new HashMap<>();

        providerApplicationData.put("providerResponseContactEmail",
                providerSubmission.getInputData().getOrDefault("providerResponseContactEmail", ""));
        providerApplicationData.put("providerName", getProviderResponseName(providerSubmission));
        providerApplicationData.putAll(setProviderResponseName(providerSubmission));
        providerApplicationData.put("providerSubmissionId", providerSubmission.getId());
        providerApplicationData.put("providerResponseAgreeToCare", providerSubmission.getInputData().getOrDefault(
                "providerResponseAgreeToCare", ""));

        return providerApplicationData;
    }

    public static Map<String, Object> getFamilySubmissionDataForEmails(Submission familySubmission) {
        return getFamilySubmissionDataForEmails(familySubmission, null);
    }

    public static Map<String, Object> getFamilySubmissionDataForEmails(Submission familySubmission,
            Map<String, Object> subflowIteration) {
        Map<String, Object> applicationData = new HashMap<>();

        applicationData.put("parentContactEmail",
                (String) familySubmission.getInputData().getOrDefault("parentContactEmail", ""));
        applicationData.put("parentFirstName", (String) familySubmission.getInputData().get("parentFirstName"));
        applicationData.put("ccrrName", (String) familySubmission.getInputData().getOrDefault("ccrrName", ""));
        applicationData.put("ccrrPhoneNumber", (String) familySubmission.getInputData().getOrDefault("ccrrPhoneNumber", ""));

        applicationData.put("childrenInitialsList",
                ProviderSubmissionUtilities.getChildrenInitialsList(
                        SubmissionUtilities.getChildrenNeedingAssistance(familySubmission.getInputData())));
        applicationData.put("confirmationCode", familySubmission.getShortCode());
        applicationData.put("familySubmissionId", familySubmission.getId());
        applicationData.put("familyPreferredLanguage", familySubmission.getInputData().getOrDefault("languageRead", "English"));
        applicationData.put("shareableLink", familySubmission.getInputData().getOrDefault("shareableLink", ""));
        applicationData.put("submittedDate", SubmissionUtilities.getFormattedSubmittedAtDate(familySubmission));
        String earliestDate = (String) familySubmission.getInputData()
                .getOrDefault("earliestChildcareStartDate", "");
        applicationData.put("ccapStartDate",
                ProviderSubmissionUtilities.getCCAPStartDateFromProviderOrFamilyChildcareStartDate(earliestDate,
                        Optional.empty()));
        applicationData.put("hasMultipleProviders", hasMoreThan1Provider(familySubmission.getInputData()));
        applicationData.put("hasProviderAndNoProvider",
                SubmissionUtilities.hasSelectedAProviderAndNoProvider(familySubmission.getInputData()));

        // provider specific fields can come from a subflow and not the main data
        Map<String, Object> data = subflowIteration == null ? familySubmission.getInputData() : subflowIteration;
        applicationData.put("familyIntendedProviderName", data.getOrDefault("familyIntendedProviderName", ""));
        applicationData.put("familyIntendedProviderEmail", data.getOrDefault("familyIntendedProviderEmail", ""));
        applicationData.put("providerType", data.getOrDefault("providerType", ""));
        applicationData.put("childCareProgramName", data.getOrDefault("childCareProgramName", ""));
        applicationData.put("childCareProviderInitials",
                getInitials(data.getOrDefault("providerFirstName", "").toString(),
                        data.getOrDefault("providerLastName", "").toString()));
        if (subflowIteration != null) {
            Map<String, List<Map<String, Object>>> mergedChildrenAndSchedules =
                    SchedulePreparerUtility.getRelatedChildrenSchedulesForEachProvider(familySubmission.getInputData());
            applicationData.put("childrenInitialsList",
                    ProviderSubmissionUtilities.getChildrenInitialsList(mergedChildrenAndSchedules.get(data.get("uuid"))));

        }

        return applicationData;
    }

    private static boolean hasMoreThan1Provider(Map<String, Object> familyInputData) {
        List<Map<String, Object>> familyProviders = (List<Map<String, Object>>) familyInputData.get("providers");
        return familyProviders != null && familyProviders.size() > 1;
    }

    public static List<Map<String, Object>> getFamilyIntendedProviders(Submission familySubmission) {
        List<Map<String, Object>> displayProviders = new ArrayList<>();

        List<Map<String, Object>> providers = (List<Map<String, Object>>) familySubmission.getInputData()
                .getOrDefault("providers",
                        Collections.EMPTY_LIST);

        if (!providers.isEmpty()) {
            for (var provider : providers) {
                if (SubmissionStatus.INACTIVE.name().equals(provider.get("providerApplicationResponseStatus"))) {
                    continue;
                }
                Map<String, Object> providerObject = new HashMap<>();
                providerObject.put("uuid", provider.get("uuid"));
                providerObject.put("providerApplicationResponseStatus", provider.get("providerApplicationResponseStatus"));

                String providerType = provider.get("providerType").toString();
                if (providerType.equals("Individual")) {
                    providerObject.put("displayName", getInitials(provider.getOrDefault("providerFirstName", "").toString(),
                            provider.getOrDefault(
                                    "providerLastName", "").toString()));
                    providerObject.put("location", String.format("%s, %s", provider.getOrDefault("familyIntendedProviderCity",
                            "").toString(), provider.getOrDefault("familyIntendedProviderState",
                            "").toString()));
                    providerObject.put("providerType", provider.get("providerType"));
                    displayProviders.add(providerObject);
                } else {
                    providerObject.put("displayName", provider.getOrDefault("familyIntendedProviderName", ""));
                    providerObject.put("location", String.format("%s<br/>%s, %s", provider.getOrDefault(
                            "familyIntendedProviderAddress",
                            "").toString(), provider.getOrDefault(
                            "familyIntendedProviderCity",
                            "").toString(), provider.getOrDefault("familyIntendedProviderState",
                            "").toString()));
                    providerObject.put("providerType", provider.get("providerType"));
                    displayProviders.add(providerObject);
                }
            }
        }

        return displayProviders;
    }

    public static Map<String, Object> getCurrentProvider(Submission providerSubmission) {
        String currentProviderUuid = providerSubmission.getInputData().getOrDefault("currentProviderUuid", "").toString();
        List<Map<String, Object>> providers = (List<Map<String, Object>>) providerSubmission.getInputData()
                .getOrDefault("providersData",
                        Collections.EMPTY_LIST);

        Optional<Map<String, Object>> currentProvider = Optional.empty();
        if (!currentProviderUuid.isBlank() && !providers.isEmpty()) {
            currentProvider = providers.stream().filter(provider -> provider.get("uuid").equals(currentProviderUuid)).findFirst();
        }

        if (currentProvider.isPresent()) {
            return currentProvider.get();
        } else {
            return null;
        }
    }

    public static List<Map<String, Object>> getChildrenData(Map<String, Object> inputData) {
        List<Map<String, Object>> children = new ArrayList<>();

        List<Map<String, Object>> childrenNeedingAssistance = SubmissionUtilities.getChildrenNeedingAssistance(inputData);
        if (!childrenNeedingAssistance.isEmpty()) {
            for (var child : childrenNeedingAssistance) {
                children.add(setChildData(child));
            }
        }
        return children;
    }

    public static Map<String, Object> setChildData(Map<String, Object> child) {
        Map<String, Object> childObject = new HashMap<>();
        String firstName = (String) child.get("childFirstName");
        String lastName = (String) child.get("childLastName");
        childObject.put("childName", String.format("%s %s", firstName, lastName));
        childObject.put("childAge", childAge(child));
        childObject.put("childCareHours", hoursRequested(child));
        childObject.put("childStartDate", child.getOrDefault("ccapStartDate", "n/a"));
        return childObject;
    }

    public static String formatChildNamesAsCommaSeparatedList(Submission applicantSubmission, String joiner) {
        List<Map<String, Object>> children = SubmissionUtilities.getChildrenNeedingAssistance(applicantSubmission.getInputData());
        List<String> childNames = new ArrayList<>();
        for (var child : children) {
            String firstName = (String) child.get("childFirstName");
            String lastName = (String) child.get("childLastName");
            childNames.add(String.format("%s %s", firstName, lastName));
        }

        return formatListIntoReadableString(childNames, joiner);
    }

    private static Map<String, Integer> childAge(Map<String, Object> child) {
        String bdayString = String.format("%s/%s/%s", child.get("childDateOfBirthMonth"), child.get("childDateOfBirthDay"),
                child.get("childDateOfBirthYear"));

        Map<String, Integer> childAge = new HashMap<>();
        LocalDate dateOfBirth = LocalDate.parse(bdayString, MM_DD_YYYY);

        int childYears = Period.between(dateOfBirth, LocalDate.now()).getYears();
        int childMonths = Period.between(dateOfBirth, LocalDate.now()).getMonths();

        if (childYears <= 1) {
            childAge.put("months", ((childYears * 12) + childMonths));
        } else {
            childAge.put("years", childYears);
        }
        return childAge;

    }

    private static String formatHourschedule(Map<String, Object> child, String prefix, String day) {
        String hour = (String) child.get("childcare" + prefix + "Time" + day + "Hour");
        String minute = (String) child.get("childcare" + prefix + "Time" + day + "Minute");
        String amPm = (String) child.get("childcare" + prefix + "Time" + day + "AmPm");

        if (!hour.isEmpty() && !minute.isEmpty() && !amPm.isEmpty()) {
            return String.format("%s:%s %s", hour, minute, amPm);
        }

        return "";
    }

    public static Map<String, String> hoursRequested(Map<String, Object> child) {
        List<String> sameHoursEveryday = (List) child.getOrDefault("childcareHoursSameEveryDay[]", Collections.EMPTY_LIST);
        List<String> daysRequested = (List) child.getOrDefault("childcareWeeklySchedule[]", Collections.EMPTY_LIST);
        Map<String, String> dates = new LinkedHashMap<>();
        for (String day : daysRequested) {
            if (sameHoursEveryday.equals(List.of("yes"))) {
                dates.put(day, String.format("%s - %s", formatHourschedule(child, "Start", "AllDays"),
                        formatHourschedule(child, "End", "AllDays")));
            } else {
                dates.put(day, String.format("%s - %s", formatHourschedule(child, "Start", day),
                        formatHourschedule(child, "End", day)));
            }
        }
        return dates;
    }

    public static ZonedDateTime threeBusinessDaysFromSubmittedAtDate(OffsetDateTime submittedAtDate) {
        ZoneId chicagoTimeZone = ZoneId.of("America/Chicago");
        ZonedDateTime submittedAt = submittedAtDate.atZoneSameInstant(chicagoTimeZone);

        Integer daysToOffset = DAY_OF_WEEK_WITH_BUSINESS_DAYS_OFFSET.get(submittedAt.getDayOfWeek().toString());
        ZonedDateTime expiresAt = submittedAt.plusDays(daysToOffset);

        LocalDate submittedAtLocalDate = submittedAt.toLocalDate();
        LocalDate expiresAtLocalDate = expiresAt.toLocalDate();

        // If a holiday occurs after the submission and before/on the expiration date, we give the provider
        // one extra day to respond
        for (var holiday : HOLIDAYS) {
            if ((holiday.isAfter(submittedAtLocalDate) && holiday.isBefore(expiresAtLocalDate)) || holiday.isEqual(
                    expiresAtLocalDate)) {
                expiresAt = expiresAt.plusDays(1);
            }
        }

        // Because we might have had a holiday that pushes the expiration date into a weekend, we want to keep
        // pushing the expiration 1 day at a time until it's a Monday
        while (WEEKENDS.contains(expiresAt.getDayOfWeek())) {
            expiresAt = expiresAt.plusDays(1);
        }

        return expiresAt;
    }

    /**
     * Takes a date that we care about, and rolls it back 3 business days -- taking into account holidays and weekends We can use
     * this date, for example, to query for anything Submissions before this date and know that they are expired
     *
     * @param dateWeCareAbout
     * @return
     */
    public static OffsetDateTime threeBusinessDaysBeforeDate(OffsetDateTime dateWeCareAbout) {
        Integer daysToDecrement = DAY_OF_WEEK_WITH_BUSINESS_DAYS_DECREMENT.get(dateWeCareAbout.getDayOfWeek().toString());
        OffsetDateTime threeBusinessDaysBeforeDateWeCareAbout = dateWeCareAbout.minusDays(daysToDecrement);

        LocalDate dateWeCareAboutLocalDate = dateWeCareAbout.toLocalDate();
        LocalDate threeBusinessDaysBeforeLocalDate = threeBusinessDaysBeforeDateWeCareAbout.toLocalDate();

        // If a holiday occurs after the 3 business days before and before/on the original date we care about, we can push the
        // 3 days before date back 1 more day
        for (var holiday : HOLIDAYS) {
            if ((holiday.isAfter(threeBusinessDaysBeforeLocalDate) && holiday.isBefore(dateWeCareAboutLocalDate))
                    || holiday.isEqual(threeBusinessDaysBeforeLocalDate)) {
                threeBusinessDaysBeforeDateWeCareAbout = threeBusinessDaysBeforeDateWeCareAbout.minusDays(1);
            }
        }

        // Because we might have had a holiday that pushes the 3 days before date into a weekend, we want to keep
        // pushing the 3 days before date kac 1 day at a time until it's a Friday
        while (WEEKENDS.contains(threeBusinessDaysBeforeDateWeCareAbout.getDayOfWeek())) {
            threeBusinessDaysBeforeDateWeCareAbout = threeBusinessDaysBeforeDateWeCareAbout.minusDays(1);
        }

        return threeBusinessDaysBeforeDateWeCareAbout;
    }

    public static boolean providerApplicationHasExpired(Submission familySubmission) {
        // It is possible for submittedAtDate to be null when applicant downloads pdf in submit-ccap-terms screen

        LocalDate submittedAtDate =
                familySubmission.getSubmittedAt() != null ? familySubmission.getSubmittedAt().toLocalDate()
                        : null;
        if (submittedAtDate == null) {
            return false;
        }
        ZoneId chicagoTimeZone = ZoneId.of("America/Chicago");
        ZonedDateTime todaysDate = OffsetDateTime.now().atZoneSameInstant(chicagoTimeZone);
        return familySubmission.getSubmittedAt() != null &&
                MINUTES.between(
                        ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(familySubmission.getSubmittedAt()),
                        todaysDate) > 0;
    }

    public static Optional<SubmissionStatus> getProviderApplicationResponseStatus(Submission familySubmission) {
        boolean hasProviderApplicationResponseStatus = familySubmission.getInputData()
                .containsKey("providerApplicationResponseStatus");
        if (hasProviderApplicationResponseStatus) {
            return Optional.of(SubmissionStatus.valueOf(
                    familySubmission.getInputData().get("providerApplicationResponseStatus").toString()));
        } else {
            return Optional.empty();
        }
    }


    public static boolean isFamilySubmissionStatusNotInactive(@NotNull Submission familySubmission) {
        Optional<SubmissionStatus> statusOptional = getProviderApplicationResponseStatus(familySubmission);
        return statusOptional.isEmpty() || !SubmissionStatus.INACTIVE.equals(statusOptional.get());
    }

    public static SubmissionStatus calculateProviderApplicationResponseStatus(Submission familySubmission) {
        boolean familyHasProviderResponse = familySubmission.getInputData().containsKey("providerResponseSubmissionId");
        if (familyHasProviderResponse) {
            return SubmissionStatus.RESPONDED;
        }

        if (providerApplicationHasExpired(familySubmission)) {
            return SubmissionStatus.EXPIRED;
        } else {
            return SubmissionStatus.ACTIVE;
        }
    }

    public static String getCCAPStartDateFromProviderOrFamilyChildcareStartDate(String earliestDate,
            Optional<Submission> providerSubmission) {

        if (providerSubmission.isPresent()) {
            String providerCareStartDate = (String) providerSubmission.get().getInputData()
                    .getOrDefault("providerCareStartDate", "");

            if (!providerCareStartDate.isBlank()) {
                earliestDate = providerCareStartDate;
            }
        }

        return DateUtilities.convertDateToFullWordMonthPattern(earliestDate);

    }

    public static List<String> getChildrenInitialsList(List<Map<String, Object>> children) {
        List<String> childrenInitials = new ArrayList<String>();

        if (children != null) {
            for (var child : children) {
                String firstName = (String) child.get("childFirstName");
                String lastName = (String) child.get("childLastName");
                childrenInitials.add(getInitials(firstName, lastName));
            }
        }
        return childrenInitials;

    }

    public static String formatListIntoReadableString(List<String> dataList, String joiner) {
        if (dataList.isEmpty()) {
            return "";
        } else if (dataList.size() == 1) {
            return dataList.get(0); // Single name, no 'and'
        } else if (dataList.size() == 2) {
            return String.join(" " + joiner + " ", dataList); // if only 2 elements join with 'and'
        } else {
            // More than 2 elements in list, use comma for all but the last one
            String last = dataList.remove(dataList.size() - 1);
            return String.join(", ", dataList) + " " + joiner + " " + last; // Join remaining with commas, append 'and last'
        }
    }

    public static String getProviderResponseName(Submission providerSubmission) {
        String providerResponseBusinessName = (String) providerSubmission.getInputData()
                .getOrDefault("providerResponseBusinessName", "");
        if (!providerResponseBusinessName.isEmpty()) {
            return providerResponseBusinessName;
        }
        return (String) providerSubmission.getInputData().getOrDefault("providerResponseFirstName", "");
    }

    public static Map<String, Object> setProviderResponseName(Submission providerSubmission) {
        Map<String, Object> namesFields = new HashMap<>();

        String providerResponseBusinessName = (String) providerSubmission.getInputData()
                .getOrDefault("providerResponseBusinessName", "");

        String firstName = providerSubmission.getInputData().getOrDefault("providerResponseFirstName", "").toString();
        String lastName = providerSubmission.getInputData().getOrDefault("providerResponseLastName", "").toString();

        if (!providerResponseBusinessName.isBlank()) {
            namesFields.put("childCareProgramName",
                    providerResponseBusinessName);
        }

        if (!(firstName + lastName).isBlank()) {
            namesFields.put("childCareProviderInitials",
                    getInitials(firstName,
                            lastName));
        }

        return namesFields;
    }

    public static String getLocalizedChildCareHours(Map<String, Object> childSchedule, MessageSource messageSource) {
        Map<String, String> childCareHours = hoursRequested(childSchedule);

        List<String> dateString = new ArrayList<>();
        childCareHours.forEach((key, val) -> {
            String dayKey = String.format("general.week.%s", key);
            dateString.add(String.format("%s, %s</br>", messageSource.getMessage(dayKey, null,
                    LocaleContextHolder.getLocale()), val));
        });

        return String.join("", dateString);
    }

    public static String getInitials(String firstName, String lastName) {
        if (!firstName.isBlank() && !lastName.isBlank()) {
            return String.format("%s.%s.", firstName.toUpperCase().charAt(0), lastName.toUpperCase().charAt(0));
        }

        return "n/a";
    }

    /**
     * When a provider says they have not been paid by CCAP before, or if they are not sure, the provider is registering
     *
     * @return true if the provider is currently registering
     */
    public static boolean isProviderRegistering(@NotNull Submission submission) {
        return submission.getInputData().getOrDefault("providerPaidCcap", "false").toString().equals("false");
    }
}