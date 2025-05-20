package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.pdf.helpers.ProviderApplicationPreparerHelper;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.enums.ProviderDenialReason;
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
public class ProviderApplicationPreparerTest {

    @Autowired
    private ProviderApplicationPreparerHelper preparer;

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
        assertThat(result.get("providerResponseBusinessName")).isEqualTo(new SingleField("providerResponseBusinessName", "ProviderName", null));
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
}
