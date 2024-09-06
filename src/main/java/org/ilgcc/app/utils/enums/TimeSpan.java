package org.ilgcc.app.utils.enums;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class TimeSpan {

    private final String hours;
    private final String minutes;

    TimeSpan(String hours, String minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public String getPaddedHours() {
        try {
            return String.format("%02d", Integer.parseInt(hours));
        } catch (NumberFormatException e) {
            log.error("TimeSpan hour could not be parsed as Integer", e);
            return "";
        }
    }
}

