package org.ilgcc.app.submission.conditions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContactProviderViaOtherTest {
    
    Submission testSubmission;
    
    ContactProviderViaOther contactProviderViaOther;
    UUID uuid1;
    UUID uuid2;
    UUID uuid3;

    @BeforeEach
    void setUp() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("contactProviderMethod[]", List.of("OTHER"));

        uuid1 = UUID.randomUUID();
        Map<String, Object> subflowData1 = new HashMap<>();
        subflowData1.put("uuid", uuid1.toString());
        subflowData1.put("contactProviderMethod[]", List.of("OTHER"));

        uuid2 = UUID.randomUUID();
        Map<String, Object> subflowData2 = new HashMap<>();
        subflowData2.put("uuid", uuid2.toString());
        subflowData2.put("contactProviderMethod[]", List.of("EMAIL", "TEXT"));

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

    @Test
    void contactProviderViaOther_shouldReturnTrueWhenUserSelectedOther_MultipleProviders() {
        contactProviderViaOther = new ContactProviderViaOther(false);
        assertThat(contactProviderViaOther.run(testSubmission, uuid1.toString())).isFalse();

        contactProviderViaOther = new ContactProviderViaOther(true);
        assertThat(contactProviderViaOther.run(testSubmission, uuid1.toString())).isTrue();
        assertThat(contactProviderViaOther.run(testSubmission, uuid2.toString())).isFalse();
        assertThat(contactProviderViaOther.run(testSubmission, uuid3.toString())).isTrue();
    }
}