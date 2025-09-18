package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class TrimEmailOnPost implements Action {

  private String EMAIL_KEY = "";
  private String TRIMMED_EMAIL = "";
  private static final Pattern emailKeyPattern = Pattern.compile(Pattern.quote("email"), Pattern.CASE_INSENSITIVE);

  @Override
  public void run(FormSubmission formSubmission, Submission submission){
    trimEmail(formSubmission);
  }
  @Override
  public void run(FormSubmission formSubmission, Submission submission, String id){
    trimEmail(formSubmission);
  }

  @Override
  public void run(FormSubmission formSubmission, Submission submission, String id, String repeatForUuid){
    trimEmail(formSubmission);
  }

  private void trimEmail(FormSubmission formSubmission){
    Map<String, Object> formData = formSubmission.getFormData();
    formData.forEach((key, value) -> {
      if(emailKeyPattern.matcher(key).find()){
        EMAIL_KEY = key;
        TRIMMED_EMAIL = (value.toString().trim());
      }
    });
    if(!EMAIL_KEY.isEmpty()){
      formData.put(EMAIL_KEY, TRIMMED_EMAIL);
    }
  }
}
