package org.ilgcc.app.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.Optional;
import org.ilgcc.app.IlGCCApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * This test class uses providers created in the Database by FakeProviderDataImporter to confirm that the relationship between
 * Providers and Resource Organizations are linked by the site_provider_org_id column, not the resource_org_id column, and that
 * loading a Provider properly gets the ResourceOrganization when appropriate.
 * <p>
 * The main benefit of using this test is that, because the data is created and persisted by FakeProviderDataImporter and is not
 * using an Embedded Database like ProviderAndResourceOrganizationIntegrationTest, we can look at the database tables and see that
 * the columns are set correctly via manual inspection as well as the programmatic tests.
 */
@SpringBootTest(classes = IlGCCApplication.class)
@ActiveProfiles("test")
class ProviderAndSiteAdministeredResourceOrganizationTest {

    @Autowired
    ProviderRepository providerRepository;

    @Test
    public void testProviderWithoutSiteAdminResourceOrgLookup() {
        assertThat(providerRepository.existsById(new BigInteger("12345678901"))).isTrue();

        Optional<Provider> providerOptional = providerRepository.findById(new BigInteger("12345678901"));
        assertThat(providerOptional.isPresent()).isTrue();

        Provider provider = providerOptional.get();
        assertThat(provider.getResourceOrganization()).isNull();

        assertThat(providerRepository.existsById(new BigInteger("12345678909"))).isTrue();

        providerOptional = providerRepository.findById(new BigInteger("12345678909"));
        assertThat(providerOptional.isPresent()).isTrue();

        provider = providerOptional.get();
        assertThat(provider.getResourceOrganization()).isNotNull();
    }

    @Test
    public void testProviderWithSiteAdminResourceOrgLookup() {
        assertThat(providerRepository.existsById(new BigInteger("12345678909"))).isTrue();

        Optional<Provider> providerOptional = providerRepository.findById(new BigInteger("12345678909"));
        assertThat(providerOptional.isPresent()).isTrue();

        Provider provider = providerOptional.get();
        assertThat(provider.getResourceOrganization()).isNotNull();
        assertThat(provider.getResourceOrganization().getResourceOrgId().equals(new BigInteger("10101"))).isTrue();
        assertThat(provider.getResourceOrganization().getName().equals("Sample Site Admin Resource Organization")).isTrue();
        assertThat(provider.getResourceOrganization().getCity().equals("Chicago")).isTrue();
    }
}