package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@TestPropertySource(properties = {"il-gcc.allow-provider-registration-flow=true",
        "il-gcc.enable-provider-registration-with-itin=true"})
public class ProviderresponseProviderRegistrationWithItinJourneyTest extends AbstractBasePageTest {

    @AfterEach
    protected void clearSubmissions() {
        super.clearSubmissions();
    }

    @Test
    public void providerRegistrationHomeProviderSelectItin() {
        setupRegistration();

        testPage.navigateToFlowScreen("providerresponse/registration-licensing");

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

        // registration-basic-info-intro (
        testPage.clickContinue();

        // provider-info
        testPage.enter("providerResponseFirstName", "LicenseExemptProvider");
        testPage.enter("providerResponseLastName", "InChildHome");
        testPage.clickContinue();

        // service-address
        testPage.enter("providerResponseServiceStreetAddress1", "972 Mission St");
        testPage.enter("providerResponseServiceStreetAddress2", "5th floor");
        testPage.enter("providerResponseServiceCity", "San Francisco");
        testPage.selectFromDropdown("providerResponseServiceState", getEnMessage("state.ca"));
        testPage.enter("providerResponseServiceZipCode", "94103");

        testPage.clickContinue();

        // confirm-service-address
        testPage.clickButton(getEnMessage("address-validation.button.use-this-address"));

        // mailing-address
        testPage.clickElementById("providerMailingAddressSameAsServiceAddress-yes");
        testPage.clickContinue();

        // confirm-mailing-address
        testPage.clickButton(getEnMessage("address-validation.button.use-this-address"));

        // contact-info
        testPage.enter("providerResponseContactPhoneNumber", "5555555555");
        testPage.enter("providerResponseContactEmail", "foo@bar.com");
        testPage.clickContinue();

        // registration-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info-review.title"));
        testPage.clickContinue();

        // registration-home-provider-tax-id
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-home-provider-tax-id.title"));
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.select-one-option"))).isTrue();

        testPage.selectRadio("homeProviderTaxIDselection", "SSN");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-home-provider-ssn.title"));
        testPage.goBack();

        testPage.selectRadio("homeProviderTaxIDselection", "ITIN");
        testPage.clickContinue();

        // registration-home-provider-itin
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-home-provider-itin.title"));
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("registration-home-provider-itin.error-blank"))).isTrue();

        testPage.enter("providerITIN", "1231425");
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("registration-home-provider-itin.error-invalid"))).isTrue();
        testPage.enter("providerITIN", "123456789");
        testPage.clickContinue();

        // registration-home-provider-dob
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-home-provider-dob.title"));
    }

    @Test
    void providerRegistrationNonHomeProviderSelectItin() {
        setupRegistration();

        testPage.navigateToFlowScreen("providerresponse/registration-licensing");

        // registration-licensing
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-licensing.title"));
        testPage.clickNo();

        // registration-applicant
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-applicant.title"));
        testPage.selectRadio("providerLicenseExemptType", "License-exempt");
        testPage.clickContinue();

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

        // confirm-service-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("confirm-address.title"));
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
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("confirm-address.title"));
        testPage.clickButton(getEnMessage("address-validation.button.use-this-address"));

        // contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-contact-info.title"));
        testPage.enter("providerResponseContactPhoneNumber", "5555555555");
        testPage.enter("providerResponseContactEmail", "foo@bar.com");
        testPage.clickContinue();

        // registration-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info-review.title"));
        assertThat(testPage.findElementTextById("provider-full-name")).isEqualTo("ProviderFirst ProviderLast");
        assertThat(testPage.findElementTextById("provider-service-street-address-1")).isEqualTo("972 Mission St");
        assertThat(testPage.findElementTextById("provider-service-street-address-2")).isEqualTo("5th floor");
        assertThat(testPage.findElementTextById("provider-service-city-state")).isEqualTo("San Francisco, CA");
        assertThat(testPage.findElementTextById("provider-service-zipcode")).isEqualTo("94103");
        assertThat(testPage.findElementTextById("provider-mailing-street-address-1")).isEqualTo("972 Mission St");
        assertThat(testPage.findElementTextById("provider-mailing-street-address-2")).isEqualTo("5th floor");
        assertThat(testPage.findElementTextById("provider-mailing-city-state")).isEqualTo("San Francisco, CA");
        assertThat(testPage.findElementTextById("provider-mailing-zipcode")).isEqualTo("94103");
        assertThat(testPage.findElementTextById("provider-phone")).isEqualTo("(555) 555-5555");
        assertThat(testPage.findElementTextById("provider-email")).isEqualTo("foo@bar.com");
        testPage.clickContinue();

        //registration-tax-id
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id.title"));
        testPage.clickElementById("providerTaxIdType-FEIN-label");
        testPage.clickContinue();

        // registration-tax-id-ein
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id-ein.title"));
        testPage.goBack();

        //registration-tax-id
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id.title"));
        testPage.clickElementById("providerTaxIdType-SSN-label");
        testPage.clickContinue();

        //registration-tax-id-ssn.title
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id-ssn.title"));
        testPage.goBack();

        //registration-tax-id
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id.title"));

        // toDo add itin test here and uncomment the last line
//
//
//        //registration-service-languages
//        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-service-languages.title"));
//        testPage.clickElementById("providerLanguagesOffered-Spanish-label");
//        testPage.clickContinue();
    }


    private void setupRegistration() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        Submission s = saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider().withParentDetails()
                .with("parentPreferredName", "FirstName").withChild("First", "Child", "true")
                .withChild("Second", "Child", "false").withChild("NoAssistance", "Child", "false")
                .withConstantChildcareSchedule(0).with("earliestChildcareStartDate", "10/10/2011")
                .withSubmittedAtDate(OffsetDateTime.now()).build());

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/s/%s".formatted(localServerPort, s.getShortCode()));

        // submit-start
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // confirmation-code
        assertThat(testPage.findElementById("providerResponseFamilyShortCode").getAttribute("value")).isEqualTo(s.getShortCode());
        testPage.clickContinue();

        // paid-by-ccap
        testPage.clickNo();

        // registration-start
        testPage.clickButton(getEnMessage("registration-start.button"));

        // registration-getting-started
        testPage.clickContinue();

        // registration-provide-care-intro
        testPage.clickContinue();
    }
}
