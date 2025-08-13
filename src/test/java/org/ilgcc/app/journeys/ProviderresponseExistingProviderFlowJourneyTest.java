package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.CURRENT_APPROVED_PROVIDER;

import formflow.library.data.Submission;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class ProviderresponseExistingProviderFlowJourneyTest extends AbstractBasePageTest {

    String TEST_FILLED_PDF_PATH = "src/test/resources/output/test_filled_ccap_EXISTING_PROVIDER_RESPONSE.pdf";

    String FLOW = "providerresponse";
    @AfterEach
    protected void clearSubmissions() {
        super.clearSubmissions();
    }


    @Test
    void fullExistingProviderResponseFlow() throws IOException {
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
        testPage.clickYes();

        //provider-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info.title"));
        testPage.enter("providerResponseBusinessName", "ProviderSupplied Business Name");
        testPage.enter("providerResponseFirstName", "ProviderSuppliedFirst");
        testPage.enter("providerResponseLastName", "ProviderSuppliedLast");
        testPage.clickButton("Continue");

        // service-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-provider-service-address.title"));
        testPage.enter("providerResponseServiceStreetAddress1", "123 Main St");
        testPage.enter("providerResponseServiceCity", "City");
        testPage.selectFromDropdown("providerResponseServiceState", "IL - Illinois");
        testPage.enter("providerResponseServiceZipCode", "60013");
        testPage.clickButton("Continue");

        // confirm-service-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("confirm-address.title"));
        testPage.clickButton("Use this address");

        // contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-contact-info.title"));
        testPage.enter("providerResponseContactPhoneNumber", "5555555555");
        testPage.enter("providerResponseContactEmail", "foo@bar.com");
        testPage.clickButton("Continue");

        // info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info-review.title"));
        assertThat(testPage.findElementTextById("business-name")).isEqualTo("ProviderSupplied Business Name");
        assertThat(testPage.findElementTextById("full-name")).isEqualTo("ProviderSuppliedFirst ProviderSuppliedLast");
        assertThat(testPage.findElementTextById("provider-service-street-address-1")).isEqualTo("123 Main St");
        assertThat(testPage.findElementTextById("provider-service-city-state")).isEqualTo("City, IL");
        assertThat(testPage.findElementTextById("provider-service-zipcode")).isEqualTo("60013");
        assertThat(testPage.findElementTextById("phone")).isEqualTo("(555) 555-5555");
        assertThat(testPage.findElementTextById("email")).isEqualTo("foo@bar.com");
        testPage.clickButton("Continue");

        // provider-number
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-provider-number.title"));
        testPage.enter("providerResponseProviderNumber", CURRENT_APPROVED_PROVIDER.getProviderId().toString());
        testPage.clickContinue();

        // response
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-response.title"));
        assertThat(testPage.findElementTextById("confirmation-code")).contains(familySubmission.getShortCode());
        assertThat(testPage.findElementTextById("parent-name")).contains("parent first parent last");
        assertThat(testPage.findElementTextById("child-name-0")).contains("First Child");
        assertThat(testPage.findElementTextById("child-name-1")).contains("Second Child");
        assertThat(testPage.findElementTextById("child-name-2")).contains("Third Child");
        assertThat(testPage.elementDoesNotExistById("child-name-3")).isTrue();

        testPage.clickElementById("providerResponseAgreeToCare-true-label");
        testPage.clickButton("Submit");

        //submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-confirmation.title"));
        testPage.goBack();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-confirmation.title"));

        verifyPDF(TEST_FILLED_PDF_PATH, UNTESTABLE_FIELDS, FLOW);
    }

}
