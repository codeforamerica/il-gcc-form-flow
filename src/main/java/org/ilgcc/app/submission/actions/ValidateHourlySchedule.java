package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.SchedulePreparerUtility.getOptionalListField;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.context.MessageSource;

public class ValidateHourlySchedule implements Action {
    private final List<String> INPUT_POSTFIXES = List.of("Minute", "Hour", "AmPm");
    private final MessageSource messageSource;
    private final String inputPrefix;

    public ValidateHourlySchedule(MessageSource messageSource, String inputPrefix) {
        this.messageSource = messageSource;
        this.inputPrefix = inputPrefix;
    }

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {

        Map<String, Object> formData = formSubmission.getFormData();

        Map<String, List<String>> errorMessages = new java.util.HashMap<>(Collections.emptyMap());

        daysToCheck(formData).forEach(day -> {
            String startTimeGroupName = inputPrefix + "StartTime" + day;
            if (hasMissingData(formData, startTimeGroupName)) {
                errorMessages.put(startTimeGroupName,
                        List.of(messageSource.getMessage("errors.validate.start.time", null, null)));
            }

            String endTimeGroupName = inputPrefix + "EndTime" + day;
            if (hasMissingData(formData, endTimeGroupName)) {
                errorMessages.put(endTimeGroupName,
                        List.of(messageSource.getMessage("errors.validate.end.time", null, null)));
            }
        });

        return errorMessages;
    }

    protected List<String> daysToCheck(Map<String, Object> formData){
        List<String> sameEveryDayField = getOptionalListField(
                formData, "%sHoursSameEveryDay[]".formatted(inputPrefix), Object::toString).orElse(List.of());
        boolean sameEveryDay = !sameEveryDayField.isEmpty() && sameEveryDayField.get(0).equalsIgnoreCase("Yes");

        if (sameEveryDay) {
            return List.of("AllDays");
        } else{
            return List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        }
    }

    protected Boolean hasMissingData(Map<String, Object> inputtedData, String fieldName) {
        AtomicBoolean hasMissingData = new AtomicBoolean(false);
        if(inputtedData.containsKey(fieldName+"Hour")){
            INPUT_POSTFIXES.forEach(pf -> {
                if (inputtedData.get(fieldName + pf).toString().isEmpty()) {
                    hasMissingData.set(true);
                };
            });
        }
        return hasMissingData.get();
    }

}