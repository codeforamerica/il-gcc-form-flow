package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DisplaySubmitContactProviderEmailScreen implements Condition {
    @Override
    public Boolean run(Submission submission) {
        Map<String, Object> inputData = submission.getInputData();
        return selectedEmailAsProviderContactMethod(inputData) && isMissingProviderEmail(inputData);
    }

    private Boolean isMissingProviderEmail(Map<String, Object> inputData){
        return inputData.getOrDefault("familyIntendedProviderEmail", "").toString().isBlank();
    }

    private Boolean selectedEmailAsProviderContactMethod(Map<String, Object> inputData){
        List<String> contactProviderMethodList = (List<String>) inputData.getOrDefault("contactProviderMethod[]", List.of());
        return contactProviderMethodList.contains("EMAIL");
    }
}
