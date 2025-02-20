package org.ilgcc.app.utils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.format.DateTimeFormatter;

public class DateUtilities {
    private final static String FULL_DATE_INPUT = "MM/dd/yyyy";
    private final static String FULL_DATE_FORMAT_OUTPUT_PATTER = "MMMM dd, yyyy";
    private final static String MONTH_YEAR_INPUT = "MM/yyyy";
    private final static String MONTH_YEAR_OUTPUT_PATTERN = "MMMM yyyy";
    public static String getFormattedDateFromMonthDateYearInputs(String prefix, Map<String, Object> data) {
        String month = (String) data.get(prefix + "Month");
        String day = (String) data.get(prefix + "Day");
        String year = (String) data.get(prefix + "Year");
        return formatDateStringFromMonthDayYear(month, day, year);
    }

    public static String formatDateStringFromMonthDayYear(String month, String day, String year) {
        String formattedMonth = "";
        String formattedDay = "";

        try {
            formattedMonth = String.format("%02d", Integer.parseInt(month));
            formattedDay = String.format("%02d", Integer.parseInt(day));
        } catch (NumberFormatException e) {
            formattedMonth = month;
            formattedDay = day;
        }
        return String.format("%s/%s/%s",
            formattedMonth,
            formattedDay,
            year);
    }

    public static boolean isDateInvalid(String date) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate.parse(date, dtf);
        } catch (DateTimeParseException e) {
            return true;
        }
        return false;
    }

    public static Optional<LocalDate> parseStringDate(String dateStr) {
        String pattern = "(\\d{1,2})/(\\d{1,2})/(\\d{4})";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(dateStr);

        try {
            if (matcher.matches()) {
                int month = Integer.parseInt(matcher.group(1));
                int day = Integer.parseInt(matcher.group(2));
                int year = Integer.parseInt(matcher.group(3));

                return Optional.of(LocalDate.of(year, month, day));
            }
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        return Optional.empty();
    }

    public static String getEarliestDate(String dateString1, String dateString2) {
        if (dateString1.isBlank()) {
            return dateString2;
        }
        if (dateString2.isBlank()) {
            return dateString1;
        }

        Optional<LocalDate> earliestDate = DateUtilities.parseStringDate(dateString1);
        Optional<LocalDate> childcareStartDate = DateUtilities.parseStringDate(dateString2);
        return earliestDate.get().isBefore(childcareStartDate.get()) ? dateString1 : dateString2;
    }

    public static String convertDateToFullWordMonthPattern(String dateStr){
        String fullDatePattern = "(\\d{1,2})/(\\d{1,2})/(\\d{4})";
        Pattern fullDateRegex = Pattern.compile(fullDatePattern);
        Matcher fullDateMatcher = fullDateRegex.matcher(dateStr);

        if (fullDateMatcher.matches()) {
            return replaceMonthIntegerWithWord(dateStr, FULL_DATE_INPUT, FULL_DATE_FORMAT_OUTPUT_PATTER);
        }

        String MonthYearDatePattern = "(\\d{1,2})/(\\d{4})";
        Pattern MonthYearRegex = Pattern.compile(MonthYearDatePattern);
        Matcher MonthYearMatcher = MonthYearRegex.matcher(dateStr);
        if (MonthYearMatcher.matches()) {
            return replaceMonthIntegerWithWord(dateStr, MONTH_YEAR_INPUT, MONTH_YEAR_OUTPUT_PATTERN);
        }
        return "";
    }

    public static String replaceMonthIntegerWithWord(String dateStr, String inputFormat, String outputFormat) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputFormat);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);

        LocalDate date = LocalDate.parse(dateStr, inputFormatter);
        return date.format(outputFormatter);
    }
    
    public static String formatDateToYearMonthDayHourCST(OffsetDateTime submittedAt) {
        ZonedDateTime centralTime = submittedAt.atZoneSameInstant(ZoneId.of("America/Chicago"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return centralTime.format(formatter);
    }
}
