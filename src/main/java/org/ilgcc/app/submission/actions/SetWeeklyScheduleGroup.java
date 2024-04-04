package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class SetWeeklyScheduleGroup implements Action {

  public static final List<String> WEEKDAYS = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
  public static final String DISPLAY_LABEL = "displayWeeklySchedule";
  private final MessageSource messageSource;

  public SetWeeklyScheduleGroup(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  protected abstract List<String> getWeeklySchedule(Submission submission);

  protected abstract List<String> getWeeklySchedule(Submission submission, String id);

  @Override
  public void run(Submission submission) {
    var weeklySchedule = getWeeklySchedule(submission);
    if (weeklySchedule == null) {
      return;
    }

    setFormattedDisplayGroup(submission, weeklySchedule);
  }

  @Override
  public void run(Submission submission, String id) {
    var weeklySchedule = getWeeklySchedule(submission, id);
    if (weeklySchedule == null) {
      return;
    }

    setFormattedDisplayGroup(submission, weeklySchedule);
  }

  private void setFormattedDisplayGroup(Submission submission, List<String> weeklySchedule) {
    Map<String, Object> inputData = submission.getInputData();
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
