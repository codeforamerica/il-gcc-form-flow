package org.ilgcc.app.submission.conditions;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import formflow.library.data.Submission;
import org.junit.jupiter.api.Test;

class EnableProviderResponseFEINTest {
  private EnableProviderResponseFEIN enableProviderReponseFEIN;
  boolean mockEnableProviderResponseFEINFlag;
  @Test
  void shouldReturnTrueWhenEnableProviderResponseFEINFlagIsOn() {
    mockEnableProviderResponseFEINFlag = true;
    enableProviderReponseFEIN = new EnableProviderResponseFEIN(mockEnableProviderResponseFEINFlag);
    assertTrue(enableProviderReponseFEIN.run(new Submission()));
  }
  @Test
  void shouldReturnFalseWhenEnableProviderResponseFEINFlagIsOff() {
    mockEnableProviderResponseFEINFlag = false;
    enableProviderReponseFEIN = new EnableProviderResponseFEIN(mockEnableProviderResponseFEINFlag);
    assertFalse(enableProviderReponseFEIN.run(new Submission()));
  }
}