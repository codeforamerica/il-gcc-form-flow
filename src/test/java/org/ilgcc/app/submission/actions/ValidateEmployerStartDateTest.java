package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.joda.time.DateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

class ValidateEmployerStartDateTest {

    @Mock
    private MessageSource messageSource;
    private ValidateEmployerStartDate validator;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        validator = new ValidateEmployerStartDate();
        validator.messageSource = messageSource;
        when(messageSource.getMessage("activities-employer-start-date.error", null, Locale.getDefault()))
                .thenReturn("Enter a valid month and year.");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close(); // Close mocks after each test
    }

    @Test
    void shouldNotErrorWhenJobStartDateIsMissing() {
        Map<String, Object> formData = Map.of(
                "activitiesJobStartMonth", "",
                "activitiesJobStartDay", "",
                "activitiesJobStartYear", ""
        );

        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).doesNotContainKey("activitiesJobStart");
    }

    @Test
    void shouldErrorWhenJobStartMonthIsPresentAndYearIsMissing() {
        Map<String, Object> formData = Map.of(
                "activitiesJobStartMonth", "01",
                "activitiesJobStartDay", "",
                "activitiesJobStartYear", ""
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).containsKey("activitiesJobStart");
        assertThat(errors.get("activitiesJobStart")).contains("Enter a valid month and year.");
    }

    @Test
    void shouldErrorWhenJobStartMonthIsMissingAndYearIsPresent() {
        Map<String, Object> formData = Map.of(
                "activitiesJobStartMonth", "",
                "activitiesJobStartDay", "",
                "activitiesJobStartYear", "2024"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).containsKey("activitiesJobStart");
        assertThat(errors.get("activitiesJobStart")).contains("Enter a valid month and year.");
    }


    @Test
    void shouldErrorWhenJobStartDateHasNonValuePresent() {
        Map<String, Object> formData = Map.of(
                "activitiesJobStartMonth", "a",
                "activitiesJobStartDay", "",
                "activitiesJobStartYear", "2024"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).containsKey("activitiesJobStart");
        assertThat(errors.get("activitiesJobStart")).contains("Enter a valid month and year.");
    }
    @Test
    void shouldNotErrorWhenJobStartDateHasMonthAndYearPresent() {
        Map<String, Object> formData = Map.of(
            "activitiesJobStartMonth", "10",
            "activitiesJobStartDay", "",
            "activitiesJobStartYear", "2024"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).doesNotContainKey("activitiesJobStart");
    }

    @Test
    void shouldErrorWhenJobStartDateIsBefore1901() {
        Map<String, Object> formData = Map.of(
                "activitiesJobStartMonth", "01",
                "activitiesJobStartDay", "",
                "activitiesJobStartYear", "1700"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).containsKey("activitiesJobStart");
        assertThat(errors.get("activitiesJobStart")).contains("Enter a valid month and year.");
    }

    @Test
    void shouldAllowJobStartDateToBeInTheFuture() {
        DateTime present = DateTime.now();
        Map<String, Object> formData = Map.of(
                "activitiesJobStartMonth", "01",
                "activitiesJobStartDay", "",
                "activitiesJobStartYear", Integer.toString(present.plusYears(1).getYear())
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).doesNotContainKey("activitiesJobStart");
    }

    @Test
    void shouldNotErrorWhenPartnerJobStartDateIsMissing() {
        Map<String, Object> formData = Map.of(
            "activitiesPartnerJobStartMonth", "",
            "activitiesPartnerJobStartDay", "",
            "activitiesPartnerJobStartYear", ""
        );

        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).doesNotContainKey("activitiesPartnerJobStart");
    }

    @Test
    void shouldErrorWhenParnerJobStartMonthIsPresentAndYearIsMissing() {
        Map<String, Object> formData = Map.of(
            "activitiesPartnerJobStartMonth", "01",
            "activitiesPartnerJobStartDay", "",
            "activitiesPartnerJobStartYear", ""
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).containsKey("activitiesPartnerJobStart");
        assertThat(errors.get("activitiesPartnerJobStart")).contains("Enter a valid month and year.");
    }

    @Test
    void shouldErrorWhenPartnerJobStartMonthIsMissingAndYearIsPresent() {
        Map<String, Object> formData = Map.of(
            "activitiesPartnerJobStartMonth", "",
            "activitiesPartnerJobStartDay", "",
            "activitiesPartnerJobStartYear", "2024"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).containsKey("activitiesPartnerJobStart");
        assertThat(errors.get("activitiesPartnerJobStart")).contains("Enter a valid month and year.");
    }


    @Test
    void shouldErrorWhenPartnerJobDateIncludesHasANonNumberPresent() {
        Map<String, Object> formData = Map.of(
            "activitiesPartnerJobStartMonth", "a",
            "activitiesPartnerJobStartDay", "",
            "activitiesPartnerJobStartYear", "2024"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).containsKey("activitiesPartnerJobStart");
        assertThat(errors.get("activitiesPartnerJobStart")).contains("Enter a valid month and year.");
    }

    @Test
    void shouldErrorWhenPartnerJobStartDateIsBefore1901() {
        Map<String, Object> formData = Map.of(
            "activitiesPartnerJobStartMonth", "01",
            "activitiesPartnerJobStartDay", "",
            "activitiesPartnerJobStartYear", "1700"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).containsKey("activitiesPartnerJobStart");
        assertThat(errors.get("activitiesPartnerJobStart")).contains("Enter a valid month and year.");
    }

    @Test
    void shouldAllowPartnerJobStartDateToBeInTheFuture() {
        DateTime present = DateTime.now();
        Map<String, Object> formData = Map.of(
            "activitiesPartnerJobStartMonth", "01",
            "activitiesPartnerJobStartDay", "",
            "activitiesPartnerJobStartYear", Integer.toString(present.plusYears(1).getYear())
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).doesNotContainKey("activitiesPartnerJobStart");
    }
}
