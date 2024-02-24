package org.ilgcc.app.utils;

public enum RaceEthnicityOption implements InputOption {

  ASIAN("general.inputs.race-ethnicity.asian"),
  BLACK("general.inputs.race-ethnicity.black-or-african-american"),
  HISPANIC("general.inputs.race-ethnicity.hispanic-latino-or-spanish"),
  MIDDLE_EASTERN("general.inputs.race-ethnicity.middle-eastern-or-north-african"),
  NATIVE_AMERICAN("general.inputs.race-ethnicity.native-american-or-alaska-native"),
  NATIVE_HAWAIIAN("general.inputs.race-ethnicity.native-hawaiian-or-pacific-islander"),
  WHITE("general.inputs.race-ethnicity.white"),
  OTHER("general.inputs.race-ethnicity.other");

  private final String label;

  RaceEthnicityOption(String label) {
    this.label = label;
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public String getValue() {
    return this.name();
  }

  @Override
  public String getHelpText() {
    return null;
  }

}
