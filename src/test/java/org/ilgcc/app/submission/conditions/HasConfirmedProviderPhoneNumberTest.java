package org.ilgcc.app.submission.conditions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

class HasConfirmedProviderPhoneNumberTest {
    @Test
    void shouldReturnFalseWhenConfirmedProviderPhoneNumberIsFalse() {
        Submission testSubmission = new SubmissionTestBuilder().with("hasConfirmedIntendedProviderPhoneNumber", "false").build();
        HasConfirmedProviderPhoneNumber hasConfirmedProviderPhoneNumber = new HasConfirmedProviderPhoneNumber();
        assertThat(hasConfirmedProviderPhoneNumber.run(testSubmission)).isFalse();
    }

    @Test
    void shouldReturnTrueWhenConfirmedProviderPhoneNumberIsTrue() {
        Submission testSubmission = new SubmissionTestBuilder().with("hasConfirmedIntendedProviderPhoneNumber", "true").build();
        HasConfirmedProviderPhoneNumber hasConfirmedProviderPhoneNumber = new HasConfirmedProviderPhoneNumber();
        assertThat(hasConfirmedProviderPhoneNumber.run(testSubmission)).isTrue();
    }
}