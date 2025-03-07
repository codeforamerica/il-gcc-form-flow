package org.ilgcc.app.config;

import java.util.List;
import org.jobrunr.jobs.filters.JobFilter;
import org.jobrunr.server.BackgroundJobServer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobrunrConfig {
    // This is how you register a JobFilter bean with the application context without JobRunr PRO
    JobrunrConfig(BackgroundJobServer backgroundJobServer, List<? extends JobFilter> jobFilters) {
        backgroundJobServer.getJobFilters().addAll(jobFilters);
    }
}
