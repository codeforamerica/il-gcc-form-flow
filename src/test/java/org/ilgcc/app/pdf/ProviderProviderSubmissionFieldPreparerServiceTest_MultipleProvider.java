package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.ilgcc.app.utils.enums.ProviderLanguagesOffered.HINDI;
import static org.ilgcc.app.utils.enums.ProviderLanguagesOffered.TAGALOG;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.enums.ProviderDenialReason;
import org.ilgcc.app.utils.enums.ProviderType;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = IlGCCApplication.class
)
@ActiveProfiles("test")
public class ProviderProviderSubmissionFieldPreparerServiceTest_MultipleProvider {

    @Autowired
    private ProviderSubmissionFieldPreparerService preparer = new ProviderSubmissionFieldPreparerService(true);

    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;

    private Submission familySubmission;
    private Submission providerSubmission;

    @Test
    public void setsNoResponseWhenNoProviderSubmissionExistsAndSubmissionIsExpired() {
        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(10))
                .withDayCareProvider()
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .with("providerApplicationResponseStatus", SubmissionStatus.EXPIRED)
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerResponseBusinessName")).isEqualTo(
                new SingleField("providerResponseBusinessName", "ProviderName", null));
        assertThat(result.get("providerPhoneNumber")).isEqualTo(new SingleField("providerPhoneNumber", "(125) 785-67896", null));
        assertThat(result.get("providerEmail")).isEqualTo(new SingleField("providerEmail", "mail@test.com", null));
        assertThat(result.get("providerResponse")).isEqualTo(
                new SingleField("providerResponse", "No response from provider", null));
    }


    @Test
    public void usesEarliestChildCareDateWhenProviderHasNotResponded() {
        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(10))
                .withDayCareProvider()
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .with("earliestChildcareStartDate", "11/12/2023")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("childcareStartDate")).isEqualTo(new SingleField("childcareStartDate", "11/12/2023", null));
    }

    @Test
    public void usesEarliestChildCareDateWhenProviderStartDateIsNotSet() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .build();

        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withDayCareProvider()
                .withSubmittedAtDate(OffsetDateTime.now())
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .with("earliestChildcareStartDate", "01/12/2023")

                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("childcareStartDate")).isEqualTo(new SingleField("childcareStartDate", "01/12/2023", null));

    }

    @Test
    public void usesProviderCareStartDateWhenPresent() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .withProviderStateLicense()
                .with("providerResponseAgreeToCare", "true")
                .with("providerCareStartDate", "12/11/2023")
                .build();

        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withDayCareProvider()
                .withSubmittedAtDate(OffsetDateTime.now())
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .with("earliestChildcareStartDate", "11/11/2025")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("childcareStartDate")).isEqualTo(new SingleField("childcareStartDate", "12/11/2023", null));
    }

    @Test
    public void ignoresProviderCareStartDateWhenNull() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .withProviderStateLicense()
                .with("providerResponseAgreeToCare", "true")
                .with("providerCareStartDate", "")
                .build();

        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withDayCareProvider()
                .withSubmittedAtDate(OffsetDateTime.now())
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .with("earliestChildcareStartDate", "11/11/2025")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("childcareStartDate")).isEqualTo(new SingleField("childcareStartDate", "11/11/2025", null));
    }

    @Test
    public void mapsFieldsCorrectlyWhenNoProviderWasChosen() {
        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("hasChosenProvider", "false")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerNameCorporate")).isEqualTo(
                new SingleField("providerNameCorporate", "No qualified provider", null));
        assertThat(result.get("dayCareIdNumber")).isEqualTo(new SingleField("dayCareIdNumber", "460328258720008", null));
        assertThat(result.get("providerResponse")).isEqualTo(
                new SingleField("providerResponse", "No provider chosen", null));
    }

    @Test
    public void setsNoResponseWhenNoProviderSubmissionExistsAndSubmissionIsExpired2() {
        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(10))
                .withDayCareProvider()
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .with("providerApplicationResponseStatus", SubmissionStatus.EXPIRED)
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerResponseBusinessName")).isEqualTo(
                new SingleField("providerResponseBusinessName", "ProviderName", null));
        assertThat(result.get("providerPhoneNumber")).isEqualTo(new SingleField("providerPhoneNumber", "(125) 785-67896", null));
        assertThat(result.get("providerEmail")).isEqualTo(new SingleField("providerEmail", "mail@test.com", null));
        assertThat(result.get("providerResponse")).isEqualTo(
                new SingleField("providerResponse", "No response from provider", null));
    }

    @Test
    public void shouldNotMapNoProviderResponseIfSubmissionHasNotExpired() {
        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(1))
                .withDayCareProvider()
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .with("providerApplicationResponseStatus", SubmissionStatus.ACTIVE)

                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerResponse")).isNull();
    }

    @Test
    public void mapsProviderResponseServiceAndProviderMailingAddressesToPDF() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerResponseAgreeToCare", "true")
                .with("providerResponseServiceStreetAddress1", "123 Main Street")
                .with("providerResponseServiceCity", "De Kalb")
                .with("providerResponseServiceState", "IL")
                .with("providerResponseServiceZipCode", "60112")
                .with("providerMailingStreetAddress1", "888 Main Street")
                .with("providerMailingCity", "Chicago")
                .with("providerMailingState", "IL")
                .with("providerMailingZipCode", "60115")
                .with("providerConviction", "true")
                .build();

        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withDayCareProvider()
                .withSubmittedAtDate(OffsetDateTime.now())
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .with("providerResponseSubmissionId", providerSubmission.getId())

                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);

        assertThat(result.get("providerResponseFirstName")).isEqualTo(
                new SingleField("providerResponseFirstName", "Provider", null));
        assertThat(result.get("providerResponseLastName")).isEqualTo(
                new SingleField("providerResponseLastName", "LastName", null));
        assertThat(result.get("providerResponseBusinessName")).isEqualTo(
                new SingleField("providerResponseBusinessName", "DayCare Place", null));
        assertThat(result.get("providerResponseServiceStreetAddress1")).isEqualTo(
                new SingleField("providerResponseServiceStreetAddress1", "123 Main Street", null));
        assertThat(result.get("providerResponseServiceCity")).isEqualTo(
                new SingleField("providerResponseServiceCity", "De Kalb", null));
        assertThat(result.get("providerResponseServiceState")).isEqualTo(
                new SingleField("providerResponseServiceState", "IL", null));
        assertThat(result.get("providerResponseServiceZipCode")).isEqualTo(
                new SingleField("providerResponseServiceZipCode", "60112", null));

        assertThat(result.get("providerMailingStreetAddress1")).isEqualTo(
                new SingleField("providerMailingStreetAddress1", "888 Main Street", null));
        assertThat(result.get("providerMailingStreetAddress2")).isEqualTo(
                new SingleField("providerMailingStreetAddress2", "", null));
        assertThat(result.get("providerMailingCity")).isEqualTo(
                new SingleField("providerMailingCity", "Chicago", null));
        assertThat(result.get("providerMailingState")).isEqualTo(
                new SingleField("providerMailingState", "IL", null));
        assertThat(result.get("providerMailingZipCode")).isEqualTo(
                new SingleField("providerMailingZipCode", "60115", null));

        assertThat(result.get("providerResponseContactPhoneNumber")).isEqualTo(
                new SingleField("providerResponseContactPhoneNumber", "(111) 222-3333", null));
        assertThat(result.get("providerResponseContactEmail")).isEqualTo(
                new SingleField("providerResponseContactEmail", "mail@daycareplace.org", null));
        assertThat(result.get("providerConviction")).isEqualTo(
                new SingleField("providerConviction", "true", null));

    }

    @Test
    public void mapsServiceAndMailingAddressEvenIfSame() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerResponseAgreeToCare", "true")
                .with("providerResponseServiceStreetAddress1", "123 Main Street")
                .with("providerResponseServiceCity", "De Kalb")
                .with("providerResponseServiceState", "IL")
                .with("providerResponseServiceZipCode", "60112")
                .with("providerMailingAddressSameAsServiceAddress[]", List.of("yes"))
                .with("providerMailingStreetAddress1", "123 Main Street")
                .with("providerMailingCity", "De Kalb")
                .with("providerMailingState", "IL")
                .with("providerMailingZipCode", "60112")
                .with("providerConviction", "true")
                .build();

        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withDayCareProvider()
                .withSubmittedAtDate(OffsetDateTime.now())
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .with("providerResponseSubmissionId", providerSubmission.getId())

                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerResponseFirstName")).isEqualTo(
                new SingleField("providerResponseFirstName", "Provider", null));
        assertThat(result.get("providerResponseLastName")).isEqualTo(
                new SingleField("providerResponseLastName", "LastName", null));
        assertThat(result.get("providerResponseBusinessName")).isEqualTo(
                new SingleField("providerResponseBusinessName", "DayCare Place", null));
        assertThat(result.get("providerResponseServiceStreetAddress1")).isEqualTo(
                new SingleField("providerResponseServiceStreetAddress1", "123 Main Street", null));
        assertThat(result.get("providerResponseServiceCity")).isEqualTo(
                new SingleField("providerResponseServiceCity", "De Kalb", null));
        assertThat(result.get("providerResponseServiceState")).isEqualTo(
                new SingleField("providerResponseServiceState", "IL", null));
        assertThat(result.get("providerResponseServiceZipCode")).isEqualTo(
                new SingleField("providerResponseServiceZipCode", "60112", null));

        assertThat(result.get("providerMailingStreetAddress1")).isEqualTo(
                new SingleField("providerMailingStreetAddress1", "123 Main Street", null));
        assertThat(result.get("providerMailingCity")).isEqualTo(
                new SingleField("providerMailingCity", "De Kalb", null));
        assertThat(result.get("providerMailingState")).isEqualTo(
                new SingleField("providerMailingState", "IL", null));
        assertThat(result.get("providerMailingZipCode")).isEqualTo(
                new SingleField("providerMailingZipCode", "60112", null));

        assertThat(result.get("providerResponseContactPhoneNumber")).isEqualTo(
                new SingleField("providerResponseContactPhoneNumber", "(111) 222-3333", null));
        assertThat(result.get("providerResponseContactEmail")).isEqualTo(
                new SingleField("providerResponseContactEmail", "mail@daycareplace.org", null));
        assertThat(result.get("providerConviction")).isEqualTo(
                new SingleField("providerConviction", "true", null));

    }

    @Test
    public void correctlyMapsDataWhenProviderDoesNotAgreeToCareAndHasValidProviderId() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .withProviderStateLicense()
                .with("providerResponseProviderNumber", "12345678901")
                .with("providerMailingAddressSameAsServiceAddress[]", List.of("yes"))
                .with("providerMailingStreetAddress1", "123 Main Street")
                .with("providerMailingCity", "De Kalb")
                .with("providerMailingState", "IL")
                .with("providerMailingZipCode", "60112")
                .with("providerResponseAgreeToCare", "false")
                .withClientResponseConfirmationCode("testConfirmationCode")
                .with("providerTaxIdFEIN", "12-1234567")
                .build();

        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withSubmittedAtDate(OffsetDateTime.now())
                .withDayCareProvider()
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerResponseFirstName")).isEqualTo(
                new SingleField("providerResponseFirstName", "Provider", null));
        assertThat(result.get("providerResponseLastName")).isEqualTo(
                new SingleField("providerResponseLastName", "LastName", null));
        assertThat(result.get("providerResponseBusinessName")).isEqualTo(
                new SingleField("providerResponseBusinessName", "DayCare Place", null));
        assertThat(result.get("providerResponseProviderNumber")).isEqualTo(
                new SingleField("providerResponseProviderNumber", "12345678901", null));

        assertThat(result.get("providerMailingStreetAddress1")).isEqualTo(
                new SingleField("providerMailingStreetAddress1", "123 Main Street", null));
        assertThat(result.get("providerMailingCity")).isEqualTo(
                new SingleField("providerMailingCity", "De Kalb", null));
        assertThat(result.get("providerMailingState")).isEqualTo(
                new SingleField("providerMailingState", "IL", null));
        assertThat(result.get("providerMailingZipCode")).isEqualTo(
                new SingleField("providerMailingZipCode", "60112", null));

        assertThat(result.get("providerResponseServiceStreetAddress1")).isEqualTo(
                new SingleField("providerResponseServiceStreetAddress1", "123 Main St", null));
        assertThat(result.get("providerResponseServiceStreetAddress2")).isEqualTo(
                new SingleField("providerResponseServiceStreetAddress2", "Unit 10", null));
        assertThat(result.get("providerResponseServiceCity")).isEqualTo(
                new SingleField("providerResponseServiceCity", "DeKalb", null));
        assertThat(result.get("providerResponseServiceState")).isEqualTo(
                new SingleField("providerResponseServiceState", "IL", null));
        assertThat(result.get("providerResponseServiceZipCode")).isEqualTo(
                new SingleField("providerResponseServiceZipCode", "60112", null));

        assertThat(result.get("providerResponseContactEmail")).isEqualTo(
                new SingleField("providerResponseContactEmail", "mail@daycareplace.org", null));
        assertThat(result.get("providerResponseContactPhoneNumber")).isEqualTo(
                new SingleField("providerResponseContactPhoneNumber", "(111) 222-3333", null));

        assertThat(result.get("providerLicenseNumber")).isEqualTo(
                new SingleField("providerLicenseNumber", "123453646 (IL)", null));
        assertThat(result.get("providerResponse")).isEqualTo(
                new SingleField("providerResponse", "Provider declined", null));

        assertThat(result.get("clientResponseConfirmationCode")).isEqualTo(
                new SingleField("clientResponseConfirmationCode", "testConfirmationCode", null));
        assertThat(result.get("providerTaxIdFEIN")).isEqualTo(
                new SingleField("providerTaxIdFEIN", "12-1234567", null));
    }

    @Test
    public void correctlyMapsDataProviderAgreesToCareAndHasValidProviderId() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .withProviderStateLicense()
                .with("providerResponseProviderNumber", "12345678901")
                .with("providerMailingAddressSameAsServiceAddress[]", List.of("yes"))
                .with("providerMailingStreetAddress1", "123 Main Street")
                .with("providerMailingCity", "De Kalb")
                .with("providerMailingState", "IL")
                .with("providerMailingZipCode", "60112")
                .with("providerResponseAgreeToCare", "true")
                .withClientResponseConfirmationCode("testConfirmationCode")
                .with("providerTaxIdFEIN", "12-1234567")
                .build();

        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withDayCareProvider()
                .withSubmittedAtDate(OffsetDateTime.now())
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .with("providerResponseSubmissionId", providerSubmission.getId())

                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerResponseFirstName")).isEqualTo(
                new SingleField("providerResponseFirstName", "Provider", null));
        assertThat(result.get("providerResponseLastName")).isEqualTo(
                new SingleField("providerResponseLastName", "LastName", null));
        assertThat(result.get("providerResponseBusinessName")).isEqualTo(
                new SingleField("providerResponseBusinessName", "DayCare Place", null));
        assertThat(result.get("providerResponseProviderNumber")).isEqualTo(
                new SingleField("providerResponseProviderNumber", "12345678901", null));

        assertThat(result.get("providerMailingStreetAddress1")).isEqualTo(
                new SingleField("providerMailingStreetAddress1", "123 Main Street", null));
        assertThat(result.get("providerMailingCity")).isEqualTo(
                new SingleField("providerMailingCity", "De Kalb", null));
        assertThat(result.get("providerMailingState")).isEqualTo(
                new SingleField("providerMailingState", "IL", null));
        assertThat(result.get("providerMailingZipCode")).isEqualTo(
                new SingleField("providerMailingZipCode", "60112", null));

        assertThat(result.get("providerResponseServiceStreetAddress1")).isEqualTo(
                new SingleField("providerResponseServiceStreetAddress1", "123 Main St", null));
        assertThat(result.get("providerResponseServiceStreetAddress2")).isEqualTo(
                new SingleField("providerResponseServiceStreetAddress2", "Unit 10", null));
        assertThat(result.get("providerResponseServiceCity")).isEqualTo(
                new SingleField("providerResponseServiceCity", "DeKalb", null));
        assertThat(result.get("providerResponseServiceState")).isEqualTo(
                new SingleField("providerResponseServiceState", "IL", null));
        assertThat(result.get("providerResponseServiceZipCode")).isEqualTo(
                new SingleField("providerResponseServiceZipCode", "60112", null));

        assertThat(result.get("providerResponseContactEmail")).isEqualTo(
                new SingleField("providerResponseContactEmail", "mail@daycareplace.org", null));
        assertThat(result.get("providerResponseContactPhoneNumber")).isEqualTo(
                new SingleField("providerResponseContactPhoneNumber", "(111) 222-3333", null));

        assertThat(result.get("providerLicenseNumber")).isEqualTo(
                new SingleField("providerLicenseNumber", "123453646 (IL)", null));

        assertThat(result.get("clientResponseConfirmationCode")).isEqualTo(
                new SingleField("clientResponseConfirmationCode", "testConfirmationCode", null));
        assertThat(result.get("providerTaxIdFEIN")).isEqualTo(
                new SingleField("providerTaxIdFEIN", "12-1234567", null));

        assertThat(result.get("providerResponse")).isEqualTo(
                new SingleField("providerResponse", "", null));
    }

    @ParameterizedTest
    @MethodSource("providerDenialReasons")
    public void correctlyMapsProviderResponseWhenProviderDeniesCareAndSelectsReasonForDenial(ProviderDenialReason reason) {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .withProviderStateLicense()
                .with("providerResponseProviderNumber", "12345678901")
                .with("providerMailingAddressSameAsServiceAddress[]", List.of("yes"))
                .with("providerMailingStreetAddress1", "123 Main Street")
                .with("providerMailingCity", "De Kalb")
                .with("providerMailingState", "IL")
                .with("providerMailingZipCode", "60112")
                .with("providerResponseAgreeToCare", "false")
                .with("providerResponseDenyCareReason", reason.name())
                .withClientResponseConfirmationCode("testConfirmationCode")
                .with("providerTaxIdEIN", "12-1234567")
                .build();

        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withDayCareProvider()
                .withSubmittedAtDate(OffsetDateTime.now())
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .with("providerResponseSubmissionId", providerSubmission.getId())

                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerResponse")).isEqualTo(
                new SingleField("providerResponse", reason.getPdfValue(), null));
    }

    @Test
    public void correctlyMapsDataWhenExistingProviderDoesNotHaveValidProviderId() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .withProviderStateLicense()
                .with("providerPaidCcap", "true")
                .with("providerMailingAddressSameAsServiceAddress[]", List.of("yes"))
                .with("providerMailingStreetAddress1", "123 Main Street")
                .with("providerMailingCity", "De Kalb")
                .with("providerMailingState", "IL")
                .with("providerMailingZipCode", "60112")
                .withClientResponseConfirmationCode("testConfirmationCode")
                .build();

        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withDayCareProvider()
                .withSubmittedAtDate(OffsetDateTime.now())
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .with("providerResponseSubmissionId", providerSubmission.getId())

                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerResponseFirstName")).isEqualTo(
                new SingleField("providerResponseFirstName", "Provider", null));
        assertThat(result.get("providerResponseLastName")).isEqualTo(
                new SingleField("providerResponseLastName", "LastName", null));
        assertThat(result.get("providerResponseBusinessName")).isEqualTo(
                new SingleField("providerResponseBusinessName", "DayCare Place", null));

        assertThat(result.get("providerMailingStreetAddress1")).isEqualTo(
                new SingleField("providerMailingStreetAddress1", "123 Main Street", null));
        assertThat(result.get("providerMailingCity")).isEqualTo(
                new SingleField("providerMailingCity", "De Kalb", null));
        assertThat(result.get("providerMailingState")).isEqualTo(
                new SingleField("providerMailingState", "IL", null));
        assertThat(result.get("providerMailingZipCode")).isEqualTo(
                new SingleField("providerMailingZipCode", "60112", null));

        assertThat(result.get("providerResponseServiceStreetAddress1")).isEqualTo(
                new SingleField("providerResponseServiceStreetAddress1", "123 Main St", null));
        assertThat(result.get("providerResponseServiceStreetAddress2")).isEqualTo(
                new SingleField("providerResponseServiceStreetAddress2", "Unit 10", null));
        assertThat(result.get("providerResponseServiceCity")).isEqualTo(
                new SingleField("providerResponseServiceCity", "DeKalb", null));
        assertThat(result.get("providerResponseServiceState")).isEqualTo(
                new SingleField("providerResponseServiceState", "IL", null));
        assertThat(result.get("providerResponseServiceZipCode")).isEqualTo(
                new SingleField("providerResponseServiceZipCode", "60112", null));

        assertThat(result.get("providerResponseContactEmail")).isEqualTo(
                new SingleField("providerResponseContactEmail", "mail@daycareplace.org", null));
        assertThat(result.get("providerResponseContactPhoneNumber")).isEqualTo(
                new SingleField("providerResponseContactPhoneNumber", "(111) 222-3333", null));

        assertThat(result.get("providerLicenseNumber")).isEqualTo(
                new SingleField("providerLicenseNumber", "123453646 (IL)", null));

        assertThat(result.get("clientResponseConfirmationCode")).isEqualTo(
                new SingleField("clientResponseConfirmationCode", "testConfirmationCode", null));

        assertThat(result.get("providerResponse")).isEqualTo(
                new SingleField("providerResponse", "Unable to identify provider - no response to care arrangement", null));
    }

    static Stream<Arguments> providerDenialReasons() {
        return Stream.of(ProviderDenialReason.values())
                .map(reason -> Arguments.of(reason));
    }

    @Test
    public void shouldPrintProviderHouseholdMembersDateOfBirthToPDFIfMonthDayAndYearIsPresentAndIterationIsComplete() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .withProviderHouseholdMember("1-Iteration",
                        "One",
                        "30",
                        "10",
                        "2004",
                        "brother",
                        "555-555-5555")

                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerHouseholdMemberDateOfBirth_1")).isEqualTo(
                new SingleField("providerHouseholdMemberDateOfBirth", "10/30/2004", 1));
    }

    @Test
    public void shouldPrintMultipleProviderHouseholdMembersInfoToPDFFields() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .withProviderHouseholdMember("1-Iteration",
                        "One",
                        "30",
                        "10",
                        "2004",
                        "brother",
                        "555-55-5555")
                .withProviderHouseholdMember("2-Iteration",
                        "Two",
                        "28",
                        "6",
                        "2000",
                        "Sister",
                        "444-55-5555")
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerHouseholdMemberFirstName_1")).isEqualTo(
                new SingleField("providerHouseholdMemberFirstName", "1-Iteration", 1));
        assertThat(result.get("providerHouseholdMemberLastName_1")).isEqualTo(
                new SingleField("providerHouseholdMemberLastName", "One", 1));
        assertThat(result.get("providerHouseholdMemberRelationship_1")).isEqualTo(
                new SingleField("providerHouseholdMemberRelationship", "brother", 1));
        assertThat(result.get("providerHouseholdMemberSSN_1")).isEqualTo(
                new SingleField("providerHouseholdMemberSSN", "555-55-5555", 1));
        assertThat(result.get("providerHouseholdMemberDateOfBirth_1")).isEqualTo(
                new SingleField("providerHouseholdMemberDateOfBirth", "10/30/2004", 1));

        assertThat(result.get("providerHouseholdMemberFirstName_2")).isEqualTo(
                new SingleField("providerHouseholdMemberFirstName", "2-Iteration", 2));
        assertThat(result.get("providerHouseholdMemberLastName_2")).isEqualTo(
                new SingleField("providerHouseholdMemberLastName", "Two", 2));
        assertThat(result.get("providerHouseholdMemberRelationship_2")).isEqualTo(
                new SingleField("providerHouseholdMemberRelationship", "Sister", 2));
        assertThat(result.get("providerHouseholdMemberSSN_2")).isEqualTo(
                new SingleField("providerHouseholdMemberSSN", "444-55-5555", 2));
        assertThat(result.get("providerHouseholdMemberDateOfBirth_2")).isEqualTo(
                new SingleField("providerHouseholdMemberDateOfBirth", "6/28/2000", 2));
    }

    @Test
    public void doesNotSetProviderLanguageIfProviderLanguagesOfferedIsNone() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerLanguagesOffered[]", List.of("None"))
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerLanguageEnglish")).isEqualTo(null);
        assertThat(result.get("providerLanguageSpanish")).isEqualTo(null);
        assertThat(result.get("providerLanguagePolish")).isEqualTo(null);
        assertThat(result.get("providerLanguageChinese")).isEqualTo(null);
        assertThat(result.get("providerLanguageOther")).isEqualTo(null);
        assertThat(result.get("providerLanguageOtherDetail")).isEqualTo(null);
    }


    @Test
    public void doesNotSetProviderLanguageIfProviderLanguagesOfferedIsNotPresent() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerLanguageEnglish")).isEqualTo(null);
        assertThat(result.get("providerLanguageSpanish")).isEqualTo(null);
        assertThat(result.get("providerLanguagePolish")).isEqualTo(null);
        assertThat(result.get("providerLanguageChinese")).isEqualTo(null);
        assertThat(result.get("providerLanguageOther")).isEqualTo(null);
        assertThat(result.get("providerLanguageOtherDetail")).isEqualTo(null);
    }

    @Test
    public void selectsProviderLanguageCheckboxesWhenProviderLanguagesOfferedIncludesSupportedLanguages() {
        //English, Spanish, Polish, and Chinese languages have Provider Language checkboxes that can be selected
        //Any other language that is included in the providerLanguages offered array results in the PROVIDER_LANGUAGE_OTHER field being set to true
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerLanguagesOffered[]", List.of("English", "Spanish", "Polish", "Chinese"))
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerLanguageEnglish")).isEqualTo(new SingleField("providerLanguageEnglish", "true", null));
        assertThat(result.get("providerLanguageSpanish")).isEqualTo(new SingleField("providerLanguageSpanish", "true", null));
        assertThat(result.get("providerLanguagePolish")).isEqualTo(new SingleField("providerLanguagePolish", "true", null));
        assertThat(result.get("providerLanguageChinese")).isEqualTo(new SingleField("providerLanguageChinese", "true", null));
        assertThat(result.get("providerLanguageOther")).isEqualTo(null);
        assertThat(result.get("providerLanguageOtherDetail")).isEqualTo(null);
    }

    @Test
    public void selectOtherCheckboxAndListLanguagesInOtherDetailWhenProviderLanguagesValueIsOther() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerLanguagesOffered[]", List.of("Tagalog", "Hindi"))
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerLanguageOther")).isEqualTo(new SingleField("providerLanguageOther", "true", null));
        assertThat(result.get("providerLanguageOtherDetail")).isEqualTo(new SingleField("providerLanguageOtherDetail",
                String.format("%s, %s", TAGALOG.getProviderLanguageOtherDetailPdfFieldValue(),
                        HINDI.getProviderLanguageOtherDetailPdfFieldValue()), null));
    }

    @Test
    public void whenOtherIsIncludedAsProviderLanguageOfferedSelectOtherCheckboxAndAddDetails() {
        String providerLanguagesOfferedOtherString = "test, double test, gibberish";
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerLanguagesOffered[]", List.of("other"))
                .with("providerLanguagesOffered_other", providerLanguagesOfferedOtherString)
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerLanguageOther")).isEqualTo(new SingleField("providerLanguageOther", "true", null));
        assertThat(result.get("providerLanguageOtherDetail")).isEqualTo(
                new SingleField("providerLanguageOtherDetail", providerLanguagesOfferedOtherString, null));
    }

    @Test
    public void shouldSelectOtherCheckboxAndAllOtherLanguagesWhenOtherProviderLanguagesAreSelected() {
        String providerLanguagesOfferedOtherString = "test, double gibberish";
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerLanguagesOffered[]", List.of("Tagalog", "other"))
                .with("providerLanguagesOffered_other", providerLanguagesOfferedOtherString)
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerLanguageOther")).isEqualTo(new SingleField("providerLanguageOther", "true", null));
        assertThat(result.get("providerLanguageOtherDetail")).isEqualTo(new SingleField("providerLanguageOtherDetail",
                String.format("%s, %s", TAGALOG.getProviderLanguageOtherDetailPdfFieldValue(),
                        providerLanguagesOfferedOtherString), null));
    }

    @Test
    public void shouldSelectLanguagesWithCheckboxesAndOtherLanguagesWhenOtherProviderLanguagesAreSelected() {
        String providerLanguagesOfferedOtherString = "test, double gibberish";
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerLanguagesOffered[]", List.of("English", "Tagalog", "Spanish", "Hindi", "other"))
                .with("providerLanguagesOffered_other", providerLanguagesOfferedOtherString)
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerLanguageEnglish")).isEqualTo(new SingleField("providerLanguageEnglish", "true", null));
        assertThat(result.get("providerLanguageSpanish")).isEqualTo(new SingleField("providerLanguageSpanish", "true", null));
        assertThat(result.get("providerLanguageOther")).isEqualTo(new SingleField("providerLanguageOther", "true", null));
        assertThat(result.get("providerLanguageOtherDetail")).isEqualTo(new SingleField("providerLanguageOtherDetail",
                String.format("%s, %s, %s", TAGALOG.getProviderLanguageOtherDetailPdfFieldValue(),
                        HINDI.getProviderLanguageOtherDetailPdfFieldValue(), providerLanguagesOfferedOtherString), null));
        assertThat(result.get("providerLanguageChinese")).isNotEqualTo(new SingleField("providerLanguageChinese", "true", null));
    }

    @Test
    public void shouldPrintProviderIdentityCheckSSNToPDFIfPresent() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerIdentityCheckSSN", "445-32-6666")
                .with("providerTaxIdSSN", "555-55-5555")
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerSSN")).isEqualTo(new SingleField("providerSSN", "445-32-6666", null));
    }

    @Test
    public void shouldPrintProviderTaxIdSSNIfPresent() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerTaxIdSSN", "444-44-4444")
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerSSN")).isEqualTo(new SingleField("providerSSN", "444-44-4444", null));
    }

    @Test
    public void setsITINAsSSNWhenAvailable() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerITIN", "123456789")
                .with("providerTaxIdSSN", "444-44-4444")
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerSSN")).isEqualTo(new SingleField("providerSSN", "123456789", null));
    }

    @Test
    public void doesNotSetProviderTypeIfNull() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerType")).isEqualTo(null);
    }
    @Test
    public void shouldSelectLicensedDayCareCenter() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerType", ProviderType.LICENSED_DAY_CARE_CENTER.name())
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerType")).isEqualTo(new SingleField("providerType", "LICENSED_DAY_CARE_CENTER_760", null));
    }

    @Test
    public void shouldSelectLicensedDayCareHome() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerType", ProviderType.LICENSED_DAY_CARE_HOME.name())
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerType")).isEqualTo(new SingleField("providerType", "LICENSED_DAY_CARE_HOME_762", null));
    }

    @Test
    public void shouldSelectLicensedGroupDayCare() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerType", ProviderType.LICENSED_GROUP_CHILD_CARE_HOME.name())
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerType")).isEqualTo(
                new SingleField("providerType", "LICENSED_GROUP_DAY_CARE_HOME_763", null));
    }

    @Test
    public void shouldSelectDayCareCenterExemptFromLicensing() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerType", ProviderType.LICENSE_EXEMPT_CHILD_CARE_CENTER.name())
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerType")).isEqualTo(
                new SingleField("providerType", "DAY_CARE_CENTER_EXEMPT_FROM_LICENSING_761", null));
    }

    @Test
    public void shouldSelectCareByRelativeInChildCareProvidersHome() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerType", ProviderType.LICENSE_EXEMPT_RELATIVE_IN_PROVIDER_HOME.name())
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerType")).isEqualTo(
                new SingleField("providerType", "CARE_BY_RELATIVE_IN_CHILD_CARE_PROVIDERS_HOME_765", null));
    }

    @Test
    public void shouldSelectCareByNonRelativeInChildCareProvidersHome() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerType", ProviderType.LICENSE_EXEMPT_NONRELATIVE_IN_PROVIDER_HOME.name())
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerType")).isEqualTo(
                new SingleField("providerType", "CARE_BY_NON_RELATIVE_IN_CHILD_CARE_PROVIDERS_HOME_764", null));
    }

    @Test
    public void shouldSelectCareByRelativeInChildsHome() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerType", ProviderType.LICENSE_EXEMPT_RELATIVE_IN_CHILDS_HOME.name())
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerType")).isEqualTo(
                new SingleField("providerType", "CARE_BY_RELATIVE_IN_CHILDS_HOME_767", null));
    }

    @Test
    public void shouldSelectCareByNonRelativeInChildsHome() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerType", ProviderType.LICENSE_EXEMPT_NONRELATIVE_IN_CHILDS_HOME.name())
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerType")).isEqualTo(
                new SingleField("providerType", "CARE_BY_NON_RELATIVE_IN_CHILDS_HOME_766", null));
    }
}
