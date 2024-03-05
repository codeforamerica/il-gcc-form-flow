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

  private final String INPUT_NAME = "parentContactEmail";

  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {

    Locale locale = LocaleContextHolder.getLocale();
    Map<String, List<String>> errorMessages = new HashMap<>();
    Map<String, Object> inputData = formSubmission.getFormData();
    if (!inputData.containsKey("parentContactPreferCommunicate")) {
      return errorMessages;
    }

    String paperless = submission.getInputData().get("parentContactPreferCommunicate").toString();
    if (paperless.equals("paperless") && inputData.containsKey("parentContactEmail")) {
      errorMessages.put(INPUT_NAME, List.of(messageSource.getMessage("errors.require", null, locale)));
    }

    return errorMessages;
  }
}
