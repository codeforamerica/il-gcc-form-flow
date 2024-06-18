package org.ilgcc.app.utils.enums;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum CommuteTimeType {

    NO_MINUTES("general.hours.0.minutes", new TimeSpan("0", "0")),
    THIRTY_MINUTES("general.hours.30.minutes", new TimeSpan("0", "30")),
    ONE_HOUR("general.hours.1.hour", new TimeSpan("1", "0")),
    HOUR_THIRTY("general.hours.1.5.hours", new TimeSpan("1", "30")),
    TWO_HOURS("general.hours.2.hours", new TimeSpan("2", "0")),
    TWO_HOURS_THIRTY("general.hours.2.hours", new TimeSpan("2", "30")),
    THREE_HOURS("general.hours.3.hours", new TimeSpan("3", "0"));

    private final String label;
    private final TimeSpan value;

    CommuteTimeType(String label, TimeSpan value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public static TimeSpan getTimeSpanByName(String name) {
        return  CommuteTimeType.valueOf(name).value;
    }
}


