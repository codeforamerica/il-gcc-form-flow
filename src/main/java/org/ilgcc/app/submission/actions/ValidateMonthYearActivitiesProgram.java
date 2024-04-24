package org.ilgcc.app.submission.actions;


import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
public class ValidateMonthYearActivitiesProgram extends VerifyDate {

    @Autowired
    MessageSource messageSource;

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {

        Locale locale = LocaleContextHolder.getLocale();
        DateTime minDate = DTF.parseDateTime("01/01/1901");
        Map<String, List<String>> errorMessages = new HashMap<>();
        Map<String, Object> inputData = formSubmission.getFormData();

        String startGroup = "activitiesProgramStart";
        String startMonthField = startGroup.concat("Month");
        String startDayField = startGroup.concat("Day");
        String startYearField = startGroup.concat("Year");
        String startMonth = inputData.get(startMonthField).toString();
        String startDay = inputData.getOrDefault(startDayField, "01").toString();
        String startYear = inputData.get(startYearField).toString();
        String startDateString = String.format("%s/%s/%s", startMonth, startDay, startYear);

        String endGroup = "activitiesProgramEnd";
        String endMonthField = endGroup.concat("Month");
        String endDayField = endGroup.concat("Day");
        String endYearField = endGroup.concat("Year");
        String endMonth = inputData.get(endMonthField).toString();
        String endDay = inputData.getOrDefault(endDayField, "01").toString();
        String endYear = inputData.get(endYearField).toString();
        String endDateString = String.format("%s/%s/%s", endMonth, endDay, endYear);

        // If a month or year is provided, both must be included
        if (startMonth.isBlank() && !startYear.isBlank()) {
            errorMessages.put(startMonthField,
                    List.of(messageSource.getMessage("general.month.validation", null, locale)));
        }

        if (!startMonth.isBlank() && startYear.isBlank()) {
            errorMessages.put(startYearField,
                    List.of(messageSource.getMessage("general.year.validation", null, locale)));
        }

        if (endMonth.isBlank() && !endYear.isBlank()) {
            errorMessages.put(endMonthField,
                    List.of(messageSource.getMessage("general.month.validation", null, locale)));
        }

        if (!endMonth.isBlank() && endYear.isBlank()) {
            errorMessages.put(endYearField,
                    List.of(messageSource.getMessage("general.year.validation", null, locale)));
        }

        // TODO: add check to stop if either month or year are blank
        if (!errorMessages.isEmpty()) {
            return errorMessages;
        }

        // Ensure months and years create a valid date
        if (isDateInvalid(startDateString)) {
            errorMessages.put(startGroup,
                    List.of(messageSource.getMessage("errors.invalid-date-format", null, locale)));
        }

        if (isDateInvalid(endDateString)) {
            errorMessages.put(endGroup,
                    List.of(messageSource.getMessage("errors.invalid-date-format", null, locale)));
        }

        if (!errorMessages.isEmpty()) {
            return errorMessages;
        }

        // Ensure dates are before 1901
        DateTime startDate = DTF.parseDateTime(startDateString);
        if (this.isDateNotWithinSupportedRange(startDate, minDate, null)) {
            errorMessages.put(startGroup,
                    List.of(messageSource.getMessage("errors.invalid-date-range", null, locale)));
        }

        DateTime endDate = DTF.parseDateTime(endDateString);
        if (this.isDateNotWithinSupportedRange(endDate, minDate, null)) {
            errorMessages.put(endGroup,
                    List.of(messageSource.getMessage("errors.invalid-date-range", null, locale)));
        }

        return errorMessages;
    }
}
