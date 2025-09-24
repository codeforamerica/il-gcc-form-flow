package org.ilgcc.app.submission.actions;


import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getFamilySubmissionId;

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
public class ValidateExistingProviderSubmissionHasNotExpired implements Action {

  private final SubmissionRepositoryService submissionRepositoryService;
  private static final String PROVIDER_AGREES_TO_CARE = "providerResponseAgreeToCare";
  private final MessageSource messageSource;
  private Optional<Submission> familySubmissionOptional;
  private static final String CCRR_NAME_INPUT = "ccrrName";
  private static final String CCRR_PHONE_NUMBER_INPUT = "ccrrPhoneNumber";

  public ValidateExistingProviderSubmissionHasNotExpired(SubmissionRepositoryService submissionRepositoryService,
      MessageSource messageSource) {
    this.submissionRepositoryService = submissionRepositoryService;
    this.messageSource = messageSource;
  }

  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission providerSubmission) {
    Map<String, Object> providerInputData = providerSubmission.getInputData();
    //get family submission from provider submission
    Optional<UUID> familySubmissionID = getFamilySubmissionId(providerSubmission);

    familySubmissionID.ifPresent(familySubmissionId -> {
      familySubmissionOptional = submissionRepositoryService.findById(familySubmissionId);
      if (familySubmissionOptional.isPresent()) {
        Map<String, Object> familyInputData = familySubmissionOptional.get().getInputData();
        if (familyInputData.containsKey(CCRR_NAME_INPUT)) {
          providerInputData.put(CCRR_NAME_INPUT, familyInputData.getOrDefault(CCRR_NAME_INPUT, ""));
        } else {
          log.error("Could not find CCR&R name for the familySubmissionId: {}", familySubmissionID);
        }
        if (familyInputData.containsKey(CCRR_PHONE_NUMBER_INPUT)) {
          providerInputData.put(CCRR_PHONE_NUMBER_INPUT, familyInputData.getOrDefault(CCRR_PHONE_NUMBER_INPUT, ""));
        } else {
          log.error("Could not find CCR&R phone number for the familySubmissionId: {}", familySubmissionID);
        }
      } else {
        log.error("Could not find submission for the familySubmissionId: {}", familySubmissionID);
      }
    });

    Map<String, List<String>> errorMessages = new HashMap<>();
    boolean providerSubmissionHasExpired = !(ProviderSubmissionUtilities.providerSubmissionHasNotExpired(providerSubmission,
        submissionRepositoryService));
    Map<String, Object> providerData = providerSubmission.getInputData();

    Locale locale = LocaleContextHolder.getLocale();
    if (providerSubmissionHasExpired) {
      errorMessages.put(PROVIDER_AGREES_TO_CARE,
          List.of(messageSource.getMessage("errors.provider-response-expired",
              new Object[]{providerData.getOrDefault("ccrrName", ""), providerData.getOrDefault("ccrrPhoneNumber", "")},
              locale)));
    }
    return errorMessages;
  }
}
