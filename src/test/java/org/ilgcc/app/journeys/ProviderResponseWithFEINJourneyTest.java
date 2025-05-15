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
import org.springframework.test.context.TestPropertySource;

@Slf4j
@TestPropertySource(properties = {"il-gcc.enable-provider-response-with-fein=true"})
public class ProviderResponseWithFEINJourneyTest extends AbstractBasePageTest {

    @AfterEach
    protected void clearSubmissions() {
        super.clearSubmissions();
    }

    @Test
    public void whenAProviderRespondsNoToProvidingFEINOrProviderNumber(){
        existingProviderBasicFlow();

        //fein
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-fein.title"));
        assertThat(testPage.getElementText("not-sure-hasFEIN-link")).isEqualTo(getEnMessage("general.skip.im-not-sure"));
        testPage.clickNo();

        //confirm-application-submission-no-response
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("confirm-application-submission-no-response.title"));
        testPage.clickButton("Submit application");

        //submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-complete-final.title"));
        // Assert shows correct header language when the provider did not provide any way to identify them
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("submit-confirmation.denied-care-or-unidentifiable.header"));
        // They did not register and should see the provider experience version of the survey language
        WebElement surveyLegend = driver.findElement(By.xpath("//legend[@id='providerSurveyProviderDifficulty-legend']/span"));
        assertThat(surveyLegend.getText()).isEqualTo(getEnMessage("submit-confirmation.existing-provider.experience-question"));
        List<WebElement> notices = driver.findElements(By.cssSelector(".notice.notice--gray"));
        assertThat(notices).isNotEmpty();
    }

    public void existingProviderBasicFlow() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        Submission s = submissionRepositoryService.save(getSessionSubmissionTestBuilder().withDayCareProvider().withParentDetails()
            .with("parentPreferredName", "FirstName").withChild("First", "Child", "true")
            .withChild("Second", "Child", "false").withChild("NoAssistance", "Child", "No").withConstantChildcareSchedule(0)
            .with("earliestChildcareStartDate", "10/10/2011").withSubmittedAtDate(OffsetDateTime.now()).build());

        submissionRepositoryService.generateAndSetUniqueShortCode(s);

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/s/%s".formatted(localServerPort, s.getShortCode()));

        // submit-start
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        assertThat(testPage.findElementById("providerResponseFamilyShortCode").getAttribute("value")).isEqualTo(s.getShortCode());
        testPage.clickContinue();

        // paid-by-ccap
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("paid-by-ccap.title"));
        testPage.clickYes();

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

        // confirm-service-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("confirm-address.title"));
        testPage.clickButton("Use this address");

        //mailing-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response.mailing-address.title"));
        testPage.clickElementById("providerMailingAddressSameAsServiceAddress-yes");
        testPage.clickContinue();

        //confirm-mailing-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("confirm-address.title"));
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
        assertThat(testPage.findElementTextById("provider-service-street-address-1")).isEqualTo("123 Main St");
        assertThat(testPage.findElementTextById("provider-service-city-state")).isEqualTo("City, IL");
        assertThat(testPage.findElementTextById("provider-service-zipcode")).isEqualTo("12345");
        assertThat(testPage.findElementTextById("phone")).isEqualTo("(555) 555-5555");
        assertThat(testPage.findElementTextById("email")).isEqualTo("foo@bar.com");
        testPage.clickButton("Continue");

        //provider-number
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-provider-number.title"));
        testPage.enter("providerResponseProviderNumber", "12345678901");
        assertThat(testPage.getElementText("skip-to-fein")).isEqualTo(getEnMessage("provider-response-provider-number.skip-text"));
        testPage.clickElementById("skip-to-fein");
    }
}
