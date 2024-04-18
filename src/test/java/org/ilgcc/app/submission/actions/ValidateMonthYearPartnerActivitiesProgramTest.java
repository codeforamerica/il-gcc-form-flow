package org.ilgcc.app.submission.actions;

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
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close(); // Close mocks after each test
    }

    @Test
    void shouldErrorWhenStartMonthIsPresentAndStartYearIsMissing() {
        Map<String, Object> formData = Map.of(
                "partnerProgramStartMonth", "01",
                "partnerProgramStartYear", "",
                "partnerProgramEndMonth", "02",
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
                "partnerProgramStartYear", "2024",
                "partnerProgramEndMonth", "02",
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
                "partnerProgramStartYear", "2024",
                "partnerProgramEndMonth", "02",
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
                "partnerProgramStartYear", "2024",
                "partnerProgramEndMonth", "",
                "partnerProgramEndYear", "2024"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());
        assertThat(errors).containsKey("partnerProgramEndMonth");
        assertThat(errors.get("partnerProgramEndMonth")).contains("Month is required when year is provided.");
    }
}
