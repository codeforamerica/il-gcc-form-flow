package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import java.time.LocalDate;
import java.util.Optional;
import org.ilgcc.app.utils.DateUtilities;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

abstract class VerifyDate implements Action {

    public static final DateTimeFormatter DTF = DateTimeFormat.forPattern("MM/dd/yyyy");
    public static final DateTime MIN_DATE = DTF.parseDateTime("01/01/1901");

    protected boolean isDateInvalid(String date) {
        try {
            DTF.parseDateTime(date);
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    protected boolean isBeforeMinDate(String dateAsString) {
        try {
            DateTime date = DTF.parseDateTime(dateAsString);
            return date.isBefore(MIN_DATE.getMillis());
        } catch (Exception e) {
            return true;
        }
    }

    protected boolean isNotBetweenNowAndMinDate(String dateAsString) {
        try {
            DateTime date = DTF.parseDateTime(dateAsString);
            return date.isBefore(MIN_DATE.getMillis()) || !date.isBeforeNow();
        } catch (Exception e) {
            return true;
        }
    }

    protected boolean isDateNotWithinSupportedRange(DateTime date, DateTime earliestSupportedDate, DateTime latestSupportedDate) {
        if (latestSupportedDate == null) {
            return (date.withTimeAtStartOfDay().isBefore(earliestSupportedDate.withTimeAtStartOfDay()));
        }
        return (date.withTimeAtStartOfDay().isBefore(earliestSupportedDate.withTimeAtStartOfDay()) || date.withTimeAtStartOfDay()
                .isAfter(latestSupportedDate.withTimeAtStartOfDay()));
    }

    protected String getEarliestDate(String dateString1, String dateString2) {
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
