package org.ilgcc.app.utils;

public enum YesNoAnswer {
  YES("Yes"),
  NO("No");

  private final String displayValue;

  YesNoAnswer(String displayValue) {
    this.displayValue = displayValue;
  }

  public String getDisplayValue() {
    return displayValue;
  }
}
