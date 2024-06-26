package org.ilgcc.app.utils;

public enum IncomeOption implements InputOption {
  DIVIDENDS("unearned-income-source.dividends", "unearnedIncomeDividends"),
  RENTAL("unearned-income-source.rental", "unearnedIncomeRental"),
  ROYALTIES("unearned-income-source.royalties", "unearnedIncomeRoyalties"),
  PENSION("unearned-income-source.pension", "unearnedIncomePension"),
  UNEMPLOYMENT("unearned-income-source.unemployment", "unearnedIncomeUnemployment"),
  WORKERS("unearned-income-source.workers", "unearnedIncomeWorkers");

  private final String label;
  private final String inputFieldName;

  IncomeOption(String label, String inputFieldName) {
    this.label = label;
    this.inputFieldName = inputFieldName;
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

  public String getInputFieldName(){
    return this.inputFieldName;
  }

}
