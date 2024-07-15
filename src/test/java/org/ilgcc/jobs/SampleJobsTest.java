package org.ilgcc.jobs;

import static java.time.Duration.ofMillis;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.awaitility.Awaitility.await;
import static org.ilgcc.jobs.HttpClient.getJson;
import static org.jobrunr.server.BackgroundJobServerConfiguration.usingStandardBackgroundJobServerConfiguration;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.configuration.JobRunr;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * These tests are generally based on this article: <a
 * href="https://www.jobrunr.io/en/blog/2020-06-01-testing-against-12-jvms/"></a>
 */
@Slf4j
public class SampleJobsTest {

  private JobProcessorInterface testService;
  private static final InMemoryStorageProvider storageProvider = new InMemoryStorageProvider();

  @BeforeEach
  public void startJobRunr() {
    testService = new SampleJobProcessor();

    JobRunr.configure().useStorageProvider(storageProvider).useJobActivator(this::jobActivator).useDashboard()
        .useBackgroundJobServer(usingStandardBackgroundJobServerConfiguration().andPollInterval(ofMillis(200))).initialize();
  }

  @AfterEach
  public void stopJobRunr() {
    JobRunr.destroy();
  }

  /**
   * Tests that a simple job moves through the 3 job lifecycle states in order correctly, called via an instance
   */
  @Test
  void testSimpleJobUsingInstance() {
    BackgroundJob.enqueue(() -> testService.doSimpleJob(UUID.randomUUID().toString()));

    await().atMost(15, TimeUnit.SECONDS).untilAsserted(
        () -> assertThatJson(getSucceededJobs()).inPath("$.items[0].jobHistory[*].state")
            .isEqualTo("['ENQUEUED', 'PROCESSING', 'SUCCEEDED']"));

  }

  /**
   * Tests that a simple job moves through the 3 job lifecycle states in order correctly, called without an instance
   */
  @Test
  void testSimpleJobWithoutInstance() {
    BackgroundJob.<SampleJobProcessor>enqueue(x -> x.doSimpleJob(UUID.randomUUID().toString()));

    await().atMost(15, TimeUnit.SECONDS).untilAsserted(
        () -> assertThatJson(getSucceededJobs()).inPath("$.items[0].jobHistory[*].state")
            .isEqualTo("['ENQUEUED', 'PROCESSING', 'SUCCEEDED']"));
  }

  @Test
  void testLongRunningJobUsingInstance() {
    BackgroundJob.enqueue(() -> testService.doLongRunningJob(UUID.randomUUID().toString()));

    // Get the enqueued jobs list immediately and check that there's an enqueued job
    String enqueuedJobs = getEnqueuedJobs();
    assertThatJson(enqueuedJobs).inPath("$.items[0].jobHistory[0].state").asString().contains("ENQUEUED");

    // Now that job should be processing, so check that the enqueued jobs list eventually goes to zero
    await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> assertThatJson(getEnqueuedJobs()).inPath("$.total").isEqualTo("0"));

    // Now that the job is processing, check that list
    await().atMost(5, TimeUnit.SECONDS).untilAsserted(
        () -> assertThatJson(getProcessingJobs()).inPath("$.items[0].jobHistory[1].state").asString().contains("PROCESSING"));

    // Finally check that the entire long running job passed
    await().atMost(15, TimeUnit.SECONDS).untilAsserted(
        () -> assertThatJson(getSucceededJobs()).inPath("$.items[0].jobHistory[2].state").asString().contains("SUCCEEDED"));
  }

  private String getSucceededJobs() {
    return getJson("http://localhost:8000/api/jobs?state=SUCCEEDED");
  }

  private String getEnqueuedJobs() {
    return getJson("http://localhost:8000/api/jobs?state=ENQUEUED");
  }

  private String getProcessingJobs() {
    return getJson("http://localhost:8000/api/jobs?state=PROCESSING");
  }

  @SuppressWarnings("unchecked")
  private <T> T jobActivator(Class<T> clazz) {
    return (T) testService;
  }
}
