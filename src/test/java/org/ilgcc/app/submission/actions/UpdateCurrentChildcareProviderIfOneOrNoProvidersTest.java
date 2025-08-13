package org.ilgcc.app.submission.actions;

import static org.junit.jupiter.api.Assertions.*;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

class UpdateCurrentChildcareProviderIfOneOrNoProvidersTest {
  private FormSubmission formSubmission;
  private Submission submission;
  private UpdateCurrentChildcareProviderIfOneOrNoProviders action;


  void setUpWhenNoFormDataIsPassed() {
    Map<String, Object> formData = new HashMap<>();
    action = new UpdateCurrentChildcareProviderIfOneOrNoProviders();
    formSubmission = new FormSubmission(formData);
  }

  @Test
  void whenFamilyHasNoProviderChosenSetChildcareProvidersToNoProvider() {
    setUpWhenNoFormDataIsPassed();
    submission = new SubmissionTestBuilder().with("hasChosenProvider", "false").build();
    action.run(formSubmission, submission, "1");
    assertEquals(List.of("NO_PROVIDER"), formSubmission.getFormData().get("childcareProvidersForCurrentChild[]"));
  }

  @Test
  void whenFamilyHasOnlyOneProviderAndHasAProviderForEveryChild() {
    setUpWhenNoFormDataIsPassed();
    submission = new SubmissionTestBuilder()
        .withProvider("TestProvider", "1")
        .with("hasChosenProvider", "true")
        .build();
    action.run(formSubmission, submission, "1");
    Map<String, Object> firstProvider = ((List<Map<String, Object>>) submission.getInputData().get("providers")).getFirst();
    assertEquals(List.of(firstProvider.get("uuid")), formSubmission.getFormData().get("childcareProvidersForCurrentChild[]"));
  }

  @Test
  void whenFamilySelectedAProviderFromAListReturnThatProvider(){
    action = new UpdateCurrentChildcareProviderIfOneOrNoProviders();
    submission = new SubmissionTestBuilder()
        .withProvider("TestProvider", "1")
        .with("hasChosenProvider", "true")
        .build();
    Map<String, Object> firstProvider = ((List<Map<String, Object>>) submission.getInputData().get("providers")).getFirst();
    Map<String, Object> formData = new HashMap<>();
    formData.put("childcareProvidersForCurrentChild[]", List.of("NO_PROVIDER"));
    formSubmission = new FormSubmission(formData);
    action.run(formSubmission, submission, "1");
    assertEquals(List.of("NO_PROVIDER"), formSubmission.getFormData().get("childcareProvidersForCurrentChild[]"));
  }
}