package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.SubmissionRepository;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@TestPropertySource(properties = {"il-gcc.allow-provider-registration-flow=true", "il-gcc.validate-provider-id=false"})
public class ProviderresponseProviderRegistrationJourneyTest extends AbstractBasePageTest {

    @Autowired
    SubmissionRepository repository;

    private static String CONF_CODE = "A2123B";

    @Test
    void UnlicensedProvidersProviderTypeFlow() {
        createAValidLink();

        driver.navigate()
                .to("http://localhost:%s/providerresponse/submit/%s?utm_medium=test".formatted(localServerPort, CONF_CODE));

        // submit-start
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // paid-by-ccap
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("paid-by-ccap.title"));
        testPage.clickNo();

        // registration-start
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-start.title"));
        testPage.clickButton(getEnMessage("registration-start.button"));

        // registration-getting-started
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-getting-started.title"));
        testPage.clickContinue();

        // registration-provide-care-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-provide-care-intro.title"));
        testPage.clickContinue();

        // registration-licensing
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-licensing.title"));
        testPage.clickNo();

        // registration-applicant
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-applicant.title"));
        testPage.selectRadio("providerLicenseExemptType", "Self");
        testPage.clickContinue();

        // registration-unlicensed-care-location
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-unlicensed-care-location.title"));
        testPage.selectRadio("providerLicenseExemptCareLocation", "Providers home");
        testPage.clickContinue();

        // registration-unlicensed-relationship
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-unlicensed-relationship.title"));
        testPage.selectRadio("providerLicenseExemptRelationship", "Relative");
        testPage.clickContinue();

        // registration-basic-info-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-basic-info-intro.title"));
        testPage.clickContinue();

        // provider-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info.title"));
    }

    @Test
    void LicensedProvidersProviderTypeFlow() {
        createAValidLink();

        driver.navigate()
                .to("http://localhost:%s/providerresponse/submit/%s?utm_medium=test".formatted(localServerPort, CONF_CODE));

        // submit-start
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // paid-by-ccap
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("paid-by-ccap.title"));
        testPage.clickNo();

        // registration-start
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-start.title"));
        testPage.clickButton(getEnMessage("registration-start.button"));

        // registration-getting-started
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-getting-started.title"));
        testPage.clickContinue();

        // registration-provide-care-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-provide-care-intro.title"));
        testPage.clickContinue();

        // registration-licensing
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-licensing.title"));
        testPage.clickYes();

        // registration-licensing-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-licensing-info.title"));
        testPage.enterInputById("providerLicenseNumber", "1231412");
        testPage.clickContinue();

        // registration-licensed-care-location
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-licensed-care-location.title"));
        testPage.clickElementById("providerLicensedCareLocation-childCareHome-label");
        testPage.clickContinue();

        // registration-basic-info-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-basic-info-intro.title"));
    }

    @Test
    void BasicInfoFlow() {
        createAValidLink();

        driver.navigate()
                .to("http://localhost:%s/providerresponse/submit/%s?utm_medium=test".formatted(localServerPort, CONF_CODE));
        testPage.navigateToFlowScreen("providerresponse/registration-basic-info-intro");

        // registration-basic-info-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-basic-info-intro.title"));
        testPage.clickContinue();

        // provider-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info.title"));
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-first-name"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-last-name"))).isTrue();

        testPage.enter("providerResponseFirstName", "ProviderFirst");
        testPage.enter("providerResponseLastName", "ProviderLast");
        testPage.clickContinue();

        // service-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-provider-service-address.title"));
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-street"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-city"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-zip"))).isTrue();

        testPage.enter("providerResponseServiceStreetAddress1", "972 Mission St");
        testPage.enter("providerResponseServiceStreetAddress2", "5th floor");
        testPage.enter("providerResponseServiceCity", "San Francisco");
        testPage.selectFromDropdown("providerResponseServiceState", getEnMessage("state.ca"));
        testPage.enter("providerResponseServiceZipCode", "94103");

        testPage.clickContinue();

        // confirm-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirm-address.title"));
        testPage.clickButton(getEnMessage("address-validation.button.use-this-address"));

        // mailing-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response.mailing-address.title"));
        testPage.clickElementById("providerMailingAddressSameAsServiceAddress-yes");
        // Click it twice so it populates the mailing address fields
        testPage.clickElementById("providerMailingAddressSameAsServiceAddress-yes");
        // Check that JS is correct populating fields when selecting same as home address
        assertThat(testPage.getInputValue("providerMailingStreetAddress1")).isEqualTo("972 Mission St");
        assertThat(testPage.getInputValue("providerMailingStreetAddress2")).isEqualTo("5th floor");
        assertThat(testPage.getInputValue("providerMailingCity")).isEqualTo("San Francisco");
        assertThat(testPage.getSelectValue("providerMailingState")).isEqualTo(getEnMessage("state.ca"));
        assertThat(testPage.getInputValue("providerMailingZipCode")).isEqualTo("94103");

        testPage.clickContinue();

        // confirm-mailing-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("confirm-mailing-address.title"));
        testPage.clickButton(getEnMessage("address-validation.button.use-this-address"));

        // contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-contact-info.title"));
        testPage.enter("providerResponseContactPhoneNumber", "5555555555");
        testPage.enter("providerResponseContactEmail", "foo@bar.com");
        testPage.clickContinue();
        
        // Temporary Confirmation Code
        // TODO - Remove this when we have a solution for downloading the PDF in the provider response flow
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.enter("providerResponseFamilyShortCode", CONF_CODE);
        testPage.clickContinue();

        // registration-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info-review.title"));
        assertThat(testPage.findElementTextById("provider-full-name")).isEqualTo("ProviderFirst ProviderLast");
        assertThat(testPage.findElementTextById("provider-service-address-1")).isEqualTo("972 Mission St");
        assertThat(testPage.findElementTextById("provider-service-address-2")).isEqualTo("5th floor");
        assertThat(testPage.findElementTextById("provider-service-city-state")).isEqualTo("San Francisco, CA");
        assertThat(testPage.findElementTextById("provider-service-zipcode")).isEqualTo("94103");
        assertThat(testPage.findElementTextById("provider-mailing-address-1")).isEqualTo("972 Mission St");
        assertThat(testPage.findElementTextById("provider-mailing-address-2")).isEqualTo("5th floor");
        assertThat(testPage.findElementTextById("provider-mailing-city-state")).isEqualTo("San Francisco, CA");
        assertThat(testPage.findElementTextById("provider-mailing-zipcode")).isEqualTo("94103");
        assertThat(testPage.findElementTextById("provider-phone")).isEqualTo("(555) 555-5555");
        assertThat(testPage.findElementTextById("provider-email")).isEqualTo("foo@bar.com");
        testPage.clickContinue();

        // registration-home-provider-ssn
    }


    private void createAValidLink() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider().withParentDetails()
                .with("parentPreferredName", "FirstName").withChild("First", "Child", "Yes").withChild("Second", "Child", "No")
                .withChild("NoAssistance", "Child", "No").withConstantChildcareSchedule(0)
                .withSubmittedAtDate(OffsetDateTime.now()).withShortCode(CONF_CODE).build());

        testPage.clickContinue();

        driver.navigate()
                .to("http://localhost:%s/providerresponse/submit/%s?utm_medium=test".formatted(localServerPort, CONF_CODE));
    }


}
