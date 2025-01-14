package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.Map;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.enums.ProviderType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = IlGCCApplication.class
)
@ActiveProfiles("test")
public class ProviderTypePreparerTest {


    @Autowired
    private ProviderTypePreparer preparer;

    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;

    private Submission familySubmission;
    private Submission providerSubmission;

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
