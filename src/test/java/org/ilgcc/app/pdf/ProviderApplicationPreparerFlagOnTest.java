package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = IlGCCApplication.class,
        properties = "il-gcc.dts.expand-existing-provider-flow=true"
)
@ActiveProfiles("test")
public class ProviderApplicationPreparerFlagOnTest {

    @Autowired
    private ProviderApplicationPreparer preparer;

    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;

    private Submission familySubmission;
    private Submission providerSubmission;

    @Test
    public void setsNoResponseWhenNoProviderSubmissionExists() {
        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
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
    public void setsProviderInputtedDataWhenProviderResponds() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
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
        assertThat(result.get("providerNameCorporate")).isNull();

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

        assertThat(result.get("dayCareName")).isEqualTo(null);
        assertThat(result.get("dayCareIdNumber")).isEqualTo(null);
        assertThat(result.get("dayCareAddressStreet")).isEqualTo(null);
        assertThat(result.get("dayCareAddressApt")).isEqualTo(null);
        assertThat(result.get("dayCareAddressCity")).isEqualTo(null);
        assertThat(result.get("dayCareAddressState")).isEqualTo(null);
        assertThat(result.get("dayCareAddressZip")).isEqualTo(null);
    }

    @Test
    public void setsValidatedAddressWhenSelected() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerResponseAgreeToCare", "true")
                .with("useSuggestedProviderAddress", "true")
                .with("providerResponseServiceStreetAddress1_validated", "123 Main Street")
                .with("providerResponseServiceCity_validated", "De Kalb")
                .with("providerResponseServiceState_validated", "IL")
                .with("providerResponseServiceZipCode_validated", "60112-1234")
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
        assertThat(result.get("providerResponseServiceStreetAddress2")).isEqualTo(
                new SingleField("providerResponseServiceStreetAddress2", "", null));
        assertThat(result.get("providerResponseServiceCity")).isEqualTo(
                new SingleField("providerResponseServiceCity", "De Kalb", null));
        assertThat(result.get("providerResponseServiceState")).isEqualTo(
                new SingleField("providerResponseServiceState", "IL", null));
        assertThat(result.get("providerResponseServiceZipCode")).isEqualTo(
                new SingleField("providerResponseServiceZipCode", "60112-1234", null));
        assertThat(result.get("providerResponseContactPhoneNumber")).isEqualTo(
                new SingleField("providerResponseContactPhoneNumber", "(111) 222-3333", null));
        assertThat(result.get("providerResponseContactEmail")).isEqualTo(
                new SingleField("providerResponseContactEmail", "mail@daycareplace.org", null));

        assertThat(result.get("dayCareName")).isEqualTo(null);
        assertThat(result.get("dayCareIdNumber")).isEqualTo(null);
        assertThat(result.get("dayCareAddressStreet")).isEqualTo(null);
        assertThat(result.get("dayCareAddressApt")).isEqualTo(null);
        assertThat(result.get("dayCareAddressCity")).isEqualTo(null);
        assertThat(result.get("dayCareAddressState")).isEqualTo(null);
        assertThat(result.get("dayCareAddressZip")).isEqualTo(null);
    }

    @Test
    public void setsNoProviderInputtedDataWhenProviderResponseCannotBeFound() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .build();

        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withSubmittedAtDate(OffsetDateTime.now())
                .withDayCareProvider()
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .with("providerResponseSubmissionId", UUID.randomUUID())
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerNameCorporate")).isNull();
        assertThat(result.get("providerResponseFirstName")).isNull();
        assertThat(result.get("providerResponseLastName")).isNull();
        assertThat(result.get("providerResponseBusinessName")).isNull();
        assertThat(result.get("providerResponseServiceStreetAddress1")).isNull();
        assertThat(result.get("providerResponseServiceStreetAddress2")).isNull();
        assertThat(result.get("providerResponseServiceCity")).isNull();
        assertThat(result.get("providerResponseServiceState")).isNull();
        assertThat(result.get("providerResponseServiceZipCode")).isNull();
        assertThat(result.get("providerResponseContactPhoneNumber")).isNull();
        assertThat(result.get("providerResponseContactEmail")).isNull();

        assertThat(result.get("dayCareName")).isEqualTo(null);
        assertThat(result.get("dayCareIdNumber")).isEqualTo(null);
        assertThat(result.get("dayCareAddressStreet")).isEqualTo(null);
        assertThat(result.get("dayCareAddressApt")).isEqualTo(null);
        assertThat(result.get("dayCareAddressCity")).isEqualTo(null);
        assertThat(result.get("dayCareAddressState")).isEqualTo(null);
        assertThat(result.get("dayCareAddressZip")).isEqualTo(null);

    }
    @Test
    public void shouldReturnFamilyIntendedProviderInfoIfProviderDoesNotAgreeToCare(){
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
    public void shouldAddProviderInfoToPDFIfProviderAgreesToCare(){
        providerSubmission = new SubmissionTestBuilder()
            .withFlow("providerresponse")
            .withProviderSubmissionData()
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

        assertThat(result.get("dayCareName")).isEqualTo(null);
        assertThat(result.get("dayCareIdNumber")).isEqualTo(null);
        assertThat(result.get("dayCareAddressStreet")).isEqualTo(null);
        assertThat(result.get("dayCareAddressZip")).isEqualTo(null);

    }

}
