package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetFamilyIntendedProviderName implements Action {
    
    @Autowired
    SubmissionRepositoryService submissionRepositoryService;
    
    @Override
    public void run(FormSubmission formData, Submission submission, String iterationId) {
        Map<String, Object> subflowEntryByUuid = submission.getSubflowEntryByUuid("providers", iterationId);
        String childCareProgramName = formData.getFormData().getOrDefault("childCareProgramName", "").toString();
        String providerFirstName = formData.getFormData().getOrDefault("providerFirstName", "").toString();
        String providerLastName = formData.getFormData().getOrDefault("providerLastName", "").toString();
        
        if (!childCareProgramName.isBlank()) {
            subflowEntryByUuid.put("familyIntendedProviderName", childCareProgramName);
        }
        else if (!providerFirstName.isBlank() && !providerLastName.isBlank()) {
            subflowEntryByUuid.put("familyIntendedProviderName", providerFirstName + " " + providerLastName);
        }
        
        submissionRepositoryService.save(submission);
    }
}
