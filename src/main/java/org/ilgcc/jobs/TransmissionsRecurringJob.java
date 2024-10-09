package org.ilgcc.jobs;


import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import static org.ilgcc.app.utils.ProviderSubmissionUtilities.providerApplicationHasExpired;
import org.ilgcc.app.data.TransmissionRepositoryService;
import org.ilgcc.app.file_transfer.DocumentTransferRequestService;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.stereotype.Service;

@Slf4j
@Service

public class TransmissionsRecurringJob {

    private final S3PresignService s3PresignService;
    private final JobScheduler jobScheduler;
    private final DocumentTransferRequestService documentTransferRequestService;
    private final TransmissionRepositoryService transmissionRepositoryService;
    private final SubmissionRepositoryService submissionRepositoryService;

    public TransmissionsRecurringJob(
            S3PresignService s3PresignService,
            JobScheduler jobScheduler,
            DocumentTransferRequestService documentTransferRequestService,
            TransmissionRepositoryService transmissionRepositoryService,
            SubmissionRepositoryService submissionRepositoryService) {
        this.s3PresignService = s3PresignService;
        this.jobScheduler = jobScheduler;
        this.documentTransferRequestService = documentTransferRequestService;
        this.transmissionRepositoryService = transmissionRepositoryService;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Recurring(id = "no-provider-response-job", cron = "* * * * *")
    @Job(name = "No provider response job")
    public void NoProviderResponseJob(){
        List<Submission> submissionsWithoutTransmissions = transmissionRepositoryService.findSubmissionsWithoutTransmission();
        LocalDate todaysDate = LocalDate.now();

        if(submissionsWithoutTransmissions.isEmpty()){
            return;
        } else {
            for(Submission s: submissionsWithoutTransmissions){
                if(!hasProviderResponse(s) && providerApplicationHasExpired(s, todaysDate)){
//                   // enqueue
                } else if(hasProviderResponse(s) && providerApplicationHasExpired(s, todaysDate)){
                    throw new IllegalStateException(String.format("Weird, provider response exist but the provider response expired. Check submission: %s", s.getId()));
                }
            }
        }
    }

    private boolean hasProviderResponse(Submission submission){
        return submission.getInputData().containsKey("providerResponseSubmissionId");
    }
}
