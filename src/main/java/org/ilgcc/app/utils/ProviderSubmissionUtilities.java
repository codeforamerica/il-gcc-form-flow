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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.springframework.stereotype.Component;

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

    public static Map<String, String> getFamilySubmissionForProviderResponse(Optional<Submission> familySubmission) {
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
        Map<String, Object> applicationData = new HashMap<>();

        applicationData.putAll(getFamilySubmissionDataForEmails(familySubmission));
        applicationData.put("providerResponseContactEmail",
                providerSubmission.getInputData().getOrDefault("providerResponseContactEmail", ""));
        applicationData.put("providerName", getProviderResponseName(providerSubmission));
        applicationData.put("providerSubmissionId", providerSubmission.getId());
        applicationData.put("ccapStartDate",
                ProviderSubmissionUtilities.getCCAPStartDateFromProviderOrFamilyChildcareStartDate(familySubmission,
                        Optional.of(providerSubmission)));

        return applicationData;
    }

    public static Map<String, Object> getFamilySubmissionDataForEmails(Submission familySubmission) {
        return getFamilySubmissionDataForEmails(familySubmission, null);
    }

    public static Map<String, Object> getFamilySubmissionDataForEmails(Submission familySubmission,
            Map<String, Object> subflowData) {
        Map<String, Object> applicationData = new HashMap<>();

        applicationData.put("parentContactEmail",
                (String) familySubmission.getInputData().getOrDefault("parentContactEmail", ""));
        applicationData.put("parentFirstName", (String) familySubmission.getInputData().get("parentFirstName"));
        applicationData.put("ccrrName", (String) familySubmission.getInputData().getOrDefault("ccrrName", ""));
        applicationData.put("ccrrPhoneNumber", (String) familySubmission.getInputData().getOrDefault("ccrrPhoneNumber", ""));
        applicationData.put("childrenInitialsList",
                ProviderSubmissionUtilities.getChildrenInitialsListFromApplication(familySubmission));
        applicationData.put("confirmationCode", familySubmission.getShortCode());
        applicationData.put("familySubmissionId", familySubmission.getId());
        applicationData.put("familyPreferredLanguage", familySubmission.getInputData().getOrDefault("languageRead", "English"));
        applicationData.put("shareableLink", familySubmission.getInputData().getOrDefault("shareableLink", ""));
        applicationData.put("submittedDate", SubmissionUtilities.getFormattedSubmittedAtDate(familySubmission));
        applicationData.put("ccapStartDate",
                ProviderSubmissionUtilities.getCCAPStartDateFromProviderOrFamilyChildcareStartDate(familySubmission,
                        Optional.empty()));

        // provider specific fields can come from a subflow and not the main data
        Map<String, Object> data = subflowData == null ? familySubmission.getInputData() : subflowData;
        applicationData.put("familyIntendedProviderName", data.getOrDefault("familyIntendedProviderName", ""));
        applicationData.put("familyIntendedProviderEmail", data.getOrDefault("familyIntendedProviderEmail", ""));

        return applicationData;
    }

    public static List<Map<String, Object>> getChildrenDataForProviderResponse(Submission applicantSubmission) {
        List<Map<String, Object>> children = new ArrayList<>();

        if (!SubmissionUtilities.getChildrenNeedingAssistance(applicantSubmission).isEmpty()) {
            for (var child : SubmissionUtilities.getChildrenNeedingAssistance(applicantSubmission)) {
                Map<String, Object> childObject = new HashMap<>();
                String firstName = (String) child.get("childFirstName");
                String lastName = (String) child.get("childLastName");
                childObject.put("childName", String.format("%s %s", firstName, lastName));
                childObject.put("childAge", childAge(child));
                childObject.put("childCareHours", hoursRequested(child));
                childObject.put("childStartDate", child.get("ccapStartDate"));
                children.add(childObject);
            }
        }
        return children;
    }

    public static String formatChildNamesAsCommaSeparatedList(Submission applicantSubmission, String joiner) {
        List<Map<String, Object>> children = SubmissionUtilities.getChildrenNeedingAssistance(applicantSubmission);
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
        List<String> sameHoursEveryday = (List) child.get("childcareHoursSameEveryDay[]");
        List<String> daysRequested = (List) child.get("childcareWeeklySchedule[]");
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

    public static String getCCAPStartDateFromProviderOrFamilyChildcareStartDate(Submission familySubmission,
            Optional<Submission> providerSubmission) {
        String earliestDate = (String) familySubmission.getInputData()
                .getOrDefault("earliestChildcareStartDate", "");

        if (providerSubmission.isPresent()) {
            String providerCareStartDate = (String) providerSubmission.get().getInputData()
                    .getOrDefault("providerCareStartDate", "");

            if (!providerCareStartDate.isBlank()) {
                earliestDate = providerCareStartDate;
            }
        }

        return DateUtilities.convertDateToFullWordMonthPattern(earliestDate);

    }

    public static List<String> getChildrenInitialsListFromApplication(Submission familySubmission) {
        List<Map<String, Object>> children = SubmissionUtilities.getChildrenNeedingAssistance(familySubmission);
        List<String> childrenInitials = new ArrayList<String>();
        for (var child : children) {
            String firstName = (String) child.get("childFirstName");
            String lastName = (String) child.get("childLastName");
            childrenInitials.add(String.format("%s.%s.", firstName.toUpperCase().charAt(0), lastName.toUpperCase().charAt(0)));
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
}
