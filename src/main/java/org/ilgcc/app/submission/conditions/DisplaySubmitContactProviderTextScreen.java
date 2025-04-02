package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DisplaySubmitContactProviderTextScreen implements Condition {

    @Value("${il-gcc.enable-provider-messaging}")
    private boolean enableProviderMessaging;

    @Override
    public Boolean run(Submission submission) {
        Map<String, Object> inputData = submission.getInputData();
        return enableProviderMessaging && selectedTextAsProviderContactMethod(inputData) && isMissingProviderPhoneNumber(inputData);
    }

    private Boolean isMissingProviderPhoneNumber(Map<String, Object> inputData){
        return inputData.getOrDefault("familyIntendedProviderPhoneNumber", "").toString().isBlank();
    }

    private Boolean selectedTextAsProviderContactMethod(Map<String, Object> inputData){
        List<String> contactProviderMethodList = (List<String>) inputData.getOrDefault("contactProviderMethod[]", List.of());
        return contactProviderMethodList.contains("TEXT");
    }
}
