package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

class ValidatePhoneNumberNotBlankTest {
    @Mock
    private MessageSource messageSource;
    private ValidatePhoneNumberNotBlank validator;
    private AutoCloseable closeable;

    private final String PROVIDER_PHONE_INPUT = "familyIntendedProviderPhoneNumber";
    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        validator = new ValidatePhoneNumberNotBlank();
        validator.messageSource = messageSource;
        when(messageSource.getMessage("errors.invalid-phone-number", null, Locale.getDefault()))
            .thenReturn("Make sure the phone number is valid and includes 10 digits.");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close(); // Close mocks after each test
    }
    @Test
    void shouldErrorWhenFamilyIntendedProviderPhoneNumberIsBlank() {
        Map<String, Object> formData = Map.of(
                PROVIDER_PHONE_INPUT, "     "
        );

        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).containsKey("familyIntendedProviderPhoneNumber");
        assertThat(errors.get("familyIntendedProviderPhoneNumber")).contains("Make sure the phone number is valid and includes 10 digits.");
    }

    @Test
    void shouldNotReturnErrorWhenFamilyIntendedProviderPhoneNumberIsNotBlank() {
        Map<String, Object> formData = Map.of(
            PROVIDER_PHONE_INPUT, "Not_Blank"
        );
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).doesNotContainKey("familyIntendedProviderPhoneNumber");
    }

    @Test
    void shouldErrorFamilyIntendedProviderPhoneNumberIsMissing() {
        Map<String, Object> formData = Map.of();
        FormSubmission submission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(submission, new Submission());

        assertThat(errors).containsKey("familyIntendedProviderPhoneNumber");
        assertThat(errors.get("familyIntendedProviderPhoneNumber")).contains("Make sure the phone number is valid and includes 10 digits.");
    }
}
