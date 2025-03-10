package org.ilgcc.app.config;

import java.util.List;
import org.jobrunr.jobs.filters.JobFilter;
import org.jobrunr.server.BackgroundJobServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
@ConditionalOnProperty(
        name = {"il-gcc.ccms-integration-enabled", "il-gcc.jobrunr.background-job-server.enabled"},
        havingValue = "true",
        matchIfMissing = false
)
public class JobrunrConfig {
    // This is how you register a JobFilter bean with the application context without JobRunr PRO
    JobrunrConfig(BackgroundJobServer backgroundJobServer, List<? extends JobFilter> jobFilters) {
        backgroundJobServer.getJobFilters().addAll(jobFilters);
    }
}
