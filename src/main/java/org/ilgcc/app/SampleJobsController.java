package org.ilgcc.app;

import static java.time.Instant.now;

import java.time.Duration;
import java.util.UUID;
import org.ilgcc.jobs.JobProcessorInterface;
import org.ilgcc.jobs.SampleJobProcessor;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class SampleJobsController {

  private final JobScheduler jobScheduler;
  private final JobProcessorInterface jobProcessor;

  public SampleJobsController(JobScheduler jobScheduler, SampleJobProcessor jobProcessor) {
    this.jobScheduler = jobScheduler;
    this.jobProcessor = jobProcessor;
  }

  @GetMapping(path = "/sample-jobs", produces = {MediaType.TEXT_HTML_VALUE})
  public String index() {
    return """
                Hello World from SampleJobsController!<br /><br />\
                You can:<br />\
                - <a href="/jobs/simple-job">Enqueue a simple job</a><br />\
                - <a href="/jobs/simple-job-instance">Enqueue a simple job using a service instance</a><br />\
                - <a href="/jobs/schedule-simple-job">Schedule a simple job 3 hours from now using a service instance</a><br />\
                - <a href="/jobs/long-running-job">Enqueue a long-running job</a><br />\
                - <a href="/jobs/long-running-job-with-job-context">Enqueue a long-running job using a JobContext to log progress</a><br />\
                - Learn more on <a href="https://www.jobrunr.io/">www.jobrunr.io</a><br />\
                """
        ;
  }

  @GetMapping(value = "/simple-job", produces = {MediaType.TEXT_PLAIN_VALUE})
  public String simpleJob(@RequestParam(defaultValue = "World") String name) {
    final JobId enqueuedJobId = jobScheduler.<JobProcessorInterface>enqueue(jobProcessor -> jobProcessor.doSimpleJob("Hello " + name));
    return "Job Enqueued: " + enqueuedJobId;
  }

  @GetMapping(value = "/simple-job-instance", produces = {MediaType.TEXT_PLAIN_VALUE})
  public String simpleJobUsingInstance(@RequestParam(defaultValue = "World") String name) {
    final JobId enqueuedJobId = jobScheduler.enqueue(() -> jobProcessor.doSimpleJob("Hello " + name));
    return "Job Enqueued: " + enqueuedJobId;
  }

  @GetMapping(value = "/schedule-simple-job", produces = {MediaType.TEXT_PLAIN_VALUE})
  public String scheduleSimpleJob(
      @RequestParam(defaultValue = "Hello world") String value,
      @RequestParam(defaultValue = "PT3H") String when) {
    final JobId scheduledJobId = jobScheduler.schedule(now().plus(Duration.parse(when)), () -> jobProcessor.doSimpleJob(value));
    return "Job Scheduled: " + scheduledJobId;
  }

  @GetMapping(value = "/long-running-job", produces = {MediaType.TEXT_PLAIN_VALUE})
  public String longRunningJob(@RequestParam(defaultValue = "World") String name) {
    final JobId enqueuedJobId = jobScheduler.<JobProcessorInterface>enqueue(jobProcessor -> jobProcessor.doLongRunningJob("Hello " + name));
    return "Job Enqueued: " + enqueuedJobId;
  }

  @GetMapping(value = "/long-running-job-with-job-context", produces = {MediaType.TEXT_PLAIN_VALUE})
  public String longRunningJobWithJobContext(@RequestParam(defaultValue = "World") String name) {
    final JobId enqueuedJobId = jobScheduler.<JobProcessorInterface>enqueue(jobProcessor -> jobProcessor.doLongRunningJobWithJobContext("Hello " + name, JobContext.Null));
    return "Job Enqueued: " + enqueuedJobId;
  }

  @GetMapping(value = "/delete-job", produces = {MediaType.TEXT_PLAIN_VALUE})
  public String deleteJob(@RequestParam UUID jobId) {
    jobScheduler.delete(jobId);
    return "Job Deleted: " + jobId;
  }
}

