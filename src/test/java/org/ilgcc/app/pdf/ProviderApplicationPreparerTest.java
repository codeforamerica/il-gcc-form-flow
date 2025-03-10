package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = IlGCCApplication.class
)
@ActiveProfiles("test")
public class ProviderApplicationPreparerTest {

    @Autowired
    private ProviderApplicationPreparer preparer;

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
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerNameCorporate")).isEqualTo(new SingleField("providerNameCorporate", "ProviderName", null));
        assertThat(result.get("providerPhoneNumber")).isEqualTo(new SingleField("providerPhoneNumber", "(125) 785-67896", null));
        assertThat(result.get("providerEmail")).isEqualTo(new SingleField("providerEmail", "mail@test.com", null));
        assertThat(result.get("providerResponse")).isEqualTo(
                new SingleField("providerResponse", "No response from provider", null));

        assertThat(result.get("dayCareName")).isEqualTo(null);
        assertThat(result.get("dayCareIdNumber")).isEqualTo(null);
        assertThat(result.get("dayCareAddressStreet")).isEqualTo(null);
        assertThat(result.get("dayCareAddressApt")).isEqualTo(null);
        assertThat(result.get("dayCareAddressCity")).isEqualTo(null);
        assertThat(result.get("dayCareAddressState")).isEqualTo(null);
        assertThat(result.get("dayCareAddressZip")).isEqualTo(null);
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
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerResponse")).isEqualTo(
                new SingleField("providerResponse", "", null));
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
        assertThat(result.get("providerNameCorporate")).isNull();

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
                .with("providerMailingAddressSameAsServiceAddress[]", List.of("yes"))
                .with("providerResponseServiceZipCode", "60112")
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
        assertThat(result.get("providerNameCorporate")).isNull();

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
    public void mapsFamilyIntendedProviderInfoIfProviderDoesNotAgreeToCare() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerResponseAgreeToCare", "false")
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
        assertThat(result.get("providerNameCorporate")).isEqualTo(new SingleField("providerNameCorporate", "ProviderName", null));
        assertThat(result.get("providerPhoneNumber")).isEqualTo(new SingleField("providerPhoneNumber", "(125) 785-67896", null));
        assertThat(result.get("providerEmail")).isEqualTo(new SingleField("providerEmail", "mail@test.com", null));
        assertThat(result.get("providerResponse")).isEqualTo(
                new SingleField("providerResponse", "Provider declined", null));

        assertThat(result.get("providerResponseFirstName")).isEqualTo(null);
        assertThat(result.get("providerResponseLastName")).isEqualTo(null);
        assertThat(result.get("providerResponseBusinessName")).isEqualTo(null);
    }

    @Test
    public void mapsProviderResponseIfProviderAgreesToCare() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .withProviderStateLicense()
                .with("providerResponseAgreeToCare", "true")
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
                new SingleField("providerResponseServiceStreetAddress1", "123 Main St", null));
        assertThat(result.get("providerResponseServiceStreetAddress2")).isEqualTo(
                new SingleField("providerResponseServiceStreetAddress2", "Unit 10", null));
        assertThat(result.get("providerResponseServiceCity")).isEqualTo(
                new SingleField("providerResponseServiceCity", "DeKalb", null));
        assertThat(result.get("providerResponseServiceState")).isEqualTo(
                new SingleField("providerResponseServiceState", "IL", null));
        assertThat(result.get("providerResponseServiceZipCode")).isEqualTo(
                new SingleField("providerResponseServiceZipCode", "60112", null));
        assertThat(result.get("providerResponseContactPhoneNumber")).isEqualTo(
                new SingleField("providerResponseContactPhoneNumber", "(111) 222-3333", null));
        assertThat(result.get("providerResponseContactEmail")).isEqualTo(
                new SingleField("providerResponseContactEmail", "mail@daycareplace.org", null));
        assertThat(result.get("providerLicenseNumber")).isEqualTo(
                new SingleField("providerLicenseNumber", "123453646 (IL)", null));

        assertThat(result.get("dayCareName")).isEqualTo(null);
        assertThat(result.get("dayCareIdNumber")).isEqualTo(null);
        assertThat(result.get("dayCareAddressStreet")).isEqualTo(null);
        assertThat(result.get("dayCareAddressZip")).isEqualTo(null);
    }

    @Test
    public void mapsApplicantConfirmationCodeToPdf(){
        providerSubmission = new SubmissionTestBuilder()
            .withFlow("providerresponse")
            .withProviderSubmissionData()
            .withProviderStateLicense()
            .with("providerResponseAgreeToCare", "true")
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
        assertThat(result.get("clientResponseConfirmationCode")).isEqualTo(new SingleField("clientResponseConfirmationCode", "testConfirmationCode", null));

    }

}
