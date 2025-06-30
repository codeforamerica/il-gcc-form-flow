package org.ilgcc.app.submission.actions;

import static java.util.Collections.emptyList;
import static org.ilgcc.app.utils.SchedulePreparerUtility.relatedSubflowIterationData;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CopySameChildcareSchedule implements Action {

    @Override
    @SuppressWarnings("unchecked")
    public void run(FormSubmission formSubmission, Submission submission, String subflowUuid, String repeatForUuid) {
        Map<String, Object> formData = formSubmission.getFormData();
        boolean clientAgreesToCopy = formData.getOrDefault("sameSchedule", "false").equals("true");
        if (clientAgreesToCopy) {
            Map<String, Object> currentChildcareSchedule = submission.getSubflowEntryByUuid("childcareSchedules", subflowUuid);
            Map<String, Object> currentProviderSchedule = relatedSubflowIterationData(currentChildcareSchedule, "providerSchedules",
                repeatForUuid);
            Map<String, Object> matchingChildcareProviderSchedule = (Map<String, Object>) currentProviderSchedule.get("matchingProviderSchedule");
            if (matchingChildcareProviderSchedule != null) {
                //copy over weekly schedule
                formData.put("childcareWeeklySchedule[]", matchingChildcareProviderSchedule.get("childcareWeeklySchedule[]"));
                //copy over sameHoursEveryDay if selected
                List<String> sameHoursEveryDay = (List<String>) matchingChildcareProviderSchedule.getOrDefault("childcareHoursSameEveryDay[]", emptyList());
                if (sameHoursEveryDay.equals(List.of("yes"))) {
                    formData.put("childcareHoursSameEveryDay[]", List.of("yes"));
                }
                //copy values into currentProviderSchedule
                for(String key : getHoursScheduleKeys(matchingChildcareProviderSchedule)) {
                    formData.put(key, matchingChildcareProviderSchedule.get(key));
                }
            }
        }
    }
    public static List<String> getHoursScheduleKeys(Map<String, Object> childcareProviderSchedule ) {
        List<String> sameHoursEveryDay = (List) childcareProviderSchedule.getOrDefault("childcareHoursSameEveryDay[]", Collections.EMPTY_LIST);
        List<String> hoursScheduleKeys = new ArrayList<>();
        if (sameHoursEveryDay.equals(List.of("yes"))) {
            hoursScheduleKeys.add("childcareStartTimeAllDaysHour");
            hoursScheduleKeys.add("childcareStartTimeAllDaysMinute");
            hoursScheduleKeys.add("childcareStartTimeAllDaysAmPm");
            hoursScheduleKeys.add("childcareEndTimeAllDaysHour");
            hoursScheduleKeys.add("childcareEndTimeAllDaysMinute");
            hoursScheduleKeys.add("childcareEndTimeAllDaysAmPm");
        } else {
            List<String> childcareWeeklySchedule = (List<String>) childcareProviderSchedule.get("childcareWeeklySchedule[]");
            for (String day : childcareWeeklySchedule) {
                hoursScheduleKeys.add("childcareStartTime" + day + "Hour");
                hoursScheduleKeys.add("childcareStartTime" + day + "Minute");
                hoursScheduleKeys.add("childcareStartTime" + day + "AmPm");
                hoursScheduleKeys.add("childcareEndTime" + day + "Hour");
                hoursScheduleKeys.add("childcareEndTime" + day + "Minute");
                hoursScheduleKeys.add("childcareEndTime" + day + "AmPm");
            }
        }
        return hoursScheduleKeys;
    }
}
