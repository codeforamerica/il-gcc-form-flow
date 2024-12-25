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
    void BasicInfoFlow() {
        createAValidLink();

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
    }

    @Test
    void LicenseExemptInProviderHomeFlow() {
        createAValidLink();

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

        // confirm-address
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

        // Temporary Confirmation Code
        // TODO - Remove this when we have a solution for downloading the PDF in the provider response flow
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.enter("providerResponseFamilyShortCode", CONF_CODE);
        testPage.clickContinue();

        // registration-info-review
        testPage.clickContinue();

        // registration-home-provider-ssn
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-home-provider-ssn.title"));
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("registration-home-provider-ssn.error"))).isTrue();
        testPage.enter("providerIdentityCheckSSN", "123456789");

        testPage.clickContinue();
        // registration-home-provider-dob
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-home-provider-dob.title"));
        testPage.enter("providerIdentityCheckDateOfBirthMonth", "12");
        testPage.enter("providerIdentityCheckDateOfBirthDay", "25");
        testPage.enter("providerIdentityCheckDateOfBirthYear", "1985");
        testPage.clickContinue();


        // registration-checks-trainings-intro
        testPage.navigateToFlowScreen("providerresponse/registration-checks-trainings-intro");

        // registration-checks-trainings-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-checks-trainings-intro.title"));
        testPage.clickContinue();

        // registration-checks-trainings-notice
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-checks-trainings-notice.title"));
        testPage.clickContinue();

        // registration-convictions
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-convictions.title"));
        testPage.clickContinue();
    }

    @Test
    void LicenseExemptInChildHomeFlow() {
        createAValidLink();

        // registration-licensing
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-licensing.title"));
        testPage.clickNo();

        // registration-applicant
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-applicant.title"));
        testPage.selectRadio("providerLicenseExemptType", "Self");
        testPage.clickContinue();

        // registration-unlicensed-care-location
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-unlicensed-care-location.title"));
        testPage.selectRadio("providerLicenseExemptCareLocation", "Childs home");
        testPage.clickContinue();

        // registration-unlicensed-relationship
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-unlicensed-relationship.title"));
        testPage.selectRadio("providerLicenseExemptRelationship", "Relative");
        testPage.clickContinue();

        // registration-basic-info-intro (
        testPage.clickContinue();

        // provider-info
        testPage.enter("providerResponseFirstName", "LicenseExemptProvider");
        testPage.enter("providerResponseLastName", "InProviderHome");
        testPage.clickContinue();

        // service-address
        testPage.enter("providerResponseServiceStreetAddress1", "972 Mission St");
        testPage.enter("providerResponseServiceStreetAddress2", "5th floor");
        testPage.enter("providerResponseServiceCity", "San Francisco");
        testPage.selectFromDropdown("providerResponseServiceState", getEnMessage("state.ca"));
        testPage.enter("providerResponseServiceZipCode", "94103");

        testPage.clickContinue();

        // confirm-address
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

        // Temporary Confirmation Code
        // TODO - Remove this when we have a solution for downloading the PDF in the provider response flow
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.enter("providerResponseFamilyShortCode", CONF_CODE);
        testPage.clickContinue();

        // registration-info-review
        testPage.clickContinue();

        // registration-home-provider-ssn
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-home-provider-ssn.title"));
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("registration-home-provider-ssn.error"))).isTrue();
        testPage.enter("providerIdentityCheckSSN", "123456789");

        testPage.clickContinue();
        // registration-home-provider-dob
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-home-provider-dob.title"));
        testPage.enter("providerIdentityCheckDateOfBirthMonth", "12");
        testPage.enter("providerIdentityCheckDateOfBirthDay", "25");
        testPage.enter("providerIdentityCheckDateOfBirthYear", "1985");
        testPage.clickContinue();


        // registration-checks-trainings-intro
        testPage.navigateToFlowScreen("providerresponse/registration-checks-trainings-intro");

        // registration-checks-trainings-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-checks-trainings-intro.title"));
        testPage.clickContinue();

        // registration-checks-trainings-notice
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-checks-trainings-notice.title"));
        testPage.clickContinue();

        // registration-convictions
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-convictions.title"));
        testPage.clickContinue();
    }

    @Test
    void LicensedChildCareHomeFlow() {
        createAValidLink();

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

        // registration-basic-info-intro (
        testPage.clickContinue();

        // provider-info
        testPage.enter("providerResponseFirstName", "Licensed");
        testPage.enter("providerResponseLastName", "ChildCareProvider");
        testPage.clickContinue();

        // service-address
        testPage.enter("providerResponseServiceStreetAddress1", "972 Mission St");
        testPage.enter("providerResponseServiceStreetAddress2", "5th floor");
        testPage.enter("providerResponseServiceCity", "San Francisco");
        testPage.selectFromDropdown("providerResponseServiceState", getEnMessage("state.ca"));
        testPage.enter("providerResponseServiceZipCode", "94103");

        testPage.clickContinue();

        // confirm-address
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

        // Temporary Confirmation Code
        // TODO - Remove this when we have a solution for downloading the PDF in the provider response flow
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.enter("providerResponseFamilyShortCode", CONF_CODE);
        testPage.clickContinue();

        // registration-info-review
        testPage.clickContinue();

        // registration-home-provider-ssn
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-home-provider-ssn.title"));
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("registration-home-provider-ssn.error"))).isTrue();
        testPage.enter("providerIdentityCheckSSN", "123456789");

        testPage.clickContinue();
        // registration-home-provider-dob



        // registration-checks-trainings-intro
        testPage.navigateToFlowScreen("providerresponse/registration-checks-trainings-intro");

        // registration-checks-trainings-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-checks-trainings-intro.title"));
        testPage.clickContinue();

        // SKIPS registration-checks-trainings-notice

        // registration-convictions
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-convictions.title"));
        testPage.clickContinue();
    }

    @Test
    void LicensedChildCareCenterFlow() {
        createAValidLink();

        // registration-licensing
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-licensing.title"));
        testPage.clickYes();

        // registration-licensing-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-licensing-info.title"));
        testPage.enterInputById("providerLicenseNumber", "1231412");
        testPage.clickContinue();

        // registration-licensed-care-location
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-licensed-care-location.title"));
        testPage.clickElementById("providerLicensedCareLocation-childCareCenter-label");
        testPage.clickContinue();

        // registration-basic-info-intro (
        testPage.clickContinue();

        // provider-info
        testPage.enter("providerResponseFirstName", "Licensed");
        testPage.enter("providerResponseLastName", "ChildCareCenter");
        testPage.clickContinue();

        // service-address
        testPage.enter("providerResponseServiceStreetAddress1", "972 Mission St");
        testPage.enter("providerResponseServiceStreetAddress2", "5th floor");
        testPage.enter("providerResponseServiceCity", "San Francisco");
        testPage.selectFromDropdown("providerResponseServiceState", getEnMessage("state.ca"));
        testPage.enter("providerResponseServiceZipCode", "94103");

        testPage.clickContinue();

        // confirm-address
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

        // Temporary Confirmation Code
        // TODO - Remove this when we have a solution for downloading the PDF in the provider response flow
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.enter("providerResponseFamilyShortCode", CONF_CODE);
        testPage.clickContinue();

        // registration-info-review
        testPage.clickContinue();

        // skips registration-home-provider-ssn

        // registration-home-provider-dob


        // registration-checks-trainings-intro
        testPage.navigateToFlowScreen("providerresponse/registration-checks-trainings-intro");

        // registration-checks-trainings-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-checks-trainings-intro.title"));
        testPage.clickContinue();

        // Skips registration-checks-trainings-notice

        // registration-convictions
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-convictions.title"));
        testPage.clickContinue();
    }
    @Test
    void LicensedGroupChildCareCenterFlow() {
        createAValidLink();

        // registration-licensing
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-licensing.title"));
        testPage.clickYes();

        // registration-licensing-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-licensing-info.title"));
        testPage.enterInputById("providerLicenseNumber", "1231412");
        testPage.clickContinue();

        // registration-licensed-care-location
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-licensed-care-location.title"));
        testPage.clickElementById("providerLicensedCareLocation-groupChildCareHome-label");
        testPage.clickContinue();

        // registration-basic-info-intro (
        testPage.clickContinue();

        // provider-info
        testPage.enter("providerResponseFirstName", "Licenses");
        testPage.enter("providerResponseLastName", "GroupChildCareHome");
        testPage.clickContinue();

        // service-address
        testPage.enter("providerResponseServiceStreetAddress1", "972 Mission St");
        testPage.enter("providerResponseServiceStreetAddress2", "5th floor");
        testPage.enter("providerResponseServiceCity", "San Francisco");
        testPage.selectFromDropdown("providerResponseServiceState", getEnMessage("state.ca"));
        testPage.enter("providerResponseServiceZipCode", "94103");

        testPage.clickContinue();

        // confirm-address
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

        // Temporary Confirmation Code
        // TODO - Remove this when we have a solution for downloading the PDF in the provider response flow
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.enter("providerResponseFamilyShortCode", CONF_CODE);
        testPage.clickContinue();

        // registration-info-review
        testPage.clickContinue();

        // registration-home-provider-ssn
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-home-provider-ssn.title"));
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("registration-home-provider-ssn.error"))).isTrue();
        testPage.enter("providerIdentityCheckSSN", "123456789");

        testPage.clickContinue();

        // registration-checks-trainings-intro
        testPage.navigateToFlowScreen("providerresponse/registration-checks-trainings-intro");

        // registration-checks-trainings-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-checks-trainings-intro.title"));
        testPage.clickContinue();

        // Skips registration-checks-trainings-notice

        // registration-convictions
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-convictions.title"));
        testPage.clickContinue();
    }

    @Test
    void LicenseExemptChildCareCenter() {
        createAValidLink();

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

        // Skips registration-home-provider-ssn

        // registration-home-provider-dob


        // registration-checks-trainings-intro
        testPage.navigateToFlowScreen("providerresponse/registration-checks-trainings-intro");

        // registration-checks-trainings-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-checks-trainings-intro.title"));
        testPage.clickContinue();

        // registration-checks-trainings-notice
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-checks-trainings-notice.title"));
        testPage.clickContinue();

        // registration-convictions
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-convictions.title"));
        testPage.clickContinue();
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
    }
}
