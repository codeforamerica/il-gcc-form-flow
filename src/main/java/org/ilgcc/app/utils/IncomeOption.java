package org.ilgcc.app.utils;

public enum IncomeOption implements InputOption {

  DIVIDENDS("unearned-income-source.dividends"),
  RENTAL("unearned-income-source.rental"),
  ROYALTIES("unearned-income-source.royalties"),
  PENSION("unearned-income-source.pension"),
  UNEMPLOYMENT("unearned-income-source.unemployment"),
  WORKERS("unearned-income-source.workers");

  private final String label;

  IncomeOption(String label) {
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
