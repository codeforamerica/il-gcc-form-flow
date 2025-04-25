package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ProviderChoseITINTaxIdType implements Condition {

    @Override
    public Boolean run(Submission submission) {
        Map<String, Object> inputData = submission.getInputData();
        return ("ITIN".equals(inputData.getOrDefault("providerTaxIdType", "").toString()) && inputData.getOrDefault("providerITIN", "").toString().isEmpty());
    }
}
