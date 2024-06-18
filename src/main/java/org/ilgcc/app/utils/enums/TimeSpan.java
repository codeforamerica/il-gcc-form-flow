package org.ilgcc.app.utils.enums;
import lombok.Getter;

@Getter
public class TimeSpan {

    private final String hours;
    private final String minutes;

    TimeSpan(String hours, String minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }
}

