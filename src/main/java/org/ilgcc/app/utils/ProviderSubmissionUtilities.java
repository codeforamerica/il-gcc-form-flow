package org.ilgcc.app.utils;

import static org.ilgcc.app.utils.SubmissionUtilities.MM_DD_YYYY;

import formflow.library.data.Submission;
import java.time.LocalDate;
import java.time.Period;
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

    public static Optional<UUID> getClientId(Submission providerSubmission) {
        if (providerSubmission.getInputData().containsKey("clientApplicationId")) {
            String applicantID = (String) providerSubmission.getInputData().get("clientApplicationId");
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
}
