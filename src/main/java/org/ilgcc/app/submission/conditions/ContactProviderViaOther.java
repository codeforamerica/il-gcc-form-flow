package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ContactProviderViaOther implements Condition {

    @Value("${il-gcc.enable-provider-messaging}")

    private boolean enableProviderMessaging;

    @Override
    public Boolean run(Submission submission) {
        Map<String, Object> inputData = submission.getInputData();
        return enableProviderMessaging && selectedOtherAsProviderContactMethod(inputData);
    }

    private Boolean selectedOtherAsProviderContactMethod(Map<String, Object> inputData){
        List<String> contactProviderMethodList = (List<String>) inputData.getOrDefault("contactProviderMethod[]", List.of());
        return contactProviderMethodList.contains("OTHER");
    }
}