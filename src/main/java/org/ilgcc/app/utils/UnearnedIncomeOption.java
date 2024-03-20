package org.ilgcc.app.utils;

public enum UnearnedIncomeOption implements InputOption {

  SNAP ("general.inputs.assistance-programs.snap"),
  HOMELESS_SHELTER_OR_PREVENTION_PROGRAMS("general.inputs.assistance-programs.homeless-shelter-or-prevention-programs"),
  CASH_ASSISTANCE("general.inputs.assistance-programs.cash-assistance"),
  HOUSING_VOUCHERS("general.inputs.assistance-programs.housing-vouchers");

  private final String label;

  UnearnedIncomeOption(String label) {
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
