package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SetWeeklyScheduleGroup implements Action {

  private final MessageSource messageSource;

  public static final List<String> WEEKDAYS = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

  public static final String DISPLAY_LABEL = "displayWeeklySchedule";

  public SetWeeklyScheduleGroup(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public void run(Submission submission) {
    Map<String, Object> inputData = submission.getInputData();
    if (!inputData.containsKey("weeklySchedule[]") && !inputData.containsKey("childcareWeeklySchedule[]")) {
      return;
    }
    String weeklyScheduleType = inputData.containsKey("weeklySchedule[]") ? "weeklySchedule[]" : "childcareWeeklySchedule[]";

    var weeklySchedule = new ArrayList<>((List<String>) inputData.get(weeklyScheduleType));
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
        inputData.put(DISPLAY_LABEL, displayDays);
        return;
      }
    }

    inputData.put(DISPLAY_LABEL, lastDay == null ? firstDay : "%s-%s".formatted(firstDay, lastDay));

  }

  private String formatWeekdaysSeparated(List<String> sortedDays, Locale locale) {
    return sortedDays.stream()
        .map(d -> messageSource.getMessage("general.week." + d, null, locale))
        .collect(Collectors.joining(", "));
  }
}
