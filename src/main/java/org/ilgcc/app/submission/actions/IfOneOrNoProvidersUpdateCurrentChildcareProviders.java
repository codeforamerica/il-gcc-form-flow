package org.ilgcc.app.submission.actions;

import static java.util.Collections.emptyList;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class IfOneOrNoProvidersUpdateCurrentChildcareProviders implements Action {

    private final SubmissionRepositoryService submissionRepositoryService;

    public IfOneOrNoProvidersUpdateCurrentChildcareProviders(SubmissionRepositoryService submissionRepositoryService) {
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(FormSubmission formSubmission, Submission submission, String id){
        var providers = (List<Map<String, Object>>) submission.getInputData().getOrDefault("providers", emptyList());
        List<String> currentChildcareProviders = new ArrayList<>();
        if (providers.size() <= 1) {
            if (providers.isEmpty()){
                currentChildcareProviders.add("NO_PROVIDER");
            }else {
                currentChildcareProviders.add(providers.getFirst().getOrDefault("familyIntendedProviderName", "").toString());
            }
            formSubmission.getFormData().putIfAbsent("currentChildcareProvider[]", currentChildcareProviders);
//            submission.getSubflowEntryByUuid("childcareSchedules", id).putIfAbsent("currentChildcareProvider[]", currentChildcareProviders);
//            submissionRepositoryService.save(submission);
        }
        return;
    }
}
