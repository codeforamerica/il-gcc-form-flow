package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
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
public class ValidateMonthYearPartnerActivitiesProgram extends VerifyDate {

    @Autowired
    MessageSource messageSource;

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {

        Locale locale = LocaleContextHolder.getLocale();
        Map<String, List<String>> errorMessages = new HashMap<>();
        Map<String, Object> inputData = formSubmission.getFormData();

        String startGroup = "partnerProgramStart";
        String startMonthField = startGroup.concat("Month");
        String startDayField = startGroup.concat("Day");
        String startYearField = startGroup.concat("Year");
        String startMonth = inputData.get(startMonthField).toString();
        String startDay = inputData.get(startDayField).toString().isEmpty() ? "01" : inputData.get(startDayField).toString();
        String startYear = inputData.get(startYearField).toString();
        String startDate = String.format("%s/%s/%s", startMonth, startDay, startYear);

        String endGroup = "partnerProgramEnd";
        String endMonthField = endGroup.concat("Month");
        String endDayField = endGroup.concat("Day");
        String endYearField = endGroup.concat("Year");
        String endMonth = inputData.get(endMonthField).toString();
        String endDay = inputData.get(endDayField).toString().isEmpty() ? "01" : inputData.get(endDayField).toString();
        String endYear = inputData.get(endYearField).toString();
        String endDate = String.format("%s/%s/%s", endMonth, endDay, endYear);

        // Error checks for only months or only years present
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

        // Error checks for valid dates when present
        if (!(startMonth.isEmpty() && startYear.isEmpty())) {
            if (isDateInvalid(startDate)) {
                errorMessages.put(startGroup,
                        List.of(messageSource.getMessage("errors.invalid-date-format", null, locale)));
            } else {
                if (!isBetweenNowAndMinDate(startDate)) {
                    errorMessages.put(startGroup,
                            List.of(messageSource.getMessage("errors.invalid-date-range", null, locale)));
                }
            }
        }

        if (!(endMonth.isEmpty() && endYear.isEmpty())) {
            if (isDateInvalid(endDate)) {
                errorMessages.put(endGroup,
                        List.of(messageSource.getMessage("errors.invalid-date-format", null, locale)));
            } else {
                if (!isBetweenNowAndMinDate(endDate)) {
                    errorMessages.put(endGroup,
                            List.of(messageSource.getMessage("errors.invalid-date-range", null, locale)));
                }
            }
        }

        return errorMessages;
    }
}

