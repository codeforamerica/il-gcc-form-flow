package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ProviderresponseFlowJourneyTest extends AbstractBasePageTest {

    @Autowired
    SubmissionRepository repository;

    private static String CONF_CODE = "A2123B";

    @Test
    void ProviderresponseJourneyTest_validLink_No_Agreement_To_Care(){
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider().withParentDetails()
            .with("parentPreferredName", "FirstName").withChild("First", "Child", "Yes").withChild("Second", "Child", "No")
            .withChild("NoAssistance", "Child", "No").withConstantChildcareSchedule(0)
            .withSubmittedAtDate(OffsetDateTime.now()).withShortCode(CONF_CODE).build());

        testPage.clickContinue();

        driver.navigate()
            .to("http://localhost:%s/providerresponse/submit/%s?utm_medium=test".formatted(localServerPort, CONF_CODE));

        // submit-start when application is still active
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // provider-number
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-provider-number.title"));
        testPage.enter("providerResponseProviderNumber", "123456789012345");
        testPage.clickContinue();

        // confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
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

        testPage.clickElementById("providerResponseAgreeToCare-false-label");
        testPage.clickContinue();

        //confirm-deny-care
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirm-deny-care.title"));
        testPage.clickButton(getEnMessage("provider-response-confirm-deny-care.confirm-button"));

        //submit-complete-final
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-complete-final.title"));
        assertThat(testPage.elementDoesNotExistById("continue-link")).isFalse();
    }
    @Test
    void ProviderresponseJourneyTest_validLink() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider().withParentDetails()
                .with("parentPreferredName", "FirstName").withChild("First", "Child", "Yes").withChild("Second", "Child", "No")
                .withChild("NoAssistance", "Child", "No").withConstantChildcareSchedule(0)
                .withSubmittedAtDate(OffsetDateTime.now()).withShortCode(CONF_CODE).build());

        testPage.clickContinue();

        driver.navigate()
                .to("http://localhost:%s/providerresponse/submit/%s?utm_medium=test".formatted(localServerPort, CONF_CODE));

        // submit-start when application is still active
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // provider-number
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-provider-number.title"));
        testPage.enter("providerResponseProviderNumber", "123456789012345");
        testPage.clickContinue();

        // confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
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

        //basic-info
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

    @Test
    void ProviderresponseJourneyTest_noLink() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider().withParentDetails()
                .with("parentPreferredName", "FirstName").withChild("First", "Child", "Yes").withChild("Second", "Child", "No")
                .withChild("NoAssistance", "Child", "No").withConstantChildcareSchedule(0)
                .withSubmittedAtDate(OffsetDateTime.now()).withShortCode(CONF_CODE).build());

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/providerresponse/submit".formatted(localServerPort));

        // submit-start when application is still active
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // provider-number
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-provider-number.title"));
        testPage.enter("providerResponseProviderNumber", "123456789012345");
        testPage.clickContinue();

        // confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.enter("providerResponseFamilyShortCode", CONF_CODE);
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

    @Test
    void ProviderresponseJourneyTest_noLink_invalidConfirmationCode() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider().withParentDetails()
                .with("parentPreferredName", "FirstName").withChild("First", "Child", "Yes").withChild("Second", "Child", "No")
                .withChild("NoAssistance", "Child", "No").withConstantChildcareSchedule(0)
                .withSubmittedAtDate(OffsetDateTime.now()).withShortCode(CONF_CODE).build());

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/providerresponse/submit".formatted(localServerPort));

        // submit-start when application is still active
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // provider-number
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-provider-number.title"));
        testPage.enter("providerResponseProviderNumber", "123456789012345");
        testPage.clickContinue();

        // application-id
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.enter("providerResponseFamilyShortCode", "");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-applicant-number"))).isTrue();
        testPage.enter("providerResponseFamilyShortCode", "a2123b");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-applicant-number"))).isTrue();
        testPage.enter("providerResponseFamilyShortCode", "123");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-applicant-number"))).isTrue();
        testPage.enter("providerResponseFamilyShortCode", "ABCDEFGHIJKLMNOP");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-applicant-number"))).isTrue();
    }

    @Test
    void ProviderresponseJourneyTest_noLink_expiredConfirmationCode() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider().withParentDetails()
                .with("parentPreferredName", "FirstName").withChild("First", "Child", "Yes").withChild("Second", "Child", "No")
                .withChild("NoAssistance", "Child", "No").withConstantChildcareSchedule(0)
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(10)).withShortCode(CONF_CODE).build());

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/providerresponse/submit".formatted(localServerPort));

        // submit-start when application is still active
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // provider-number
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-provider-number.title"));
        testPage.enter("providerResponseProviderNumber", "123456789012345");
        testPage.clickContinue();

        // application-id
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.enter("providerResponseFamilyShortCode", CONF_CODE);
        testPage.clickContinue();

        // expired
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        assertThat(testPage.getHeader()).isEqualTo(
                getEnMessageWithParams("provider-response-submit-start.expired.header", new Object[]{"Open Sesame"}));
    }

    @Test
    void ProviderresponseJourneyTest_noLink_alreadyResponded() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        Submission providerSubmission = new Submission();
        providerSubmission.setFlow("providerresponse");
        providerSubmission.setSubmittedAt(OffsetDateTime.now().minusDays(1));
        saveSubmission(providerSubmission);

        Submission familySubmission = new Submission();
        familySubmission.setFlow("gcc");
        familySubmission.setSubmittedAt(OffsetDateTime.now().minusDays(2));
        familySubmission.setShortCode(CONF_CODE);
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("familyIntendedProviderName", "Dev Provider");
        inputData.put("parentFirstName", "Devy");
        inputData.put("parentLastName", "McDeverson");
        inputData.put("providerResponseSubmissionId", providerSubmission.getId());
        familySubmission.setInputData(inputData);
        saveSubmission(familySubmission);

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/providerresponse/submit".formatted(localServerPort));

        // submit-start when application is still active
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // provider-number
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-provider-number.title"));
        testPage.enter("providerResponseProviderNumber", "123456789012345");
        testPage.clickContinue();

        // application-id
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.enter("providerResponseFamilyShortCode", CONF_CODE);
        testPage.clickContinue();

        // response
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        assertThat(testPage.getHeader()).isEqualTo(
                getEnMessageWithParams("provider-response-submit-start.responded.header", new Object[]{"Dev Provider"}));
    }
}
