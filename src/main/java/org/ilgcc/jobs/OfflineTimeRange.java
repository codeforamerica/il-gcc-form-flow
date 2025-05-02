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

    public boolean isTimeWithinRange(ZonedDateTime time) {
        LocalTime localTime = time.toLocalTime();
        if (start.equals(end)) {
            // 24 hour offline
            return true;
        } else if (start.isBefore(end)) {
            return !localTime.isBefore(start) && !localTime.isAfter(end);
        } else {
            // Overnight offline
            return !localTime.isBefore(start) || !localTime.isAfter(end);
        }
    }

    public Long secondsUntilEnd(ZonedDateTime time) {
        ZonedDateTime endDateTime = time.with(end);

        if (start.isAfter(end)) {
            // Overnight range: end is on the next day
            if (time.toLocalTime().isAfter(end)) {
                endDateTime = endDateTime.plusDays(1);
            }
        }

        if (isTimeWithinRange(time)) {
            return Duration.between(time, endDateTime).getSeconds();
        }

        return null;
    }
}
