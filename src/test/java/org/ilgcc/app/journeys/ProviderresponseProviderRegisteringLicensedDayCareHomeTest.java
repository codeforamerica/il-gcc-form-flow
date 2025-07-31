package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class ProviderresponseProviderRegisteringLicensedDayCareHomeTest extends AbstractBasePageTest {

    String TEST_FILLED_PDF_PATH = "src/test/resources/output/test_filled_ccap_REGISTERING_LICENSED_DAY_CARE_HOME.pdf";

    String FLOW = "providerresponse";

    @AfterEach
    protected void clearSubmissions() {
        super.clearSubmissions();
    }

    @Test
    void fullFlow() throws IOException {
        Submission familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withSubmittedApplicationAndSingleProvider()
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

        // registration-payment-tax-id
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-payment-tax-id.title"));
        testPage.clickElementById("providerTaxIdType-SSN-label");
        testPage.clickContinue();

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
        testPage.enter("providerCareStartDay", "12");
        testPage.enter("providerCareStartMonth", "12");
        testPage.enter("providerCareStartYear", "2025");
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

        // response
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-response.title"));
        assertThat(testPage.findElementTextById("confirmation-code")).contains(familySubmission.getShortCode());
        assertThat(testPage.findElementTextById("parent-name")).contains("parent first parent last");
        assertThat(testPage.findElementTextById("child-name-0")).contains("First Child");
        assertThat(testPage.findElementTextById("child-name-1")).contains("Second Child");
        assertThat(testPage.findElementTextById("child-name-2")).contains("Third Child");
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
        testPage.clickButton(getEnMessage("registration-submit-complete.yes-add-document-now"));

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

        // submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-confirmation.new-provider-registration.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("submit-confirmation.new-provider.header"));

        verifyPDF(TEST_FILLED_PDF_PATH, UNTESTABLE_FIELDS, FLOW);
    }

}
