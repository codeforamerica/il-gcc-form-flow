package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.math.BigInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SaveProviderNumberAsBigInteger implements Action {

    @Override
    public void run(Submission providerSubmission) {
        String providerNumber = (String) providerSubmission.getInputData().getOrDefault("providerResponseProviderNumber", "");
        if (!providerNumber.isEmpty()) {
            providerSubmission.getInputData().put("providerResponseProviderNumber", new BigInteger(providerNumber));
        }
    }
}
