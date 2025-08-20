package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@Slf4j
public class ProviderresponseProviderRegistrationJourneyTest extends AbstractBasePageTest {

    @AfterEach
    protected void clearSubmissions() {
        super.clearSubmissions();
    }

    @Test
    public void providerRegistersWhenNotSureIfPaidByCCCAP() {
        String confirmationCode = createAValidLink();

        // submit-start
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        assertThat(testPage.findElementById("providerResponseFamilyShortCode").getAttribute("value")).isEqualTo(confirmationCode);

        testPage.clickContinue();

        // paid-by-ccap
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("paid-by-ccap.title"));
        testPage.clickElementById("not-sure-providerPaidCcap-link");

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
        testPage.clickElementById("homeProviderTaxIDselection-SSN");
        testPage.clickContinue();

        // registration-home-provider-ssn
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-home-provider-ssn.title"));
        testPage.enter("providerIdentityCheckSSN", "123456789");
        testPage.clickContinue();

        // registration-home-provider-dob
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-home-provider-dob.title"));
        testPage.enter("providerIdentityCheckDateOfBirthMonth", "12");
        testPage.enter("providerIdentityCheckDateOfBirthDay", "25");
        testPage.enter("providerIdentityCheckDateOfBirthYear", "1985");
        testPage.clickContinue();

        // registration-payment-tax-id
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-payment-tax-id.title"));
        testPage.clickElementById("providerTaxIdType-SSN-label");
        testPage.clickContinue();

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
        assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-ssn"))).isTrue();

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
        assertThat(testPage.findElementsByClass("subflow-delete").get(1).getAccessibleName()).isEqualTo("remove Tester Lastenson");
        testPage.findElementsByClass("subflow-delete").get(1).click();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-household-add-person-delete.title"));
        testPage.clickButton(getEnMessage("registration-household-add-person-delete.button.yes-delete"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-household-add-person.title"));

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-household-add-person.title"));
        assertThat(testPage.findElementsByClass("m").getFirst().getText()).isEqualTo("First_Name_Test Last_Name_Test");
        assertThat(testPage.findElementById("done-adding-provider-household-member").getText()).isEqualTo(
                getEnMessage("registration-household-add-person.im-done"));
        testPage.clickElementById("done-adding-provider-household-member");

        // registration-family-response-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-family-response-intro.title"));
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
        testPage.clickButton(getEnMessage("registration-submit-complete.button.do-this-later"));

        // registration-submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-submit-next-steps.title"));
        testPage.goBack();
        testPage.clickButton(getEnMessage("registration-submit-complete.yes-add-document-now"));

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

        // submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-confirmation.new-provider-registration.title"));
        // New provider who agreed to care should see this header
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("submit-confirmation.new-provider.header"));
        // If they registered they should see the survey legend that says how was the registration experience
        WebElement surveyLegend = driver.findElement(By.xpath("//legend[@id='providerSurveyProviderDifficulty-legend']/span"));
        assertThat(surveyLegend.getText()).isEqualTo(getEnMessage("submit-confirmation.new-provider.experience-question"));
        // We should see the notice about contacting CCR&R
        List<WebElement> notices = driver.findElements(By.cssSelector(".notice.notice--gray"));
        assertThat(notices).isNotEmpty();
    }

    @Test
    public void providerRegistrationHomeProviderSelectITIN() {
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
        testPage.enter("providerITIN", "123-45-6789");
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("registration-tax-id-itin.error"))).isTrue();
        testPage.enter("providerITIN", "923-45-6781");
        testPage.clickContinue();

        // registration-home-provider-dob
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-home-provider-dob.title"));
        testPage.enter("providerIdentityCheckDateOfBirthMonth", "12");
        testPage.enter("providerIdentityCheckDateOfBirthYear", "1992");
        testPage.enter("providerIdentityCheckDateOfBirthDay", "25");
        testPage.clickContinue();

        //registration-payment-tax-id
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-payment-tax-id.title"));
        assertThat(testPage.findElementById("providerTaxIdType-ITIN-label")).isNotNull();
        assertThat(testPage.findElementById("providerTaxIdType-FEIN-label")).isNotNull();
        testPage.clickContinue();

        //registration-payment-tax-id
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
        assertThat(testPage.hasErrorText(getEnMessage("errors.select-one-option"))).isNotNull();
        testPage.clickElementById("providerTaxIdType-ITIN-label");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-service-languages.title"));
        testPage.clickContinue();
    }

    @Test
    public void providerRegistrationHomeProviderSelectSSN() {
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
        testPage.enter("providerIdentityCheckSSN", "5555555555");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-home-provider-dob.title"));
        testPage.enter("providerIdentityCheckDateOfBirthMonth", "12");
        testPage.enter("providerIdentityCheckDateOfBirthYear", "1992");
        testPage.enter("providerIdentityCheckDateOfBirthDay", "25");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-payment-tax-id.title"));
        assertThat(testPage.findElementById("providerTaxIdType-SSN-label")).isNotNull();
        assertThat(testPage.findElementById("providerTaxIdType-FEIN-label")).isNotNull();
        testPage.clickElementById("providerTaxIdType-SSN-label");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-service-languages.title"));
        testPage.clickContinue();
    }

    @Test
    void providerRegistrationNonHomeProviderSelectITIN() {
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

        //registration-payment-tax-id
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-payment-tax-id.title"));
        testPage.clickElementById("providerTaxIdType-FEIN-label");
        testPage.clickContinue();

        // registration-tax-id-fein
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id-fein.title"));
        testPage.goBack();

        //registration-payment-tax-id
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-payment-tax-id.title"));
        testPage.clickElementById("providerTaxIdType-SSN-label");
        testPage.clickContinue();

        //registration-tax-id-ssn.title
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id-ssn.title"));
        testPage.goBack();

        // registration-payment-tax-id
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-payment-tax-id.title"));
        testPage.clickElementById("providerTaxIdType-ITIN-label");
        testPage.clickContinue();

        // registration-tax-id-itin
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-tax-id-itin.title"));
        testPage.enter("providerITIN", "1231425");
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("registration-tax-id-itin.error"))).isTrue();
        testPage.enter("providerITIN", "923-45-6781");
        testPage.clickContinue();

        //registration-service-languages
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-service-languages.title"));
        testPage.clickElementById("providerLanguagesOffered-Spanish-label");
        testPage.clickContinue();
    }

}
