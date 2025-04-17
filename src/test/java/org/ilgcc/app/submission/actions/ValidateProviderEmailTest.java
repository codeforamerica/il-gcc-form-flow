package org.ilgcc.app.submission.actions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.email.SendGridEmailValidationService;
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


@ActiveProfiles("test")
@SpringBootTest( classes = {IlGCCApplication.class, SendGridEmailValidationService.class})
class ValidateProviderEmailTest {
  @Mock
  private MessageSource messageSource;

  @MockitoSpyBean
  SendGridEmailValidationService mockSendGridEmailValidationService;
  @Autowired
  private ValidateProviderEmail validateProviderEmailAction;

  private AutoCloseable closeable;
  private String suggestedEmail = "foo@bar.com";
  private String VALID_EMAIL = "bar@foo.com";
  private String INVALID_EMAIL = "bar@gemaildas.com";
  private String MALFORMED_EMAIL = "foo@bar";
  private final String PROVIDER_EMAIL_INPUT = "familyIntendedProviderEmail";


  @BeforeEach
  void setUp() throws Exception {
    closeable = MockitoAnnotations.openMocks(this);
    validateProviderEmailAction.messageSource = messageSource;
    when(messageSource.getMessage("errors.invalid-email.no-suggested-email-address", null, Locale.getDefault()))
        .thenReturn("");
    when(messageSource.getMessage("errors.invalid-email", null, Locale.getDefault()))
        .thenReturn("Enter an email that follows the right format. For example: name@email.com");
    when(messageSource.getMessage("errors.invalid-email.with-suggested-email-address", new Object[]{suggestedEmail}, Locale.getDefault()))
        .thenReturn("Make sure the email address is valid. Did you mean foo@bar.com?");
  }
  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void shouldNotValidateEmailIfEmailIsBlank() throws IOException {
    HashMap<String, String> result = new HashMap<>();
    result.put("ShouldNotReachThisService", "true");
    Map<String, Object> formData = Map.of(PROVIDER_EMAIL_INPUT, "");
    FormSubmission formSubmission = new FormSubmission(formData);
    doReturn(result).when(mockSendGridEmailValidationService).validateEmail(PROVIDER_EMAIL_INPUT);
    Map<String, List<String>> errors = validateProviderEmailAction.runValidation(formSubmission, new Submission());
    assertTrue(errors.isEmpty());
  }

  @Test
  void shouldNotValidateEmailIfEmailIsRegexEmailPatternNotMet() throws IOException {
    HashMap<String, String> result = new HashMap<>();
    result.put("ShouldNotReachThisService", "true");
    Map<String, Object> formData = Map.of(PROVIDER_EMAIL_INPUT, MALFORMED_EMAIL);
    FormSubmission formSubmission = new FormSubmission(formData);

    verify(mockSendGridEmailValidationService, never()).validateEmail(MALFORMED_EMAIL);
    Map<String, List<String>> errors = validateProviderEmailAction.runValidation(formSubmission, new Submission());
    assertTrue(errors.isEmpty());
  }

  @Test
  void shouldReturnNoErrorsWhenSendGridIsReachedAndEmailIsValid() throws IOException {
    HashMap<String, String> emailIsValidReturn = new HashMap<>();
    emailIsValidReturn.put("endpointReached", "success");
    emailIsValidReturn.put("emailIsValid", "true");

    Map<String, Object> formData = Map.of(PROVIDER_EMAIL_INPUT, VALID_EMAIL);
    FormSubmission formSubmission = new FormSubmission(formData);
    when(mockSendGridEmailValidationService.validateEmail(VALID_EMAIL))
        .thenReturn(emailIsValidReturn);
    Map<String, List<String>> errors = validateProviderEmailAction.runValidation(formSubmission, new Submission());
    assertTrue(errors.isEmpty());
  }

  @Test
  void shouldReturnErrorWhenSendGridIsReachedEmailIsNotValidAndHasNoSuggestions() throws IOException {
    HashMap<String, String> emailIsInvalidReturn = new HashMap<>();
    emailIsInvalidReturn.put("endpointReached", "success");
    emailIsInvalidReturn.put("emailIsValid", "false");
    emailIsInvalidReturn.put("hasSuggestion", "false");

    Map<String, Object> formData = Map.of(PROVIDER_EMAIL_INPUT, INVALID_EMAIL);
    FormSubmission formSubmission = new FormSubmission(formData);
    when(mockSendGridEmailValidationService.validateEmail(INVALID_EMAIL))
        .thenReturn(emailIsInvalidReturn);
    Map<String, List<String>> errors = validateProviderEmailAction.runValidation(formSubmission, new Submission());
    assertFalse(errors.isEmpty());
  }
}