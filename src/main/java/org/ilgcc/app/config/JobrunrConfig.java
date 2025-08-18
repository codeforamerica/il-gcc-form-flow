package org.ilgcc.app.config;

import java.util.List;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.jobs.filters.JobFilter;
import org.jobrunr.server.BackgroundJobServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
@Slf4j
public class JobrunrConfig {
    // This is how you register a JobFilter bean with the application context without JobRunr PRO
    JobrunrConfig(@Nullable BackgroundJobServer backgroundJobServer, List<? extends JobFilter> jobFilters) {
        if (backgroundJobServer != null) {
            log.info("Registering {} JobRunr filters: {}", jobFilters.size(), jobFilters);;
            backgroundJobServer.getJobFilters().addAll(jobFilters);
        } else {
            log.info("Not registering JobrunrConfig because backgroundJobServer is null");
        }
    }
}
