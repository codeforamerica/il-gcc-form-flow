package org.ilgcc.app.utils;

import lombok.Getter;

@Getter
public enum RaceEthnicityOption implements InputOption {

  ASIAN("general.inputs.race-ethnicity.asian"),
  BLACK("general.inputs.race-ethnicity.black-or-african-american"),
  HISPANIC("general.inputs.race-ethnicity.hispanic-latino-or-spanish"),
  MIDDLE_EASTERN("general.inputs.race-ethnicity.middle-eastern-or-north-african"),
  NATIVE_AMERICAN("general.inputs.race-ethnicity.native-american-or-alaska-native"),
  NATIVE_HAWAIIAN("general.inputs.race-ethnicity.native-hawaiian-or-pacific-islander"),
  WHITE("general.inputs.race-ethnicity.white"),
  OTHER("general.inputs.race-ethnicity.other");

  private final String displayName;

  RaceEthnicityOption(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getValue() {
    return this.name();
  }
}
