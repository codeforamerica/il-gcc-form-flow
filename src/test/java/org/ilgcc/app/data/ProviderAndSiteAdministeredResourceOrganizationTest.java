package org.ilgcc.app.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.ACTIVE_SITE_ADMINISTERED_PROVIDER;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.ACTIVE_SITE_ADMIN_RESOURCE_ORG;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.CURRENT_APPROVED_PROVIDER;

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
        BigInteger providerId = CURRENT_APPROVED_PROVIDER.getProviderId();

        assertThat(providerRepository.existsById(providerId)).isTrue();

        Optional<Provider> providerOptional = providerRepository.findById(providerId);
        assertThat(providerOptional.isPresent()).isTrue();

        Provider provider = providerOptional.get();
        assertThat(provider.getResourceOrganization()).isNull();

        Optional<ResourceOrganization> siteAdminOrgOptional = applicationRouterService.getSiteAdministeredOrganizationByProviderId(
                providerId);
        assertThat(siteAdminOrgOptional.isPresent()).isFalse();
    }

    @Test
    public void testProviderWithSiteAdminResourceOrgLookup() {
        BigInteger providerId = ACTIVE_SITE_ADMINISTERED_PROVIDER.getProviderId();
        BigInteger resourceOrgId = ACTIVE_SITE_ADMIN_RESOURCE_ORG.getResourceOrgId();

        assertThat(providerRepository.existsById(providerId)).isTrue();

        Optional<Provider> providerOptional = providerRepository.findById(providerId);
        assertThat(providerOptional.isPresent()).isTrue();

        Provider provider = providerOptional.get();
        assertThat(provider.getResourceOrganization()).isNotNull();
        assertThat(provider.getResourceOrganization().getResourceOrgId().equals(resourceOrgId)).isTrue();
        assertThat(provider.getResourceOrganization().getName().equals(ACTIVE_SITE_ADMIN_RESOURCE_ORG.getName())).isTrue();
        assertThat(provider.getResourceOrganization().getCity().equals(ACTIVE_SITE_ADMIN_RESOURCE_ORG.getCity())).isTrue();

        Optional<ResourceOrganization> siteAdminOrgOptional = applicationRouterService.getSiteAdministeredOrganizationByProviderId(
                providerId);
        assertThat(siteAdminOrgOptional.isPresent()).isTrue();

        ResourceOrganization siteAdmin = siteAdminOrgOptional.get();
        assertThat(siteAdmin.getResourceOrgId().equals(resourceOrgId)).isTrue();
    }
}