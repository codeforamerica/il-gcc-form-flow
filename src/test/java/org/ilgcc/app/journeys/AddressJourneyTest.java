package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ilgcc.app.data.FakeResourceOrganization.ACTIVE_FOUR_C_COUNTY;

import java.util.List;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.ilgcc.app.utils.CountyOption;
import org.ilgcc.app.utils.ZipcodeOption;
import org.junit.jupiter.api.Test;

public class AddressJourneyTest extends AbstractBasePageTest {

    @Test
    void whenApplicantIsUnhousedAndHasMailingAddress(){
        testPage.navigateToFlowScreen("gcc/parent-info-disability");
        saveSubmission(getSessionSubmissionTestBuilder()
                .withParentBasicInfo()
                .with("familyIntendedProviderName", "ACME Daycare")
                .with("applicationCounty", CountyOption.LEE.getLabel())
                .build());
        testPage.clickYes();

        //parent-home-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-home-address.title"));
        testPage.clickElementById("parentHomeExperiencingHomelessness-yes");
        testPage.clickContinue();

        //parent-home-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-no-permanent-address.title"));
        testPage.clickButton(getEnMessage("parent-no-permanent-address.has-place-to-get-mail"));

        // parent-mailing-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-mailing-address.title"));

        // confirms that this should not exist when a person is unhoused
        assertThat(testPage.elementDoesNotExistById("parentMailingAddressSameAsHomeAddress-yes")).isTrue();

        testPage.enter("parentMailingStreetAddress1", "123 Main St");
        testPage.enter("parentMailingStreetAddress2", "5th floor");
        testPage.enter("parentMailingCity", "Sycamore");
        testPage.selectFromDropdown("parentMailingState", getEnMessage("state.il"));
        testPage.enter("parentMailingZipCode", "60001");
        testPage.clickContinue();

        // parent-confirm-address
        // Todo mock the address validation response so something actually comes back
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-confirm-address.title"));
        testPage.clickButton(getEnMessage("address-validation.button.use-this-address"));
        // parent-comm-preference
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-comm-preference.title"));
        testPage.selectRadio("parentContactPreferredCommunicationMethod", "email");
        testPage.clickContinue();
        // parent-contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-contact-info.title"));
        testPage.enter("parentContactEmail", "test@email.org");
        testPage.clickContinue();

        //parent-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-review.title"));
        assertThat(testPage.getElementText("no-mailing-address")).isEqualTo(getEnMessage("general.none-added"));
        assertThat(testPage.elementDoesNotExistById("mailing-street-address-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("mailing-street-address-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("mailing-city-state")).isTrue();
        assertThat(testPage.elementDoesNotExistById("mailing-zipcode")).isTrue();
        assertThat(testPage.getElementText("mailing-street-address-1")).isEqualTo("123 Main St");
        assertThat(testPage.getElementText("mailing-street-address-2")).isEqualTo("5th floor");
        assertThat(testPage.getElementText("mailing-city-state")).isEqualTo("Sycamore, IL");
        assertThat(testPage.getElementText("mailing-zipcode")).isEqualTo("60001");
    }


    @Test
    void whenApplicantIsUnhousedAndDoesNotHaveMailingAddress(){
        return;
    }

    @Test
    void whenApplicantAcceptsHomeAddressValidationAndMailingAddressIsSame(){
        return;
    }


    @Test
    void whenApplicantAcceptsHomeAddressValidationAndMailingAddressIsDifferent(){
        return;
    }

    @Test
    void whenApplicantRejectsHomeAddressValidationAndMailingAddressIsSame(){
        return;
    }

    @Test
    void whenApplicantRejectsHomeAddressValidationAndMailingAddressIsDifferent(){
        return;
    }
    

}
