package org.ilgcc.app.utils;

public enum ChildcareReasonOption implements InputOption {

  WORKING("working"),
  SCHOOL("going-to-school"),
  TANF_TRAINING("tanf-training");

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
