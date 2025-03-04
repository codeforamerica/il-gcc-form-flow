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
@TestPropertySource(properties = {"il-gcc.allow-provider-registration-flow=true"})
public class ProviderresponseProviderRegistrationJourneyTest extends AbstractBasePageTest {

    @Autowired
    SubmissionRepository repository;

    private static final String CONF_CODE = "A2123B";

    @Test
    void basicInfoFlow() {
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

        // registration-contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-contact-info.title"));
        testPage.enter("providerResponseContactPhoneNumber", "5555555555");
        testPage.enter("providerResponseContactEmail", "foo@bar.com");
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

        //registration-tax-id
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id.title"));
        testPage.clickElementById("providerTaxIdType-SSN-label");
        testPage.clickContinue();

        //registration-tax-id-ssn (test error)
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id-ssn.title"));
        testPage.enter("providerTaxIdSSN", "222-22-222");
        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("registration-tax-id-ssn.error"))).isTrue();

        //registration-tax-id-ssn
        testPage.enter("providerTaxIdSSN", "333-22-2222");
        testPage.clickContinue();

        //registration-service-languages-error
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-service-languages.title"));
        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("registration-service-languages.error"))).isTrue();

        //registration-service-languages
        testPage.clickElementById("providerLanguagesOffered-English-label");
        testPage.clickContinue();
    }

    @Test
    public void existingProviderBasicflow() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider().withParentDetails()
            .with("parentPreferredName", "FirstName").withChild("First", "Child", "Yes").withChild("Second", "Child", "No")
            .withChild("NoAssistance", "Child", "No").withConstantChildcareSchedule(0)
            .with("earliestChildcareStartDate", "10/10/2011")
            .withSubmittedAtDate(OffsetDateTime.now()).withShortCode(CONF_CODE).build());

        testPage.clickContinue();

        driver.navigate()
            .to("http://localhost:%s/s/%s".formatted(localServerPort, CONF_CODE));

        // submit-start
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // paid-by-ccap
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("paid-by-ccap.title"));
        testPage.clickYes();

        //provider-number
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-provider-number.title"));
        testPage.enter("providerResponseProviderNumber", "12345678901");
        testPage.clickContinue();

        //confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.clickContinue();

        //response
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-response.title"));
        testPage.clickElementById("providerResponseAgreeToCare-true");
        testPage.clickContinue();
        //provider-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info.title"));
        testPage.enter("providerResponseBusinessName", "Business Name");
        testPage.enter("providerResponseFirstName", "First Name");
        testPage.enter("providerResponseLastName", "Last Name");
        testPage.clickButton("Continue");

        // service-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-provider-service-address.title"));
        testPage.enter("providerResponseServiceStreetAddress1", "123 Main St");
        testPage.enter("providerResponseServiceCity", "City");
        testPage.selectFromDropdown("providerResponseServiceState", "IL - Illinois");
        testPage.enter("providerResponseServiceZipCode", "12345");
        testPage.clickButton("Continue");

        // confirm-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirm-address.title"));
        testPage.clickButton("Use this address");

        //mailing-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response.mailing-address.title"));
        testPage.clickElementById("providerMailingAddressSameAsServiceAddress-yes");
        testPage.clickContinue();

        //confirm-mailing-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("confirm-mailing-address.title"));
        testPage.clickButton("Use this address");

        // contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-contact-info.title"));
        testPage.enter("providerResponseContactPhoneNumber", "5555555555");
        testPage.enter("providerResponseContactEmail", "foo@bar.com");
        testPage.clickButton("Continue");

        // info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info-review.title"));
        assertThat(testPage.findElementTextById("business-name")).isEqualTo("Business Name");
        assertThat(testPage.findElementTextById("full-name")).isEqualTo("First Name Last Name");
        assertThat(testPage.findElementTextById("provider-service-address-1")).isEqualTo("123 Main St");
        assertThat(testPage.findElementTextById("provider-service-city-state")).isEqualTo("City, IL");
        assertThat(testPage.findElementTextById("provider-service-zipcode")).isEqualTo("12345");
        assertThat(testPage.findElementTextById("phone")).isEqualTo("(555) 555-5555");
        assertThat(testPage.findElementTextById("email")).isEqualTo("foo@bar.com");
        testPage.clickButton("Continue");

        // registration-submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-confirmation.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("registration-submit-confirmation.existing-provider.header"));

    }

    @Test
    void licenseExemptInProviderHomeFlow() {
        createAValidLink();
        submitFamilyConfirmationCode();

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

        // registration-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info-review.title"));
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
        //registration-tax-id
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id.title"));
        testPage.clickElementById("providerTaxIdType-SSN-label");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isNotEqualTo(getEnMessage("registration-tax-id-ssn.title"));
        assertThat(testPage.getTitle()).isNotEqualTo(getEnMessage("registration-tax-id-ein.title"));

        //registration-service-languages-error
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-service-languages.title"));
        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("registration-service-languages.error"))).isTrue();

        //registration-service-languages
        testPage.clickElementById("providerLanguagesOffered-other");
        testPage.enter("providerLanguagesOffered_other", "Test");
        testPage.clickContinue();

        // registration-start-date
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-start-date.title"));
        testPage.clickContinue();

        // registration-checks-trainings-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-checks-trainings-intro.title"));
        testPage.clickContinue();

        // registration-checks-trainings-notice
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-checks-trainings-notice.title"));
        testPage.clickContinue();

        // registration-convictions
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-convictions.title"));
        testPage.clickYes();

        // registration-convictions-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-convictions-info.title"));
        testPage.enter("providerConvictionExplanation", "Reason for conviction");
        testPage.clickContinue();

        // registration-household-members
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-household-members.title"));
        testPage.clickNo();

        // registration-family-response-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-family-response-intro.title"));
        testPage.goBack();

        // registration-household-members
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-household-members.title"));
        testPage.clickYes();

        // registration-household-add-person
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-household-add-person.title"));
        testPage.clickElementById("add-provider-household-member");

        //registration-household-add-person-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-household-add-person-info.title"));
        testPage.clickContinue();

        //registration-household-add-person-info-error
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
        assertThat(testPage.hasErrorText(getEnMessage("registration-household-add-person-info.error.first-name"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("registration-household-add-person-info.error.last-name"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("registration-household-add-person-info.error.dob"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("registration-household-add-person-info.error.relationship"))).isTrue();

        testPage.enter("providerHouseholdMemberDateOfBirthDay", "10");
        testPage.enter("providerHouseholdMemberSSN", "333");
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("registration-household-add-person-info.error.date"))).isTrue();
        assertThat(testPage.hasErrorText(getEnMessage("registration-household-add-person-info.error.dob"))).isFalse();
        assertThat(testPage.hasErrorText(getEnMessage("registration-household-add-person-info.error.ssn"))).isTrue();

        testPage.enter("providerHouseholdMemberFirstName", "First_Name_Test");
        testPage.enter("providerHouseholdMemberLastName", "Last_Name_Test");
        testPage.enter("providerHouseholdMemberRelationship", "Aunt");
        testPage.enter("providerHouseholdMemberDateOfBirthDay", "9");
        testPage.enter("providerHouseholdMemberDateOfBirthMonth", "10");
        testPage.enter("providerHouseholdMemberDateOfBirthYear", "2009");
        testPage.enter("providerHouseholdMemberSSN", "333-33-3333");
        testPage.clickContinue();

        // registration-household-add-person
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-household-add-person.title"));
        testPage.clickElementById("add-provider-household-member");

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-household-add-person-info.title"));
        testPage.enter("providerHouseholdMemberFirstName", "Tester");
        testPage.enter("providerHouseholdMemberLastName", "Lastenson");
        testPage.enter("providerHouseholdMemberRelationship", "Brother");
        testPage.enter("providerHouseholdMemberDateOfBirthDay", "2");
        testPage.enter("providerHouseholdMemberDateOfBirthMonth", "1");
        testPage.enter("providerHouseholdMemberDateOfBirthYear", "1999");
        testPage.enter("providerHouseholdMemberSSN", "888-33-3333");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-household-add-person.title"));

        assertThat(testPage.findElementsByClass("m").get(1).getText()).isEqualTo("Tester Lastenson");
        testPage.findElementsByClass("subflow-delete").get(1).click();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-household-add-person-delete.title"));
        testPage.clickButton(getEnMessage("registration-household-add-person-delete.button.yes-delete"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-household-add-person.title"));

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-household-add-person.title"));
        assertThat(testPage.findElementsByClass("m").getFirst().getText()).isEqualTo("First_Name_Test Last_Name_Test");
        assertThat(testPage.findElementById("done-adding-provider-household-member").getText()).isEqualTo(getEnMessage("registration-household-add-person.im-done"));
        testPage.clickElementById("done-adding-provider-household-member");

        // registration-family-response-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-family-response-intro.title"));
        testPage.clickContinue();

        // confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.clickContinue();

        // response
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-response.title"));
        testPage.selectRadio("providerResponseAgreeToCare", "true");
        testPage.clickContinue();

        // registration-submit-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-intro.title"));
        testPage.clickContinue();

        // registration-terms
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-terms.title"));
        testPage.clickCheckbox("providerAgreesToLegalTerms-true");
        testPage.clickContinue();

        // registration-signature
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-signature.title"));
        testPage.enter("providerSignedName", "test name");
        testPage.clickSubmit();

        // registration-submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-complete.title"));
        testPage.clickButton(getEnMessage("submit-complete.button.do-this-later"));

        // registration-submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-next-steps.title"));
        testPage.goBack();
        testPage.clickButton(getEnMessage("submit-complete.yes-add-document-now"));

        // registration-doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-doc-upload-recommended-docs.title"));
        assertThat(testPage.elementDoesNotExistById("ssn-recommendation")).isFalse();
        assertThat(testPage.elementDoesNotExistById("id-recommendation")).isFalse();
        assertThat(testPage.elementDoesNotExistById("child-care-license-recommendation")).isTrue();
        assertThat(testPage.elementDoesNotExistById("w9-recommendation")).isFalse();
        assertThat(testPage.elementDoesNotExistById("ein-recommendation")).isTrue();
        assertThat(testPage.elementDoesNotExistById("license-exempt-letter-recommendation")).isTrue();
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // registration-doc-upload-add-files
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-doc-upload-add-files.title"));
        assertThat(testPage.elementDoesNotExistById("show-ssn-card-required")).isFalse();
        assertThat(testPage.elementDoesNotExistById("show-id-card-required")).isFalse();
        assertThat(testPage.elementDoesNotExistById("show-child-care-license-required")).isTrue();
        assertThat(testPage.elementDoesNotExistById("show-w9-tax-form-required")).isFalse();
        assertThat(testPage.elementDoesNotExistById("show-irs-letter-required")).isTrue();
        assertThat(testPage.elementDoesNotExistById("show-license-exempt-letter-required")).isTrue();
        testPage.goBack();

        // registration-doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-doc-upload-recommended-docs.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.skip"));

        // registration-submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-next-steps.title"));
        testPage.goBack();

        // registration-doc-upload-recommended-docs
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // registration-doc-upload-add-files.title
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-doc-upload-add-files.title"));
        uploadJpgFile("providerUploadDocuments");
        
        testPage.clickButton(getEnMessage("doc-upload-add-files.confirmation"));

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-submit-confirmation.title"));
        testPage.clickButton(getEnMessage("doc-upload-submit-confirmation.yes"));

        // registration-submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-next-steps.title"));
        testPage.clickContinue();

        // registration-submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-confirmation.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("registration-submit-confirmation.new-provider.header"));
    }

    @Test
    void licenseExemptInChildHomeFlow() {
        createAValidLink();
        submitFamilyConfirmationCode();

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

        // registration-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info-review.title"));
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

        //registration-tax-id
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id.title"));
        testPage.clickElementById("providerTaxIdType-FEIN-label");
        testPage.clickContinue();

        //registration-tax-id-ein (error)
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id-ein.title"));
        testPage.enter("providerTaxIdEIN", "12345");
        testPage.clickContinue();

        //registration-tax-id-ein
        assertThat(testPage.hasErrorText(getEnMessage("registration-tax-id-ein.error"))).isTrue();
        testPage.enter("providerTaxIdEIN", "123456789");
        testPage.clickContinue();

        //Test Prefer Not to Answer Implementation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-service-languages.title"));
        testPage.clickElementById("providerLanguagesOffered-English-label");
        assertThat(testPage.findElementById("providerLanguagesOffered-English").isSelected()).isTrue();
        testPage.clickElementById("none__checkbox-providerLanguagesOffered");
        assertThat(testPage.findElementById("providerLanguagesOffered-English").isSelected()).isFalse() ;
        testPage.clickContinue();

        // registration-start-date
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-start-date.title"));
        testPage.clickContinue();

        // registration-checks-trainings-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-checks-trainings-intro.title"));
        testPage.clickContinue();

        // registration-checks-trainings-notice
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-checks-trainings-notice.title"));
        testPage.clickContinue();

        // registration-convictions
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-convictions.title"));
        testPage.clickNo();

        // Skips registration-convictions-info and household screens
        // registration-family-response-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-family-response-intro.title"));
        testPage.clickContinue();

        // confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.clickContinue();

        // response
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-response.title"));
        testPage.selectRadio("providerResponseAgreeToCare", "true");
        testPage.clickContinue();

        // registration-submit-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-intro.title"));
        testPage.clickContinue();

        // registration-terms
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-terms.title"));
        testPage.clickCheckbox("providerAgreesToLegalTerms-true");
        testPage.clickContinue();

        // registration-signature
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-signature.title"));
        testPage.enter("providerSignedName", "test name");
        testPage.clickSubmit();

        // registration-submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-complete.title"));
        testPage.clickButton(getEnMessage("submit-complete.yes-add-document-now"));

        // registration-doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-doc-upload-recommended-docs.title"));
        assertThat(testPage.elementDoesNotExistById("ssn-recommendation")).isFalse();
        assertThat(testPage.elementDoesNotExistById("id-recommendation")).isFalse();
        assertThat(testPage.elementDoesNotExistById("child-care-license-recommendation")).isTrue();
        assertThat(testPage.elementDoesNotExistById("w9-recommendation")).isFalse();
        assertThat(testPage.elementDoesNotExistById("ein-recommendation")).isFalse();
        assertThat(testPage.elementDoesNotExistById("license-exempt-letter-recommendation")).isTrue();
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));
        
        // registration-doc-upload-add-files
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-doc-upload-add-files.title"));
        assertThat(testPage.elementDoesNotExistById("show-ssn-card-required")).isFalse();
        assertThat(testPage.elementDoesNotExistById("show-id-card-required")).isFalse();
        assertThat(testPage.elementDoesNotExistById("show-child-care-license-required")).isTrue();
        assertThat(testPage.elementDoesNotExistById("show-w9-tax-form-required")).isFalse();
        assertThat(testPage.elementDoesNotExistById("show-irs-letter-required")).isFalse();
        assertThat(testPage.elementDoesNotExistById("show-license-exempt-letter-required")).isTrue();
        assertThat(testPage.findElementById("form-submit-button").getAttribute("class").contains("display-none")).isTrue();
        uploadJpgFile("providerUploadDocuments");

        testPage.clickButton(getEnMessage("doc-upload-add-files.confirmation"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-submit-confirmation.title"));
        testPage.clickButton(getEnMessage("doc-upload-submit-confirmation.yes"));
        
        // registration-submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-next-steps.title"));
        testPage.clickContinue();

        // registration-submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-confirmation.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("registration-submit-confirmation.new-provider.header"));

    }

    @Test
    void licensedChildCareHomeFlow() {
        createAValidLink();
        submitFamilyConfirmationCode();

        testPage.navigateToFlowScreen("providerresponse/registration-licensing");

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

        // registration-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info-review.title"));
        testPage.clickContinue();

        // registration-home-provider-ssn
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-home-provider-ssn.title"));
        testPage.enter("providerIdentityCheckSSN", "123456789");
        testPage.clickContinue();

        //registration-tax-id.title
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id.title"));
        testPage.clickElementById("providerTaxIdType-SSN-label");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isNotEqualTo(getEnMessage("registration-tax-id-ssn.title"));
        assertThat(testPage.getTitle()).isNotEqualTo(getEnMessage("registration-tax-id-ein.title"));

        //registration-service-languages
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-service-languages.title"));
        testPage.clickElementById("providerLanguagesOffered-Arabic-label");
        testPage.clickElementById("providerLanguagesOffered-Chinese-label");
        testPage.clickElementById("providerLanguagesOffered-Polish-label");
        testPage.clickElementById("providerLanguagesOffered-Tagalog-label");
        testPage.clickElementById("providerLanguagesOffered-other");
        testPage.enter("providerLanguagesOffered_other", "Test");
        testPage.clickContinue();
        testPage.goBack();

        assertThat(testPage.findElementById("providerLanguagesOffered-Arabic").isSelected()).isTrue();
        assertThat(testPage.findElementById("providerLanguagesOffered-Tagalog").isSelected()).isTrue();
        assertThat(testPage.findElementById("providerLanguagesOffered-Chinese").isSelected()).isTrue();
        assertThat(testPage.findElementById("providerLanguagesOffered-English-label").isSelected()).isFalse();
        assertThat(testPage.findElementById("providerLanguagesOffered-other").isSelected()).isTrue();
        assertThat(testPage.getInputValue("providerLanguagesOffered_other")).isEqualTo("Test");
        testPage.clickContinue();

        // registration-start-date
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-start-date.title"));
        testPage.clickContinue();

        // registration-checks-trainings-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-checks-trainings-intro.title"));
        testPage.clickContinue();

        // SKIPS registration-checks-trainings-notice

        // registration-convictions
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-convictions.title"));
        testPage.clickYes();

        // registration-convictions-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-convictions-info.title"));
        testPage.enter("providerConvictionExplanation", "Reason for conviction");
        testPage.clickContinue();

        // registration-family-response-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-family-response-intro.title"));
        testPage.clickContinue();

        // confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.clickContinue();

        // response
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-response.title"));
        testPage.selectRadio("providerResponseAgreeToCare", "true");
        testPage.clickContinue();

        // registration-submit-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-intro.title"));
        testPage.clickContinue();

        // registration-terms
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-terms.title"));
        testPage.clickCheckbox("providerAgreesToLegalTerms-true");
        testPage.clickContinue();

        // registration-signature
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-signature.title"));
        testPage.enter("providerSignedName", "test name");
        testPage.clickSubmit();

        // registration-submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-complete.title"));
        testPage.clickButton(getEnMessage("submit-complete.yes-add-document-now"));

        // registration-doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-doc-upload-recommended-docs.title"));
        assertThat(testPage.elementDoesNotExistById("ssn-recommendation")).isFalse();
        assertThat(testPage.elementDoesNotExistById("id-recommendation")).isTrue();
        assertThat(testPage.elementDoesNotExistById("child-care-license-recommendation")).isFalse();
        assertThat(testPage.elementDoesNotExistById("w9-recommendation")).isFalse();
        assertThat(testPage.elementDoesNotExistById("ein-recommendation")).isTrue();
        assertThat(testPage.elementDoesNotExistById("license-exempt-letter-recommendation")).isTrue();
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // registration-doc-upload-add-files
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-doc-upload-add-files.title"));
        assertThat(testPage.elementDoesNotExistById("show-ssn-card-required")).isFalse();
        assertThat(testPage.elementDoesNotExistById("show-id-card-required")).isTrue();
        assertThat(testPage.elementDoesNotExistById("show-child-care-license-required")).isFalse();
        assertThat(testPage.elementDoesNotExistById("show-w9-tax-form-required")).isFalse();
        assertThat(testPage.elementDoesNotExistById("show-irs-letter-required")).isTrue();
        assertThat(testPage.elementDoesNotExistById("show-license-exempt-letter-required")).isTrue();
        uploadJpgFile("providerUploadDocuments");

        testPage.clickButton(getEnMessage("doc-upload-add-files.confirmation"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-submit-confirmation.title"));
        testPage.clickButton(getEnMessage("doc-upload-submit-confirmation.yes"));
        
        // registration-submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-next-steps.title"));
        testPage.clickContinue();

        // registration-submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-confirmation.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("registration-submit-confirmation.new-provider.header"));
    }

    @Test
    void licensedChildCareCenterFlow() {
        createAValidLink();
        submitFamilyConfirmationCode();

        testPage.navigateToFlowScreen("providerresponse/registration-licensing");

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

        // registration-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info-review.title"));
        testPage.clickContinue();

        //registration-tax-id.title
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id.title"));
        testPage.clickElementById("providerTaxIdType-FEIN-label");
        testPage.clickContinue();

        //registration-tax-id-ein
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id-ein.title"));
        testPage.enter("providerTaxIdEIN", "123456789");
        testPage.clickContinue();

        //registration-service-languages-error
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-service-languages.title"));
        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("registration-service-languages.error"))).isTrue();

        //registration-service-languages
        testPage.clickElementById("providerLanguagesOffered-Polish-label");
        testPage.clickContinue();

        // registration-start-date
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-start-date.title"));
        testPage.clickContinue();

        // registration-checks-trainings-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-checks-trainings-intro.title"));
        testPage.clickContinue();

        // Skips registration-checks-trainings-notice

        // registration-convictions
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-convictions.title"));
        testPage.clickNo();

        // Skips registration-convictions-info and registration screens
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-family-response-intro.title"));
        testPage.clickContinue();

        // confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.clickContinue();

        // response
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-response.title"));
        testPage.selectRadio("providerResponseAgreeToCare", "true");
        testPage.clickContinue();

        // registration-submit-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-intro.title"));
        testPage.clickContinue();

        // registration-terms
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-terms.title"));
        testPage.clickCheckbox("providerAgreesToLegalTerms-true");
        testPage.clickContinue();

        // registration-signature
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-signature.title"));
        testPage.enter("providerSignedName", "test name");
        testPage.clickSubmit();

        // registration-submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-complete.title"));
        testPage.clickButton(getEnMessage("submit-complete.yes-add-document-now"));

        // registration-doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-doc-upload-recommended-docs.title"));
        assertThat(testPage.elementDoesNotExistById("ssn-recommendation")).isTrue();
        assertThat(testPage.elementDoesNotExistById("id-recommendation")).isTrue();
        assertThat(testPage.elementDoesNotExistById("child-care-license-recommendation")).isFalse();
        assertThat(testPage.elementDoesNotExistById("w9-recommendation")).isFalse();
        assertThat(testPage.elementDoesNotExistById("ein-recommendation")).isFalse();
        assertThat(testPage.elementDoesNotExistById("license-exempt-letter-recommendation")).isTrue();
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // registration-doc-upload-add-files
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-doc-upload-add-files.title"));
        assertThat(testPage.elementDoesNotExistById("show-ssn-card-required")).isTrue();
        assertThat(testPage.elementDoesNotExistById("show-id-card-required")).isTrue();
        assertThat(testPage.elementDoesNotExistById("show-child-care-license-required")).isFalse();
        assertThat(testPage.elementDoesNotExistById("show-w9-tax-form-required")).isFalse();
        assertThat(testPage.elementDoesNotExistById("show-irs-letter-required")).isFalse();
        assertThat(testPage.elementDoesNotExistById("show-license-exempt-letter-required")).isTrue();
        uploadJpgFile("providerUploadDocuments");

        testPage.clickButton(getEnMessage("doc-upload-add-files.confirmation"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-submit-confirmation.title"));
        testPage.clickButton(getEnMessage("doc-upload-submit-confirmation.yes"));

        // registration-submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-next-steps.title"));
        testPage.clickContinue();

        // registration-submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-confirmation.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("registration-submit-confirmation.new-provider.header"));
    }

    @Test
    void licensedGroupChildCareHomeFlow() {
        createAValidLink();
        submitFamilyConfirmationCode();

        testPage.navigateToFlowScreen("providerresponse/registration-licensing");

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

        // registration-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info-review.title"));
        testPage.clickContinue();

        // registration-home-provider-ssn
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-home-provider-ssn.title"));
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("registration-home-provider-ssn.error"))).isTrue();
        testPage.enter("providerIdentityCheckSSN", "123456789");

        testPage.clickContinue();

        //registration-tax-id.title
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id.title"));
        testPage.clickElementById("providerTaxIdType-SSN-label");
        testPage.clickContinue();

        //registration-service-languages-error
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-service-languages.title"));
        testPage.clickContinue();
        assertThat(testPage.hasErrorText(getEnMessage("registration-service-languages.error"))).isTrue();

        //registration-service-languages
        testPage.clickElementById("providerLanguagesOffered-Hindi-label");
        testPage.clickContinue();

        // registration-start-date
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-start-date.title"));
        testPage.clickContinue();

        // registration-checks-trainings-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-checks-trainings-intro.title"));
        testPage.clickContinue();

        // Skips registration-checks-trainings-notice

        // registration-convictions
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-convictions.title"));
        testPage.clickYes();

        // registration-convictions-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-convictions-info.title"));
        testPage.enter("providerConvictionExplanation", "Reason for conviction");
        testPage.clickContinue();

        // registration-family-response-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-family-response-intro.title"));
        testPage.clickContinue();

        // confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.clickContinue();

        // response
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-response.title"));
        testPage.selectRadio("providerResponseAgreeToCare", "true");
        testPage.clickContinue();

        // registration-submit-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-intro.title"));
        testPage.clickContinue();

        // registration-terms
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-terms.title"));
        testPage.clickCheckbox("providerAgreesToLegalTerms-true");
        testPage.clickContinue();

        // registration-signature
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-signature.title"));
        testPage.enter("providerSignedName", "test name");
        testPage.clickSubmit();

        // registration-submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-complete.title"));
        testPage.clickButton(getEnMessage("submit-complete.yes-add-document-now"));

        // registration-doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-doc-upload-recommended-docs.title"));
        assertThat(testPage.elementDoesNotExistById("ssn-recommendation")).isFalse();
        assertThat(testPage.elementDoesNotExistById("id-recommendation")).isTrue();
        assertThat(testPage.elementDoesNotExistById("child-care-license-recommendation")).isFalse();
        assertThat(testPage.elementDoesNotExistById("w9-recommendation")).isFalse();
        assertThat(testPage.elementDoesNotExistById("ein-recommendation")).isTrue();
        assertThat(testPage.elementDoesNotExistById("license-exempt-letter-recommendation")).isTrue();
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // registration-doc-upload-add-files
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-doc-upload-add-files.title"));
        assertThat(testPage.elementDoesNotExistById("show-ssn-card-required")).isFalse();
        assertThat(testPage.elementDoesNotExistById("show-id-card-required")).isTrue();
        assertThat(testPage.elementDoesNotExistById("show-child-care-license-required")).isFalse();
        assertThat(testPage.elementDoesNotExistById("show-w9-tax-form-required")).isFalse();
        assertThat(testPage.elementDoesNotExistById("show-irs-letter-required")).isTrue();
        assertThat(testPage.elementDoesNotExistById("show-license-exempt-letter-required")).isTrue();
        uploadJpgFile("providerUploadDocuments");

        testPage.clickButton(getEnMessage("doc-upload-add-files.confirmation"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-submit-confirmation.title"));
        testPage.clickButton(getEnMessage("doc-upload-submit-confirmation.yes"));
        
        // registration-submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-next-steps.title"));
        testPage.clickContinue();

        // registration-submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-confirmation.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("registration-submit-confirmation.new-provider.header"));
    }

    @Test
    void licenseExemptChildCareCenter() {
        createAValidLink();
        submitFamilyConfirmationCode();

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

        //registration-tax-id.title
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id.title"));
        testPage.clickElementById("providerTaxIdType-SSN-label");
        testPage.clickContinue();

        //registration-tax-id-ssn.title
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id-ssn.title"));
        testPage.enter("providerTaxIdSSN", "333-22-2222");
        testPage.clickContinue();

        //registration-service-languages
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-service-languages.title"));
        testPage.clickElementById("providerLanguagesOffered-Spanish-label");
        testPage.clickContinue();

        // registration-start-date
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-start-date.title"));
        testPage.clickContinue();

        // registration-checks-trainings-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-checks-trainings-intro.title"));
        testPage.clickContinue();

        // registration-checks-trainings-notice
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-checks-trainings-notice.title"));
        testPage.clickContinue();

        // registration-convictions
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-convictions.title"));
        testPage.clickYes();

        // registration-convictions-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-convictions-info.title"));
        testPage.enter("providerConvictionExplanation", "Reason for conviction");
        testPage.clickContinue();

        // registration-family-response-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-family-response-intro.title"));
        testPage.clickContinue();

        // confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.clickContinue();

        // response
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-response.title"));
        testPage.selectRadio("providerResponseAgreeToCare", "true");
        testPage.clickContinue();

        // registration-submit-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-intro.title"));
        testPage.clickContinue();

        // registration-terms
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-terms.title"));
        testPage.clickCheckbox("providerAgreesToLegalTerms-true");
        testPage.clickContinue();

        // registration-signature
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-signature.title"));
        testPage.enter("providerSignedName", "test name");
        testPage.clickSubmit();

        // registration-submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-complete.title"));
        testPage.clickButton(getEnMessage("submit-complete.button.do-this-later"));

        // registration-submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-next-steps.title"));
        testPage.goBack();
        testPage.clickButton(getEnMessage("submit-complete.yes-add-document-now"));

        // registration-doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-doc-upload-recommended-docs.title"));
        assertThat(testPage.elementDoesNotExistById("ssn-recommendation")).isFalse();
        assertThat(testPage.elementDoesNotExistById("id-recommendation")).isTrue();
        assertThat(testPage.elementDoesNotExistById("child-care-license-recommendation")).isTrue();
        assertThat(testPage.elementDoesNotExistById("w9-recommendation")).isFalse();
        assertThat(testPage.elementDoesNotExistById("ein-recommendation")).isTrue();
        assertThat(testPage.elementDoesNotExistById("license-exempt-letter-recommendation")).isFalse();
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // registration-doc-upload-add-files
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-doc-upload-add-files.title"));
        assertThat(testPage.elementDoesNotExistById("show-ssn-card-required")).isFalse();
        assertThat(testPage.elementDoesNotExistById("show-id-card-required")).isTrue();
        assertThat(testPage.elementDoesNotExistById("show-child-care-license-required")).isTrue();
        assertThat(testPage.elementDoesNotExistById("show-w9-tax-form-required")).isFalse();
        assertThat(testPage.elementDoesNotExistById("show-irs-letter-required")).isTrue();
        assertThat(testPage.elementDoesNotExistById("show-license-exempt-letter-required")).isFalse();
        uploadJpgFile("providerUploadDocuments");

        testPage.clickButton(getEnMessage("doc-upload-add-files.confirmation"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-submit-confirmation.title"));
        testPage.clickButton(getEnMessage("doc-upload-submit-confirmation.yes"));
        
        // registration-submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-next-steps.title"));
        testPage.clickContinue();

        // registration-submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-confirmation.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("registration-submit-confirmation.new-provider.header"));
    }


    private void createAValidLink() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider().withParentDetails()
                .with("parentPreferredName", "FirstName").withChild("First", "Child", "Yes").withChild("Second", "Child", "No")
                .withChild("NoAssistance", "Child", "No").withConstantChildcareSchedule(0)
                .with("earliestChildcareStartDate", "10/10/2011")
                .withSubmittedAtDate(OffsetDateTime.now()).withShortCode(CONF_CODE).build());

        testPage.clickContinue();

        driver.navigate()
                .to("http://localhost:%s/s/%s".formatted(localServerPort, CONF_CODE));

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

    private void submitFamilyConfirmationCode(){
        testPage.navigateToFlowScreen("providerresponse/confirmation-code");
        testPage.clickContinue();
    }
}
