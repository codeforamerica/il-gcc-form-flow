package org.ilgcc.jobs;

import org.jobrunr.jobs.context.JobContext;

public interface JobProcessorInterface {
  void doSimpleJob(String anArgument);

  void doLongRunningJob(String anArgument);

  void doLongRunningJobWithJobContext(String anArgument, JobContext jobContext);
}