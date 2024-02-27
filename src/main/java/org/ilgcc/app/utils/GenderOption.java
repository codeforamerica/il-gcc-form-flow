package org.ilgcc.app.utils;

public enum GenderOption implements InputOption {

  MALE("general.inputs.male"),
  FEMALE("general.inputs.female"),
  NONBINARY("general.inputs.non-binary"),
  TRANSGENDER("general.inputs.transgender");

  private final String label;

  GenderOption(String label) {
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
