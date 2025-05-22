package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
public class DisplayContactProvidersPhoneNumberScreen extends EnableMultipleProviders implements Condition {

    @Override
    public Boolean run(Submission submission, String subflowUuid) {
        Map<String, Object> subflowData = submission.getSubflowEntryByUuid("contactProviders", subflowUuid);
        return super.run(submission) && SubmissionUtilities.isSelectedAsProviderContactMethod(subflowData, "TEXT")
                && isMissingProviderPhoneNumber(subflowData);
    }

    private Boolean isMissingProviderPhoneNumber(Map<String, Object> subflowData) {
        return subflowData.getOrDefault("familyIntendedProviderPhoneNumber", "").toString().isBlank();
    }

}
