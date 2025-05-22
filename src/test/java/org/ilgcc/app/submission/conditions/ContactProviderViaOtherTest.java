package org.ilgcc.app.submission.conditions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContactProviderViaOtherTest {
    
    Submission testSubmission;
    
    ContactProviderViaOther contactProviderViaOther;

    @BeforeEach
    void setUp() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("contactProviderMethod[]", List.of("OTHER"));
        testSubmission = Submission.builder()
                .inputData(inputData)
                .build();
    }
    
    @Test
    void contactProviderViaOther_shouldReturnTrueWhenUserSelectedOther() {
        contactProviderViaOther = new ContactProviderViaOther(false);
        assertThat(contactProviderViaOther.run(testSubmission)).isTrue();

        contactProviderViaOther = new ContactProviderViaOther(true);
        assertThat(contactProviderViaOther.run(testSubmission)).isTrue();
    }

    @Test
    void contactProviderViaOther_shouldReturnFalseWhenUserDidNotSelectOther() {
        contactProviderViaOther = new ContactProviderViaOther(false);
        testSubmission.getInputData().put("contactProviderMethod[]", List.of("EMAIL", "TEXT"));
        assertThat(contactProviderViaOther.run(testSubmission)).isFalse();

        contactProviderViaOther = new ContactProviderViaOther(true);
        testSubmission.getInputData().put("contactProviderMethod[]", List.of("EMAIL", "TEXT"));
        assertThat(contactProviderViaOther.run(testSubmission)).isFalse();
    }
}