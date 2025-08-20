package org.ilgcc.app.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.SubmissionRepositoryService;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ActiveProfiles("test")
@SpringBootTest

class ProviderSubmissionUtilitiesTest {
    private Submission familySubmission;
    private Submission providerSubmission;
    private Submission secondProviderSubmission;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Test
    void formatChildNamesAsCommaSeparatedList() {
        Submission singleChildName = new SubmissionTestBuilder().withChild("John", "Doe", "true").build();
        assertThat(ProviderSubmissionUtilities.formatChildNamesAsCommaSeparatedList(singleChildName, "and")).isEqualTo("John Doe");

        Submission twoChildren = new SubmissionTestBuilder().withChild("John", "Doe", "true").withChild("Jane", "Doe", "true")
                .build();
        assertThat(ProviderSubmissionUtilities.formatChildNamesAsCommaSeparatedList(twoChildren, "and")).isEqualTo(
                "John Doe and Jane Doe");

        Submission threeChildren = new SubmissionTestBuilder().withChild("John", "Doe", "true").withChild("Jane", "Doe", "true")
                .withChild("June", "Doe", "true").build();
        assertThat(ProviderSubmissionUtilities.formatChildNamesAsCommaSeparatedList(threeChildren, "and")).isEqualTo(
                "John Doe, Jane Doe and June Doe");
    }

    @Test
    void threeBusinessDaysFromSubmittedAtDate_noHoliday() {
        ZoneId chicagoTimeZone = ZoneId.of("America/Chicago");

        // Monday submission, Thursday Expiration
        OffsetDateTime submittedAt = OffsetDateTime.parse("2024-12-02T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 5, 9, 56, 54).atZone(chicagoTimeZone));

        // Tuesday submission, Friday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-03T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 6, 9, 56, 54).atZone(chicagoTimeZone));

        // Wednesday submission, Monday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-04T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 9, 9, 56, 54).atZone(chicagoTimeZone));

        // Thursday submission, Tuesday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-05T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 10, 9, 56, 54).atZone(chicagoTimeZone));

        // Friday submission, Wednesday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-06T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 11, 9, 56, 54).atZone(chicagoTimeZone));

        // Saturday submission, Wednesday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-07T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 11, 9, 56, 54).atZone(chicagoTimeZone));

        // Sunday submission, Wednesday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-08T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 11, 9, 56, 54).atZone(chicagoTimeZone));
    }

    @Test
    void threeBusinessDaysFromSubmittedAtDate_WithHoliday() {

        ZoneId chicagoTimeZone = ZoneId.of("America/Chicago");

        // Monday submission, Wednesday Holiday, Friday Expiration
        OffsetDateTime submittedAt = OffsetDateTime.parse("2024-12-23T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 27, 9, 56, 54).atZone(chicagoTimeZone));

        // Tuesday submission, Wednesday Holiday, Monday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-24T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 30, 9, 56, 54).atZone(chicagoTimeZone));

        // Wednesday submission, Wednesday Holiday, Monday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-25T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 30, 9, 56, 54).atZone(chicagoTimeZone));

        // Thursday submission, Tuesday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-26T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 31, 9, 56, 54).atZone(chicagoTimeZone));

        // Friday submission, Wednesday Holiday, Thursday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-27T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2025, 1, 2, 9, 56, 54).atZone(chicagoTimeZone));

        // Saturday submission, Wednesday Holiday, Thursday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-28T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2025, 1, 2, 9, 56, 54).atZone(chicagoTimeZone));

        // Sunday submission, Wednesday Holiday, Thursday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-29T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2025, 1, 2, 9, 56, 54).atZone(chicagoTimeZone));
    }

    @Test
    void threeBusinessDaysBeforeDate_noHoliday() {
        // Friday current date, Tuesday rollback
        OffsetDateTime dateWeCareAbout = OffsetDateTime.parse("2025-09-12T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-09-09T15:56:54+00:00"));

        // Thursday current date, Monday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-11T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-09-08T15:56:54+00:00"));

        // Wednesday current date, Friday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-10T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-09-05T15:56:54+00:00"));

        // Tuesday current date, Thursday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-09T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-09-04T15:56:54+00:00"));

        // Monday current date, Wednesday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-08T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-09-03T15:56:54+00:00"));

        // Sunday current date, Wednesday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-07T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-09-03T15:56:54+00:00"));

        // Saturday current date, Wednesday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-06T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-09-03T15:56:54+00:00"));
    }

    @Test
    void threeBusinessDaysBeforeDate_Holiday() {
        // Friday current date, Tuesday rollback
        OffsetDateTime dateWeCareAbout = OffsetDateTime.parse("2025-09-05T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-09-02T15:56:54+00:00"));

        // Thursday current date, Monday holiday, Friday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-04T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-08-29T15:56:54+00:00"));

        // Wednesday current date, Monday holiday, Thursday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-03T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-08-28T15:56:54+00:00"));

        // Tuesday current date, Monday holiday, Wednesday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-02T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-08-27T15:56:54+00:00"));

        // Monday current date, Monday holiday, Tuesday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-01T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-08-27T15:56:54+00:00"));

    }

    @Test
    void getCCAPStartDateForProviderReturnsEarliestCCAPStartDateIfProviderCareStartDateIsNotPresent() {
        Map<String, Object> provider;
        Map<String, Object> secondProvider;

        provider = new HashMap<>();
        provider.put("uuid", "first-provider-uuid");
        provider.put("iterationIsComplete", true);
        provider.put("familyIntendedProviderEmail", "familyChildCareEmail");

        secondProvider = new HashMap<>();
        secondProvider.put("uuid", "second-provider-uuid");
        secondProvider.put("iterationIsComplete", true);
        secondProvider.put("familyIntendedProviderEmail", "secondFamilyChildCareEmail");

        Map<String, Object> child1 = new HashMap<>();
        child1.put("uuid", "child-1-uuid");
        child1.put("childFirstName", "First");
        child1.put("childLastName", "Child");
        child1.put("childInCare", "true");
        child1.put("childDateOfBirthMonth", "10");
        child1.put("childDateOfBirthDay", "11");
        child1.put("childDateOfBirthYear", "2002");
        child1.put("needFinancialAssistanceForChild", "true");
        child1.put("childIsUsCitizen", "Yes");
        child1.put("ccapStartDate", "01/10/2020");

        Map<String, Object> child2 = new HashMap<>();
        child2.put("uuid", "child-2-uuid");
        child2.put("childFirstName", "Second");
        child2.put("childLastName", "Child");
        child2.put("childInCare", "true");
        child2.put("childDateOfBirthMonth", "10");
        child2.put("childDateOfBirthDay", "11");
        child2.put("childDateOfBirthYear", "2002");
        child2.put("needFinancialAssistanceForChild", "true");
        child2.put("childIsUsCitizen", "Yes");
        child2.put("ccapStartDate", "01/10/2024");

        Map<String, Object> child3 = new HashMap<>();
        child3.put("uuid", "child-3-uuid");
        child3.put("childFirstName", "Third");
        child3.put("childLastName", "Person");
        child3.put("childInCare", "true");
        child3.put("childDateOfBirthMonth", "10");
        child3.put("childDateOfBirthDay", "11");
        child3.put("childDateOfBirthYear", "2002");
        child3.put("needFinancialAssistanceForChild", "true");
        child3.put("childIsUsCitizen", "Yes");
        child3.put("ccapStartDate", "01/10/2023");

        Map<String, Object> child4 = new HashMap<>();
        child4.put("uuid", "child-4-uuid");
        child4.put("childFirstName", "Fourth");
        child4.put("childLastName", "Kid");
        child4.put("childInCare", "true");
        child4.put("childDateOfBirthMonth", "10");
        child4.put("childDateOfBirthDay", "11");
        child4.put("childDateOfBirthYear", "2002");
        child4.put("needFinancialAssistanceForChild", "true");
        child4.put("childIsUsCitizen", "Yes");
        child4.put("ccapStartDate", "01/10/2022");


        familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
            .withFlow("gcc")
            .with("parentFirstName", "FirstName")
            .with("parentContactEmail", "familyemail@test.com")
            .with("languageRead", "English")
            .with("providers", List.of(provider, secondProvider))
            .with("children", List.of(child1, child2, child3, child4))
            .withMultipleChildcareSchedulesForProvider(List.of(child2.get("uuid").toString(), child4.get("uuid").toString()), secondProvider.get("uuid").toString())
            .withMultipleChildcareSchedulesForProvider(List.of(child1.get("uuid").toString(), child3.get("uuid").toString()), provider.get("uuid").toString())
            .withSubmittedAtDate(OffsetDateTime.now())
            .withCCRR()
            .withShortCode("ABC123")
            .build());

        providerSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
            .withFlow("providerresponse")
            .with("familySubmissionId", familySubmission.getId().toString())
            .with("providerResponseContactEmail", "provideremail@test.com")
            .with("providerResponseFirstName", "ProviderFirst")
            .with("providerResponseLastName", "ProviderLast")
            .with("providerResponseBusinessName", "BusinessName")
            .with("providerCareStartDate", "01/10/2025")
            .with("providerPaidCcap", "true")
            .with("providerResponseAgreeToCare", "true")
            .build());

        secondProviderSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
            .withFlow("providerresponse")
            .with("familySubmissionId", familySubmission.getId().toString())
            .with("providerResponseContactEmail", "secondProvideremail@test.com")
            .with("providerResponseFirstName", "SecondProviderFirst")
            .with("providerResponseLastName", "LastNameSecondProvider")
            .with("providerResponseBusinessName", "SecondBusinessName")
            .with("providerPaidCcap", "false")
            .with("providerResponseAgreeToCare", "true")
            .with("currentProviderUuid", secondProvider.get("uuid").toString())
            .build());

        secondProvider.put("providerResponseSubmissionId", secondProviderSubmission.getId());

        String ccapStartDate = ProviderSubmissionUtilities.getCCAPStartDateForProvider(secondProviderSubmission, familySubmission);
        assertThat(ccapStartDate).isEqualTo("01/10/2022");
    }

    @Test
    void getCCAPStartDateForProviderReturnsProviderCareStartDateIfPresent() {
        Map<String, Object> provider;
        Map<String, Object> secondProvider;

        provider = new HashMap<>();
        provider.put("uuid", "first-provider-uuid");
        provider.put("iterationIsComplete", true);
        provider.put("familyIntendedProviderEmail", "familyChildCareEmail");

        secondProvider = new HashMap<>();
        secondProvider.put("uuid", "second-provider-uuid");
        secondProvider.put("iterationIsComplete", true);
        secondProvider.put("familyIntendedProviderEmail", "secondFamilyChildCareEmail");

        Map<String, Object> child1 = new HashMap<>();
        child1.put("uuid", "child-1-uuid");
        child1.put("childFirstName", "First");
        child1.put("childLastName", "Child");
        child1.put("childInCare", "true");
        child1.put("childDateOfBirthMonth", "10");
        child1.put("childDateOfBirthDay", "11");
        child1.put("childDateOfBirthYear", "2002");
        child1.put("needFinancialAssistanceForChild", "true");
        child1.put("childIsUsCitizen", "Yes");
        child1.put("ccapStartDate", "01/10/2020");

        Map<String, Object> child2 = new HashMap<>();
        child2.put("uuid", "child-2-uuid");
        child2.put("childFirstName", "Second");
        child2.put("childLastName", "Child");
        child2.put("childInCare", "true");
        child2.put("childDateOfBirthMonth", "10");
        child2.put("childDateOfBirthDay", "11");
        child2.put("childDateOfBirthYear", "2002");
        child2.put("needFinancialAssistanceForChild", "true");
        child2.put("childIsUsCitizen", "Yes");
        child2.put("ccapStartDate", "01/10/2024");

        Map<String, Object> child3 = new HashMap<>();
        child3.put("uuid", "child-3-uuid");
        child3.put("childFirstName", "Third");
        child3.put("childLastName", "Person");
        child3.put("childInCare", "true");
        child3.put("childDateOfBirthMonth", "10");
        child3.put("childDateOfBirthDay", "11");
        child3.put("childDateOfBirthYear", "2002");
        child3.put("needFinancialAssistanceForChild", "true");
        child3.put("childIsUsCitizen", "Yes");
        child3.put("ccapStartDate", "01/10/2023");

        Map<String, Object> child4 = new HashMap<>();
        child4.put("uuid", "child-4-uuid");
        child4.put("childFirstName", "Fourth");
        child4.put("childLastName", "Kid");
        child4.put("childInCare", "true");
        child4.put("childDateOfBirthMonth", "10");
        child4.put("childDateOfBirthDay", "11");
        child4.put("childDateOfBirthYear", "2002");
        child4.put("needFinancialAssistanceForChild", "true");
        child4.put("childIsUsCitizen", "Yes");
        child4.put("ccapStartDate", "01/10/2022");


        familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
            .withFlow("gcc")
            .with("parentFirstName", "FirstName")
            .with("parentContactEmail", "familyemail@test.com")
            .with("languageRead", "English")
            .with("providers", List.of(provider, secondProvider))
            .with("children", List.of(child1, child2, child3, child4))
            .withMultipleChildcareSchedulesForProvider(List.of(child2.get("uuid").toString(), child4.get("uuid").toString()), secondProvider.get("uuid").toString())
            .withMultipleChildcareSchedulesForProvider(List.of(child1.get("uuid").toString(), child3.get("uuid").toString()), provider.get("uuid").toString())
            .withSubmittedAtDate(OffsetDateTime.now())
            .withCCRR()
            .withShortCode("ABC123")
            .build());

        providerSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
            .withFlow("providerresponse")
            .with("familySubmissionId", familySubmission.getId().toString())
            .with("providerResponseContactEmail", "provideremail@test.com")
            .with("providerResponseFirstName", "ProviderFirst")
            .with("providerResponseLastName", "ProviderLast")
            .with("providerResponseBusinessName", "BusinessName")
            .with("providerCareStartDate", "12/02/2018")
            .with("providerPaidCcap", "true")
            .with("providerResponseAgreeToCare", "true")
            .build());

        secondProviderSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
            .withFlow("providerresponse")
            .with("familySubmissionId", familySubmission.getId().toString())
            .with("providerResponseContactEmail", "secondProvideremail@test.com")
            .with("providerResponseFirstName", "SecondProviderFirst")
            .with("providerResponseLastName", "LastNameSecondProvider")
            .with("providerResponseBusinessName", "SecondBusinessName")
            .with("providerPaidCcap", "false")
            .with("providerResponseAgreeToCare", "true")
            .with("currentProviderUuid", secondProvider.get("uuid").toString())
            .build());

        provider.put("providerResponseSubmissionId", providerSubmission.getId());

        String ccapStartDate = ProviderSubmissionUtilities.getCCAPStartDateForProvider(providerSubmission, familySubmission);
        assertThat(ccapStartDate).isEqualTo("12/02/2018");
    }
}