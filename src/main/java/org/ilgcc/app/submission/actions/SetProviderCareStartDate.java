package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.DateUtilities;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetProviderCareStartDate implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Override
    public void run(Submission providerSubmission) {
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionId.isPresent()) {
            Optional<Submission> familySubmission = submissionRepositoryService.findById(familySubmissionId.get());
            if (familySubmission.isEmpty()) {
                log.warn("No family submission found for this id: {}", familySubmissionId.get());
                return;
            }
            Optional<LocalDate> earliestDate = getEarliestChildCCAPDate(familySubmission.get());
            if(earliestDate.isPresent()){
                providerSubmission.getInputData().put("providerCareStartDay", earliestDate.get().getDayOfMonth());
                providerSubmission.getInputData().put("providerCareStartMonth", earliestDate.get().getMonthValue());
                providerSubmission.getInputData().put("providerCareStartYear", earliestDate.get().getYear());
            }

            submissionRepositoryService.save(providerSubmission);
        }
    }

    private Optional<LocalDate> getEarliestChildCCAPDate(Submission submission) {
        String earliestChildCareDate = (String) submission.getInputData().getOrDefault("earliestChildcareStartDate", "");
        if (!earliestChildCareDate.isEmpty()) {
            return DateUtilities.parseStringDate(earliestChildCareDate);
        }
        return Optional.empty();
    }
}