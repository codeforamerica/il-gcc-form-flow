package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.enums.ProviderType;
import org.junit.jupiter.api.Test;

public class SetProviderTypeTest {

    private final SetProviderType action = new SetProviderType();

    @Test
    public void setsProviderTypeNullIfMissingData() {
        Submission submission = new SubmissionTestBuilder()
                .with("providerCurrentlyLicensed", "")
                .build();

        Map<String, Object> formData = Map.of(
                "providerLicensedCareLocation", ""
        );

        FormSubmission formSubmission = new FormSubmission(formData);

        action.run(formSubmission, submission);

        assertThat(submission.getInputData().get("providerType")).isEqualTo(null);
    }

    @Test
    public void setsLicensedChildCareCenter() {
        Submission submission = new SubmissionTestBuilder()
                .with("providerCurrentlyLicensed", "true")
                .build();

        Map<String, Object> formData = Map.of(
                "providerLicensedCareLocation", "childCareCenter"
        );

        FormSubmission formSubmission = new FormSubmission(formData);

        action.run(formSubmission, submission);

        assertThat(submission.getInputData().get("providerType")).isEqualTo(ProviderType.LICENSED_DAY_CARE_CENTER.name());
    }

    @Test
    public void setsLicensedChildCareHome() {
        Submission submission = new SubmissionTestBuilder()
                .with("providerCurrentlyLicensed", "true")
                .build();

        Map<String, Object> formData = Map.of(
                "providerLicensedCareLocation", "childCareHome"
        );

        FormSubmission formSubmission = new FormSubmission(formData);

        action.run(formSubmission, submission);

        assertThat(submission.getInputData().get("providerType")).isEqualTo(ProviderType.LICENSED_DAY_CARE_HOME.name());
    }

    @Test
    public void setLicensedGroupChildCareHome() {
        Submission submission = new SubmissionTestBuilder()
                .with("providerCurrentlyLicensed", "true")
                .build();

        Map<String, Object> formData = Map.of(
                "providerLicensedCareLocation", "groupChildCareHome"
        );

        FormSubmission formSubmission = new FormSubmission(formData);

        action.run(formSubmission, submission);

        assertThat(submission.getInputData().get("providerType")).isEqualTo(ProviderType.LICENSED_GROUP_CHILD_CARE_HOME.name());
    }

    @Test
    public void setLicenseExemptChildCareCenter() {
        Submission submission = new SubmissionTestBuilder()
                .with("providerCurrentlyLicensed", "false")
                .build();

        Map<String, Object> formData = Map.of("providerLicenseExemptType", "License-exempt");

        FormSubmission formSubmission = new FormSubmission(formData);

        action.run(formSubmission, submission);

        assertThat(submission.getInputData().get("providerType")).isEqualTo(ProviderType.LICENSE_EXEMPT_CHILD_CARE_CENTER.name());
    }

    @Test
    public void setLicenseExemptRelativeInProviderHome() {
        Submission submission = new SubmissionTestBuilder()
                .with("providerCurrentlyLicensed", "false")
                .with("providerLicenseExemptType", "Self")
                .with("providerLicenseExemptCareLocation", "Providers home")
                .build();

        Map<String, Object> formData = Map.of(
                "providerLicenseExemptRelationship", "Relative"
        );

        FormSubmission formSubmission = new FormSubmission(formData);

        action.run(formSubmission, submission);

        assertThat(submission.getInputData().get("providerType")).isEqualTo(
                ProviderType.LICENSE_EXEMPT_RELATIVE_IN_PROVIDER_HOME.name());
    }

    @Test
    public void setLicenseExemptRelativeInChildHome() {
        Submission submission = new SubmissionTestBuilder()
                .with("providerCurrentlyLicensed", "false")
                .with("providerLicenseExemptType", "Self")
                .with("providerLicenseExemptCareLocation", "Childs home")
                .build();

        Map<String, Object> formData = Map.of(
                "providerLicenseExemptRelationship", "Relative"
        );

        FormSubmission formSubmission = new FormSubmission(formData);

        action.run(formSubmission, submission);

        assertThat(submission.getInputData().get("providerType")).isEqualTo(
                ProviderType.LICENSE_EXEMPT_RELATIVE_IN_CHILDS_HOME.name());
    }

    @Test
    public void setLicenseExemptNonRelativeInProviderHome() {
        Submission submission = new SubmissionTestBuilder()
                .with("providerCurrentlyLicensed", "false")
                .with("providerLicenseExemptType", "Self")
                .with("providerLicenseExemptCareLocation", "Providers home")
                .build();

        Map<String, Object> formData = Map.of(
                "providerLicenseExemptRelationship", "Not related"
        );

        FormSubmission formSubmission = new FormSubmission(formData);

        action.run(formSubmission, submission);

        assertThat(submission.getInputData().get("providerType")).isEqualTo(
                ProviderType.LICENSE_EXEMPT_NONRELATIVE_IN_PROVIDER_HOME.name());
    }

    @Test
    public void setLicenseExemptNonRelativeInChildHome() {
        Submission submission = new SubmissionTestBuilder()
                .with("providerCurrentlyLicensed", "false")
                .with("providerLicenseExemptType", "Self")
                .with("providerLicenseExemptCareLocation", "Childs home")
                .build();

        Map<String, Object> formData = Map.of(
                "providerLicenseExemptRelationship", "Not related"
        );

        FormSubmission formSubmission = new FormSubmission(formData);

        action.run(formSubmission, submission);

        assertThat(submission.getInputData().get("providerType")).isEqualTo(
                ProviderType.LICENSE_EXEMPT_NONRELATIVE_IN_CHILDS_HOME.name());
    }
}
