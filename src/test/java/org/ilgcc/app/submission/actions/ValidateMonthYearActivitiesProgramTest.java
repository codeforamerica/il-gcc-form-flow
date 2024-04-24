package org.ilgcc.app.submission.actions;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;
import java.util.Map;

class ValidateMonthYearActivitiesProgramTest {

    @Mock
    private MessageSource messageSource;
    private ValidateMonthYearActivitiesProgram validator;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        validator = new ValidateMonthYearActivitiesProgram();
        validator.messageSource = messageSource;
        when(messageSource.getMessage("general.month.validation", null, Locale.getDefault()))
                .thenReturn("Month is required when year is provided.");
        when(messageSource.getMessage("general.year.validation", null, Locale.getDefault()))
                .thenReturn("Year is required when month is provided.");
        when(messageSource.getMessage("errors.invalid-date-format", null, Locale.getDefault()))
                .thenReturn("Make sure the date you entered is in this format: mm/dd/yyyy");
        when(messageSource.getMessage("errors.invalid-date-range", null, Locale.getDefault()))
                .thenReturn("Make sure the date you entered is after 01/01/1901.");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close(); // Close mocks after each test
    }

    // TODO: add test if month and years are blank, that no errors return

    @Test
    void shouldErrorWhenStartMonthIsPresentAndStartYearIsMissing() {
        Map<String, Object> formData = Map.of(
                "activitiesProgramStartMonth", "01",
                "activitiesProgramStartYear", "",
                "activitiesProgramEndMonth", "02",
                "activitiesProgramEndYear", "2024"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());
        assertThat(errors).containsKey("activitiesProgramStartYear");
        assertThat(errors.get("activitiesProgramStartYear")).contains("Year is required when month is provided.");
    }

    @Test
    void shouldErrorWhenStartMonthIsMissingAndStartYearIsPresent() {
        Map<String, Object> formData = Map.of(
                "activitiesProgramStartMonth", "",
                "activitiesProgramStartYear", "2024",
                "activitiesProgramEndMonth", "02",
                "activitiesProgramEndYear", "2024"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());
        assertThat(errors).containsKey("activitiesProgramStartMonth");
        assertThat(errors.get("activitiesProgramStartMonth")).contains("Month is required when year is provided.");
    }

    @Test
    void shouldErrorWhenEndMonthIsPresentAndEndYearIsMissing() {
        Map<String, Object> formData = Map.of(
                "activitiesProgramStartMonth", "01",
                "activitiesProgramStartYear", "2024",
                "activitiesProgramEndMonth", "02",
                "activitiesProgramEndYear", ""
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());
        assertThat(errors).containsKey("activitiesProgramEndYear");
        assertThat(errors.get("activitiesProgramEndYear")).contains("Year is required when month is provided.");
    }

    @Test
    void shouldErrorWhenEndMonthIsMissingAndEndYearIsPresent() {
        Map<String, Object> formData = Map.of(
                "activitiesProgramStartMonth", "01",
                "activitiesProgramStartYear", "2024",
                "activitiesProgramEndMonth", "",
                "activitiesProgramEndYear", "2024"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());
        assertThat(errors).containsKey("activitiesProgramEndMonth");
        assertThat(errors.get("activitiesProgramEndMonth")).contains("Month is required when year is provided.");
    }

    @Test
    void shouldErrorWhenStartHasNonDigitDatesArePresent() {
        Map<String, Object> formData = Map.of(
                "activitiesProgramStartMonth", "a",
                "activitiesProgramStartYear", "2024",
                "activitiesProgramEndMonth", "01",
                "activitiesProgramEndYear", "2024"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());
        assertThat(errors).containsKey("activitiesProgramStart");
        assertThat(errors.get("activitiesProgramStart")).contains("Make sure the date you entered is in this format: mm/dd/yyyy");
    }

    @Test
    void shouldErrorWhenEndHasNonDigitDatesArePresent() {
        Map<String, Object> formData = Map.of(
                "activitiesProgramStartMonth", "01",
                "activitiesProgramStartYear", "2024",
                "activitiesProgramEndMonth", "01",
                "activitiesProgramEndYear", "2a24"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());
        assertThat(errors).containsKey("activitiesProgramEnd");
        assertThat(errors.get("activitiesProgramEnd")).contains("Make sure the date you entered is in this format: mm/dd/yyyy");
    }

    @Test
    void shouldErrorWhenStartDateIsBefore1901() {
        Map<String, Object> formData = Map.of(
                "activitiesProgramStartMonth", "01",
                "activitiesProgramStartYear", "1700",
                "activitiesProgramEndMonth", "01",
                "activitiesProgramEndYear", "2024"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());
        assertThat(errors).containsKey("activitiesProgramStart");
        assertThat(errors.get("activitiesProgramStart")).contains("Make sure the date you entered is after 01/01/1901.");
    }

    @Test
    void shouldErrorWhenEndDateIsBefore1901() {
        Map<String, Object> formData = Map.of(
                "activitiesProgramStartMonth", "01",
                "activitiesProgramStartYear", "2024",
                "activitiesProgramEndMonth", "01",
                "activitiesProgramEndYear", "1700"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());
        assertThat(errors).containsKey("activitiesProgramEnd");
        assertThat(errors.get("activitiesProgramEnd")).contains("Make sure the date you entered is after 01/01/1901.");
    }
}
