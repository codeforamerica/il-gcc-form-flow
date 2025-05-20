package org.ilgcc.app.submission.conditions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContactProviderViaTextTest {
    
    Submission testSubmission;
    
    ContactProviderViaText contactProviderViaText = new ContactProviderViaText();

    @BeforeEach
    void setUp() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("contactProviderMethod[]", List.of("TEXT"));
        testSubmission = Submission.builder()
                .inputData(inputData)
                .build();
    }
    
    @Test
    void selectedTextAsProviderContactMethod_shouldReturnTrueWhenUserSelectedText() {
        assertThat(contactProviderViaText.run(testSubmission)).isTrue();
    }

    @Test
    void selectedTextAsProviderContactMethod_shouldReturnFalseWhenUserDidNotSelectText() {
        testSubmission.getInputData().put("contactProviderMethod[]", List.of("EMAIL", "OTHER"));
        assertThat(contactProviderViaText.run(testSubmission)).isFalse();
    }
}