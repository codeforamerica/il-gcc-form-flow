package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SetWeeklyScheduleGroup implements Action {

  private final MessageSource messageSource;

  public static final List<String> WEEKDAYS = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

  public SetWeeklyScheduleGroup(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public void run(Submission submission) {
    if (!submission.getInputData().containsKey("weeklySchedule[]")) {
      return;
    }

    var weeklySchedule = new ArrayList<>((List<String>) submission.getInputData().get("weeklySchedule[]"));
    var sortedDays = WEEKDAYS.stream().filter(weeklySchedule::contains).toList();
    String firstDay = null, lastDay = null;
    Locale locale = LocaleContextHolder.getLocale();

    Iterator<String> iter = WEEKDAYS.iterator();
    while (iter.hasNext() && !weeklySchedule.isEmpty()) {
      String day = iter.next();
      if (weeklySchedule.contains(day)) {
        if (firstDay == null) {
          firstDay = messageSource.getMessage("general.week." + day, null, locale);
        } else {
          lastDay = messageSource.getMessage("general.week." + day, null, locale);
        }
        weeklySchedule.remove(day);
      } else if (firstDay != null) {
        var displayDays = formatWeekdaysSeparated(sortedDays, locale);
        submission.getInputData().put("displayWeeklySchedule", displayDays);
        return;
      }
    }

    if (lastDay != null) {
      submission.getInputData().put("displayWeeklySchedule", "%s-%s".formatted(firstDay, lastDay));
      return;
    }

    submission.getInputData().put("displayWeeklySchedule", firstDay);
  }

  private String formatWeekdaysSeparated(List<String> sortedDays, Locale locale) {
    return sortedDays.stream()
        .map(d -> messageSource.getMessage("general.week." + d, null, locale))
        .collect(Collectors.joining(", "));
  }
}
