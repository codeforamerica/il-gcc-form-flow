package org.ilgcc.app.submission.conditions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContactProviderViaTextTest {
    
    Submission testSubmission;
    
    ContactProviderViaText contactProviderViaText = new ContactProviderViaText(true);

    @BeforeEach
    void setUp() {
        testSubmission = Submission.builder()
                .inputData(Map.of("contactProviderMethod[]", List.of("TEXT")))
                .build();
    }
    
    @Test
    void selectedTextAsProviderContactMethod_shouldReturnTrueWhenUserSelectedText() {
        assertThat(contactProviderViaText.run(testSubmission)).isTrue();
    }
}