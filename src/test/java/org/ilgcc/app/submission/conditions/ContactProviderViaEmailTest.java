package org.ilgcc.app.submission.conditions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContactProviderViaEmailTest {

    Submission testSubmission;

    ContactProviderViaEmail contactProviderViaEmail;
    UUID uuid1;
    UUID uuid2;
    UUID uuid3;

    @BeforeEach
    void setUp() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("contactProviderMethod[]", List.of("EMAIL"));

        uuid1 = UUID.randomUUID();
        Map<String, Object> subflowData1 = new HashMap<>();
        subflowData1.put("uuid", uuid1.toString());
        subflowData1.put("contactProviderMethod[]", List.of("EMAIL"));

        uuid2 = UUID.randomUUID();
        Map<String, Object> subflowData2 = new HashMap<>();
        subflowData2.put("uuid", uuid2.toString());
        subflowData2.put("contactProviderMethod[]", List.of("TEXT", "OTHER"));

        uuid3 = UUID.randomUUID();
        Map<String, Object> subflowData3 = new HashMap<>();
        subflowData3.put("uuid", uuid3.toString());
        subflowData3.put("contactProviderMethod[]", List.of("EMAIL", "OTHER"));

        inputData.put("contactProviders", List.of(subflowData1, subflowData2, subflowData3));
        testSubmission = Submission.builder().inputData(inputData).build();
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

    @Test
    void contactProviderViaEmail_shouldReturnTrueWhenUserSelectedEmail_MultipleProviders() {
        contactProviderViaEmail = new ContactProviderViaEmail(false);
        assertThat(contactProviderViaEmail.run(testSubmission, uuid1.toString())).isFalse();

        contactProviderViaEmail = new ContactProviderViaEmail(true);
        assertThat(contactProviderViaEmail.run(testSubmission, uuid1.toString())).isTrue();
        assertThat(contactProviderViaEmail.run(testSubmission, uuid2.toString())).isFalse();
        assertThat(contactProviderViaEmail.run(testSubmission, uuid3.toString())).isTrue();
    }
}