package org.ilgcc.app.utils;

public enum DayOfWeekOption implements InputOption {

  Monday("general.week.Monday"),
  Tuesday("general.week.Tuesday"),
  Wednesday("general.week.Wednesday"),
  Thursday("general.week.Thursday"),
  Friday("general.week.Friday"),
  Saturday("general.week.Saturday"),
  Sunday("general.week.Sunday");

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
