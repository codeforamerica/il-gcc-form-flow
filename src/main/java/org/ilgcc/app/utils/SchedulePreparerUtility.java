package org.ilgcc.app.utils;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.function.Function;
import org.ilgcc.app.utils.ActivitySchedules.ConsistentHourlySchedule;
import org.ilgcc.app.utils.ActivitySchedules.HourlySchedule;
import org.ilgcc.app.utils.ActivitySchedules.LocalTimeRange;
import org.ilgcc.app.utils.ActivitySchedules.PerDayHourlySchedule;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.function.Function.identity;
import static org.ilgcc.app.utils.SubmissionUtilities.*;

@Component
public class SchedulePreparerUtility {
    protected static DateTimeFormatter CLOCK_TIME_OF_AM_PM = DateTimeFormatter.ofPattern("hh:mm");
    protected static DateTimeFormatter AM_PM_OF_DAY = DateTimeFormatter.ofPattern("a");

    public static Map<String, SubmissionField> createSubmissionFieldsFromSchedule(LocalTimeRange schedule, DayOfWeekOption day, String fieldPrefixKey) {
        Map<String, SubmissionField> fields = new HashMap<>();
        if (!fieldPrefixKey.isBlank()) {
            String fieldPrefix = fieldPrefixKey + day.name();
            fields.put(fieldPrefix + "Start",
                new SingleField(fieldPrefix + "Start", schedule.startTime().format(CLOCK_TIME_OF_AM_PM), null));
            fields.put(fieldPrefix + "StartAmPm",
                new SingleField(fieldPrefix + "StartAmPm", schedule.startTime().format(AM_PM_OF_DAY), null));
            fields.put(fieldPrefix + "End",
                new SingleField(fieldPrefix + "End", schedule.endTime().format(CLOCK_TIME_OF_AM_PM), null));
            fields.put(fieldPrefix + "EndAmPm",
                new SingleField(fieldPrefix + "EndAmPm", schedule.endTime().format(AM_PM_OF_DAY), null));
        }
        return fields;
    }

    public static Map<String, SubmissionField> createSubmissionFieldsFromSchedule(LocalTimeRange schedule, DayOfWeekOption day, String fieldPrefixKey, int iterator) {
        Map<String, SubmissionField> fields = new HashMap<>();
        if (!fieldPrefixKey.isBlank()) {
            String fieldPrefix = fieldPrefixKey + day.name();
            fields.put(fieldPrefix + "Start_"+iterator,
                new SingleField(fieldPrefix + "Start", schedule.startTime().format(CLOCK_TIME_OF_AM_PM), iterator));
            fields.put(fieldPrefix + "StartAmPm_"+iterator,
                new SingleField(fieldPrefix + "StartAmPm", schedule.startTime().format(AM_PM_OF_DAY), iterator));
            fields.put(fieldPrefix + "End_"+iterator,
                new SingleField(fieldPrefix + "End", schedule.endTime().format(CLOCK_TIME_OF_AM_PM), iterator));
            fields.put(fieldPrefix + "EndAmPm_"+iterator,
                new SingleField(fieldPrefix + "EndAmPm", schedule.endTime().format(AM_PM_OF_DAY), iterator));
        }
        return fields;
    }

    public static Optional<List<DayOfWeekOption>> getDaysOfWeekField(Map<String, Object> inputData, String inputName) {
        Object value = inputData.get(inputName);
        if (value == null) {
            return Optional.empty();
        } else if (value instanceof List<?> valueList) {
            return Optional.of(valueList.stream().map(d -> DayOfWeekOption.valueOf(d.toString())).toList());
        } else {
            throw new IllegalArgumentException("Weekdays field does not contain a list");
        }
    }

    public static Optional<HourlySchedule> getHourlySchedule(
        Submission submission, String inputName, String weeklyScheduleInputName) {
        return getHourlySchedule(submission.getInputData(), inputName, weeklyScheduleInputName);
    }

    public static Optional<HourlySchedule> getHourlySchedule(
        Map<String, Object> inputData, String inputName, String weeklyScheduleInputName) {
        Optional<List<DayOfWeekOption>> daysActive = getDaysOfWeekField(inputData, weeklyScheduleInputName);
        if (daysActive.isEmpty()) {
            return Optional.empty();
        }

        List<String> sameEveryDayField = getOptionalListField(
            inputData, "%sHoursSameEveryDay[]".formatted(inputName), Object::toString).orElse(List.of());
        boolean sameEveryDay = !sameEveryDayField.isEmpty() && sameEveryDayField.get(0).equalsIgnoreCase("Yes");

        if (sameEveryDay) {
            LocalTimeRange scheduleEveryDay = getTimeRangeInput(inputData, inputName, "AllDays").orElseThrow();
            return Optional.of(new ConsistentHourlySchedule(daysActive.get(), scheduleEveryDay));
        } else {
            var dailyScheduleMap = daysActive.get().stream().collect(
                toImmutableMap(
                    identity(),
                    day -> getTimeRangeInput(inputData, inputName, day.name()).orElseThrow()));
            return Optional.of(new PerDayHourlySchedule(dailyScheduleMap));
        }
    }

    public static <T> Optional<List<T>> getOptionalListField(
        Map<String, Object> inputData, String fieldName, Function<Object, T> converter) {
        Object value = inputData.get(fieldName);
        if (value == null) {
            return Optional.empty();
        } else if (value instanceof List<?> valueList) {
            return Optional.of(valueList.stream().map(converter).toList());
        } else {
            throw new IllegalArgumentException("List field does not contain a list");
        }
    }
}