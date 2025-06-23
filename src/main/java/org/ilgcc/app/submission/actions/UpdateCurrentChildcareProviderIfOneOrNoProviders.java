package org.ilgcc.app.submission.actions;

import static java.util.Collections.emptyList;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class UpdateCurrentChildcareProviderIfOneOrNoProviders implements Action {

    @Override
    @SuppressWarnings("unchecked")
    public void run(FormSubmission formSubmission, Submission submission, String id) {
        List<Map<String, Object>> providers = (List<Map<String, Object>>) submission.getInputData()
            .getOrDefault("providers", emptyList());
        List<String> currentChildcareProviders = new ArrayList<>();
        if (providers.size() <= 1) {
            if (providers.isEmpty()) {
                currentChildcareProviders.add("NO_PROVIDER");
            } else {
                currentChildcareProviders.add(providers.getFirst().getOrDefault("uuid", "").toString());
            }
            formSubmission.getFormData().putIfAbsent("currentChildcareProvider[]", currentChildcareProviders);
        }
    }
}
