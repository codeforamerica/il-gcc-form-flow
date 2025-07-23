package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetCurrentProviderForSingleProviderSubmission implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    MessageSource messageSource;

    @Override
    public void run(Submission providerSubmission) {
        Optional<String> familySubmissionShortCode =
                ProviderSubmissionUtilities.getFamilySubmissionShortCode(providerSubmission);
        if (familySubmissionShortCode.isPresent()) {
            Optional<Submission> familySubmission = submissionRepositoryService.findByShortCode(familySubmissionShortCode.get());
            if (familySubmission.isPresent()) {
                if (SubmissionUtilities.allChildcareSchedulesAreForTheSameProvider(familySubmission.get().getInputData())) {
                    List<Map<String, Object>> providers = (List<Map<String, Object>>) familySubmission.get().getInputData().get("providers");
                    String providerId = providers.getFirst().get("uuid").toString();
                    providerSubmission.getInputData().put("currentProviderUuid", providerId);
                    submissionRepositoryService.save(providerSubmission);
                }
            }
        }
    }
}
