package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;

public class AddressJourneyTest extends AbstractBasePageTest {
    @Test
    void shouldDisplayDifferentHomeAndMailingAddressIfParentMailingIsNotSameAsHomeAddress() {
        // parent-contact-info
        testPage.navigateToFlowScreen("gcc/parent-contact-info");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .withHomeAddress("Home Street", "10","Home_city", "IL", "11111")
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .build());
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-contact-info.title"));
        testPage.enter("parentContactEmail", "test@email.org");
        testPage.clickContinue();
        // parent-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-review.title"));
        assertThat(testPage.getElementText("mailing-street-address-1")).isEqualTo("972 Mission St");
        assertThat(testPage.getElementText("home-street-address-1")).isEqualTo("Home Street");
    }
    @Test
    void shouldDisplayDifferentValidatedMailingAddressAndHomeAddressIfParentMailingIsNotSameAsHomeAddress(){
        // parent-contact-info
        testPage.navigateToFlowScreen("gcc/parent-contact-info");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .with("useSuggestedParentAddress", "true")
            .withHomeAddress("Home Street", "10","Home_city", "IL", "11111")
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .withValidatedMailingAddress("972 Mission St [Validated]", "5 [Validated]", "San Francisco [Validated]", "CA [Validated]", "94103 [Validated]")
            .build());
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-contact-info.title"));
        testPage.enter("parentContactEmail", "test@email.org");
        testPage.clickContinue();
        // parent-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-review.title"));
        assertThat(testPage.getElementText("mailing-street-address-1")).isEqualTo("972 Mission St [Validated]");
        assertThat(testPage.getElementText("home-street-address-1")).isEqualTo("Home Street");
    }
    @Test
    void shouldDisplaySameValidatedMailingAddressAndHomeAddressIfParentMailingSameAsHomeAddress(){
        // parent-contact-info
        testPage.navigateToFlowScreen("gcc/parent-contact-info");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("yes"))
            .with("useSuggestedParentAddress", "true")
            .withHomeAddress("Home Street", "10","Home_city", "IL", "11111")
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .withValidatedMailingAddress("972 Mission St [Validated]", "5 [Validated]", "San Francisco [Validated]", "CA [Validated]", "94103 [Validated]")
            .build());
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-contact-info.title"));
        testPage.enter("parentContactEmail", "test@email.org");
        testPage.clickContinue();
        // parent-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-review.title"));
        assertThat(testPage.getElementText("mailing-street-address-1")).isEqualTo("972 Mission St [Validated]");
        assertThat(testPage.getElementText("home-street-address-1")).isEqualTo("972 Mission St [Validated]");
    }

}
