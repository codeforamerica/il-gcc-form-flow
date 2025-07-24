package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.CURRENT_APPROVED_PROVIDER;

import formflow.library.data.Submission;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@TestPropertySource(properties = {"il-gcc.enable-multiple-providers=true"})
public class ProviderresponseMultiProviderFlowJourneyTest extends AbstractBasePageTest {

    private static Map<String, Object> individualProvider = new HashMap<>();

    private static Map<String, Object> programProvider = new HashMap<>();

    private static Map<String, Object> noResponseProvider = new HashMap<>();

    private Submission familySubmission;

    private static Map<String, Object> child1 = new HashMap<>();

    private static Map<String, Object> child2 = new HashMap<>();

    @BeforeAll
    public static void setUpOnce() {
        individualProvider.put("uuid", UUID.randomUUID().toString());
        individualProvider.put("iterationIsComplete", true);
        individualProvider.put("providerFirstName", "FirstName");
        individualProvider.put("providerLastName", "LastName");
        individualProvider.put("familyIntendedProviderName", "FirstName Last");
        individualProvider.put("familyIntendedProviderEmail", "firstLast@mail.com");
        individualProvider.put("familyIntendedProviderPhoneNumber", "(999) 123-1234");
        individualProvider.put("familyIntendedProviderAddress", "123 Main St.");
        individualProvider.put("familyIntendedProviderCity", "Chicago");
        individualProvider.put("familyIntendedProviderState", "IL");
        individualProvider.put("providerType", "Individual");

        programProvider.put("uuid", UUID.randomUUID().toString());
        programProvider.put("iterationIsComplete", true);
        programProvider.put("childCareProgramName", "Child Care Program Name");
        programProvider.put("familyIntendedProviderName", "Child Care Program Name");
        programProvider.put("familyIntendedProviderEmail", "ccpn@mail.com");
        programProvider.put("familyIntendedProviderPhoneNumber", "(123) 123-1234");
        programProvider.put("familyIntendedProviderAddress", "456 Main St.");
        programProvider.put("familyIntendedProviderCity", "Chicago");
        programProvider.put("familyIntendedProviderState", "IL");
        programProvider.put("providerType", "Care Program");

        noResponseProvider.put("uuid", UUID.randomUUID().toString());
        noResponseProvider.put("iterationIsComplete", true);
        noResponseProvider.put("childCareProgramName", "Great Care Child Care");
        noResponseProvider.put("familyIntendedProviderEmail", "ccpn@mail.com");
        noResponseProvider.put("familyIntendedProviderPhoneNumber", "(123) 123-1234");
        noResponseProvider.put("familyIntendedProviderAddress", "456 Main St.");
        noResponseProvider.put("familyIntendedProviderCity", "Chicago");
        noResponseProvider.put("familyIntendedProviderState", "IL");
        noResponseProvider.put("providerType", "Care Program");

        child1.put("uuid", UUID.randomUUID().toString());
        child1.put("childFirstName", "First");
        child1.put("childLastName", "Child");
        child1.put("childInCare", "true");
        child1.put("childDateOfBirthMonth", "10");
        child1.put("childDateOfBirthDay", "11");
        child1.put("childDateOfBirthYear", "2020");
        child1.put("needFinancialAssistanceForChild", true);
        child1.put("childIsUsCitizen", "Yes");
        child1.put("ccapStartDate", "01/10/2025");

        child2.put("uuid", UUID.randomUUID().toString());
        child2.put("childFirstName", "Second");
        child2.put("childLastName", "Child");
        child2.put("childInCare", "true");
        child2.put("childDateOfBirthMonth", "12");
        child2.put("childDateOfBirthDay", "11");
        child2.put("childDateOfBirthYear", "2021");
        child2.put("needFinancialAssistanceForChild", true);
        child2.put("childIsUsCitizen", "Yes");
        child2.put("ccapStartDate", "12/10/2025");
    }

    @AfterEach
    protected void clearSubmissions() {
        super.clearSubmissions();
    }

    @Nested
    public class whenThereIsMoreThanOneProvider {

        @BeforeEach
        void setup() {
            familySubmission = saveSubmission(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .withParentBasicInfo()
                    .with("parentContactEmail", "familyemail@test.com")
                    .with("languageRead", "English")
                    .with("providers", List.of(individualProvider, programProvider))
                    .with("children", List.of(child1, child2))
                    .withMultipleChildcareSchedules(
                            List.of(child1.get("uuid").toString(), child2.get("uuid").toString(), child2.get("uuid").toString()),
                            List.of(individualProvider.get("uuid").toString(),
                                    individualProvider.get("uuid").toString(), programProvider.get("uuid").toString()))
                    .withSubmittedAtDate(OffsetDateTime.now())
                    .withCCRR()
                    .withShortCode("ABC123")
                    .build());
        }

        @Test
        void ProviderresponseJourneyTest_noLink() {
            driver.navigate().to("http://localhost:%s/s".formatted(localServerPort));

            // submit-start when application is still active
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
            testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

            // confirmation-code
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
            testPage.enter("providerResponseFamilyShortCode", familySubmission.getShortCode());
            testPage.clickContinue();
            
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-multiple-providers.title"));
            testPage.clickElementById(String.format("currentProviderUuid-%s", individualProvider.get("uuid")));
            testPage.clickContinue();

            // confirm-provider
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirm-provider.title"));
            assertThat(testPage.getHeader()).contains("F.L.");
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
            assertThat(testPage.elementDoesNotExistById("child-name-2")).isTrue();

            testPage.clickElementById("providerResponseAgreeToCare-true-label");
            testPage.clickButton("Submit");

            //submit-confirmation displays submit-complete-final screen
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-complete-final.title"));
            testPage.goBack();

            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-complete-final.title"));
        }

        @Test
        void ProviderresponseJourneyTest_Link() {
            driver.navigate().to("http://localhost:%s/s/%s".formatted(localServerPort, familySubmission.getShortCode()));

            // submit-start when application is still active
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
            testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

            // confirmation-code
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
            assertThat(testPage.findElementById("providerResponseFamilyShortCode").getAttribute("value")).isEqualTo(
                    familySubmission.getShortCode());
            testPage.clickContinue();

            // multiple-providers
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-multiple-providers.title"));
            testPage.clickElementById(String.format("currentProviderUuid-%s", individualProvider.get("uuid")));
            testPage.clickContinue();

            // confirm-provider
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirm-provider.title"));
            assertThat(testPage.getHeader()).contains("F.L.");
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
            assertThat(testPage.elementDoesNotExistById("child-name-2")).isTrue();

            testPage.clickElementById("providerResponseAgreeToCare-true-label");
            testPage.clickButton("Submit");

            //submit-confirmation displays submit-complete-final screen
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-complete-final.title"));
            testPage.goBack();

            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-complete-final.title"));
        }
    }

    @Nested
    public class whenThereIsASingleProvider {

        @BeforeEach
        void setup() {
            familySubmission = saveSubmission(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .withParentBasicInfo()
                    .with("parentContactEmail", "familyemail@test.com")
                    .with("languageRead", "English")
                    .with("providers", List.of(individualProvider))
                    .with("children", List.of(child1, child2))
                    .withMultipleChildcareSchedules(
                            List.of(child1.get("uuid").toString(), child2.get("uuid").toString()),
                            List.of(individualProvider.get("uuid").toString(),
                                    individualProvider.get("uuid").toString()))
                    .withSubmittedAtDate(OffsetDateTime.now())
                    .withCCRR()
                    .withShortCode("ABC123")
                    .build());
        }

        @Test
        void ProviderresponseJourneyTest_noLink() {
            driver.navigate().to("http://localhost:%s/s".formatted(localServerPort));

            // submit-start when application is still active
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
            assertThat(testPage.elementDoesNotExistById("child-name-2")).isTrue();

            testPage.clickElementById("providerResponseAgreeToCare-true-label");
            testPage.clickButton("Submit");

            //submit-confirmation displays submit-complete-final screen
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-complete-final.title"));
            testPage.goBack();

            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-complete-final.title"));
        }

        @Test
        void ProviderresponseJourneyTest_Link() {
            driver.navigate().to("http://localhost:%s/s/%s".formatted(localServerPort, familySubmission.getShortCode()));

            // submit-start when application is still active
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
            testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

            // confirmation-code
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
            assertThat(testPage.findElementById("providerResponseFamilyShortCode").getAttribute("value")).isEqualTo(
                    familySubmission.getShortCode());
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

            // provider-number
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-provider-number.title"));
            testPage.enter("providerResponseProviderNumber", CURRENT_APPROVED_PROVIDER.getProviderId().toString());
            testPage.clickContinue();

            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-response.title"));
            assertThat(testPage.findElementTextById("confirmation-code")).contains(familySubmission.getShortCode());
            assertThat(testPage.findElementTextById("parent-name")).contains("parent first parent last");
            assertThat(testPage.findElementTextById("child-name-0")).contains("First Child");
            assertThat(testPage.findElementTextById("child-name-1")).contains("Second Child");
            assertThat(testPage.elementDoesNotExistById("child-name-2")).isTrue();

            testPage.clickElementById("providerResponseAgreeToCare-true-label");
            testPage.clickButton("Submit");

            //submit-confirmation displays submit-complete-final screen
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-complete-final.title"));
            testPage.goBack();

            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-complete-final.title"));
        }
    }

}
