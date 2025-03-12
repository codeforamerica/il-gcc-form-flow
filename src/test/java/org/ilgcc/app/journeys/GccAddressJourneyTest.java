package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.smartystreets.api.exceptions.SmartyException;
import formflow.library.address_validation.AddressValidationService;
import formflow.library.address_validation.ValidatedAddress;
import formflow.library.data.FormSubmission;
import java.io.IOException;
import java.util.Map;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public class GccAddressJourneyTest extends AbstractBasePageTest {

    private final static String STREET_ADDRESS_LINE_1 = "110 E Sycamore St";
    private final static String STREET_ADDRESS_LINE_2 = "#1010";
    private final static String CITY = "Sycamore";
    private final static String ZIP_CODE = "60168";

    private final static String VALIDATED_STREET_ADDRESS_LINE_1 = "110 E Sycamore Street, UNIT 1010";
    private final static String VALIDATED_CITY = "Sycamore";
    private final static String VALIDATED_STATE = "IL";
    private final static String VALIDATED_ZIP_CODE = "60178";

    private final static String COUNTY_LEE_LABEL = "LEE";
    @MockitoBean
    AddressValidationService addressValidationService;

    @Test
    void whenNoHomeAddressAndHasMailingAddress() throws SmartyException, IOException, InterruptedException {
        testPage.navigateToFlowScreen("gcc/parent-info-disability");

        saveSubmission(getSessionSubmissionTestBuilder()
                .withParentBasicInfo()
                .with("familyIntendedProviderName", "ACME Daycare")
                .with("applicationCounty", COUNTY_LEE_LABEL)
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

        testPage.enter("parentMailingStreetAddress1", STREET_ADDRESS_LINE_1);
        testPage.enter("parentMailingStreetAddress2", STREET_ADDRESS_LINE_2);
        testPage.enter("parentMailingCity", CITY);
        testPage.selectFromDropdown("parentMailingState", getEnMessage("state.il"));
        testPage.enter("parentMailingZipCode", ZIP_CODE);

        // Ensures there is an address returned in address validation
        when(addressValidationService.validate(ArgumentMatchers.any(FormSubmission.class))).thenReturn(
                validatedAddressReturnValue("parentMailing", true));
        testPage.clickContinue();

        // parent-confirm-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-confirm-address.title"));
        assertThat(testPage.elementDoesNotExistById("original-address-label")).isFalse();
        assertThat(testPage.elementDoesNotExistById("validated-address-label")).isFalse();
        assertThat(testPage.findElementById("validated-address").isSelected()).isTrue();
        // this selects the suggested address
        testPage.clickContinue();

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
        assertThat(testPage.getElementText("no-home-address")).isEqualTo(getEnMessage("general.none-added"));
        assertThat(testPage.elementDoesNotExistById("home-street-address-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("home-street-address-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("home-city-state")).isTrue();
        assertThat(testPage.elementDoesNotExistById("home-zipcode")).isTrue();
        assertThat(testPage.elementDoesNotExistById("no-mailing-address")).isTrue();
        assertThat(testPage.getElementText("mailing-street-address-1")).isEqualTo(VALIDATED_STREET_ADDRESS_LINE_1);
        assertThat(testPage.elementDoesNotExistById("mailing-street-address-2")).isTrue();
        assertThat(testPage.getElementText("mailing-city-state")).isEqualTo(VALIDATED_CITY + ", " + VALIDATED_STATE);
        assertThat(testPage.getElementText("mailing-zipcode")).isEqualTo(VALIDATED_ZIP_CODE);

        // Data in mailing address screen matches the selected validated data
        testPage.clickElementById("edit-mailing-address");
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-mailing-address.title"));

        assertThat(testPage.getInputValue("parentMailingStreetAddress1")).isEqualTo(VALIDATED_STREET_ADDRESS_LINE_1);
        assertThat(testPage.getInputValue("parentMailingStreetAddress2")).isEqualTo("");
        assertThat(testPage.getInputValue("parentMailingCity")).isEqualTo(VALIDATED_CITY);
        assertThat(testPage.getSelectValue("parentMailingState")).isEqualTo(getEnMessage("state.il"));
        assertThat(testPage.getInputValue("parentMailingZipCode")).isEqualTo(VALIDATED_ZIP_CODE);
    }

    @Test
    void whenNoHomeAddressAndDoesNotHaveMailingAddress() {
        testPage.navigateToFlowScreen("gcc/parent-info-disability");

        saveSubmission(getSessionSubmissionTestBuilder()
                .withParentBasicInfo()
                .with("familyIntendedProviderName", "ACME Daycare")
                .with("applicationCounty", COUNTY_LEE_LABEL)
                .build());

        testPage.clickYes();

        //parent-home-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-home-address.title"));
        testPage.clickElementById("parentHomeExperiencingHomelessness-yes");
        testPage.clickContinue();

        //parent-home-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-no-permanent-address.title"));
        testPage.clickLink(getEnMessage("parent-no-permanent-address.contact-by-email"));

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
        assertThat(testPage.getElementText("no-home-address")).isEqualTo(getEnMessage("general.none-added"));
        assertThat(testPage.elementDoesNotExistById("home-street-address-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("home-street-address-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("home-city-state")).isTrue();
        assertThat(testPage.elementDoesNotExistById("home-zipcode")).isTrue();
        assertThat(testPage.getElementText("no-mailing-address")).isEqualTo(getEnMessage("general.none-added"));
        assertThat(testPage.elementDoesNotExistById("mailing-street-address-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("mailing-street-address-2")).isTrue();
        assertThat(testPage.elementDoesNotExistById("mailing-city-state")).isTrue();
        assertThat(testPage.elementDoesNotExistById("mailing-zipcode")).isTrue();
    }

    @Test
    void whenAHomeAddressValidationAcceptedAndMailingAddressIsSame()
            throws SmartyException, IOException, InterruptedException {
        testPage.navigateToFlowScreen("gcc/parent-info-disability");

        saveSubmission(getSessionSubmissionTestBuilder()
                .withParentBasicInfo()
                .with("familyIntendedProviderName", "ACME Daycare")
                .with("applicationCounty", COUNTY_LEE_LABEL)
                .build());

        testPage.clickYes();

        //parent-home-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-home-address.title"));
        testPage.enter("parentHomeStreetAddress1", STREET_ADDRESS_LINE_1);
        testPage.enter("parentHomeStreetAddress2", STREET_ADDRESS_LINE_2);
        testPage.enter("parentHomeCity", CITY);
        testPage.selectFromDropdown("parentHomeState", getEnMessage("state.il"));
        testPage.enter("parentHomeZipCode", ZIP_CODE);

        // Mocks a successful address validationEnsures there is an address returned in address validation
        when(addressValidationService.validate(ArgumentMatchers.any(FormSubmission.class))).thenReturn(
                validatedAddressReturnValue("parentHome", true));
        testPage.clickContinue();

        // confirm-parent-home-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("confirm-address.title"));
        assertThat(testPage.elementDoesNotExistById("original-address-label")).isFalse();
        assertThat(testPage.elementDoesNotExistById("validated-address-label")).isFalse();
        assertThat(testPage.findElementById("validated-address").isSelected()).isTrue();
        testPage.clickContinue();

        // parent-mailing-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-mailing-address.title"));
        assertThat(testPage.elementDoesNotExistById("parentMailingAddressSameAsHomeAddress-yes")).isFalse();
        testPage.clickElementById("parentMailingAddressSameAsHomeAddress-yes");

        // Mocks a successful addressValidationService.validate for parentMailing
        when(addressValidationService.validate(ArgumentMatchers.any(FormSubmission.class))).thenReturn(
                validatedAddressReturnValue("parentMailing", true));
        testPage.clickContinue();

        // skips parent-confirm-address

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
        assertThat(testPage.elementDoesNotExistById("no-home-address")).isTrue();
        assertThat(testPage.getElementText("home-street-address-1")).isEqualTo(VALIDATED_STREET_ADDRESS_LINE_1);
        // Validated addresses never have a second line since it all gets combined during validation
        assertThat(testPage.elementDoesNotExistById("home-street-address-2")).isTrue();
        assertThat(testPage.getElementText("home-city-state")).isEqualTo(VALIDATED_CITY + ", " + VALIDATED_STATE);
        assertThat(testPage.getElementText("home-zipcode")).isEqualTo(VALIDATED_ZIP_CODE);
        assertThat(testPage.elementDoesNotExistById("no-mailing-address")).isTrue();
        assertThat(testPage.getElementText("mailing-street-address-1")).isEqualTo(VALIDATED_STREET_ADDRESS_LINE_1);
        assertThat(testPage.elementDoesNotExistById("mailing-street-address-2")).isTrue();
        assertThat(testPage.getElementText("mailing-city-state")).isEqualTo(VALIDATED_CITY + ", " + VALIDATED_STATE);
        assertThat(testPage.getElementText("mailing-zipcode")).isEqualTo(VALIDATED_ZIP_CODE);

        // Data in mailing address screen matches the selected validated data
        testPage.clickElementById("edit-home-address");
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-home-address.title"));

        assertThat(testPage.getInputValue("parentHomeStreetAddress1")).isEqualTo(VALIDATED_STREET_ADDRESS_LINE_1);
        assertThat(testPage.getInputValue("parentHomeStreetAddress2")).isEqualTo("");
        assertThat(testPage.getInputValue("parentHomeCity")).isEqualTo(VALIDATED_CITY);
        assertThat(testPage.getSelectValue("parentHomeState")).isEqualTo(getEnMessage("state.il"));
        assertThat(testPage.getInputValue("parentHomeZipCode")).isEqualTo(VALIDATED_ZIP_CODE);

        testPage.goBack();

        //parent-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-review.title"));
        // Data in mailing address screen matches the selected validated data
        testPage.clickElementById("edit-mailing-address");
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-mailing-address.title"));
        assertThat(testPage.elementDoesNotExistById("parentMailingAddressSameAsHomeAddress-yes")).isFalse();
        assertThat(testPage.findElementById("parentMailingAddressSameAsHomeAddress-yes").isSelected()).isTrue();

        // unselect parentMailingAddressSameAsHomeAddress to see the data
        testPage.clickElementById("parentMailingAddressSameAsHomeAddress-yes");

        assertThat(testPage.getInputValue("parentMailingStreetAddress1")).isEqualTo(VALIDATED_STREET_ADDRESS_LINE_1);
        assertThat(testPage.getInputValue("parentMailingStreetAddress2")).isEqualTo("");
        assertThat(testPage.getInputValue("parentMailingCity")).isEqualTo(VALIDATED_CITY);
        assertThat(testPage.getSelectValue("parentMailingState")).isEqualTo(getEnMessage("state.il"));
        assertThat(testPage.getInputValue("parentMailingZipCode")).isEqualTo(VALIDATED_ZIP_CODE);
    }

    @Test
    void whenHomeAddressValidationIsRejectedAndMailingAddressIsSame()
            throws SmartyException, IOException, InterruptedException {
        testPage.navigateToFlowScreen("gcc/parent-info-disability");

        saveSubmission(getSessionSubmissionTestBuilder()
                .withParentBasicInfo()
                .with("familyIntendedProviderName", "ACME Daycare")
                .with("applicationCounty", COUNTY_LEE_LABEL)
                .build());

        testPage.clickYes();

        //parent-home-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-home-address.title"));
        testPage.enter("parentHomeStreetAddress1", STREET_ADDRESS_LINE_1);
        testPage.enter("parentHomeStreetAddress2", STREET_ADDRESS_LINE_2);
        testPage.enter("parentHomeCity", CITY);
        testPage.selectFromDropdown("parentHomeState", getEnMessage("state.il"));
        testPage.enter("parentHomeZipCode", ZIP_CODE);

        // Mocks a successful addressValidationService.validate for parentHome
        when(addressValidationService.validate(ArgumentMatchers.any(FormSubmission.class))).thenReturn(
                validatedAddressReturnValue("parentHome", true));
        testPage.clickContinue();

        // confirm-parent-home-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("confirm-address.title"));
        assertThat(testPage.elementDoesNotExistById("original-address-label")).isFalse();
        assertThat(testPage.elementDoesNotExistById("validated-address-label")).isFalse();
        testPage.clickElementById("original-address-label");
        assertThat(testPage.findElementById("validated-address").isSelected()).isFalse();
        testPage.clickContinue();

        // parent-mailing-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-mailing-address.title"));
        assertThat(testPage.elementDoesNotExistById("parentMailingAddressSameAsHomeAddress-yes")).isFalse();
        testPage.clickElementById("parentMailingAddressSameAsHomeAddress-yes");

        // Mocks a successful addressValidationService.validate for parentMailing
        when(addressValidationService.validate(ArgumentMatchers.any(FormSubmission.class))).thenReturn(
                validatedAddressReturnValue("parentMailing", true));
        testPage.clickContinue();

        // confirm-parent-mailing-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-confirm-address.title"));
        assertThat(testPage.elementDoesNotExistById("original-address-label")).isFalse();
        assertThat(testPage.elementDoesNotExistById("validated-address-label")).isFalse();
        assertThat(testPage.findElementById("validated-address").isSelected()).isTrue();
        // this selects the suggested address
        testPage.clickContinue();

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
        assertThat(testPage.elementDoesNotExistById("no-home-address")).isTrue();
        // home address is the unvalidated version
        assertThat(testPage.getElementText("home-street-address-1")).isEqualTo(STREET_ADDRESS_LINE_1);
        assertThat(testPage.getElementText("home-street-address-2")).isEqualTo(STREET_ADDRESS_LINE_2);
        assertThat(testPage.getElementText("home-city-state")).isEqualTo(CITY + ", IL");
        assertThat(testPage.getElementText("home-zipcode")).isEqualTo(ZIP_CODE);

        // mailing address is the validated version
        assertThat(testPage.elementDoesNotExistById("no-mailing-address")).isTrue();
        assertThat(testPage.getElementText("mailing-street-address-1")).isEqualTo(VALIDATED_STREET_ADDRESS_LINE_1);
        assertThat(testPage.elementDoesNotExistById("mailing-street-address-2")).isTrue();
        assertThat(testPage.getElementText("mailing-city-state")).isEqualTo(VALIDATED_CITY + ", " + VALIDATED_STATE);
        assertThat(testPage.getElementText("mailing-zipcode")).isEqualTo(VALIDATED_ZIP_CODE);

        // Data in mailing address screen matches the selected validated data
        testPage.clickElementById("edit-home-address");
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-home-address.title"));

        assertThat(testPage.getInputValue("parentHomeStreetAddress1")).isEqualTo(STREET_ADDRESS_LINE_1);
        assertThat(testPage.getInputValue("parentHomeStreetAddress2")).isEqualTo(STREET_ADDRESS_LINE_2);
        assertThat(testPage.getInputValue("parentHomeCity")).isEqualTo(CITY);
        assertThat(testPage.getSelectValue("parentHomeState")).isEqualTo(getEnMessage("state.il"));
        assertThat(testPage.getInputValue("parentHomeZipCode")).isEqualTo(ZIP_CODE);

        testPage.goBack();

        //parent-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-review.title"));
        // Data in mailing address screen matches the selected validated data
        testPage.clickElementById("edit-mailing-address");
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-mailing-address.title"));
        assertThat(testPage.elementDoesNotExistById("parentMailingAddressSameAsHomeAddress-yes")).isFalse();
        assertThat(testPage.findElementById("parentMailingAddressSameAsHomeAddress-yes").isSelected()).isTrue();

        // unselect parentMailingAddressSameAsHomeAddress to see the data and it shows the stored parenHomeStreetAddress
        testPage.clickElementById("parentMailingAddressSameAsHomeAddress-yes");

        assertThat(testPage.getInputValue("parentMailingStreetAddress1")).isEqualTo(STREET_ADDRESS_LINE_1);
        assertThat(testPage.getInputValue("parentMailingStreetAddress2")).isEqualTo(STREET_ADDRESS_LINE_2);
        assertThat(testPage.getInputValue("parentMailingCity")).isEqualTo(CITY);
        assertThat(testPage.getSelectValue("parentMailingState")).isEqualTo(getEnMessage("state.il"));
        assertThat(testPage.getInputValue("parentMailingZipCode")).isEqualTo(ZIP_CODE);
    }

    @Test
    void whenHomeAddressAndMailingAddressAreDifferent()
            throws SmartyException, IOException, InterruptedException {
        testPage.navigateToFlowScreen("gcc/parent-info-disability");

        saveSubmission(getSessionSubmissionTestBuilder()
                .withParentBasicInfo()
                .with("familyIntendedProviderName", "ACME Daycare")
                .with("applicationCounty", COUNTY_LEE_LABEL)
                .build());

        testPage.clickYes();

        //parent-home-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-home-address.title"));
        testPage.enter("parentHomeStreetAddress1", STREET_ADDRESS_LINE_1);
        testPage.enter("parentHomeStreetAddress2", STREET_ADDRESS_LINE_2);
        testPage.enter("parentHomeCity", CITY);
        testPage.selectFromDropdown("parentHomeState", getEnMessage("state.il"));
        testPage.enter("parentHomeZipCode", ZIP_CODE);

        // Mocks a successful addressValidationService.validate for parentHome
        when(addressValidationService.validate(ArgumentMatchers.any(FormSubmission.class))).thenReturn(
                validatedAddressReturnValue("parentHome", true));
        testPage.clickContinue();

        // confirm-parent-home-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("confirm-address.title"));
        assertThat(testPage.elementDoesNotExistById("original-address-label")).isFalse();
        assertThat(testPage.elementDoesNotExistById("validated-address-label")).isFalse();
        assertThat(testPage.findElementById("validated-address").isSelected()).isTrue();
        testPage.clickContinue();

        // parent-mailing-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-mailing-address.title"));
        assertThat(testPage.elementDoesNotExistById("parentMailingAddressSameAsHomeAddress-yes")).isFalse();

        testPage.enter("parentMailingStreetAddress1", VALIDATED_STREET_ADDRESS_LINE_1);
        testPage.enter("parentMailingStreetAddress2", "");
        testPage.enter("parentMailingCity", VALIDATED_CITY);
        testPage.selectFromDropdown("parentMailingState", getEnMessage("state.il"));
        testPage.enter("parentMailingZipCode", VALIDATED_ZIP_CODE);

        // Mocks a successful addressValidationService.validate for parentMailing
        when(addressValidationService.validate(ArgumentMatchers.any(FormSubmission.class))).thenReturn(
                validatedAddressReturnValue("parentMailing", false));
        testPage.clickContinue();

        // confirm-parent-mailing-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-confirm-address.title"));
        assertThat(testPage.elementDoesNotExistById("original-address-label")).isFalse();
        assertThat(testPage.elementDoesNotExistById("validated-address-label")).isFalse();
        assertThat(testPage.findElementById("validated-address").isSelected()).isTrue();
        testPage.clickContinue();

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
        assertThat(testPage.elementDoesNotExistById("no-home-address")).isTrue();
        // home address is the unvalidated version
        assertThat(testPage.getElementText("home-street-address-1")).isEqualTo(VALIDATED_STREET_ADDRESS_LINE_1);
        assertThat(testPage.elementDoesNotExistById("home-street-address-2")).isTrue();
        assertThat(testPage.getElementText("home-city-state")).isEqualTo(VALIDATED_CITY + ", IL");
        assertThat(testPage.getElementText("home-zipcode")).isEqualTo(VALIDATED_ZIP_CODE);

        // validated mailing address is the unvalidated version
        assertThat(testPage.elementDoesNotExistById("no-mailing-address")).isTrue();
        assertThat(testPage.getElementText("mailing-street-address-1")).isEqualTo(STREET_ADDRESS_LINE_1);
        // we never get back address 2 in address validation
        assertThat(testPage.elementDoesNotExistById("mailing-street-address-2")).isTrue();
        assertThat(testPage.getElementText("mailing-city-state")).isEqualTo(CITY + ", IL");
        assertThat(testPage.getElementText("mailing-zipcode")).isEqualTo(ZIP_CODE);

        // Data in mailing address screen matches the selected validated data
        testPage.clickElementById("edit-home-address");
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-home-address.title"));

        assertThat(testPage.getInputValue("parentHomeStreetAddress1")).isEqualTo(VALIDATED_STREET_ADDRESS_LINE_1);
        assertThat(testPage.getInputValue("parentHomeStreetAddress2")).isEqualTo("");
        assertThat(testPage.getInputValue("parentHomeCity")).isEqualTo(VALIDATED_CITY);
        assertThat(testPage.getSelectValue("parentHomeState")).isEqualTo(getEnMessage("state.il"));
        assertThat(testPage.getInputValue("parentHomeZipCode")).isEqualTo(VALIDATED_ZIP_CODE);

        testPage.goBack();

        //parent-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-review.title"));
        // Data in mailing address screen matches the selected validated data
        testPage.clickElementById("edit-mailing-address");
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-mailing-address.title"));
        assertThat(testPage.elementDoesNotExistById("parentMailingAddressSameAsHomeAddress-yes")).isFalse();
        assertThat(testPage.findElementById("parentMailingAddressSameAsHomeAddress-yes").isSelected()).isFalse();

        assertThat(testPage.getInputValue("parentMailingStreetAddress1")).isEqualTo(STREET_ADDRESS_LINE_1);
        // we never get back address 2 in address validation
        assertThat(testPage.getInputValue("parentMailingStreetAddress2")).isEqualTo("");
        assertThat(testPage.getInputValue("parentMailingCity")).isEqualTo(CITY);
        assertThat(testPage.getSelectValue("parentMailingState")).isEqualTo(getEnMessage("state.il"));
        assertThat(testPage.getInputValue("parentMailingZipCode")).isEqualTo(ZIP_CODE);
    }

    private static Map<String, ValidatedAddress> validatedAddressReturnValue(String addressGroupInputPrefix, boolean validated) {
        if (validated) {
            return Map.of(addressGroupInputPrefix,
                    new ValidatedAddress(VALIDATED_STREET_ADDRESS_LINE_1, "", VALIDATED_CITY, VALIDATED_STATE,
                            VALIDATED_ZIP_CODE));
        } else {
            return Map.of(addressGroupInputPrefix,
                    new ValidatedAddress(STREET_ADDRESS_LINE_1, STREET_ADDRESS_LINE_2, CITY, "IL", ZIP_CODE));
        }


    }

}
