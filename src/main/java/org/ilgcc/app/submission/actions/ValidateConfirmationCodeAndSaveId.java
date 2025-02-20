package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_FAMILY_SUBMISSION_STATUS;
import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_FAMILY_SUBMISSION_ID;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateConfirmationCodeAndSaveId implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    MessageSource messageSource;

    private final HttpSession httpSession;

    public ValidateConfirmationCodeAndSaveId(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission providerSubmission) {
        Map<String, List<String>> errorMessages = new HashMap<>();

        String providerProvidedConfirmationCode = (String) formSubmission.getFormData()
                .getOrDefault("providerResponseFamilyShortCode", "");

        if (!providerProvidedConfirmationCode.isBlank()) {
            Optional<Submission> familySubmission = submissionRepositoryService.findByShortCode(providerProvidedConfirmationCode);

            if (familySubmission.isPresent()) {
                httpSession.setAttribute(SESSION_KEY_FAMILY_SUBMISSION_ID, familySubmission.get().getId());
                httpSession.removeAttribute(SESSION_KEY_FAMILY_SUBMISSION_STATUS);

                providerSubmission.getInputData().put("familySubmissionId", familySubmission.get().getId());
            } else {
                setErrorMessages(errorMessages);
            }
        } else {
            setErrorMessages(errorMessages);
        }

        return errorMessages;
    }

    private void setErrorMessages(Map<String, List<String>> errorMessages) {
        Locale locale = LocaleContextHolder.getLocale();
        errorMessages.put("providerResponseFamilyShortCode",
                List.of(messageSource.getMessage("errors.provide-applicant-number", null, locale)));
    }
}
