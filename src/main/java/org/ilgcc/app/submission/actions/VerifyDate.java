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

 protected boolean isDateNotWithinSupportedRange (DateTime date, DateTime earliestSupportedDate, DateTime latestSupportedDate){
   if(latestSupportedDate == null){
     return (date.withTimeAtStartOfDay().isBefore(earliestSupportedDate.withTimeAtStartOfDay()) );
   }
    return (date.withTimeAtStartOfDay().isBefore(earliestSupportedDate.withTimeAtStartOfDay()) || date.withTimeAtStartOfDay().isAfter(latestSupportedDate.withTimeAtStartOfDay()));
 }
}
