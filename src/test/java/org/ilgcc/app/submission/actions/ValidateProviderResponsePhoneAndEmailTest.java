package org.ilgcc.app.submission.actions;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.assertj.core.api.AssertionsForClassTypes;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.email.sendgrid.SendGridEmailValidationService;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_INVALID_PROVIDER_RESPONSE_CONTACT_EMAIL;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(classes = {IlGCCApplication.class, SendGridEmailValidationService.class})
class ValidateProviderResponsePhoneAndEmailTest {

    @Mock
    private MessageSource messageSource;

    @MockitoSpyBean
    SendGridEmailValidationService mockSendGridEmailValidationService;

    @Autowired
    private ValidateProviderResponsePhoneAndEmail validateProviderResponsePhoneAndEmail;

    private AutoCloseable closeable;
    private final String BLANK_PHONE_EMAIL = "";
    private final String VALID_PHONE_NUMBER = "(858) 934-9734";
    private final String INVALID_PHONE_NUMBER = "545rgr";
    private final String VALID_EMAIL_ADDRESS =  "test@email.com";
    private final String INVALID_EMAIL_ADDRESS = "test@email";
    private final String VALID_REGEX_EMAIL_ADDRESS = "test@gemail.com";
    private final String ROLE_EMAIL = "admin@gmail.com";
    private final String suggestedEmail = "test@gmail.com";

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        validateProviderResponsePhoneAndEmail.messageSource = messageSource;
        when(messageSource.getMessage("errors.require-email", null, Locale.getDefault()))
                .thenReturn("Please provide an email address.");
        when(messageSource.getMessage("errors.invalid-email.no-suggested-email-address", null, Locale.getDefault()))
                .thenReturn("Make sure the email address is valid and follows this format: name@email.com");
        when(messageSource.getMessage("errors.invalid-email.with-suggested-email-address", new Object[]{suggestedEmail},
                Locale.getDefault()))
                .thenReturn("Make sure the email address is valid. Did you mean test@gmail.com?");
        when(messageSource.getMessage("errors.invalid-phone-number", null, Locale.getDefault()))
                .thenReturn("Make sure the phone number is valid and includes 10 digits.");

    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close(); // Close mocks after each test
        RequestContextHolder.resetRequestAttributes();
    }

    void setupMockSession(String value) {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpSession mockSession = mock(HttpSession.class);
        when(mockSession.getAttribute(SESSION_KEY_INVALID_PROVIDER_RESPONSE_CONTACT_EMAIL)).thenReturn(value);
        when(mockRequest.getSession()).thenReturn(mockSession);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
    }

    @Test
    void shouldThrowAnErrorWhenEmailDoesNotMatchRegexAndPhoneIsBlank() {
        //create submission object with inputData
        Submission submission = new SubmissionTestBuilder().build();

        Map<String, Object> formData = Map.of(
                "providerResponseContactEmail", INVALID_EMAIL_ADDRESS,
                "providerResponseContactPhoneNumber",BLANK_PHONE_EMAIL
        );
        FormSubmission formSubmission = new FormSubmission(formData);
        Map<String, List<String>> errors = validateProviderResponsePhoneAndEmail.runValidation(formSubmission, submission);
        assertThat(errors).containsKey("providerResponseContactEmail");
        assertThat(errors).containsValue(List.of("Make sure the email address is valid and follows this format: name@email.com"));
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

    @Test
    void shouldReturnNoErrorsWhenSendGridIsReachedAndEmailIsValid() throws IOException {
        HashMap<String, String> result = new HashMap<>();
        result.put("endpointReached", "success");
        result.put("emailIsValid", "true");
        setupMockSession("");
        Map<String, Object> formData = Map.of("providerResponseContactEmail", VALID_EMAIL_ADDRESS);
        FormSubmission formSubmission = new FormSubmission(formData);
        when(mockSendGridEmailValidationService.validateEmail(VALID_EMAIL_ADDRESS,false))
                .thenReturn(result);
        Map<String, List<String>> errors = validateProviderResponsePhoneAndEmail.runValidation(formSubmission, new Submission());
        assertTrue(errors.isEmpty());
    }

    @Test
    void shouldReturnErrorWhenSendGridIsReachedEmailIsNotValidAndHasNoSuggestions() throws IOException {
        HashMap<String, String> result = new HashMap<>();
        result.put("endpointReached", "success");
        result.put("emailIsValid", "false");
        result.put("hasSuggestion", "false");

        setupMockSession("");
        Map<String, Object> formData = Map.of("providerResponseContactEmail", VALID_REGEX_EMAIL_ADDRESS);
        FormSubmission formSubmission = new FormSubmission(formData);
        when(mockSendGridEmailValidationService.validateEmail(VALID_REGEX_EMAIL_ADDRESS,false))
                .thenReturn(result);
        Map<String, List<String>> errors = validateProviderResponsePhoneAndEmail.runValidation(formSubmission, new Submission());
        assertFalse(errors.isEmpty());
        AssertionsForClassTypes.assertThat(errors.get("providerResponseContactEmail").contains("Make sure the email address is valid and follows this format: name@email.com")).isTrue();
    }

    @Test
    void shouldReturnErrorWhenSendGridIsReachedEmailIsNotValidButHasSuggestion() throws IOException {
        HashMap<String, String> result = new HashMap<>();
        result.put("endpointReached", "success");
        result.put("emailIsValid", "false");
        result.put("hasSuggestion", "true");
        result.put("suggestedEmail", suggestedEmail);

        setupMockSession("");
        Map<String, Object> formData = Map.of("providerResponseContactEmail", VALID_REGEX_EMAIL_ADDRESS);
        FormSubmission formSubmission = new FormSubmission(formData);
        when(mockSendGridEmailValidationService.validateEmail(VALID_REGEX_EMAIL_ADDRESS,false))
                .thenReturn(result);
        Map<String, List<String>> errors = validateProviderResponsePhoneAndEmail.runValidation(formSubmission, new Submission());
        assertFalse(errors.isEmpty());
        AssertionsForClassTypes.assertThat(errors.get("providerResponseContactEmail").contains("Make sure the email address is valid. Did you mean test@gmail.com?")).isTrue();
    }

    @Test
    void shouldNotReturnErrorWhenSendGridIsReachedEmailIsRoleAddress() throws IOException {
        HashMap<String, String> result = new HashMap<>();
        result.put("endpointReached", "success");
        result.put("emailIsValid", "true");

        setupMockSession("");
        Map<String, Object> formData = Map.of("providerResponseContactEmail", ROLE_EMAIL);
        FormSubmission formSubmission = new FormSubmission(formData);
        when(mockSendGridEmailValidationService.validateEmail(ROLE_EMAIL,false))
                .thenReturn(result);
        Map<String, List<String>> errors = validateProviderResponsePhoneAndEmail.runValidation(formSubmission, new Submission());
        assertTrue(errors.isEmpty());
    }

    @Test
    void shouldNotReturnErrorWhenInvalidEmailIsPassedIntoInputFieldTwice() throws IOException {
        HashMap<String, String> result = new HashMap<>();
        result.put("endpointReached", "success");
        result.put("emailIsValid", "false");
        result.put("hasSuggestion", "true");
        result.put("suggestedEmail", suggestedEmail);

        setupMockSession(VALID_REGEX_EMAIL_ADDRESS);
        Map<String, Object> formData = Map.of("providerResponseContactEmail", VALID_REGEX_EMAIL_ADDRESS);
        FormSubmission formSubmission = new FormSubmission(formData);
        when(mockSendGridEmailValidationService.validateEmail(VALID_REGEX_EMAIL_ADDRESS,false))
                .thenReturn(result);
        Map<String, List<String>> errors = validateProviderResponsePhoneAndEmail.runValidation(formSubmission, new Submission());
        assertTrue(errors.isEmpty());
    }

}