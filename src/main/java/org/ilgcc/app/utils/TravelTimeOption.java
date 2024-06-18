package org.ilgcc.app.utils;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum TravelTimeOption {

    NO_MINUTES("general.hours.0.minutes", new TimeValue("0", "0")),
    THIRTY_MINUTES("general.hours.30.minutes", new TimeValue("0", "30")),
    ONE_HOUR("general.hours.1.hour", new TimeValue("1", "0")),
    HOUR_THIRTY("general.hours.1.5.hours", new TimeValue("1", "30")),
    TWO_HOURS("general.hours.2.hours", new TimeValue("2", "0")),
    TWO_HOURS_THIRTY("general.hours.2.hours", new TimeValue("2", "30")),
    THREE_HOURS("general.hours.3.hours", new TimeValue("3", "0"));

    private final String label;
    private final TimeValue value;
    private static final Map<String, TimeValue> ENUM_BY_NAME = new HashMap<>();


    TravelTimeOption(String label, TimeValue value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    static {
        for (TravelTimeOption option : TravelTimeOption.values()) {
            ENUM_BY_NAME.put(option.name(), option.getValue());
        }
    }

    public static TimeValue getTimeValueByName(String name) {
        return ENUM_BY_NAME.get(name);
    }

    @Getter
    public static class TimeValue {
        private final String hours;
        private final String minutes;

        TimeValue(String hours, String minutes) {
            this.hours = hours;
            this.minutes = minutes;
        }
    }
}


