package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;

public class OptInJourneyTest extends AbstractBasePageTest {

    @Test
    void shouldSetPreferredCommunicationToUserInputAfterUserSelectsAPreferredCommunicationMethod(){
        testPage.navigateToFlowScreen("gcc/parent-home-address");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withHomelessDetails()
            .build());

        //parent-home-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-home-address.title"));
        testPage.clickElementById("parentHomeExperiencingHomelessness-yes");
        testPage.clickContinue();
        //parent-no-permanent-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-no-permanent-address.title"));
        testPage.clickContinue();

        // parent-mailing-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-mailing-address.title"));

        // confirms that this should not exist when a person is unhoused
        assertThat(testPage.elementDoesNotExistById("parentMailingAddressSameAsHomeAddress-yes")).isTrue();

        testPage.enter("parentMailingStreetAddress1", "110 E Sycamore St");
        testPage.enter("parentMailingStreetAddress2", "#1010");
        testPage.enter("parentMailingCity", "Sycamore");
        testPage.selectFromDropdown("parentMailingState", "IL - Illinois");
        testPage.enter("parentMailingZipCode", "60002");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-confirm-address.title"));
        testPage.clickButton(getEnMessage("address-validation.button.use-this-address"));

        //parent-comm-preference
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-comm-preference.title"));
        assertThat(testPage.findElementById("parentContactPreferredCommunicationMethod-mail").getAttribute("checked")).isEqualTo(null);
        assertThat(testPage.findElementById("parentContactPreferredCommunicationMethod-email").getAttribute("checked")).isEqualTo(null);
        testPage.clickElementById("parentContactPreferredCommunicationMethod-email");
        testPage.clickContinue();
        //parent-contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-contact-info.title"));
        testPage.goBack();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-comm-preference.title"));
        assertThat(testPage.findElementById("parentContactPreferredCommunicationMethod-email").getAttribute("checked")).isEqualTo("true");
        testPage.goBack();
        //parent-no-permanent-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-confirm-address.title"));
        testPage.clickButton(getEnMessage("address-validation.button.use-this-address"));
        assertThat(testPage.findElementById("parentContactPreferredCommunicationMethod-email").getAttribute("checked")).isEqualTo("true");



    }
    @Test
    void shouldSelectNothingIfAParentExperiencingHomelessnessDoesNotSelectContactByEmail(){
        testPage.navigateToFlowScreen("gcc/parent-home-address");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withHomelessDetails()
            .build());
        //parent-home-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-home-address.title"));
        testPage.clickElementById("parentHomeExperiencingHomelessness-yes");
        testPage.clickContinue();
        //parent-no-permanent-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-no-permanent-address.title"));
        testPage.clickContinue();
        //parent-mailing-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-mailing-address.title"));
        testPage.enter("parentMailingStreetAddress1", "972 Mission St");
        testPage.enter("parentMailingStreetAddress2", "5th floor");
        testPage.enter("parentMailingCity", "San Francisco");
        testPage.selectFromDropdown("parentMailingState", "CA - California");
        testPage.enter("parentMailingZipCode", "94103");
        testPage.clickContinue();
        //parent-confirm-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-confirm-address.title"));
        testPage.clickButton("Use this address");
        //parent-comm-preference
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-comm-preference.title"));
        assertThat(testPage.findElementById("parentContactPreferredCommunicationMethod-mail").getAttribute("checked")).isEqualTo(null);
    }
}
