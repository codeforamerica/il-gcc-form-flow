package org.ilgcc.jobs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;

public class OfflineTimeRange {

    private final LocalTime start;
    private final LocalTime end;

    @JsonCreator
    public OfflineTimeRange(@JsonProperty("start") String start, @JsonProperty("end") String end) {
        this.start = LocalTime.parse(start);
        this.end = LocalTime.parse(end);
    }

    public boolean isNowWithinRange(ZonedDateTime now) {
        LocalTime currentTime = now.toLocalTime();
        if (start.equals(end)) {
            // 24 hour offline
            return true;
        } else if (start.isBefore(end)) {
            return !currentTime.isBefore(start) && !currentTime.isAfter(end);
        } else {
            // Overnight offline
            return !currentTime.isBefore(start) || !currentTime.isAfter(end);
        }
    }

    public Long secondsUntilEnd(ZonedDateTime now) {
        ZonedDateTime endDateTime = now.with(end);

        if (start.isAfter(end)) {
            // Overnight range: end is on the next day
            if (now.toLocalTime().isBefore(end)) {
                endDateTime = endDateTime.plusDays(1);
            }
        }

        if (isNowWithinRange(now)) {
            return Duration.between(now, endDateTime).getSeconds();
        }

        return null;
    }
}
