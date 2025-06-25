package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetProvidersData implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    MessageSource messageSource;

    @Override
    public void run(Submission providerSubmission) {
        Optional<String> familySubmissionShortCode =
                ProviderSubmissionUtilities.getFamilySubmissionByShortCode(providerSubmission);
        if (familySubmissionShortCode.isPresent()) {
            Optional<Submission> familySubmission = submissionRepositoryService.findByShortCode(familySubmissionShortCode.get());

            List<Map<String, Object>> providersData = ProviderSubmissionUtilities.getMultipleProviderDataForProviderResponse(
                    familySubmission.get());

            providerSubmission.getInputData().put("providersData", providersData);
            submissionRepositoryService.save(providerSubmission);
        }
    }

}
