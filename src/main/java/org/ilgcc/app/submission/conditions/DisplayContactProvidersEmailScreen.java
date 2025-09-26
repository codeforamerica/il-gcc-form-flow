package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
public class DisplayContactProvidersEmailScreen implements Condition {

    @Override
    public Boolean run(Submission submission, String subflowUuid) {
        Map<String, Object> subflow = submission.getSubflowEntryByUuid("contactProviders", subflowUuid);
        return SubmissionUtilities.isSelectedAsProviderContactMethod(subflow, "EMAIL")
                && isMissingProviderEmail(subflow);
    }

    private Boolean isMissingProviderEmail(Map<String, Object> subflowData) {
        return subflowData.getOrDefault("familyIntendedProviderEmail", "").toString().isBlank();
    }

}
