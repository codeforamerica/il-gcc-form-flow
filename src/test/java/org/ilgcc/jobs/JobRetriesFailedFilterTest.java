package org.ilgcc.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jobrunr.jobs.Job;
import org.jobrunr.jobs.JobDetails;
import org.jobrunr.jobs.JobParameter;
import org.jobrunr.jobs.states.FailedState;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class JobRetriesFailedFilterTest {

    private final JobRetriesFailedFilter filter = new JobRetriesFailedFilter();

    @Test
    void sendEmailJobLogsFailureWhenRetriesAreExhausted() {
        // Get the logger from the job retries failed filter class
        Logger logger = (Logger) LoggerFactory.getLogger(JobRetriesFailedFilter.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        // Create a mock jobrunr Job object that matches Send Email Request job
        Job job = mock(Job.class, RETURNS_DEEP_STUBS);
        when(job.getJobName()).thenReturn("Send Email Request");
        when(job.getId()).thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        when(job.getLastJobStateOfType(FailedState.class))
                .thenReturn(Optional.of(new FailedState("Help me I've failed and I can't get up!", new IOException("Help me I've failed and I can't get up!"))));
        when(job.getJobDetails().getJobParameters()).thenReturn(List.of());

        // Run the filter with the failed job
        filter.onFailedAfterRetries(job);
        
        // Assert the error message is as we would expect
        var lines = appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
        assertThat(lines).anySatisfy(msg -> {
            assertThat(msg).contains("Email job with ID 11111111-1111-1111-1111-111111111111");
            assertThat(msg).contains("failed after all retries exhausted");
            assertThat(msg).contains("Exception: Help me I've failed and I can't get up!");
        });

        logger.detachAppender(appender);
    }

    @Test
    void sendToCCMSJobLogsFailureWhenRetriesAreExhausted() {
        Logger logger = (Logger) LoggerFactory.getLogger(JobRetriesFailedFilter.class);
        ListAppender<ILoggingEvent> app = new ListAppender<>(); app.start(); logger.addAppender(app);

        Job job = mock(Job.class, RETURNS_DEEP_STUBS);
        when(job.getJobName()).thenReturn("Send CCMS Submission Payload");
        when(job.getId()).thenReturn(UUID.randomUUID());
        when(job.getLastJobStateOfType(FailedState.class))
                .thenReturn(Optional.of(new FailedState("oops", new RuntimeException("oops"))));

        JobDetails details = job.getJobDetails();
        JobParameter first = mock(JobParameter.class);
        when(details.getJobParameters()).thenReturn(List.of(first));
        when(first.getClassName()).thenReturn(UUID.class.getName());
        when(first.getObject()).thenReturn(UUID.fromString("33333333-3333-3333-3333-333333333333"));

        filter.onFailedAfterRetries(job);

        assertThat(app.list.stream().map(ILoggingEvent::getFormattedMessage).toList())
                .anyMatch(m -> m.contains("failed for Submission 33333333-3333-3333-3333-333333333333"));

        logger.detachAppender(app);
    }

    @Test
    void unspecifiedJobsLogFailuresWhenRetriesAreExhausted() {
        Logger logger = (Logger) LoggerFactory.getLogger(JobRetriesFailedFilter.class);
        ListAppender<ILoggingEvent> app = new ListAppender<>(); app.start(); logger.addAppender(app);

        Job job = mock(Job.class, RETURNS_DEEP_STUBS);
        when(job.getJobName()).thenReturn("Some Other Job");
        when(job.getId()).thenReturn(UUID.randomUUID());
        when(job.getLastJobStateOfType(FailedState.class)).thenReturn(Optional.empty()); // What if the failure isn't one of the named Jobs?
        when(job.getJobDetails().getJobParameters()).thenReturn(List.of());

        filter.onFailedAfterRetries(job);

        assertThat(app.list.stream().map(ILoggingEvent::getFormattedMessage).toList())
                .anyMatch(m -> m.contains("failed after all retries exhausted"));

        logger.detachAppender(app);
    }
}
