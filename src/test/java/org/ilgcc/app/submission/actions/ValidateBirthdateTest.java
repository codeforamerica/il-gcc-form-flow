package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

class ValidateBirthdateTest {
  @Mock
  private MessageSource messageSource;
  private ValidateBirthdate validator;
  private AutoCloseable closeable;
  private String groupName = "testBirth";
  private String BIRTH_DATE = "testBirth";

  @BeforeEach
  public void setup() {
    closeable = MockitoAnnotations.openMocks(this);
    validator = new ValidateBirthdate(messageSource, groupName, BIRTH_DATE);
    validator.messageSource = messageSource;
    when(messageSource.getMessage("errors.provide-birthday", null, Locale.getDefault()))
      .thenReturn("Enter a birthdate");
    when(messageSource.getMessage("errors.invalid-birthdate-format", null, Locale.getDefault()))
        .thenReturn("Make sure the birthdate you entered is in this format: mm/dd/yyyy");
    when(messageSource.getMessage("errors.invalid-date-range", null, Locale.getDefault()))
        .thenReturn("Make sure the date you entered is between 01/01/1901 and today");
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  public void shouldGenerateACrossFieldValidationErrorWhenYearFieldIsNull() {
    Map<String, Object> formData = new HashMap<>();
    formData.put(String.format("%s%s", BIRTH_DATE, "Day"), "01");
    formData.put(String.format("%s%s", BIRTH_DATE, "Month"), "01");
    FormSubmission formSubmission = new FormSubmission(formData);
    Submission submission = new Submission();

    Map<String, List<String>> errors = validator.runValidation(formSubmission, submission);
    Assertions.assertThat(errors).containsKey(BIRTH_DATE);
    assertThat(errors.get("testBirth").getFirst()).isEqualTo( messageSource.getMessage("errors.provide-birthday", null, Locale.getDefault()));
  }

  @Test
  public void shouldGenerateACrossFieldValidationErrorWhenMonthIsNull() {
    Map<String, Object> formData = new HashMap<>();
    formData.put(String.format("%s%s", BIRTH_DATE, "Day"), "01");
    formData.put(String.format("%s%s", BIRTH_DATE, "Year"), "2001");
    FormSubmission formSubmission = new FormSubmission(formData);
    Submission submission = new Submission();

    Map<String, List<String>> errors = validator.runValidation(formSubmission, submission);
    Assertions.assertThat(errors).containsKey(BIRTH_DATE);
    assertThat(errors.get("testBirth").getFirst()).isEqualTo( messageSource.getMessage("errors.provide-birthday", null, Locale.getDefault()));
  }

  @Test
  public void shouldGenerateACrossFieldValidationErrorWhenDayIsNull() {
    Map<String, Object> formData = new HashMap<>();
    formData.put(String.format("%s%s", BIRTH_DATE, "Month"), "01");
    formData.put(String.format("%s%s", BIRTH_DATE, "Year"), "2001");
    FormSubmission formSubmission = new FormSubmission(formData);
    Submission submission = new Submission();

    Map<String, List<String>> errors = validator.runValidation(formSubmission, submission);
    Assertions.assertThat(errors).containsKey(BIRTH_DATE);
    assertThat(errors.get("testBirth").getFirst()).isEqualTo( messageSource.getMessage("errors.provide-birthday", null, Locale.getDefault()));
  }

  @Test
  public void shouldNotGenerateErrorIfDateIncludesAllRequiredFieldsAndIsValid() {
    Map<String, Object> formData = new HashMap<>();
    formData.put(String.format("%s%s", BIRTH_DATE, "Day"), "12");
    formData.put(String.format("%s%s", BIRTH_DATE, "Month"), "01");
    formData.put(String.format("%s%s", BIRTH_DATE, "Year"), "2021");
    FormSubmission formSubmission = new FormSubmission(formData);
    Submission submission = new Submission();

    Map<String, List<String>> errors = validator.runValidation(formSubmission, submission);
    Assertions.assertThat(errors).doesNotContainKey(BIRTH_DATE);
  }

  @Test
  public void shouldThrowAnErrorIfBirthDateIsInvalid() {
    Map<String, Object> formData = new HashMap<>();
    formData.put(String.format("%s%s", BIRTH_DATE, "Day"), "14");
    formData.put(String.format("%s%s", BIRTH_DATE, "Month"), "15");
    formData.put(String.format("%s%s", BIRTH_DATE, "Year"), "2021");

    FormSubmission formSubmission = new FormSubmission(formData);
    Submission submission = new Submission();

    Map<String, List<String>> errors = validator.runValidation(formSubmission, submission);
    Assertions.assertThat(errors).containsKey(BIRTH_DATE);
    assertThat(errors.get("testBirth").getFirst()).isEqualTo( messageSource.getMessage("errors.invalid-birthdate-format", null, Locale.getDefault()));
  }
  @Test
  public void shouldThrowAnErrorIfBirthDateIsBeforeMinDate() {
    Map<String, Object> formData = new HashMap<>();
    formData.put(String.format("%s%s", BIRTH_DATE, "Month"), "12");
    formData.put(String.format("%s%s", BIRTH_DATE, "Day"), "29");
    formData.put(String.format("%s%s", BIRTH_DATE, "Year"), "1809");

    FormSubmission formSubmission = new FormSubmission(formData);
    Submission submission = new Submission();

    Map<String, List<String>> errors = validator.runValidation(formSubmission, submission);
    Assertions.assertThat(errors).containsKey(BIRTH_DATE);
    assertThat(errors.get("testBirth").getFirst()).isEqualTo( messageSource.getMessage("errors.invalid-date-range", null, Locale.getDefault()));
  }


}