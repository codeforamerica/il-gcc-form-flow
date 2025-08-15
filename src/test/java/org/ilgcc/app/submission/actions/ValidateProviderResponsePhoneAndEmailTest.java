package org.ilgcc.app.submission.actions;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionTestBuilder;
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

class ValidateProviderResponsePhoneAndEmailTest {

    @Mock
    private MessageSource messageSource;
    private ValidateProviderResponsePhoneAndEmail validateProviderResponsePhoneAndEmail;
    private AutoCloseable closeable;

    private static final String BLANK_PHONE_EMAIL = "";
    private static final String VALID_PHONE_NUMBER = "(858) 934-9734";
    private static final String INVALID_PHONE_NUMBER = "545rgr";
    private static final String VALID_EMAIL_ADDRESS =  "test@email.com";
    private static final String INVALID_EMAIL_ADDRESS = "test@email";

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        validateProviderResponsePhoneAndEmail = new ValidateProviderResponsePhoneAndEmail();
        validateProviderResponsePhoneAndEmail.messageSource = messageSource;
        when(messageSource.getMessage("errors.require-email", null, Locale.getDefault()))
                .thenReturn("Please provide an email address.");
        when(messageSource.getMessage("errors.invalid-email.no-suggested-email-address", null, Locale.getDefault()))
                .thenReturn("Make sure the email you entered follows the right format.");
        when(messageSource.getMessage("errors.invalid-phone-number", null, Locale.getDefault()))
                .thenReturn("Make sure the phone number is valid and includes 10 digits.");

    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close(); // Close mocks after each test
    }

    @Test
    void shouldThrowAnErrorWhenEmailIsNotValidAndPhoneIsBlank() {
        //create submission object with inputData
        Submission submission = new SubmissionTestBuilder().build();

        Map<String, Object> formData = Map.of(
                "providerResponseContactEmail", INVALID_EMAIL_ADDRESS,
                "providerResponseContactPhoneNumber",BLANK_PHONE_EMAIL
        );
        FormSubmission formSubmission = new FormSubmission(formData);
        Map<String, List<String>> errors = validateProviderResponsePhoneAndEmail.runValidation(formSubmission, submission);
        assertThat(errors).containsKey("providerResponseContactEmail");
        assertThat(errors).containsValue(List.of("Make sure the email you entered follows the right format."));
        assertThat(errors).doesNotContainKey("providerResponseContactPhoneNumber");
    }

    @Test
    void shouldNotThrowAnErrorWhenEmailIsValidAndPhoneIsBlank() {
        //create submission object with inputData
        Submission submission = new SubmissionTestBuilder().build();

        Map<String, Object> formData = Map.of(
                "providerResponseContactEmail", VALID_EMAIL_ADDRESS,
                "providerResponseContactPhoneNumber",BLANK_PHONE_EMAIL
        );
        FormSubmission formSubmission = new FormSubmission(formData);
        Map<String, List<String>> errors = validateProviderResponsePhoneAndEmail.runValidation(formSubmission, submission);
        assertThat(errors).doesNotContainKey("providerResponseContactEmail");
        assertThat(errors).doesNotContainKey("providerResponseContactPhoneNumber");
    }

    @Test
    void shouldThrowAnErrorWhenEmailIsBlankAndPhoneIsNotValid() {
        //create submission object with inputData
        Submission submission = new SubmissionTestBuilder().build();

        Map<String, Object> formData = Map.of(
                "providerResponseContactEmail", BLANK_PHONE_EMAIL,
                "providerResponseContactPhoneNumber",INVALID_PHONE_NUMBER
        );
        FormSubmission formSubmission = new FormSubmission(formData);
        Map<String, List<String>> errors = validateProviderResponsePhoneAndEmail.runValidation(formSubmission, submission);
        assertThat(errors).doesNotContainKey("providerResponseContactEmail");
        assertThat(errors).containsKey("providerResponseContactPhoneNumber");
        assertThat(errors).containsValue(List.of("Make sure the phone number is valid and includes 10 digits."));
    }

    @Test
    void shouldNotThrowAnErrorWhenEmailIsBlankAndPhoneIsValid() {
        //create submission object with inputData
        Submission submission = new SubmissionTestBuilder().build();

        Map<String, Object> formData = Map.of(
                "providerResponseContactEmail", BLANK_PHONE_EMAIL,
                "providerResponseContactPhoneNumber",VALID_PHONE_NUMBER
        );
        FormSubmission formSubmission = new FormSubmission(formData);
        Map<String, List<String>> errors = validateProviderResponsePhoneAndEmail.runValidation(formSubmission, submission);
        assertThat(errors).doesNotContainKey("providerResponseContactEmail");
        assertThat(errors).doesNotContainKey("providerResponseContactPhoneNumber");
    }

    @Test
    void shouldThrowAnErrorWhenEmailAndPhoneNumberIsBlank() {
        //create submission object with inputData
        Submission submission = new SubmissionTestBuilder().build();

        Map<String, Object> formData = Map.of(
                "providerResponseContactEmail", BLANK_PHONE_EMAIL,
                "providerResponseContactPhoneNumber",BLANK_PHONE_EMAIL
        );
        FormSubmission formSubmission = new FormSubmission(formData);
        Map<String, List<String>> errors = validateProviderResponsePhoneAndEmail.runValidation(formSubmission, submission);
        assertThat(errors).containsKey("providerResponseContactEmail");
        assertThat(errors).containsValue(List.of("Please provide an email address."));
        assertThat(errors).containsKey("providerResponseContactPhoneNumber");
        assertThat(errors).containsValue(List.of("Make sure the phone number is valid and includes 10 digits."));
    }

    @Test
    void shouldNotThrowAnErrorWhenEmailAndPhoneNumberIsValid() {
        //create submission object with inputData
        Submission submission = new SubmissionTestBuilder().build();

        Map<String, Object> formData = Map.of(
                "providerResponseContactEmail", VALID_EMAIL_ADDRESS,
                "providerResponseContactPhoneNumber",VALID_PHONE_NUMBER
        );
        FormSubmission formSubmission = new FormSubmission(formData);
        Map<String, List<String>> errors = validateProviderResponsePhoneAndEmail.runValidation(formSubmission, submission);
        assertThat(errors).doesNotContainKey("providerResponseContactEmail");
        assertThat(errors).doesNotContainKey("providerResponseContactPhoneNumber");
    }

}