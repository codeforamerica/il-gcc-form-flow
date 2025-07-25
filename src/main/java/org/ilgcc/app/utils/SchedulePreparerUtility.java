package org.ilgcc.app.utils;

import static java.util.stream.Collectors.toMap;

import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SchedulePreparerUtility {


    private static String START = "Start";
    private static String END = "End";
    private static String AM_PM = "AmPm";

    public static Map<String, SubmissionField> createSubmissionFieldsFromDay(Map<String, Object> inputData,
            Map<String, String> dataMappings,
            String inputPrefixKey, String pdfPrefix, int iterator) {
        Map<String, SubmissionField> fields = new HashMap<>();

        for (var day : dataMappings.keySet()) {
            List.of(START, END).forEach(prefix -> {
                String baseKey = inputPrefixKey + prefix + "Time" + dataMappings.get(day);
                String amPmValue = (String) inputData.getOrDefault(baseKey + "AmPm", "");

                fields.put(pdfPrefix + day + prefix + "_" + iterator,
                        new SingleField(pdfPrefix + day + prefix, formatTimeString(inputData, baseKey), iterator));
                fields.put(pdfPrefix + day + prefix + AM_PM + "_" + iterator,
                        new SingleField(pdfPrefix + day + prefix + AM_PM, amPmValue, iterator));

            });
        }

        return fields;
    }

    public static Map<String, SubmissionField> createSubmissionFieldsFromDay(Map<String, Object> inputData,
            Map<String, String> dataMappings,
            String inputPrefixKey, String pdfPrefix) {
        Map<String, SubmissionField> fields = new HashMap<>();

        for (var day : dataMappings.keySet()) {
            List.of(START, END).forEach(prefix -> {
                String baseKey = inputPrefixKey + prefix + "Time" + dataMappings.get(day);
                String amPmValue = (String) inputData.getOrDefault(baseKey + "AmPm", "");

                fields.put(pdfPrefix + day + prefix,
                        new SingleField(pdfPrefix + day + prefix, formatTimeString(inputData, baseKey), null));
                fields.put(pdfPrefix + day + prefix + AM_PM,
                        new SingleField(pdfPrefix + day + prefix + AM_PM, amPmValue, null));

            });
        }
        return fields;
    }

    private static String formatTimeString(Map<String, Object> inputData, String baseKey) {
        String hourValue = (String) inputData.getOrDefault(baseKey + "Hour", "");
        String minuteValue = (String) inputData.getOrDefault(baseKey + "Minute", "");

        if (hourValue.isBlank() || minuteValue.isBlank()) {
            return "";
        }

        try {
            int hourInt = Integer.parseInt(hourValue);
            int minuteInt = Integer.parseInt(minuteValue);
            return String.format("%02d:%02d", hourInt, minuteInt);
        } catch (Exception e) {
            log.error("Unable to parse hour: " + hourValue + " or minute: " + minuteValue);
            return "";
        }
    }


    public static Map<String, String> hourlyScheduleKeys(Map<String, Object> inputData, String inputName,
            String weeklyScheduleInputName) {
        List<String> sameEveryDayField = getOptionalListField(
                inputData, "%sHoursSameEveryDay[]".formatted(inputName), Object::toString).orElse(List.of());

        boolean sameEveryDay = !sameEveryDayField.isEmpty() && sameEveryDayField.get(0).equalsIgnoreCase("Yes");

        List<String> daysInSchedule = (List) inputData.getOrDefault(weeklyScheduleInputName, List.of());

        if (sameEveryDay) {
            return daysInSchedule.stream().collect(toMap(day -> day, day -> "AllDays"));
        } else {
            return daysInSchedule.stream().collect(toMap(day -> day, day -> day));
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

    public static Map<String, Object> relatedSubflowIterationData(Map<String, Object> inputData, String relatedSubflowName,
            String subflowUUID) {

        List<Map<String, Object>> nestedIterations = (List<Map<String, Object>>) inputData.getOrDefault(relatedSubflowName,
                Collections.EMPTY_LIST);

        Optional<Map<String, Object>> currentIteration = nestedIterations.stream()
                .filter(iteration -> iteration.get("uuid").equals(subflowUUID)).findFirst();

        return currentIteration.orElse(null);
    }

    public static Map<String, List<Map<String, Object>>> getRelatedChildrenSchedulesForEachProvider(Map<String, Object> inputData) {
        List<Map<String, Object>> childcareSchedules = (List<Map<String, Object>>) inputData.getOrDefault("childcareSchedules",
                Collections.EMPTY_LIST);
        Set<Map<String, Object>> providerSchedules = new LinkedHashSet<>();

        if (childcareSchedules.isEmpty()) {
            return new HashMap<>();
        }

        childcareSchedules.forEach(childCareSchedule -> {
            List<Map<String, Object>> providerSchedulesTemp = (List) childCareSchedule.getOrDefault("providerSchedules",
                    Collections.EMPTY_LIST);
            providerSchedulesTemp.forEach(schedule -> {
                schedule.putAll(relatedSubflowIterationData(inputData, "children",
                        childCareSchedule.getOrDefault("childUuid", "").toString()));
                providerSchedules.add(schedule);
            });
        });

        return providerSchedules.stream().collect(Collectors.groupingBy(provider -> provider.get("repeatForValue").toString()));
    }
}