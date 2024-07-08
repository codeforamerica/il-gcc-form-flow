package org.ilgcc.jobs;

import lombok.extern.slf4j.Slf4j;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardProgressBar;
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
}
