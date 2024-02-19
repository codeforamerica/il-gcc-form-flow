package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

abstract class VerifyDate implements Action {

  protected boolean isDateInvalid(String date) {
    try {
      DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");

      dtf.parseDateTime(date);
    } catch (Exception e) {
      return true;
    }
    return false;
  }

 protected boolean isDateNotWithinSupportedRange (DateTime date, DateTime earliest_supported_date, DateTime latest_supported_date){
   return (date.isBefore(earliest_supported_date) || date.isAfter(latest_supported_date));
 }
}
