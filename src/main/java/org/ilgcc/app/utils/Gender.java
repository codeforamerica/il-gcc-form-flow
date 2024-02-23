package org.ilgcc.app.utils;

import lombok.Getter;

@Getter
public enum Gender implements InputOption {

  MALE("general.inputs.male"),
  FEMALE("general.inputs.female"),
  NONBINARY("general.inputs.non-binary"),
  TRANSGENDER("general.inputs.transgender"),
  NO_ANSWER("general.inputs.prefer-not-to-answer");

  private final String displayName;

  Gender(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getValue() {
    return this.name();
  }
}
