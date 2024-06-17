package org.ilgcc.app.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TravelTimeOption implements InputOption {

  NO_MINUTES("general.hours.0.minutes", Map.of("hours", "0", "minutes",  "0")),
  THIRTY_MINUTES("general.hours.30.minutes", Map.of("hours", "0", "minutes",  "30")),
  ONE_HOUR("general.hours.1.hour", Map.of("hours", "1", "minutes",  "0")),

  HOUR_THIRTY("general.hours.1.5.hours", Map.of("hours", "1", "minutes",  "30")),

  TWO_HOURS("general.hours.2.hours", Map.of("hours", "2", "minutes",  "0")),

  TWO_HOURS_THIRTY("general.hours.2.hours", Map.of("hours", "2", "minutes",  "30")),

  THREE_HOURS("general.hours.3.hours", Map.of("hours", "3", "minutes",  "0"))
  ;
  private final String label;
  private final Map<String, String> value;

  TravelTimeOption(String label, Map<String, String> value) {
    this.label = label;
    this.value = value;
  }

  @Override
  public String getLabel() {
    return label;
  }

  public String getHourValue() {
    return this.value.get("hours");
  }

  public String getMinuteValue() {
    return this.value.get("minutes");
  }
}


