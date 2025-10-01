package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class ProviderresponseProviderRegisteringLicenseExemptInHomeJourneyTest extends AbstractBasePageTest {

    String TEST_FILLED_PDF_PATH = "src/test/resources/output/test_filled_ccap_REGISTERING_LICENSE_EXEMPT_HOME.pdf";

    String FLOW = "providerresponse";

    @AfterEach
    protected void clearSubmissions() {
        super.clearSubmissions();
    }

    @Test
    void fullFlow() throws IOException {
        Submission familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1, child_2, child_3),
                        List.of(programProvider, programProvider, programProvider))
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(2))
                .with("providerApplicationResponseStatus", SubmissionStatus.ACTIVE.name())
                .withShortCode("ABC123")
                .build());

        driver.navigate().to("http://localhost:%s/s".formatted(localServerPort));

        // submit-start
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.enter("providerResponseFamilyShortCode", familySubmission.getShortCode());
        testPage.clickContinue();

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

        //registration-service-languages
        testPage.clickElementById("providerLanguagesOffered-other");
        testPage.enter("providerLanguagesOffered_other", "Test");
        testPage.clickContinue();

        // registration-start-date
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-start-date.title"));
        testPage.enter("providerCareStartDay", "11");
        testPage.enter("providerCareStartMonth", "3");
        testPage.enter("providerCareStartYear", "2025");
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
        testPage.enter("providerHouseholdMemberDateOfBirthDay", "10");
        testPage.enter("providerHouseholdMemberSSN", "333");
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
        testPage.clickElementById("done-adding-provider-household-member");

        // registration-family-response-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-family-response-intro.title"));
        testPage.clickContinue();

        // response
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-response.title"));
        assertThat(testPage.findElementTextById("confirmation-code")).contains(familySubmission.getShortCode());
        assertThat(testPage.findElementTextById("parent-name")).contains("parent first parent last");
        assertThat(testPage.findElementTextById("child-name-0")).contains("childFirst childLast");
        assertThat(testPage.findElementTextById("child-name-1")).contains("childSecond childLast");
        assertThat(testPage.findElementTextById("child-name-2")).contains("childThird childLast");
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
        testPage.clickContinue();

        // submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-confirmation.new-provider-registration.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("submit-confirmation.new-provider.header"));

        verifyPDF(TEST_FILLED_PDF_PATH, UNTESTABLE_FIELDS, FLOW);
    }

}
