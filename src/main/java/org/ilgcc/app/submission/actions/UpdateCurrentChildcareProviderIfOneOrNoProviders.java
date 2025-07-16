package org.ilgcc.app.submission.actions;

import static java.util.Collections.emptyList;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
public class UpdateCurrentChildcareProviderIfOneOrNoProviders implements Action {

    @Override
    @SuppressWarnings("unchecked")
    public void run(FormSubmission formSubmission, Submission submission, String id) {
        Map<String, Object> inputData = submission.getInputData();

        List<String> childcareProvidersForCurrentChild = (List<String>) formSubmission.getFormData().getOrDefault("childcareProvidersForCurrentChild[]", new ArrayList<>());

        if(childcareProvidersForCurrentChild.isEmpty()) {
            List<Map<String, Object>> providers = (List<Map<String, Object>>) inputData.getOrDefault("providers", emptyList());
            if (SubmissionUtilities.isNoProviderSubmission(inputData)) {
                childcareProvidersForCurrentChild.add("NO_PROVIDER");
            }
            if (providers.size() == 1) {
                childcareProvidersForCurrentChild.add(providers.getFirst().getOrDefault("uuid", "").toString());
            }
            formSubmission.getFormData().put("childcareProvidersForCurrentChild[]", childcareProvidersForCurrentChild);
        }
    }
}
