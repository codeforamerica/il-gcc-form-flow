package org.ilgcc.app.submission.actions;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;
import java.util.Map;

abstract class VerifyActivityDate extends VerifyDate {
    @Autowired
    MessageSource messageSource;
    public static final Locale locale = LocaleContextHolder.getLocale();

    protected void addErrorIfMonthWithoutYear(DateFields date, Map<String, List<String>> errorMessages) {
        if (date.getValueMonth().isBlank() && !date.getValueYear().isBlank()) {
            errorMessages.put(date.getFieldMonth(),
                    List.of(messageSource.getMessage("general.month.validation", null, locale)));
        }
    }

    protected void addErrorIfYearWithoutMonth(DateFields date, Map<String, List<String>> errorMessages) {
        if (!date.getValueMonth().isBlank() && date.getValueYear().isBlank()) {
            errorMessages.put(date.getFieldYear(),
                    List.of(messageSource.getMessage("general.year.validation", null, locale)));
        }
    }

    protected void addErrorIfDateIsInvalid(DateFields date, Map<String, List<String>> errorMessages) {
        if (!(date.getValueMonth().isEmpty() && date.getValueYear().isEmpty())) {
            if (isDateInvalid(date.getValueDate())) {
                errorMessages.put(date.getGroup(),
                        List.of(messageSource.getMessage("errors.invalid-date-format", null, locale)));
            } else {
                if (isBeforeMinDate(date.getValueDate())) {
                    errorMessages.put(date.getGroup(),
                            List.of(messageSource.getMessage("errors.invalid-min-date", null, locale)));
                }
            }
        }
    }

    @Data
    protected static class DateFields {
        private final String group;
        private final String fieldMonth;
        private final String fieldDay;
        private final String fieldYear;
        private final String valueMonth;
        private final String valueDay;
        private final String valueYear;
        private final String valueDate;

        public DateFields(String group, Map<String, Object> inputData) {
            this.group = group;
            this.fieldMonth = group.concat("Month");
            this.fieldDay = group.concat("Day");
            this.fieldYear = group.concat("Year");
            this.valueMonth = inputData.get(fieldMonth).toString();
            this.valueDay = inputData.get(fieldDay).toString().isEmpty() ? "01" : inputData.get(fieldDay).toString();
            this.valueYear = inputData.get(fieldYear).toString();
            this.valueDate = String.format("%s/%s/%s", valueMonth, valueDay, valueYear);
        }
    }
}
