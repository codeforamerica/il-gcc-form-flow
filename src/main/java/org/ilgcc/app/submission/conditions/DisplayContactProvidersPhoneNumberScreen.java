package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
public class DisplayContactProvidersPhoneNumberScreen extends EnableMultipleProviders implements Condition {

    @Override
    public Boolean run(Submission submission, String uuid) {
        Map<String, Object> inputData = submission.getInputData();
        return super.run(submission) && SubmissionUtilities.isSelectedAsProviderContactMethod(inputData, "TEXT")
                && isMissingProviderEmail(inputData);
    }

    private Boolean isMissingProviderEmail(Map<String, Object> inputData) {
        return inputData.getOrDefault("familyIntendedProviderPhoneNumber", "").toString().isBlank();
    }

}
