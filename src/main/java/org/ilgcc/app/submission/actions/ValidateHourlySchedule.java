package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.SchedulePreparerUtility.getOptionalListField;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

@Slf4j
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
            ArrayList<String> startElementsWithMissingData = elementsWithMissingData(formData, startTimeGroupName);

            List<String> startTimeErrors = new ArrayList<>(List.of());
            if (!startElementsWithMissingData.isEmpty()) {
                if (startElementsWithMissingData.containsAll(INPUT_POSTFIXES)) {
                    errorMessages.put(startTimeGroupName,
                            List.of(messageSource.getMessage("errors.validate.start.time", null, null)));
                } else {
                    if (startElementsWithMissingData.contains("Hour")) {
                        startTimeErrors.add(messageSource.getMessage("errors.validate.time-hour", null, null));
                    }
                    if (startElementsWithMissingData.contains("Minute") || minuteDataIsInvalid(formData, startTimeGroupName)) {
                        startTimeErrors.add(messageSource.getMessage("errors.validate.minute", null, null));
                    }
                    if (startElementsWithMissingData.contains("AmPm")) {
                        startTimeErrors.add(messageSource.getMessage("errors.validate.time-ampm", null, null));
                    }
                    errorMessages.put(startTimeGroupName, startTimeErrors);
                }

            } else if (minuteDataIsInvalid(formData, startTimeGroupName)) {
                errorMessages.put(startTimeGroupName,
                        List.of(messageSource.getMessage("errors.validate.start.time", null, null)));
            }


            String endTimeGroupName = inputPrefix + "EndTime" + day;
            ArrayList<String> endElementsWithMissingData = elementsWithMissingData(formData, endTimeGroupName);
            List<String> endTimeErrors = new ArrayList<>(List.of());
            if (!endElementsWithMissingData.isEmpty()) {
                if (endElementsWithMissingData.containsAll(INPUT_POSTFIXES)) {
                    errorMessages.put(endTimeGroupName,
                            List.of(messageSource.getMessage("errors.validate.end.time", null, null)));
                } else {
                    if (endElementsWithMissingData.contains("Hour")) {
                        endTimeErrors.add(messageSource.getMessage("errors.validate.time-hour", null, null));
                    }
                    if (endElementsWithMissingData.contains("Minute") || minuteDataIsInvalid(formData, endTimeGroupName)) {
                        endTimeErrors.add(messageSource.getMessage("errors.validate.minute", null, null));
                    }
                    if (endElementsWithMissingData.contains("AmPm")) {
                        endTimeErrors.add(messageSource.getMessage("errors.validate.time-ampm", null, null));
                    }

                    errorMessages.put(endTimeGroupName, endTimeErrors);
                }
            } else if (minuteDataIsInvalid(formData, endTimeGroupName)) {
                errorMessages.put(endTimeGroupName,
                        List.of(messageSource.getMessage("errors.validate.end.time", null, null)));
            }


        });

        return errorMessages;
    }

    protected List<String> daysToCheck(Map<String, Object> formData) {
        List<String> sameEveryDayField = getOptionalListField(
                formData, "%sHoursSameEveryDay[]".formatted(inputPrefix), Object::toString).orElse(List.of());
        boolean sameEveryDay = !sameEveryDayField.isEmpty() && sameEveryDayField.get(0).equalsIgnoreCase("Yes");

        if (sameEveryDay) {
            return List.of("AllDays");
        } else {
            return List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        }
    }

    protected ArrayList<String> elementsWithMissingData(Map<String, Object> inputtedData, String fieldName) {
        ArrayList<String> missingData = new ArrayList<>();
        if (inputtedData.containsKey(fieldName + "Minute")) {
            INPUT_POSTFIXES.forEach(pf -> {
                if (inputtedData.getOrDefault(fieldName + pf, "").toString().isEmpty()) {
                    missingData.add(pf);
                }
                ;
            });
        }
        return missingData;
    }

    protected Boolean minuteDataIsInvalid(Map<String, Object> inputtedData, String fieldName) {
        AtomicBoolean minuteDataIsInvalid = new AtomicBoolean(false);
        if (inputtedData.containsKey(fieldName + "Minute")) {
            try {
                int minuteValue = Integer.valueOf(inputtedData.get(fieldName + "Minute").toString());
                if (minuteValue < 0 || minuteValue > 59) {
                    minuteDataIsInvalid.set(true);
                }
            } catch (Exception e) {
                log.error("Unable to parse minute value: " + inputtedData.get(fieldName + "Minute").toString());
                return true;
            }
        }
        return minuteDataIsInvalid.get();
    }

}
