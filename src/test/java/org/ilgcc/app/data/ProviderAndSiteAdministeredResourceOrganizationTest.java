package org.ilgcc.app.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.Optional;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.submission.router.ApplicationRouterService;
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

    @Autowired
    ApplicationRouterService applicationRouterService;

    @Test
    public void testProviderWithoutSiteAdminResourceOrgLookup() {
        BigInteger providerId = new BigInteger("12345678901");

        assertThat(providerRepository.existsById(providerId)).isTrue();

        Optional<Provider> providerOptional = providerRepository.findById(providerId);
        assertThat(providerOptional.isPresent()).isTrue();

        Provider provider = providerOptional.get();
        assertThat(provider.getResourceOrganization()).isNull();

        Optional<BigInteger> siteAdminOrgIdOptional = applicationRouterService.getSiteAdministeredOrganizationIdByProviderId(providerId);
        assertThat(siteAdminOrgIdOptional.isPresent()).isFalse();
    }

    @Test
    public void testProviderWithSiteAdminResourceOrgLookup() {
        BigInteger providerId = new BigInteger("12345678909");
        BigInteger resourceOrgId = new BigInteger("10101");

        assertThat(providerRepository.existsById(providerId)).isTrue();

        Optional<Provider> providerOptional = providerRepository.findById(providerId);
        assertThat(providerOptional.isPresent()).isTrue();

        Provider provider = providerOptional.get();
        assertThat(provider.getResourceOrganization()).isNotNull();
        assertThat(provider.getResourceOrganization().getResourceOrgId().equals(resourceOrgId)).isTrue();
        assertThat(provider.getResourceOrganization().getName().equals("Sample Site Admin Resource Organization")).isTrue();
        assertThat(provider.getResourceOrganization().getCity().equals("Chicago")).isTrue();

        Optional<BigInteger> siteAdminOrgIdOptional = applicationRouterService.getSiteAdministeredOrganizationIdByProviderId(providerId);
        assertThat(siteAdminOrgIdOptional.isPresent()).isTrue();

        BigInteger siteAdminOrgId = siteAdminOrgIdOptional.get();
        assertThat(siteAdminOrgId.equals(resourceOrgId)).isTrue();
    }
}