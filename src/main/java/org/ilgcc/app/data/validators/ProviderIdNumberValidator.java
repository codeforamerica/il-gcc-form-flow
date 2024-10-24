package org.ilgcc.app.data.validators;

import formflow.library.data.SubmissionRepositoryService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import org.ilgcc.app.data.ProviderRepository;
import org.ilgcc.app.data.ProviderRepositoryService;
import org.ilgcc.app.data.TransmissionRepositoryService;
import org.ilgcc.app.data.annotations.ProviderIdNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Validator class for custom {@link org.ilgcc.app.data.annotations.ProviderIdNumber} annotation. This validator checks if the input
 * string is a valid provider id number.
 */
@Component
public class ProviderIdNumberValidator implements ConstraintValidator<ProviderIdNumber, String> {

  @Autowired
  ProviderRepositoryService providerRepositoryService;

  @Autowired
  SubmissionRepositoryService submissionRepositoryService;

  @Autowired
  private ApplicationContext context;

//  public ProviderIdNumberValidator(@Autowired ProviderRepositoryService providerRepositoryService) {
//    this.providerRepositoryService = providerRepositoryService;
//  }

  /**
   * Validates the given provider id number.
   *
   * @param value   The provider id number to validate.
   * @param context Context in which the constraint is evaluated.
   * @return {@code true} if the phone number is valid.
   */
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value != null && !value.isBlank()){
//      boolean doProvidersExist = providerRepositoryService.doProvidersExist();

      return Pattern.matches("(\\([2-9][0-8][0-9]\\)\\s\\d{3}-\\d{4})", value);
    }
    return true;
  }
}
