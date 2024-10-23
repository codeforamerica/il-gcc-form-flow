package org.ilgcc.app.utils;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.ilgcc.app.utils.SubmissionUtilities.MM_DD_YYYY;

import formflow.library.data.Submission;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProviderSubmissionUtilities {
    private final static Map<String, Integer> weekWithOffset = Map.of(
            "MONDAY", 3, "TUESDAY", 3, "WEDNESDAY", 5, "THURSDAY", 5, "FRIDAY", 5, "SATURDAY", 4, "SUNDAY", 3);

    public static Optional<UUID> getClientId(Submission providerSubmission) {
        if (providerSubmission.getInputData().containsKey("familySubmissionId")) {
            String applicantID = (String) providerSubmission.getInputData().get("familySubmissionId");
            return Optional.of(UUID.fromString(applicantID));
        }
        return null;
    }

    public static Map<String, String> getApplicantSubmissionForProviderResponse(Optional<Submission> applicantSubmission) {
        Map<String, String> applicationData = new HashMap<>();

        if (applicantSubmission.isPresent()) {
            Map<String, Object> applicantInputs = applicantSubmission.get().getInputData();
            applicationData.put("clientResponseConfirmationCode", applicantSubmission.get().getShortCode());
            applicationData.put("clientResponseParentName", getApplicantName(applicantInputs));
        }

        return applicationData;
    }

    private static String getApplicantName(Map<String, Object> applicantInputData) {
        String firstName = SubmissionUtilities.applicantFirstName(applicantInputData);
        String lastName = (String) applicantInputData.get("parentLastName");

        return String.format("%s %s", firstName, lastName);
    }

    public static List<Map<String, String>> getChildrenDataForProviderResponse(Submission applicantSubmission) {
        List<Map<String, String>> children = new ArrayList<>();

        if (SubmissionUtilities.getChildrenNeedingAssistance(applicantSubmission).size() > 0) {
            for (var child : SubmissionUtilities.getChildrenNeedingAssistance(applicantSubmission)) {
                Map<String, String> childObject = new HashMap<>();
                String firstName = (String) child.get("childFirstName");
                String lastName = (String) child.get("childLastName");

                childObject.put("childName", String.format("%s %s", firstName, lastName));
                childObject.put("childAge", String.format("Age %s", childAge(child)));
                childObject.put("childCareHours", hoursRequested(child));
                childObject.put("childStartDate", (String) child.get("ccapStartDate"));
                children.add(childObject);
            }
        }
        return children;
    }
    
    public static String formatChildNamesAsComaSeperatedList(Submission applicantSubmission) {
        List<Map<String, Object>> children = SubmissionUtilities.getChildrenNeedingAssistance(applicantSubmission);
        List<String> childNames = new ArrayList<>();
        for (var child : children) {
            String firstName = (String) child.get("childFirstName");
            String lastName = (String) child.get("childLastName");
            childNames.add(String.format("%s %s", firstName, lastName));
        }
        if (childNames.isEmpty()) {
            return "";
        } else if (childNames.size() == 1) {
            return childNames.get(0); // Single name, no 'and'
        } else if (childNames.size() == 2) {
            return String.join(" and ", childNames); // Two childNames, join with 'and'
        } else {
            // More than 2 childNames, use comma for all but the last one
            String last = childNames.remove(childNames.size() - 1); // Remove and keep the last name
            return String.join(", ", childNames) + " and " + last; // Join remaining with commas, append 'and last'
        }
    }

    private static Integer childAge(Map<String, Object> child) {
        var bdayString = String.format("%s/%s/%s", child.get("childDateOfBirthMonth"), child.get("childDateOfBirthDay"),
                child.get("childDateOfBirthYear"));

        LocalDate dateOfBirth = LocalDate.parse(bdayString, MM_DD_YYYY);
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
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

    private static String hoursRequested(Map<String, Object> child) {
        List sameHoursEveryday = (List) child.get("childcareHoursSameEveryDay[]");
        List daysRequested = (List) child.get("childcareWeeklySchedule[]");
        List<String> dateString = new ArrayList<>();
        for (var day : daysRequested) {
            if (sameHoursEveryday.equals(List.of("yes"))) {
                dateString.add(String.format("<li>%s %s to %s</li>", day, formatHourschedule(child, "Start", "AllDays"),
                        formatHourschedule(child, "End", "AllDays")));
            } else {
                dateString.add(String.format("<li>%s %s to %s</li>", day, formatHourschedule(child, "Start", (String) day),
                        formatHourschedule(child, "End", (String) day)));
            }
        }
        return String.join("", dateString);
    }

    public static ZonedDateTime threeBusinessDaysFromSubmittedAtDate(OffsetDateTime submittedAtDate) {
        ZoneId chicagoTimeZone = ZoneId.of("America/Chicago");
        ZonedDateTime submissionInChicagoTime = submittedAtDate.atZoneSameInstant(chicagoTimeZone);
        Integer dayOffset = weekWithOffset.get(submissionInChicagoTime.getDayOfWeek().toString());
        return submissionInChicagoTime.plusDays(dayOffset);
    }

    public static boolean providerApplicationHasExpired(Submission submission, ZonedDateTime todaysDate){
        return submission.getSubmittedAt() != null && MINUTES.between(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submission.getSubmittedAt()), todaysDate) > 0;
    }
}
