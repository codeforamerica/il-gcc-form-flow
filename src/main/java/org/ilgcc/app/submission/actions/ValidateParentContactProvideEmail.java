package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import formflow.library.utils.RegexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class ValidateParentContactProvideEmail implements Action {

  @Autowired
  MessageSource messageSource;

  private final String INPUT_NAME_EMAIL = "parentContactEmail";
  private final String INPUT_NAME_PREFERENCE = "parentContactPreferredCommunicationMethod";

  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {

    Locale locale = LocaleContextHolder.getLocale();
    Map<String, List<String>> errorMessages = new HashMap<>();
    Map<String, Object> inputData = submission.getInputData();
    Map<String, Object> formData = formSubmission.getFormData();
    boolean emailIsNotPreferred =  !inputData.getOrDefault(INPUT_NAME_PREFERENCE, "").equals("email");
    String parentEmail = formData.get(INPUT_NAME_EMAIL).toString();
    if (emailIsNotPreferred && parentEmail.isBlank()) {
      return errorMessages;
    }

    if (parentEmail.isBlank()){
      errorMessages.put(INPUT_NAME_EMAIL, List.of(messageSource.getMessage("errors.require-email", null, locale)));
    } else if (!parentEmail.matches(RegexUtils.EMAIL_REGEX)) {
      errorMessages.put(INPUT_NAME_EMAIL, List.of(messageSource.getMessage("errors.invalid-email", null, locale)));
    }

    return errorMessages;
  }
}
