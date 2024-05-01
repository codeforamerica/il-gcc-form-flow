package org.ilgcc.app.submission.actions;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import org.joda.time.DateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ValidateMonthYearPartnerActivitiesProgramTest {

    @Mock
    private MessageSource messageSource;
    private ValidateMonthYearPartnerActivitiesProgram validator;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        validator = new ValidateMonthYearPartnerActivitiesProgram();
        validator.messageSource = messageSource;
        when(messageSource.getMessage("general.month.validation", null, Locale.getDefault()))
                .thenReturn("Month is required when year is provided.");
        when(messageSource.getMessage("general.year.validation", null, Locale.getDefault()))
                .thenReturn("Year is required when month is provided.");
        when(messageSource.getMessage("errors.invalid-date-format", null, Locale.getDefault()))
                .thenReturn("Make sure the date you entered is in this format: mm/dd/yyyy");
        when(messageSource.getMessage("errors.invalid-date-range", null, Locale.getDefault()))
                .thenReturn("Make sure the date you entered is after 01/01/1901 and before today.");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close(); // Close mocks after each test
    }

    @Test
    void shouldNotErrorWhenDateIsMissing() {
        Map<String, Object> formData = Map.of(
                "partnerProgramStartMonth", "",
                "partnerProgramStartDay", "",
                "partnerProgramStartYear", "",
                "partnerProgramEndMonth", "",
                "partnerProgramEndDay", "",
                "partnerProgramEndYear", ""
        );

        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).doesNotContainKey("partnerProgramStart");
        assertThat(errors).doesNotContainKey("partnerProgramStartMonth");
        assertThat(errors).doesNotContainKey("partnerProgramStartYear");
        assertThat(errors).doesNotContainKey("partnerProgramEnd");
        assertThat(errors).doesNotContainKey("partnerProgramEndMonth");
        assertThat(errors).doesNotContainKey("partnerProgramEndYear");
    }

    @Test
    void shouldErrorWhenStartMonthIsPresentAndStartYearIsMissing() {
        Map<String, Object> formData = Map.of(
                "partnerProgramStartMonth", "01",
                "partnerProgramStartDay", "",
                "partnerProgramStartYear", "",
                "partnerProgramEndMonth", "02",
                "partnerProgramEndDay", "",
                "partnerProgramEndYear", "2024"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).containsKey("partnerProgramStartYear");
        assertThat(errors.get("partnerProgramStartYear")).contains("Year is required when month is provided.");
    }

    @Test
    void shouldErrorWhenStartMonthIsMissingAndStartYearIsPresent() {
        Map<String, Object> formData = Map.of(
                "partnerProgramStartMonth", "",
                "partnerProgramStartDay", "",
                "partnerProgramStartYear", "2024",
                "partnerProgramEndMonth", "02",
                "partnerProgramEndDay", "",
                "partnerProgramEndYear", "2024"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).containsKey("partnerProgramStartMonth");
        assertThat(errors.get("partnerProgramStartMonth")).contains("Month is required when year is provided.");
    }

    @Test
    void shouldErrorWhenEndMonthIsPresentAndEndYearIsMissing() {
        Map<String, Object> formData = Map.of(
                "partnerProgramStartMonth", "01",
                "partnerProgramStartDay", "",
                "partnerProgramStartYear", "2024",
                "partnerProgramEndMonth", "02",
                "partnerProgramEndDay", "",
                "partnerProgramEndYear", ""
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).containsKey("partnerProgramEndYear");
        assertThat(errors.get("partnerProgramEndYear")).contains("Year is required when month is provided.");
    }

    @Test
    void shouldErrorWhenEndMonthIsMissingAndEndYearIsPresent() {
        Map<String, Object> formData = Map.of(
                "partnerProgramStartMonth", "01",
                "partnerProgramStartDay", "",
                "partnerProgramStartYear", "2024",
                "partnerProgramEndMonth", "",
                "partnerProgramEndDay", "",
                "partnerProgramEndYear", "2024"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).containsKey("partnerProgramEndMonth");
        assertThat(errors.get("partnerProgramEndMonth")).contains("Month is required when year is provided.");
    }

    @Test
    void shouldErrorWhenStartHasNonDigitDatesArePresent() {
        Map<String, Object> formData = Map.of(
                "partnerProgramStartMonth", "a",
                "partnerProgramStartDay", "",
                "partnerProgramStartYear", "2024",
                "partnerProgramEndMonth", "01",
                "partnerProgramEndDay", "",
                "partnerProgramEndYear", "2024"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());
        assertThat(errors).containsKey("partnerProgramStart");
        assertThat(errors.get("partnerProgramStart")).contains("Make sure the date you entered is in this format: mm/dd/yyyy");
    }

    @Test
    void shouldErrorWhenEndHasNonDigitDatesArePresent() {
        Map<String, Object> formData = Map.of(
                "partnerProgramStartMonth", "01",
                "partnerProgramStartDay", "",
                "partnerProgramStartYear", "2024",
                "partnerProgramEndMonth", "01",
                "partnerProgramEndDay", "",
                "partnerProgramEndYear", "2a24"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());
        assertThat(errors).containsKey("partnerProgramEnd");
        assertThat(errors.get("partnerProgramEnd")).contains("Make sure the date you entered is in this format: mm/dd/yyyy");
    }

    @Test
    void shouldErrorWhenStartDateIsBefore1901() {
        Map<String, Object> formData = Map.of(
                "partnerProgramStartMonth", "01",
                "partnerProgramStartDay", "",
                "partnerProgramStartYear", "1700",
                "partnerProgramEndMonth", "01",
                "partnerProgramEndDay", "",
                "partnerProgramEndYear", "2024"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());
        assertThat(errors).containsKey("partnerProgramStart");
        assertThat(errors.get("partnerProgramStart")).contains("Make sure the date you entered is after 01/01/1901 and before today.");
    }

    @Test
    void shouldErrorWhenEndDateIsBefore1901() {
        Map<String, Object> formData = Map.of(
                "partnerProgramStartMonth", "01",
                "partnerProgramStartDay", "",
                "partnerProgramStartYear", "2024",
                "partnerProgramEndMonth", "01",
                "partnerProgramEndDay", "",
                "partnerProgramEndYear", "1700"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());
        assertThat(errors).containsKey("partnerProgramEnd");
        assertThat(errors.get("partnerProgramEnd")).contains("Make sure the date you entered is after 01/01/1901 and before today.");
    }

    @Test
    void shouldAllowStartDateToBeInTheFuture() {
        DateTime present = DateTime.now();
        Map<String, Object> formData = Map.of(
                "partnerProgramStartMonth", "01",
                "partnerProgramStartDay", "",
                "partnerProgramStartYear", Integer.toString(present.plusYears(1).getYear()),
                "partnerProgramEndMonth", "01",
                "partnerProgramEndDay", "",
                "partnerProgramEndYear", "2024"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());
        assertThat(errors).doesNotContainKey("partnerProgramStart");
    }

    @Test
    void shouldAllowEndDateToBeInTheFuture() {
        DateTime present = DateTime.now();
        Map<String, Object> formData = Map.of(
                "partnerProgramStartMonth", "01",
                "partnerProgramStartDay", "",
                "partnerProgramStartYear", "2024",
                "partnerProgramEndMonth", "01",
                "partnerProgramEndDay", "",
                "partnerProgramEndYear", Integer.toString(present.plusYears(1).getYear())
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());
        assertThat(errors).doesNotContainKey("partnerProgramEnd");
    }
}
