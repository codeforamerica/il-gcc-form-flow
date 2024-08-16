package org.ilgcc.app.utils;

public enum HourSelectOption implements InputOption {
  HOUR_1("1"),
  HOUR_2("2"),
  HOUR_3("3"),
  HOUR_4("4"),
  HOUR_5("5"),
  HOUR_6("6"),
  HOUR_7("7"),
  HOUR_8("8"),
  HOUR_9("9"),
  HOUR_10("10"),
  HOUR_11("11"),
  HOUR_12("12");

  private final String label;

  HourSelectOption(String label) {
    this.label = label;
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public String getValue() {
    return null;
  }

  @Override
  public String getHelpText() {
    return null;
  }
}
