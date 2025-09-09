package org.ilgcc.app.data;

import org.ilgcc.app.submission.router.ApplicationRoutingServiceImpl;
import org.junit.jupiter.api.Nested;
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

  @Nested
  class GetActiveResourceOrganizations{
    @Test
    void shouldReturnUniqueAndSortedCounties() {
      CCMSDataServiceImpl ccmsDataServiceImpl = new CCMSDataServiceImpl(providerRepository, countyRepository, resourceOrganizationRepository);
      ApplicationRoutingServiceImpl applicationRoutingServiceImpl = new ApplicationRoutingServiceImpl(ccmsDataServiceImpl);

      List<County> mchenryCountyEntries = ccmsDataServiceImpl.getCountyByCountyName("MCHENRY");
      assertEquals(2, mchenryCountyEntries.size());

      List<County> result = applicationRoutingServiceImpl.getActiveCountiesByCaseLoadCodes();

      assertEquals(3, result.size());
      assertEquals("DEKALB", result.get(0).getCounty());
      assertEquals("FAYETTE", result.get(1).getCounty());
      assertEquals("MCHENRY", result.get(2).getCounty());
    }
  }

}
