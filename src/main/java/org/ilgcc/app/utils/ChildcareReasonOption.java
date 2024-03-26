package org.ilgcc.app.utils;

public enum ChildcareReasonOption implements InputOption {

  WORKING("activities-parent-type.working"),
  LOOKING_FOR_WORK("activities-parent-type.looking-for-work"),
  SCHOOL("activities-parent-type.going-to-school"),
  TANF_TRAINING("activities-parent-type.tanf-training");

  private final String label;

  ChildcareReasonOption(String label) {
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
