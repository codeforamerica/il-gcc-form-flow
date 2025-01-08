package org.ilgcc.app.utils;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtilities {

    public static String getFormattedDateFromMonthDateYearInputs(String prefix, Map<String, Object> data) {
        String month = (String) data.get(prefix + "Month");
        String day = (String) data.get(prefix + "Day");
        String year = (String) data.get(prefix + "Year");
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
            DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");

            dtf.parseDateTime(date);
        } catch (Exception e) {
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
}
