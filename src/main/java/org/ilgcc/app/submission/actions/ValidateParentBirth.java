package org.ilgcc.app.submission.actions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateParentBirth extends ValidateBirthdate {

  public ValidateParentBirth(MessageSource messageSource) {
    super(messageSource, "parentBirth", "parentBirthDate");
  }
}
