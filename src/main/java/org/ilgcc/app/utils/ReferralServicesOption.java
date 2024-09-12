package org.ilgcc.app.utils;

public enum ReferralServicesOption implements InputOption {

  SAFE_SUPPORT ("unearned-income-referral-services.safe-support"),
  HOUSING_SUPPORT ("unearned-income-referral-services.housing-support"),
  DISABILITY_SUPPORT ("unearned-income-referral-services.disability-support");

  private final String label;

  ReferralServicesOption(String label) {
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
