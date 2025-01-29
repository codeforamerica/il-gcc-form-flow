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

class ValidatePartnerEmployerStartDateTest {

    @Mock
    private MessageSource messageSource;
    private ValidatePartnerEmployerStartDate validator;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        validator = new ValidatePartnerEmployerStartDate();
        validator.messageSource = messageSource;
        when(messageSource.getMessage("activities-employer-start-date.error", null, Locale.getDefault()))
                .thenReturn("Enter a valid month and year.");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close(); // Close mocks after each test
    }

    @Test
    void shouldNotErrorWhenDateIsMissing() {
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
    void shouldErrorWhenMonthIsPresentAndYearIsMissing() {
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
    void shouldErrorWhenMonthIsMissingAndYearIsPresent() {
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
    void shouldErrorWhenDateHasNonValuePresent() {
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
    void shouldErrorWhenEmployerDateIsBefore1901() {
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
    void shouldAllowEmployerDateToBeInTheFuture() {
        DateTime present = DateTime.now();
        Map<String, Object> formData = Map.of(
                "activitiesPartnerJobStartMonth", "01",
                "activitiesPartnerJobStartDay", "",
                "activitiesPartnerJobStartYear", Integer.toString(present.plusYears(1).getYear())
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).containsKey("activitiesPartnerJobStart");
        assertThat(errors.get("activitiesPartnerJobStart")).contains("Enter a valid month and year.");
    }
}
