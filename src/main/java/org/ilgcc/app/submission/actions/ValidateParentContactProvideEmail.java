package org.ilgcc.app.submission.actions;


import com.mailgun.model.message.MessageResponse;
import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.email.MailgunEmailClient;
import formflow.library.pdf.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

@Slf4j
@Component
public class ValidateParentContactProvideEmail implements Action {

  @Autowired
  MessageSource messageSource;

  private final String INPUT_NAME_EMAIL = "parentContactEmail";
  private final String INPUT_NAME_PREFERENCE = "parentContactPreferCommunicate";

  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {

    Locale locale = LocaleContextHolder.getLocale();
    Map<String, List<String>> errorMessages = new HashMap<>();
    Map<String, Object> inputData = formSubmission.getFormData();
    if (!inputData.containsKey(INPUT_NAME_PREFERENCE)) {
      return errorMessages;
    }

    String paperless = inputData.get(INPUT_NAME_PREFERENCE).toString();
    if (paperless.isBlank()){
      errorMessages.put(INPUT_NAME_PREFERENCE, List.of(messageSource.getMessage("general.indicates-required", null, locale)));
    } else if (paperless.equals("paperless") && inputData.getOrDefault(INPUT_NAME_EMAIL, "").toString().isBlank()) {
      errorMessages.put(INPUT_NAME_EMAIL, List.of(messageSource.getMessage("errors.require-email", null, locale)));
    }

    return errorMessages;
  }
}
