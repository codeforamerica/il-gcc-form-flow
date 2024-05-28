package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PartnerSetJobWeeklyScheduleGroup implements Action {

  private final MessageSource messageSource;

  public static final List<String> WEEKDAYS = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

  public static final String DISPLAY_LABEL = "displayWeeklySchedule";

  public PartnerSetJobWeeklyScheduleGroup(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public void run(Submission submission, String id) {

    Map<String, Object> subflowEntry = submission.getSubflowEntryByUuid("partnerJobs", id);
    if (!subflowEntry.containsKey("activitiesJobWeeklySchedule[]")) {
      return;
    }

    var weeklySchedule = new ArrayList<>((List<String>) subflowEntry.get("activitiesJobWeeklySchedule[]"));
    var sortedDays = WEEKDAYS.stream().filter(weeklySchedule::contains).toList();
    String firstDay = null, lastDay = null;
    Locale locale = LocaleContextHolder.getLocale();

    Iterator<String> iter = WEEKDAYS.iterator();
    while (iter.hasNext() && !weeklySchedule.isEmpty()) {
      String day = iter.next();
      if (weeklySchedule.remove(day)) {
        if (firstDay == null) {
          firstDay = messageSource.getMessage("general.week." + day, null, locale);
        } else {
          lastDay = messageSource.getMessage("general.week." + day, null, locale);
        }
      } else if (firstDay != null) {
        var displayDays = formatWeekdaysSeparated(sortedDays, locale);
        subflowEntry.put(DISPLAY_LABEL, displayDays);
        return;
      }
    }

    subflowEntry.put(DISPLAY_LABEL, lastDay == null ? firstDay : "%s-%s".formatted(firstDay, lastDay));

  }

  private String formatWeekdaysSeparated(List<String> sortedDays, Locale locale) {
    return sortedDays.stream()
        .map(d -> messageSource.getMessage("general.week." + d, null, locale))
        .collect(Collectors.joining(", "));
  }
}
