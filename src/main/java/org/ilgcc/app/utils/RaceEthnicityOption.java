package org.ilgcc.app.utils;

public enum RaceEthnicityOption implements InputOption {

  ASIAN("general.inputs.race-ethnicity.asian", "4"),
  BLACK("general.inputs.race-ethnicity.black-or-african-american", "2"),
  HISPANIC("general.inputs.race-ethnicity.hispanic-latino-or-spanish", "3"),
  MIDDLE_EASTERN("general.inputs.race-ethnicity.middle-eastern-or-north-african", "O"),
  NATIVE_AMERICAN("general.inputs.race-ethnicity.native-american-or-alaska-native", "5"),
  WHITE("general.inputs.race-ethnicity.white", "1"),
  OTHER("general.inputs.race-ethnicity.other", "O");

  private final String label;
  private final String value;

  RaceEthnicityOption(String label, String value) {
    this.label = label; this.value = value;
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

  public static String getPdfValueByName(String name) {
    return  RaceEthnicityOption.valueOf(name).value;
  }
}
