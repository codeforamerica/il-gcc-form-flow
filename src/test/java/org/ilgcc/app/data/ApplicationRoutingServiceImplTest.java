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
    class getActiveResourceOrganizations {

        @Test
        void shouldReturnUniqueAndSortedCountyNames() {
            CCMSDataServiceImpl ccmsDataServiceImpl = new CCMSDataServiceImpl(providerRepository, countyRepository,
                    resourceOrganizationRepository);
            ApplicationRoutingServiceImpl applicationRoutingServiceImpl = new ApplicationRoutingServiceImpl(ccmsDataServiceImpl);

            List<String> result = applicationRoutingServiceImpl.getUniqueCountiesNames();

            assertEquals(4, result.size());
            assertEquals("DEKALB", result.get(0));
            assertEquals("FAYETTE", result.get(1));
            assertEquals("HENDERSON", result.get(2));
            assertEquals("MCHENRY", result.get(3));

        }
    }

}
