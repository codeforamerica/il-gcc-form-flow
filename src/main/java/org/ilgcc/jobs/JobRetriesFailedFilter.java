package org.ilgcc.jobs;

import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jobrunr.jobs.Job;
import org.jobrunr.jobs.JobParameter;
import org.jobrunr.jobs.filters.JobServerFilter;
import org.jobrunr.jobs.states.FailedState;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobRetriesFailedFilter implements JobServerFilter {
    @Override
    public void onFailedAfterRetries(Job job) {
        String exceptionMessage = getExceptionMessage(job);
        List<JobParameter> jobParameters = job.getJobDetails().getJobParameters();

        if ("Lookup Work Item ID for Transaction".equals(job.getJobName())) {

            if (jobParameters.isEmpty() || !jobParameters.getFirst().getClassName().equals(Transaction.class.getName())) {
                log.error("Work item lookup job with ID {} failed and was unable to recover the Transaction parameter. Exception: {}",
                        job.getId(), exceptionMessage);
                return;
            }

            Transaction transaction = (Transaction) jobParameters.getFirst().getObject();
            log.error("Work item lookup job with ID {} failed for Transaction ID {} after all retries exhausted. Exception: {}",
                    job.getId(), transaction.getTransactionId(), exceptionMessage);
        } else if ("Send CCMS Submission Payload".equals(job.getJobName())) {

            if (jobParameters.isEmpty() || !jobParameters.getFirst().getClassName().equals(UUID.class.getName())) {
                log.error("CCMS Submission job with ID {} failed and was unable to recover the UUID parameter. Exception: {}",
                        job.getId(), exceptionMessage);
                return;
            }

            UUID submissionId = (UUID) jobParameters.getFirst().getObject();
            log.error("CCMS Submission job with ID {} failed for Submission {} after all retries exhausted. Exception: {}",
                    job.getId(), submissionId, exceptionMessage);
        }
    }

    private static @NotNull String getExceptionMessage(Job job) {
        String exceptionMessage = job.getLastJobStateOfType(FailedState.class)
                .map(FailedState::getExceptionMessage)
                .orElse("Unknown exception occurred");
        return exceptionMessage;
    }
}
