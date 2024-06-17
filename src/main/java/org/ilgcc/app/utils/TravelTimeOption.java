package org.ilgcc.app.utils;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum TravelTimeOption {

    NO_MINUTES("general.hours.0.minutes", Map.of("hours", "0", "minutes", "0")),
    THIRTY_MINUTES("general.hours.30.minutes", Map.of("hours", "0", "minutes", "30")),
    ONE_HOUR("general.hours.1.hour", Map.of("hours", "1", "minutes", "0")),
    HOUR_THIRTY("general.hours.1.5.hours", Map.of("hours", "1", "minutes", "30")),
    TWO_HOURS("general.hours.2.hours", Map.of("hours", "2", "minutes", "0")),
    TWO_HOURS_THIRTY("general.hours.2.hours", Map.of("hours", "2", "minutes", "30")),
    THREE_HOURS("general.hours.3.hours", Map.of("hours", "3", "minutes", "0"));
    private final String label;
    private final Map<String, String> value;
    private static final Map<String, TravelTimeOption> ENUM_BY_NAME = new HashMap<>();

    TravelTimeOption(String label, Map<String, String> value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    static {
        for (TravelTimeOption option : TravelTimeOption.values()) {
            ENUM_BY_NAME.put(option.name(), option);
        }
    }

    public Map<String, String> getValueByName(String name) {
        return ENUM_BY_NAME.get(name).getValue();
    }
}


