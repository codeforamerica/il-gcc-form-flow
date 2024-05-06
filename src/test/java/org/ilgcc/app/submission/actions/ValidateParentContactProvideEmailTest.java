package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

class ValidateParentContactProvideEmailTest {

    @Mock
    private MessageSource messageSource;
    private ValidateParentContactProvideEmail validator;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        validator = new ValidateParentContactProvideEmail();
        validator.messageSource = messageSource;
        when(messageSource.getMessage("errors.invalid-email", null, Locale.getDefault()))
                .thenReturn("Make sure the email you entered follows the right format.");
        when(messageSource.getMessage("errors.require-email", null, Locale.getDefault()))
                .thenReturn("Please provide an email address.");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close(); // Close mocks after each test
    }

    @Test
    void shouldThrowAnErrorWhenWhenEmailIsPreferredCommunicationAndEmailFieldIsBlank() {
        //create submission object with inputData
        Submission submission = new SubmissionTestBuilder()
            .with("parentContactPreferredCommunicationMethod", "email")
            .build();

        Map<String, Object> formData = Map.of(
                "parentContactEmail", ""
        );
        FormSubmission formSubmission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(formSubmission, submission);
        assertThat(errors).containsKey("parentContactEmail");
        assertThat(errors).containsValue(List.of("Please provide an email address."));
    }

    @Test
    void shouldErrorWhenEmailIsThePreferredCommunicationAndTheEmailIsInvalid(){
        Submission submission = new SubmissionTestBuilder()
            .with("parentContactPreferredCommunicationMethod", "email")
            .build();
        Map<String, Object> formData = Map.of(
            "parentContactEmail", "test@bademaiaddress"
        );
        FormSubmission formSubmission = new FormSubmission(formData);
        Map<String, List<String>> errors = validator.runValidation(formSubmission, submission);
        assertThat(errors).containsKey("parentContactEmail");
        assertThat(errors).containsValue(List.of("Make sure the email you entered follows the right format."));
    }
}
