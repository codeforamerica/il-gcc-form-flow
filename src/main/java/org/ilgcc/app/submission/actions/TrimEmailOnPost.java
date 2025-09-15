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
  @Override
  public void run(FormSubmission formSubmission, Submission submission){
    trimEmail(formSubmission, submission);
  }
  @Override
  public void run(FormSubmission formSubmission, Submission submission, String id){
    trimEmail(formSubmission, submission);
  }

  @Override
  public void run(FormSubmission formSubmission, Submission submission, String id, String repeatForUuid){
    trimEmail(formSubmission, submission);
  }

  private void trimEmail(FormSubmission formSubmission, Submission submission){
    AtomicReference<String> emailKey = new AtomicReference<>("");
    AtomicReference<String> trimmedEmail = new AtomicReference<>("");
    Map<String, Object> formData = formSubmission.getFormData();
    formData.forEach((key, value) -> {
      if(Pattern.compile(Pattern.quote("email"), Pattern.CASE_INSENSITIVE).matcher(key).find()){
        emailKey.set(key);
        trimmedEmail.set(value.toString().trim());
      }
    });
    if(!emailKey.get().isEmpty()){
      formData.put(emailKey.get(), trimmedEmail.get());
    }
  }
}
