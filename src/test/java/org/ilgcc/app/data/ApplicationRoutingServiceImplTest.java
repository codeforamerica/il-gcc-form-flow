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
  ProviderRepository providerRepository;

  @Autowired
  CountyRepository countyRepository;

  @Autowired
  ResourceOrganizationRepository resourceOrganizationRepository;

  private ApplicationRoutingServiceImpl applicationRoutingServiceImpl;

  private CCMSDataServiceImpl ccmsDataServiceImpl;


  private final String FOUR_C_CASELOAD_CODE = "BB";
  private final String PROJECT_CHILD_CASELOAD_CODE = "QQ";

  @Test
  void shouldReturnEmptyListWhenNoActiveCaseLoadCodes() {
    ccmsDataServiceImpl = new CCMSDataServiceImpl(providerRepository, countyRepository, resourceOrganizationRepository);
    applicationRoutingServiceImpl = new ApplicationRoutingServiceImpl(ccmsDataServiceImpl);

    assert(ccmsDataServiceImpl.getActiveCaseLoadCodes()).equals(Collections.emptyList());

    List<County> result = applicationRoutingServiceImpl.getActiveCountiesByCaseLoadCodes();

    assertEquals(0, result.size());
  }

  @Test
  void shouldReturnEmptyListWhenNoCountiesFound() {
    ccmsDataServiceImpl = new CCMSDataServiceImpl(providerRepository, countyRepository, resourceOrganizationRepository);
    applicationRoutingServiceImpl = new ApplicationRoutingServiceImpl(ccmsDataServiceImpl);

    List<County> result = applicationRoutingServiceImpl.getActiveCountiesByCaseLoadCodes();

    assertEquals(0, result.size());
  }

  @Test
  void shouldReturnUniqueAndSortedCounties() {
    ccmsDataServiceImpl = new CCMSDataServiceImpl(providerRepository, countyRepository, resourceOrganizationRepository);
    applicationRoutingServiceImpl = new ApplicationRoutingServiceImpl(ccmsDataServiceImpl);

    List<County> mchenryCountyEntries = ccmsDataServiceImpl.getCountyByCountyName("MCHENRY");
    assertEquals(2, mchenryCountyEntries.size());

    List<County> result = applicationRoutingServiceImpl.getActiveCountiesByCaseLoadCodes();

    assertEquals(3, result.size());
    assertEquals("DEKALB", result.get(0).getCounty());
    assertEquals("FAYETTE", result.get(1).getCounty());
    assertEquals("MCHENRY", result.get(2).getCounty());
  }
}
