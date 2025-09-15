package org.ilgcc.app.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.jobs.OfflineTimeRange;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "il-gcc.ccms-api")
@Getter
@Setter
@Slf4j
public class CCMSApiConfiguration {

    private boolean integrationEnabled;
    private String apiSubscriptionKey;
    private String baseUrl;

    private int transactionDelayMinutes;
    private String offlineTimeRanges;
    private int offlineTransactionDelayOffset;

    private List<OfflineTimeRange> ccmsOfflineTimeRanges;

    @PostConstruct
    void init() {
        // On startup, if we have offline time ranges, we need to load them from the json
        if (offlineTimeRanges != null) {
            try {
                log.info("Parsing CCMS offline times: " + offlineTimeRanges);
                ObjectMapper objectMapper = new ObjectMapper();
                this.ccmsOfflineTimeRanges = objectMapper.readValue(offlineTimeRanges, new TypeReference<>() {
                });
                log.info("Parsed CCMS offline times. Number of ranges: {}", ccmsOfflineTimeRanges.size());
            } catch (JsonProcessingException e) {
                log.error(
                        "CCMSApiConfiguration: Could not parse CCMS offline times. Make sure you set the CCMS_OFFLINE_TIME_RANGES environment variable properly.",
                        e);
                ccmsOfflineTimeRanges = new ArrayList<>();
            }
        } else {
            ccmsOfflineTimeRanges = new ArrayList<>();
        }
    }

    /**
     * Checks if CCMS is online for a given dateTime. If CCMS integration is completely disabled via the boolean, then return
     * false always. If CCMS integration is enabled, there's a timerange set, and the dateTime is not within this time range,
     * return true
     */
    public boolean isOnlineAt(ZonedDateTime dateTime) {
        if (!isCCMSIntegrationEnabled()) {
            return false;
        }

        if (!ccmsOfflineTimeRanges.isEmpty()) {
            return !ccmsOfflineTimeRanges.stream().anyMatch(range -> range.isTimeWithinRange(dateTime));
        } else {
            return true;
        }
    }

    public boolean isCCMSIntegrationEnabled() {
        return integrationEnabled;
    }

    /**
     * Returns the number of seconds until CCMS integration is re-enabled. If CCMS integration is completely disabled via the
     * boolean, check if the dateTime is within an offline range. If it is, return the number of seconds until CCMS should be back
     * online. Otherwise, return 1 hour in seconds.
     */
    public long getSecondsUntilEndOfOfflineRangeStartingAt(ZonedDateTime startTime) {
        Optional<Long> seconds = ccmsOfflineTimeRanges.stream().filter(range -> range.isTimeWithinRange(startTime))
                .map(range -> range.secondsUntilEnd(startTime)).filter(Objects::nonNull).findFirst();

        if (seconds.isPresent()) {
            // If we're in an offline timerange, regardless if CCMS is also turned off, return the end of the offline
            // range. This way, if CCMS is re-enabled during the offline range, the API call is still made when CCMS
            // comes back online as scheduled
            return seconds.get();
        } else {
            // If we're not in an offline timerange then...
            if (!isCCMSIntegrationEnabled()) {
                // If CCMS is turned off, return 1 hour
                return 3600;
            } else {
                // If CCMS is turned on, return 0
                return 0;
            }
        }
    }
}
