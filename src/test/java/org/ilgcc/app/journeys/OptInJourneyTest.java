package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;

public class OptInJourneyTest extends AbstractBasePageTest {

    @Test
    void shouldPreselectEmailWhenParentExperiencingHomelessnessClicksContactByEmailWithoutExistingSelectedPreference() {
        // Activities Screen
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
        testPage.clickLink(getEnMessage("parent-no-permanent-address.contact-by-email"));
        //parent-comm-preference
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-comm-preference.title"));
        assertThat(testPage.findElementById("parentContactPreferredCommunicationMethod-email").getAttribute("checked")).isEqualTo("true");
        testPage.selectRadio("parentContactPreferredCommunicationMethod", "mail");
    }
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
        testPage.clickLink(getEnMessage("parent-no-permanent-address.contact-by-email"));
        //parent-comm-preference
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-comm-preference.title"));
        assertThat(testPage.findElementById("parentContactPreferredCommunicationMethod-email").getAttribute("checked")).isEqualTo("true");
        testPage.clickElementById("parentContactPreferredCommunicationMethod-mail");
        testPage.clickContinue();
        //parent-contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-contact-info.title"));
        testPage.goBack();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-comm-preference.title"));
        assertThat(testPage.findElementById("parentContactPreferredCommunicationMethod-mail").getAttribute("checked")).isEqualTo("true");
        testPage.goBack();
        //parent-no-permanent-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-no-permanent-address.title"));
        testPage.clickLink(getEnMessage("parent-no-permanent-address.contact-by-email"));
        assertThat(testPage.findElementById("parentContactPreferredCommunicationMethod-mail").getAttribute("checked")).isEqualTo("true");



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
        testPage.clickLink(getEnMessage("parent-no-permanent-address.has-place-to-get-mail"));
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
