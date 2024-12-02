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
import org.ilgcc.app.utils.ChildCareProvider;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = IlGCCApplication.class,
        properties = "il-gcc.dts.expand-existing-provider-flow=false"
)
@ActiveProfiles("test")
public class ProviderApplicationPreparerFlagOffTest {


    @Autowired
    private ProviderApplicationPreparer preparer;

    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;

    private ChildCareProvider provider = ChildCareProvider.OPEN_SESAME;
    private Submission familySubmission;
    private Submission providerSubmission;

    @Test
    public void setsDayCareDataWhenFlagIsOff() {
        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withDayCareProvider()
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);

        assertThat(result.get("providerNameCorporate")).isEqualTo(null);
        assertThat(result.get("providerPhoneNumber")).isEqualTo(null);
        assertThat(result.get("providerEmail")).isEqualTo(null);
        assertThat(result.get("providerResponse")).isEqualTo(null);

        assertThat(result.get("dayCareName")).isEqualTo(new SingleField("dayCareName", provider.getDisplayName(), null));
        assertThat(result.get("dayCareIdNumber")).isEqualTo(new SingleField("dayCareIdNumber", provider.getIdNumber(), null));
        assertThat(result.get("dayCareAddressStreet")).isEqualTo(
                new SingleField("dayCareAddressStreet", provider.getStreet(), null));
        assertThat(result.get("dayCareAddressApt")).isEqualTo(new SingleField("dayCareAddressApt", provider.getApt(), null));
        assertThat(result.get("dayCareAddressCity")).isEqualTo(new SingleField("dayCareAddressCity", provider.getCity(), null));
        assertThat(result.get("dayCareAddressState")).isEqualTo(new SingleField("dayCareAddressState", provider.getState(), null));
        assertThat(result.get("dayCareAddressZip")).isEqualTo(new SingleField("dayCareAddressZip", provider.getZipcode(), null));
    }

    @Test
    public void mapsDayCareDataIfProviderDoesNotAgreeToCare() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .with("providerResponseAgreeToCare", "false")
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
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerNameCorporate")).isNull();

        assertThat(result.get("providerResponseFirstName")).isEqualTo(null);
        assertThat(result.get("providerResponseLastName")).isEqualTo(null);
        assertThat(result.get("providerResponseBusinessName")).isEqualTo(null);
        assertThat(result.get("providerResponseServiceAddress1")).isEqualTo(null);
        assertThat(result.get("providerResponseServiceAddress2")).isEqualTo(null);
        assertThat(result.get("providerResponseServiceCity")).isEqualTo(null);
        assertThat(result.get("providerResponseServiceState")).isEqualTo(null);
        assertThat(result.get("providerResponseServiceZipCode")).isEqualTo(null);
        assertThat(result.get("providerResponseContactPhoneNumber")).isEqualTo(null);
        assertThat(result.get("providerResponseContactEmail")).isEqualTo(null);

        assertThat(result.get("dayCareName")).isEqualTo(new SingleField("dayCareName", provider.getDisplayName(), null));
        assertThat(result.get("dayCareIdNumber")).isEqualTo(new SingleField("dayCareIdNumber", provider.getIdNumber(), null));
        assertThat(result.get("dayCareAddressStreet")).isEqualTo(
                new SingleField("dayCareAddressStreet", provider.getStreet(), null));
        assertThat(result.get("dayCareAddressApt")).isEqualTo(new SingleField("dayCareAddressApt", provider.getApt(), null));
        assertThat(result.get("dayCareAddressCity")).isEqualTo(new SingleField("dayCareAddressCity", provider.getCity(), null));
        assertThat(result.get("dayCareAddressState")).isEqualTo(new SingleField("dayCareAddressState", provider.getState(), null));
        assertThat(result.get("dayCareAddressZip")).isEqualTo(new SingleField("dayCareAddressZip", provider.getZipcode(), null));
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
}
