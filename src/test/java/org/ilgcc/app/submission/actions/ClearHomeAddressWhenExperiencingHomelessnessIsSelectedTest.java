package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ClearHomeAddressWhenExperiencingHomelessnessIsSelectedTest {

  @Test
  void shouldClearHomeAddressFieldsWhenExperiencingHomelessnessIsSelected() {
    var clearHomeAddressWhenExperiencingHomelessnessIsSelectedAction = new ClearHomeAddressWhenExperiencingHomelessnessIsSelected();

    Map<String, Object> formData = new HashMap<>();
    formData.put("parentHomeExperiencingHomelessness[]", List.of("yes"));

    Map<String, Object> inputData = new HashMap<>();
    inputData.put("parentHomeStreetAddress1", "1234 Street Address One");
    inputData.put("parentHomeStreetAddress2", "234");
    inputData.put("parentHomeCity", "Chicago");
    inputData.put("parentHomeState", "IL");
    inputData.put("parentHomeZipCode", "60302");

    Submission submission = new Submission();
    submission.setInputData(inputData);
    FormSubmission formSubmission = new FormSubmission(formData);
    clearHomeAddressWhenExperiencingHomelessnessIsSelectedAction.run(formSubmission, submission);

    assertThat(submission.getInputData().get("parentHomeStreetAddress1")).isEqualTo("");
    assertThat(submission.getInputData().get("parentHomeStreetAddress2")).isEqualTo("");
    assertThat(submission.getInputData().get("parentHomeCity")).isEqualTo("");
    assertThat(submission.getInputData().get("parentHomeState")).isEqualTo("");
    assertThat(submission.getInputData().get("parentHomeZipCode")).isEqualTo("");
  }
}