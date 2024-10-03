package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SaveApplicationIdFromApplicationId implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;
    @Override
    public void run(FormSubmission formSubmission, Submission providerSubmission) {
        boolean hasApplicationID = providerSubmission.getInputData().containsKey("familyApplicationId");
        String providerProvidedConfirmationCode = (String) formSubmission.getFormData().getOrDefault("providerResponseFamilyConfirmationCode", "");
        if(!hasApplicationID && !providerProvidedConfirmationCode.isBlank()){
            Optional<Submission> clientSubmission = submissionRepositoryService.findByShortCode(providerProvidedConfirmationCode);
            if(clientSubmission.isPresent()){
                providerSubmission.getInputData().put("familyApplicationId", clientSubmission.get().getId());
            }
        }
    }
}
