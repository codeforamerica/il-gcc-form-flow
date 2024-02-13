package org.ilgcc.app.utils;

import lombok.Getter;

import java.util.List;

@Getter
public enum ChildCareProvider {

  CHILDRENS_LEARNING_CENTER("Children's Learning Center", List.of("905 S 4th St", "PO Box 531", "DeKalb IL 60115", "815-756-3506")),
  THE_GROWING_PLACE("The Growing Place", List.of("909 S 4th St", "DeKalb IL 60115")),
  OPEN_SESAME("Open Sesame", List.of("1101 Middle Rd", "Dixon IL 61021"));

  private final String displayName;
  private final List<String> addressComponents;

  ChildCareProvider(String displayName, List<String> addressComponents) {
    this.displayName = displayName;
    this.addressComponents = addressComponents;
  }
}
