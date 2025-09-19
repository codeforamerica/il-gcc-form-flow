package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TrimEmailOnPostTest {
  String KEY_INCLUDES_EMAIL = "exampleKeyIncludesEmail";
  String KEY_NOT_AN_EMAIL = "exampleKeyForPhoneNumber";
  String EMAIL_INCLUDES_SPACES = "    exampleEmailIncludesSpaces";
  String EMAIL_AFTER_SPACES_TRIMMED = "exampleEmailIncludesSpaces";

  @Test
  void shouldReturnATrimmedEmailIfEmailKeyIsPresentAndTheValueIncludesLeadingSpaces() {
    Map<String, Object> formData = new HashMap<String, Object>();
    formData.put(KEY_INCLUDES_EMAIL, EMAIL_INCLUDES_SPACES);
    formData.put(KEY_NOT_AN_EMAIL, "333-333-3333");

    FormSubmission formSubmission = new FormSubmission(formData);
    Submission submission = new Submission();
    TrimEmailOnPost trimmedEmailOnPost = new TrimEmailOnPost();
    trimmedEmailOnPost.run(formSubmission, submission);
    assertThat(formSubmission.getFormData().get(KEY_INCLUDES_EMAIL)).isEqualTo(EMAIL_AFTER_SPACES_TRIMMED);
  }
}
