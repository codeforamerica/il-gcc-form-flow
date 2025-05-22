package org.ilgcc.app.submission.conditions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContactProviderViaTextTest {
    
    Submission testSubmission;
    
    ContactProviderViaText contactProviderViaText;
    UUID uuid1;
    UUID uuid2;
    UUID uuid3;

    @BeforeEach
    void setUp() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("contactProviderMethod[]", List.of("TEXT"));

        uuid1 = UUID.randomUUID();
        Map<String, Object> subflowData1 = new HashMap<>();
        subflowData1.put("uuid", uuid1.toString());
        subflowData1.put("contactProviderMethod[]", List.of("TEXT"));

        uuid2 = UUID.randomUUID();
        Map<String, Object> subflowData2 = new HashMap<>();
        subflowData2.put("uuid", uuid2.toString());
        subflowData2.put("contactProviderMethod[]", List.of("EMAIL", "OTHER"));

        uuid3 = UUID.randomUUID();
        Map<String, Object> subflowData3 = new HashMap<>();
        subflowData3.put("uuid", uuid3.toString());
        subflowData3.put("contactProviderMethod[]", List.of("TEXT", "OTHER"));

        inputData.put("contactProviders", List.of(subflowData1, subflowData2, subflowData3));
        testSubmission = Submission.builder()
                .inputData(inputData)
                .build();
    }
    
    @Test
    void contactProviderViaText_shouldReturnTrueWhenUserSelectedText() {
        contactProviderViaText = new ContactProviderViaText(false);
        assertThat(contactProviderViaText.run(testSubmission)).isTrue();

        contactProviderViaText = new ContactProviderViaText(true);
        assertThat(contactProviderViaText.run(testSubmission)).isTrue();
    }

    @Test
    void contactProviderViaText_shouldReturnFalseWhenUserDidNotSelectText() {
        contactProviderViaText = new ContactProviderViaText(false);
        testSubmission.getInputData().put("contactProviderMethod[]", List.of("EMAIL", "OTHER"));
        assertThat(contactProviderViaText.run(testSubmission)).isFalse();

        contactProviderViaText = new ContactProviderViaText(true);
        testSubmission.getInputData().put("contactProviderMethod[]", List.of("EMAIL", "OTHER"));
        assertThat(contactProviderViaText.run(testSubmission)).isFalse();
    }

    @Test
    void contactProviderViaText_shouldReturnTrueWhenUserSelectedText_MultipleProviders() {
        contactProviderViaText = new ContactProviderViaText(false);
        assertThat(contactProviderViaText.run(testSubmission, uuid1.toString())).isFalse();

        contactProviderViaText = new ContactProviderViaText(true);
        assertThat(contactProviderViaText.run(testSubmission, uuid1.toString())).isTrue();
        assertThat(contactProviderViaText.run(testSubmission, uuid2.toString())).isFalse();
        assertThat(contactProviderViaText.run(testSubmission, uuid3.toString())).isTrue();
    }
}