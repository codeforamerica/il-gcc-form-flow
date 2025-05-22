package org.ilgcc.app.submission.conditions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContactProviderViaEmailTest {
    
    Submission testSubmission;
    
    ContactProviderViaEmail contactProviderViaEmail;

    @BeforeEach
    void setUp() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("contactProviderMethod[]", List.of("EMAIL"));
        testSubmission = Submission.builder()
                .inputData(inputData)
                .build();
    }
    
    @Test
    void contactProviderViaEmail_shouldReturnTrueWhenUserSelectedEmail() {
        contactProviderViaEmail = new ContactProviderViaEmail(false);
        assertThat(contactProviderViaEmail.run(testSubmission)).isTrue();

        contactProviderViaEmail = new ContactProviderViaEmail(true);
        assertThat(contactProviderViaEmail.run(testSubmission)).isTrue();
    }

    @Test
    void contactProviderViaEmail_shouldReturnFalseWhenUserDidNotSelectEmail() {
        contactProviderViaEmail = new ContactProviderViaEmail(false);
        testSubmission.getInputData().put("contactProviderMethod[]", List.of("TEXT", "OTHER"));
        assertThat(contactProviderViaEmail.run(testSubmission)).isFalse();

        contactProviderViaEmail = new ContactProviderViaEmail(true);
        testSubmission.getInputData().put("contactProviderMethod[]", List.of("TEXT", "OTHER"));
        assertThat(contactProviderViaEmail.run(testSubmission)).isFalse();
    }
}