package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.SubmissionRepository;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ProviderresponseFlowJourneyTest extends AbstractBasePageTest {

    @Autowired
    SubmissionRepository repository;

    private static String CONF_CODE="A2123B";

    @Test
    void ProviderresponseJourneyTest() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .with("parentPreferredName", "FirstName")
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "No")
                .withChild("NoAssistance", "Child", "No")
                .withConstantChildcareSchedule(0)
                .withSubmittedAtDate(OffsetDateTime.now())
                .withShortCode(CONF_CODE)
                .build());

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/providerresponse/submit/%s?utm_medium=test".formatted(localServerPort, CONF_CODE));

        // submit-start when application is still active
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // ccap-registration
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-ccap-registration.title"));
        testPage.enter("providerResponseProviderNumber", "123456789012345");
        testPage.clickContinue();

        // application-id
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-application-id.title"));
        testPage.findElementTextById("providerResponseFamilyShortCode").equals(CONF_CODE);
        testPage.clickContinue();

        // response
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-response.title"));
        assertThat(testPage.findElementTextById("confirmation-code")).isEqualTo(CONF_CODE);
        assertThat(testPage.findElementTextById("parent-name")).isEqualTo("FirstName parent last");

        assertThat(testPage.findElementTextById("child-name-0")).isEqualTo("First Child");
        assertThat(testPage.findElementTextById("child-age-0")).isEqualTo("Age 22");
        assertThat(testPage.findElementTextById("child-schedule-0")).isNotNull();
        assertThat(testPage.findElementTextById("child-start-0")).isEqualTo("01/10/2025");

        assertThat(testPage.elementDoesNotExistById("child-name-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("child-name-2")).isTrue();

        testPage.clickElementById("providerResponseAgreeToCare-true-label");
        testPage.clickContinue();

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-complete.title"));
        assertThat(testPage.findElementsByClass("notice").get(0).getText()).isEqualTo(getEnMessage("provider-response-submit-complete.notice-yes"));

        testPage.goBack();

        // response
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-response.title"));
        testPage.clickElementById("providerResponseAgreeToCare-true-label");
        testPage.clickContinue();

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-complete.title"));
        assertThat(testPage.findElementsByClass("notice").get(0).getText()).isEqualTo(getEnMessage("provider-response-submit-complete.notice-yes"));
        testPage.clickContinue();

        //basic-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info.title"));
        testPage.enter("providerResponseBusinessName", "Business Name");
        testPage.enter("providerResponseFirstName", "First Name");
        testPage.enter("providerResponseLastName", "Last Name");
        testPage.enter("providerResponseServiceStreetAddress1", "123 Main St");
        testPage.enter("providerResponseServiceCity", "City");
        testPage.selectFromDropdown("providerResponseServiceState", "IL - Illinois");
        testPage.enter("providerResponseServiceZipCode", "12345");
        testPage.clickButton("Continue");

        // confirm-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirm-address.title"));
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
        assertThat(testPage.findElementTextById("address-1")).isEqualTo("123 Main St");
        assertThat(testPage.findElementTextById("city-state")).isEqualTo("City, IL");
        assertThat(testPage.findElementTextById("zipcode")).isEqualTo("12345");
        assertThat(testPage.findElementTextById("phone")).isEqualTo("(555) 555-5555");
        assertThat(testPage.findElementTextById("email")).isEqualTo("foo@bar.com");
        testPage.clickButton("Continue");

        //submit-complete-final
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-complete-final.title"));
        testPage.goBack();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-complete-final.title"));
        testPage.findElementById("respond-to-another-app-button").click();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));

    }

}
