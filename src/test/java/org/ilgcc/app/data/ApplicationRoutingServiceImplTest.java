package org.ilgcc.app.data;

import org.ilgcc.app.submission.router.ApplicationRoutingServiceImpl;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
class ApplicationRoutingServiceImplTest {

  @Autowired
  private CCMSDataServiceImpl ccmsDataServiceImpl;

  @Autowired
  private ApplicationRoutingServiceImpl applicationRoutingServiceImpl;


  private final String FOUR_C_CASELOAD_CODE = "BB";
  private final String PROJECT_CHILD_CASELOAD_CODE = "QQ";
  private final String INACTIVE_CASELOAD_CODE = "INACTIVE";
  @Test
  void shouldReturnEmptyListWhenNoActiveCaseLoadCodes() {
    applicationRoutingServiceImpl.activeCaseLoadCodes = Collections.emptyList();

    List<County> result = applicationRoutingServiceImpl.getActiveCountiesByCaseLoadCodes();

    assertEquals(0, result.size());
  }

  @Test
  void shouldReturnEmptyListWhenNoCountiesFound() {
    applicationRoutingServiceImpl.activeCaseLoadCodes = List.of(INACTIVE_CASELOAD_CODE);

    List<County> result = applicationRoutingServiceImpl.getActiveCountiesByCaseLoadCodes();

    assertEquals(0, result.size());
  }

  @Test
  void shouldReturnUniqueAndSortedCounties() {
    applicationRoutingServiceImpl.activeCaseLoadCodes = List.of(FOUR_C_CASELOAD_CODE, PROJECT_CHILD_CASELOAD_CODE);

    List<County> mchenryCountyEntries = ccmsDataServiceImpl.getCountyByCountyName("MCHENRY");
    assertEquals(2, mchenryCountyEntries.size());

    List<County> result = applicationRoutingServiceImpl.getActiveCountiesByCaseLoadCodes();

    assertEquals(3, result.size());
    assertEquals("DEKALB", result.get(0).getCounty());
    assertEquals("FAYETTE", result.get(1).getCounty());
    assertEquals("MCHENRY", result.get(2).getCounty());

  }
}
