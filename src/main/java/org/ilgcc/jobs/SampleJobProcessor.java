package org.ilgcc.jobs;

import static org.jobrunr.scheduling.JobBuilder.aJob;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardProgressBar;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SampleJobProcessor implements JobProcessorInterface {

  @Recurring(id = "sample-recurring-job", cron = "0 0/15 * * *")
  @Job(name = "Sample recurring job")
  public void doRecurringJob() {
    System.out.println("Doing some work without arguments");
  }

  @Override
  public void doSimpleJob(String anArgument) {
    System.out.println("Doing some work: " + anArgument);
  }

  @Override
  public void doLongRunningJob(String anArgument) {
    try {
      for (int i = 0; i < 10; i++) {
        System.out.printf("Doing work item %d: %s%n", i, anArgument);
        Thread.sleep(1000);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public void doLongRunningJobWithJobContext(String anArgument, JobContext jobContext) {
    try {
      log.warn("Starting long running job...");
      final JobDashboardProgressBar progressBar = jobContext.progressBar(10);
      for (int i = 0; i < 10; i++) {
        if (anArgument.matches("\\w*")) {
          log.info(String.format("Processing item %d: %s", i, anArgument));
          System.out.printf("Doing work item %d: %s%n", i, anArgument);
        }
        Thread.sleep(15000);
        progressBar.increaseByOne();
      }
      log.warn("Finished long running job...");
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * This is probably the way we want to do our jobs for ILCCAP but all will work, this just gives
   * the best way to set job specific meta data like labels and name
   * @param jobScheduler
   * @param uuid
   * @return
   */
  @Override
  public String doSimpleJobWithJobScheduler(JobScheduler jobScheduler, UUID uuid) {
    JobId id = jobScheduler.create(aJob()
        .withName("Sample job: " + uuid)
        .withId(uuid) // We can also just use the UUID generated by default per job
        .withLabels("Sample Label #1", "Sample Label #2")
        .withDetails(() -> doSimpleJob(uuid.toString())));

    return id.toString();
  }
}