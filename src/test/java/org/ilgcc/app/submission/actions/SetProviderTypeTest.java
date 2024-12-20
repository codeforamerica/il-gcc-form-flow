package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.enums.ProviderType;
import org.junit.jupiter.api.Test;

public class SetProviderTypeTest {

    private final SetProviderType action = new SetProviderType();

    @Test
    public void setsProviderTypeNullIfMissingData() {
        Submission submission = new SubmissionTestBuilder()
                .with("providerCurrentlyLicensed", "")
                .with("providerLicensedCareLocation", "")
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("providerType")).isEqualTo(null);
    }

    @Test
    public void setsLicensedChildCareCenter() {
        Submission submission = new SubmissionTestBuilder()
                .with("providerCurrentlyLicensed", "true")
                .with("providerLicensedCareLocation", "childCareCenter")
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("providerType")).isEqualTo(ProviderType.LICENSED_DAY_CARE_CENTER.name());
    }

    @Test
    public void setsLicensedChildCareHome() {
        Submission submission = new SubmissionTestBuilder()
                .with("providerCurrentlyLicensed", "true")
                .with("providerLicensedCareLocation", "childCareHome")
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("providerType")).isEqualTo(ProviderType.LICENSED_DAY_CARE_HOME.name());
    }

    @Test
    public void setsLicensedGroupChildCareHome() {
        Submission submission = new SubmissionTestBuilder()
                .with("providerCurrentlyLicensed", "true")
                .with("providerLicensedCareLocation", "groupChildCareHome")
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("providerType")).isEqualTo(ProviderType.LICENSED_GROUP_CHILD_CARE_HOME.name());
    }

    @Test
    public void setLicenseExemptChildCareCenter() {
        Submission submission = new SubmissionTestBuilder()
                .with("providerCurrentlyLicensed", "false")
                .with("providerLicenseExemptType", "License-exempt")
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("providerType")).isEqualTo(ProviderType.LICENSE_EXEMPT_CHILD_CARE_CENTER.name());
    }

    @Test
    public void setLicenseExemptRelativeInProviderHome() {
        Submission submission = new SubmissionTestBuilder()
                .with("providerCurrentlyLicensed", "false")
                .with("providerLicenseExemptType", "Self")
                .with("providerLicenseExemptCareLocation", "Providers home")
                .with("providerLicenseExemptRelationship", "Relative")
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("providerType")).isEqualTo(
                ProviderType.LICENSE_EXEMPT_RELATIVE_IN_PROVIDER_HOME.name());
    }

    @Test
    public void setLicenseExemptRelativeInChildHome() {
        Submission submission = new SubmissionTestBuilder()
                .with("providerCurrentlyLicensed", "false")
                .with("providerLicenseExemptType", "Self")
                .with("providerLicenseExemptCareLocation", "Childs home")
                .with("providerLicenseExemptRelationship", "Relative")
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("providerType")).isEqualTo(
                ProviderType.LICENSE_EXEMPT_RELATIVE_IN_CHILDS_HOME.name());
    }

    @Test
    public void setLicenseExemptNonRelativeInProviderHome() {
        Submission submission = new SubmissionTestBuilder()
                .with("providerCurrentlyLicensed", "false")
                .with("providerLicenseExemptType", "Self")
                .with("providerLicenseExemptCareLocation", "Childs home")
                .with("providerLicenseExemptRelationship", "Not related")
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("providerType")).isEqualTo(
                ProviderType.LICENSE_EXEMPT_RELATIVE_IN_CHILDS_HOME.name());
    }

    @Test
    public void setLicenseExemptNonRelativeInChildHome() {
        Submission submission = new SubmissionTestBuilder()
                .with("providerCurrentlyLicensed", "false")
                .with("providerLicenseExemptType", "Self")
                .with("providerLicenseExemptCareLocation", "Childs home")
                .with("providerLicenseExemptRelationship", "Relative")
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("providerType")).isEqualTo(
                ProviderType.LICENSE_EXEMPT_RELATIVE_IN_CHILDS_HOME.name());
    }
}
