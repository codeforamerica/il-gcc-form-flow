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
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateNewProviderSubmissionHasNotExpired implements Action {

  private final SubmissionRepositoryService submissionRepositoryService;
  private static final String PROVIDER_SIGNED_NAME = "providerSignedName";
  private final MessageSource messageSource;
  private static final String CCRR_NAME_INPUT = "ccrrName";
  private static final String CCRR_PHONE_NUMBER_INPUT = "ccrrPhoneNumber";

  public ValidateNewProviderSubmissionHasNotExpired(SubmissionRepositoryService submissionRepositoryService,
      MessageSource messageSource) {
    this.submissionRepositoryService = submissionRepositoryService;
    this.messageSource = messageSource;
  }

  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission providerSubmission) {
    Map<String, Object> providerInputData = providerSubmission.getInputData();
    Optional<Submission> familySubmissionOptional = getFamilySubmission(providerSubmission, submissionRepositoryService);
    Map<String, List<String>> errorMessages = new HashMap<>();
    if (familySubmissionOptional.isPresent()) {
      addCcrrNameAndPhoneNumberToProviderData(familySubmissionOptional.get(), providerInputData);
      boolean providerSubmissionHasExpired = ProviderSubmissionUtilities.hasProviderApplicationExpired(familySubmissionOptional.get(), providerSubmission);
      Map<String, Object> providerData = providerSubmission.getInputData();
      Locale locale = LocaleContextHolder.getLocale();
      if (providerSubmissionHasExpired) {
        errorMessages.put(PROVIDER_SIGNED_NAME,
            List.of(messageSource.getMessage("errors.provider-response-expired",
                new Object[]{providerData.getOrDefault("ccrrName", ""), providerData.getOrDefault("ccrrPhoneNumber", "")},
                locale)));
      }
    } else {
      log.error("Could not find submission for the familySubmission for provider: {}", providerSubmission.getId());
    }
    return errorMessages;
  }

  private static void addCcrrNameAndPhoneNumberToProviderData(Submission familySubmission, Map<String, Object> providerInputData) {
    Map<String, Object> familyInputData = familySubmission.getInputData();
    if (familyInputData.containsKey(CCRR_NAME_INPUT)) {
      providerInputData.put(CCRR_NAME_INPUT, familyInputData.getOrDefault(CCRR_NAME_INPUT, ""));
    } else {
      log.error("Could not find CCR&R name for the familySubmissionId: {}", familySubmission.getId());
    }
    if (familyInputData.containsKey(CCRR_PHONE_NUMBER_INPUT)) {
      providerInputData.put(CCRR_PHONE_NUMBER_INPUT, familyInputData.getOrDefault(CCRR_PHONE_NUMBER_INPUT, ""));
    } else {
      log.error("Could not find CCR&R phone number for the familySubmissionId: {}", familySubmission.getId());
    }
  }

  private static Optional<Submission> getFamilySubmission(Submission providerSubmission, SubmissionRepositoryService submissionRepositoryService) {
    Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
    if (familySubmissionId.isPresent()) {
      return submissionRepositoryService.findById(familySubmissionId.get());
    }
    return Optional.empty();
  }
}
