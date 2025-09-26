package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ValidateProviderNoResponseSubmissionHasNotExpired implements Action {

  private final SubmissionRepositoryService submissionRepositoryService;
  private static final String HIDDEN_PROVIDER_NO_RESPONSE_INPUT = "applicationSubmissionValid";
  private final MessageSource messageSource;

  public ValidateProviderNoResponseSubmissionHasNotExpired(SubmissionRepositoryService submissionRepositoryService,
      MessageSource messageSource) {
    this.submissionRepositoryService = submissionRepositoryService;
    this.messageSource = messageSource;
  }

  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission providerSubmission) {

    Map<String, List<String>> errorMessages = new HashMap<>();
    Optional<Submission> familySubmissionOptional = getFamilySubmission(providerSubmission, submissionRepositoryService);
    //check if family submission is present and if so check, that the
    boolean providerSubmissionHasExpired = familySubmissionOptional.map(familySubmission ->
        ProviderSubmissionUtilities.hasProviderApplicationExpired(familySubmission, providerSubmission))
        .orElse(false);

    Map<String, Object> providerData = providerSubmission.getInputData();

    Locale locale = LocaleContextHolder.getLocale();
    if (providerSubmissionHasExpired) {
      errorMessages.put(HIDDEN_PROVIDER_NO_RESPONSE_INPUT,
          List.of(messageSource.getMessage("errors.provider-response-expired",
              new Object[]{providerData.getOrDefault("ccrrName", ""), providerData.getOrDefault("ccrrPhoneNumber", "")},
              locale)));
    }
    return errorMessages;
  }

  private static Optional<Submission> getFamilySubmission(Submission providerSubmission, SubmissionRepositoryService submissionRepositoryService) {
    Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
    if (familySubmissionId.isPresent()) {
      return submissionRepositoryService.findById(familySubmissionId.get());
    }
    return Optional.empty();
  }
}
