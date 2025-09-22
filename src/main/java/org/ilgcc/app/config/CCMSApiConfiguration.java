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

    private String apiSubscriptionKey;
    private String baseUrl;

    private int transactionDelayMinutes;
    private String offlineTimeRanges;
    private int offlineTransactionDelayOffset;
    private boolean enableV2Api;

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

    public boolean isOnlineAt(ZonedDateTime dateTime) {
        if (!ccmsOfflineTimeRanges.isEmpty()) {
            return !ccmsOfflineTimeRanges.stream().anyMatch(range -> range.isTimeWithinRange(dateTime));
        } else {
            return true;
        }
    }

    public long getSecondsUntilEndOfOfflineRangeStartingAt(ZonedDateTime startTime) {
        Optional<Long> seconds = ccmsOfflineTimeRanges.stream().filter(range -> range.isTimeWithinRange(startTime))
                .map(range -> range.secondsUntilEnd(startTime)).filter(Objects::nonNull).findFirst();
        return seconds.isPresent() ? seconds.get() : 0;
    }
}
