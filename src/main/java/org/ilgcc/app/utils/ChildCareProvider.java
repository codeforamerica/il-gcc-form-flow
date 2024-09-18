package org.ilgcc.app.utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum ChildCareProvider {

  BROWN_BEAR_DAYCARE("Brown Bear Daycare", "747750839233009", "21007 McGuire Rd", null, "Harvard", "IL", "60033", null),
  CHILDRENS_LEARNING_CENTER("Children's Learning Center", "895593365433000", "905 S 4th St", "PO Box 531", "DeKalb", "IL", "60115", "815-756-3506"),
  THE_GROWING_PLACE("The Growing Place", "155014365433009", "909 S 4th St", null, "DeKalb", "IL", "60115", null),
  KISHWAUKEE_COLLEGE_EARLY_LEARNING_CENTER("Kishwaukee College Early Learning Center", "226234365433006", "21193 Malta Rd", null, "Malta", "IL", "60150", null),
  NIU_CHILD_DEVELOPMENT_FAMILY_CENTER("NIU Child Development & Family Center", "786045365433005", "NIU Campus Child Care Bldg", null, "DeKalb", "IL", "60115", null),
  ONCE_UPON_A_TIME_CHILD_CARE("Once Upon A Time Child Care", "295762914133052", "1778 W State St", "Ste C", "Sycamore", "IL", "60178", null),
  OPEN_SESAME("Open Sesame", "239246768533006", "1101 Middle Rd", null, "Dixon", "IL", "61021", null);


  private final String displayName;
  private final String idNumber;
  private final String street;
  private final String apt;
  private final String city;
  private final String state;
  private final String zipcode;
  private final String phone;

  ChildCareProvider(String displayName, String idNumber, String street, String apt, String city, String state, String zipcode, String phone) {
    this.displayName = displayName;
    this.idNumber = idNumber;
    this.street = street;
    this.apt = apt;
    this.city = city;
    this.state = state;
    this.zipcode = zipcode;
    this.phone = phone;
  }

  public List<String> getAddressComponents() {
    var addressComponents = new ArrayList<>(List.of(street));
    if (apt != null) {
      addressComponents.add(apt);
    }
    addressComponents.add("%s, %s %s".formatted(city, state, zipcode));
    if (phone != null) {
      addressComponents.add(phone);
    }
    return addressComponents;
  }
}
