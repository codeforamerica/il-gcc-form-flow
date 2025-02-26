package org.ilgcc.app.journeys;

import org.ilgcc.app.utils.AbstractMockMvcTest;
import org.ilgcc.app.utils.FormScreen;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

public class ParentAddressTest extends AbstractMockMvcTest {

  @Test
  void testNoPermanentAddress() throws Exception {
    var screen = postToParentHomeAddressPage(true);

    assertThat(screen.getHeader()).isEqualTo("Okay, we just need a place to send you mail about your application.");
  }

  private FormScreen postToParentHomeAddressPage(String streetAddress, String city, String state, String zipcode) throws Exception {
    Map<String, List<String>> params = Map.of(
        "parentHomeStreetAddress1", List.of(streetAddress),
//        "parentHomeStreetAddress2", List.of(apt),
        "parentHomeCity", List.of(city),
        "parentHomeState", List.of(state),
        "parentHomeZipCode", List.of(zipcode)

    );
    return new FormScreen(postToUrl(formatUrl("parent-home-address"), params));
  }

  private FormScreen postToParentHomeAddressPage(boolean noPermanentAddress) throws Exception {
    String postUrl = formatUrl("parent-home-address");
    Map<String, List<String>> params = Map.of("parentHomeExperiencingHomelessness[]", noPermanentAddress ? List.of("yes") : emptyList());
    return new FormScreen(postAndGetRedirect(postUrl, params));
  }

  private String formatUrl(String pageName) {
    return "/flow/gcc/%s".formatted(pageName);
  }
}
