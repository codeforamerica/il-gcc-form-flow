package org.ilgcc.jobs;

import static java.time.Duration.ofMillis;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.awaitility.Awaitility.await;
import static org.ilgcc.jobs.HttpClient.getJson;
import static org.jobrunr.server.BackgroundJobServerConfiguration.usingStandardBackgroundJobServerConfiguration;

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
 * These tests are generally based on this article: <a href="https://www.jobrunr.io/en/blog/2020-06-01-testing-against-12-jvms/"></a>
 */
@Slf4j
public class SampleJobsTest {

  private JobProcessorInterface testService;
  private static final InMemoryStorageProvider storageProvider = new InMemoryStorageProvider();

  @BeforeEach
  public void startJobRunr() {
    testService = new SampleJobProcessor();

    JobRunr
        .configure()
        .useStorageProvider(storageProvider)
        .useJobActivator(this::jobActivator)
        .useDashboard()
        .useBackgroundJobServer(usingStandardBackgroundJobServerConfiguration().andPollInterval(ofMillis(200)))
        .initialize();
  }

  @AfterEach
  public void stopJobRunr() {
    JobRunr
        .destroy();
  }

  @Test
  void testSimpleJobUsingInstance() {
    BackgroundJob.enqueue(() -> testService.doSimpleJob(UUID.randomUUID().toString()));

    await()
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () -> assertThatJson(getSucceededJobs()).inPath("$.items[0].jobHistory[2].state").asString().contains("SUCCEEDED"));
  }

  @Test
  void testSimpleJobWithoutInstance() {
    BackgroundJob.<SampleJobProcessor>enqueue(x -> x.doSimpleJob(UUID.randomUUID().toString()));

    await()
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () -> assertThatJson(getSucceededJobs()).inPath("$.items[0].jobHistory[2].state").asString().contains("SUCCEEDED"));
  }

  @Test
//  void testLongRunningJobUsingInstance() {
//    BackgroundJob.enqueue(() -> testService.doLongRunningJob(UUID.randomUUID().toString()));
//
//    await()
//        .atMost(15, TimeUnit.SECONDS)
//        .untilAsserted(
//            () -> assertThatJson(getSucceededJobs()).inPath("$.items[0].jobHistory[2].state").asString().contains("SUCCEEDED"));
//  }

  private String getSucceededJobs() {
//    log.info("\n\n*********\n\n" + getJson("http://localhost:8000/api/jobs?state=SUCCEEDED") + "\n\n**********\n\n");
    return getJson("http://localhost:8000/api/jobs?state=SUCCEEDED");
  }

  @SuppressWarnings("unchecked")
  private <T> T jobActivator(Class<T> clazz) {
    return (T) testService;
  }
}
