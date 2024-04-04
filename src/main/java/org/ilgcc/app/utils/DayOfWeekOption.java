package org.ilgcc.app.utils;

public enum DayOfWeekOption implements InputOption {

  MONDAY("general.week.Monday"),
  TUESDAY("general.week.Tuesday"),
  WEDNESDAY("general.week.Wednesday"),
  THURSDAY("general.week.Thursday"),
  FRIDAY("general.week.Friday"),
  SATURDAY("general.week.Saturday"),
  SUNDAY("general.week.Sunday");

  private final String label;

  DayOfWeekOption(String label) {
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
