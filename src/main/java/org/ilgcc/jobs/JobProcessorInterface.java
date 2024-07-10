package org.ilgcc.jobs;

import java.util.UUID;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.scheduling.JobScheduler;

public interface JobProcessorInterface {
  void doSimpleJob(String anArgument);

  void doLongRunningJob(String anArgument);

  void doLongRunningJobWithJobContext(String anArgument, JobContext jobContext);

  String doSimpleJobWithJobScheduler(JobScheduler jobScheduler, UUID uuid);
}