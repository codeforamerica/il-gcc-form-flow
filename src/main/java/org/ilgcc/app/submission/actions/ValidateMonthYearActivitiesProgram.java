package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
public class ValidateMonthYearActivitiesProgram implements Action {

  @Autowired
  MessageSource messageSource;

  private final String INPUT_NAME_START_MONTH = "activitiesProgramStartMonth";
  private final String INPUT_NAME_START_YEAR = "activitiesProgramStartYear";
  private final String INPUT_NAME_END_MONTH = "activitiesProgramEndMonth";
  private final String INPUT_NAME_END_YEAR = "activitiesProgramEndYear";

  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {

    Locale locale = LocaleContextHolder.getLocale();
    Map<String, List<String>> errorMessages = new HashMap<>();
    Map<String, Object> inputData = formSubmission.getFormData();

    String startMonth = inputData.get(INPUT_NAME_START_MONTH).toString();
    String startYear = inputData.get(INPUT_NAME_START_YEAR).toString();

    String endMonth = inputData.get(INPUT_NAME_END_MONTH).toString();
    String endYear = inputData.get(INPUT_NAME_END_YEAR).toString();

    if (startMonth.isBlank() && !startYear.isBlank()){
      errorMessages.put(INPUT_NAME_START_MONTH, List.of(messageSource.getMessage("general.month.validation", null, locale)));
    } else if (!startMonth.isBlank() && startYear.isBlank()){
      errorMessages.put(INPUT_NAME_START_YEAR, List.of(messageSource.getMessage("general.year.validation", null, locale)));
    } else if (endMonth.isBlank() && !endYear.isBlank()) {
      errorMessages.put(INPUT_NAME_END_MONTH, List.of(messageSource.getMessage("general.month.validation", null, locale)));
    } else if (!endMonth.isBlank() && endYear.isBlank()) {
      errorMessages.put(INPUT_NAME_END_YEAR, List.of(messageSource.getMessage("general.year.validation", null, locale)));
    }

    return errorMessages;
  }
}
