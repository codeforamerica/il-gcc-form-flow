package org.ilgcc.app.utils;

import java.util.Map;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtilities {
  public static String getFormattedDateFromMonthDateYearInputs(String prefix, Map<String, Object> data){
    return String.format("%s/%s/%s",
         data.get(prefix + "Month"),
         data.get(prefix + "Day"),
         data.get(prefix + "Year"));
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
}
