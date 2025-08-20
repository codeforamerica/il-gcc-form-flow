package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.ilgcc.app.utils.enums.ProviderLanguagesOffered.HINDI;
import static org.ilgcc.app.utils.enums.ProviderLanguagesOffered.TAGALOG;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.enums.ProviderDenialReason;
import org.ilgcc.app.utils.enums.ProviderType;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = IlGCCApplication.class,
        properties = "il-gcc.enable-multiple-providers=true"

)
@ActiveProfiles("test")
public class ProviderSubmissionFieldPreparerServiceTest_MultipleProvider {

    @Autowired
    private ProviderSubmissionFieldPreparerService preparer;

    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    SubmissionRepository submissionRepository;

    private Submission familySubmission;
    private Submission providerSubmission;

    private static Map<String, Object> individualProvider = new HashMap<>();

    private static Map<String, Object> programProvider = new HashMap<>();

    private static Map<String, Object> childWithCCAP = new HashMap<>();

    @BeforeAll
    public static void setUpOnce() {
        individualProvider.put("uuid", UUID.randomUUID().toString());
        individualProvider.put("iterationIsComplete", true);
        individualProvider.put("providerFirstName", "FirstName");
        individualProvider.put("providerLastName", "LastName");
        individualProvider.put("familyIntendedProviderEmail", "firstLast@mail.com");
        individualProvider.put("familyIntendedProviderPhoneNumber", "(999) 123-1234");
        individualProvider.put("familyIntendedProviderAddress", "123 Main St.");
        individualProvider.put("familyIntendedProviderCity", "Chicago");
        individualProvider.put("familyIntendedProviderState", "IL");
        individualProvider.put("providerType", "Individual");
        individualProvider.put("providerApplicationResponseStatus", SubmissionStatus.ACTIVE.name());

        programProvider.put("uuid", UUID.randomUUID().toString());
        programProvider.put("iterationIsComplete", true);
        programProvider.put("childCareProgramName", "Child Care Program Name");
        programProvider.put("familyIntendedProviderEmail", "ccpn@mail.com");
        programProvider.put("familyIntendedProviderPhoneNumber", "(123) 123-1234");
        programProvider.put("familyIntendedProviderAddress", "456 Main St.");
        programProvider.put("familyIntendedProviderCity", "Chicago");
        programProvider.put("familyIntendedProviderState", "IL");
        programProvider.put("providerType", "Care Program");
        programProvider.put("providerApplicationResponseStatus", SubmissionStatus.ACTIVE.name());

        childWithCCAP.put("uuid", UUID.randomUUID().toString());
        childWithCCAP.put("childFirstName", "First");
        childWithCCAP.put("childLastName", "Child");
        childWithCCAP.put("childInCare", "true");
        childWithCCAP.put("childDateOfBirthMonth", "10");
        childWithCCAP.put("childDateOfBirthDay", "11");
        childWithCCAP.put("childDateOfBirthYear", "2002");
        childWithCCAP.put("needFinancialAssistanceForChild", true);
        childWithCCAP.put("childIsUsCitizen", "Yes");
        childWithCCAP.put("ccapStartDate", "01/10/2025");
    }

    @Nested
    class whenThereIsNoProviderResponse {

        @Test
        public void whenApplicationExpiredAndProviderIsIndividual() {
            individualProvider.put("providerApplicationResponseStatus", SubmissionStatus.EXPIRED.name());

            familySubmission = new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .withSubmittedAtDate(OffsetDateTime.now().minusDays(10))
                    .with("providers", List.of(individualProvider, programProvider))
                    .with("children", List.of(childWithCCAP))
                    .withChildcareScheduleForProvider(childWithCCAP.get("uuid").toString(),
                            individualProvider.get("uuid").toString())
                    .build();

            Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);

            // Details about the provider
            assertThat(result.get("providerResponseFirstName"))
                    .isEqualTo(new SingleField("providerResponseFirstName", "FirstName", null));
            assertThat(result.get("providerResponseLastName"))
                    .isEqualTo(new SingleField("providerResponseLastName", "LastName", null));
            assertThat(result.get("providerResponseContactEmail"))
                    .isEqualTo(new SingleField("providerResponseContactEmail", "firstLast@mail.com", null));
            assertThat(result.get("providerResponseContactPhoneNumber"))
                    .isEqualTo(new SingleField("providerResponseContactPhoneNumber", "(999) 123-1234", null));
            assertThat(result.get("providerMailingStreetAddress1"))
                    .isEqualTo(new SingleField("providerMailingStreetAddress1", "123 Main St.", null));
            assertThat(result.get("providerMailingCity"))
                    .isEqualTo(new SingleField("providerMailingCity", "Chicago", null));
            assertThat(result.get("providerMailingState"))
                    .isEqualTo(new SingleField("providerMailingState", "IL", null));

            assertThat(result.get("providerResponse"))
                    .isEqualTo(new SingleField("providerResponse", "No response from provider", null));

            validateChildData(result);
        }

        @Test
        public void whenApplicationIsActiveAndProviderIsIndividual() {
            individualProvider.put("providerApplicationResponseStatus", SubmissionStatus.ACTIVE.name());

            familySubmission = new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .withSubmittedAtDate(OffsetDateTime.now().minusDays(10))
                    .with("providers", List.of(individualProvider, programProvider))
                    .with("children", List.of(childWithCCAP))
                    .withChildcareScheduleForProvider(childWithCCAP.get("uuid").toString(),
                            individualProvider.get("uuid").toString())
                    .build();

            Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);

            // Details about the provider
            assertThat(result.get("providerResponseFirstName"))
                    .isEqualTo(new SingleField("providerResponseFirstName", "FirstName", null));
            assertThat(result.get("providerResponseLastName"))
                    .isEqualTo(new SingleField("providerResponseLastName", "LastName", null));
            assertThat(result.get("providerResponseContactEmail"))
                    .isEqualTo(new SingleField("providerResponseContactEmail", "firstLast@mail.com", null));
            assertThat(result.get("providerResponseContactPhoneNumber"))
                    .isEqualTo(new SingleField("providerResponseContactPhoneNumber", "(999) 123-1234", null));
            assertThat(result.get("providerMailingStreetAddress1"))
                    .isEqualTo(new SingleField("providerMailingStreetAddress1", "123 Main St.", null));
            assertThat(result.get("providerMailingCity"))
                    .isEqualTo(new SingleField("providerMailingCity", "Chicago", null));
            assertThat(result.get("providerMailingState"))
                    .isEqualTo(new SingleField("providerMailingState", "IL", null));

            assertThat(result.get("providerResponse"))
                    .isEqualTo(null);

            validateChildData(result);
        }

        @Test
        public void whenApplicationExpiredAndProviderIsProgram() {
            programProvider.put("providerApplicationResponseStatus", SubmissionStatus.EXPIRED.name());

            familySubmission = new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .withSubmittedAtDate(OffsetDateTime.now().minusDays(10))
                    .with("providers", List.of(individualProvider, programProvider))
                    .with("children", List.of(childWithCCAP))
                    .withChildcareScheduleForProvider(childWithCCAP.get("uuid").toString(),
                            programProvider.get("uuid").toString())
                    .build();

            Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);

            // Details about the provider
            assertThat(result.get("providerResponseBusinessName"))
                    .isEqualTo(new SingleField("providerResponseBusinessName", "Child Care Program Name", null));
            assertThat(result.get("providerResponseContactEmail"))
                    .isEqualTo(new SingleField("providerResponseContactEmail", "ccpn@mail.com", null));
            assertThat(result.get("providerResponseContactPhoneNumber"))
                    .isEqualTo(new SingleField("providerResponseContactPhoneNumber", "(123) 123-1234", null));
            assertThat(result.get("providerMailingStreetAddress1"))
                    .isEqualTo(new SingleField("providerMailingStreetAddress1", "456 Main St.", null));
            assertThat(result.get("providerMailingCity"))
                    .isEqualTo(new SingleField("providerMailingCity", "Chicago", null));
            assertThat(result.get("providerMailingState"))
                    .isEqualTo(new SingleField("providerMailingState", "IL", null));

            assertThat(result.get("providerResponse"))
                    .isEqualTo(new SingleField("providerResponse", "No response from provider", null));

            validateChildData(result);
        }

        @Test
        public void whenApplicationIsActiveAndProviderIsProgram() {
            programProvider.put("providerApplicationResponseStatus", SubmissionStatus.ACTIVE.name());

            familySubmission = new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .withSubmittedAtDate(OffsetDateTime.now().minusDays(10))
                    .with("providers", List.of(individualProvider, programProvider))
                    .with("children", List.of(childWithCCAP))
                    .withChildcareScheduleForProvider(childWithCCAP.get("uuid").toString(),
                            programProvider.get("uuid").toString())
                    .build();

            Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);

            // Details about the provider
            assertThat(result.get("providerResponseBusinessName"))
                    .isEqualTo(new SingleField("providerResponseBusinessName", "Child Care Program Name", null));
            assertThat(result.get("providerResponseContactEmail"))
                    .isEqualTo(new SingleField("providerResponseContactEmail", "ccpn@mail.com", null));
            assertThat(result.get("providerResponseContactPhoneNumber"))
                    .isEqualTo(new SingleField("providerResponseContactPhoneNumber", "(123) 123-1234", null));
            assertThat(result.get("providerMailingStreetAddress1"))
                    .isEqualTo(new SingleField("providerMailingStreetAddress1", "456 Main St.", null));
            assertThat(result.get("providerMailingCity"))
                    .isEqualTo(new SingleField("providerMailingCity", "Chicago", null));
            assertThat(result.get("providerMailingState"))
                    .isEqualTo(new SingleField("providerMailingState", "IL", null));

            assertThat(result.get("providerResponse"))
                    .isEqualTo(null);

            validateChildData(result);
        }
    }

    @Nested
    class whenThereIsAProviderResponse {

        @BeforeEach
        void setUp() {
            familySubmission = new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .withSubmittedAtDate(OffsetDateTime.now().minusDays(10))
                    .with("providers", List.of(individualProvider, programProvider))
                    .with("children", List.of(childWithCCAP))
                    .withChildcareScheduleForProvider(childWithCCAP.get("uuid").toString(),
                            individualProvider.get("uuid").toString())
                    .build();

            submissionRepositoryService.save(familySubmission);

            providerSubmission = new SubmissionTestBuilder()
                    .withFlow("providerresponse")
                    .with("familySubmissionId", familySubmission.getId())
                    .with("providerResponseFirstName", "ProviderFirst")
                    .with("providerResponseLastName", "ProviderLast")
                    .with("providerResponseBusinessName", "DayCare Place")
                    .with("providerResponseContactPhoneNumber", "(111) 222-3333")
                    .with("providerResponseContactEmail", "mail@daycareplace.org")
                    .with("providerResponseServiceStreetAddress1", "123 Main Street")
                    .with("providerResponseServiceCity", "De Kalb")
                    .with("providerResponseServiceState", "IL")
                    .with("providerResponseServiceZipCode", "60112")
                    .with("providerMailingStreetAddress1", "888 Main Street")
                    .with("providerMailingStreetAddress2", "Apt 55")
                    .with("providerMailingCity", "Chicago")
                    .with("providerMailingState", "IL")
                    .with("providerMailingZipCode", "60115")
                    .with("providerPaidCcap", "true")
                    .withProviderStateLicense()
                    .with("currentProviderUuid", individualProvider.get("uuid"))
                    .build();

            submissionRepositoryService.save(providerSubmission);

            individualProvider.put("providerResponseSubmissionId", providerSubmission.getId());
        }

        @AfterEach
        void tearDown() {
            submissionRepository.deleteAll();
        }

        @Test
        void mapsCommonFieldsToPDFForIndividual() {
            Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);

            assertThat(result.get("providerResponseFirstName")).isEqualTo(
                    new SingleField("providerResponseFirstName", "ProviderFirst", null));
            assertThat(result.get("providerResponseLastName")).isEqualTo(
                    new SingleField("providerResponseLastName", "ProviderLast", null));
            assertThat(result.get("providerResponseBusinessName")).isEqualTo(
                    new SingleField("providerResponseBusinessName", "DayCare Place", null));
            assertThat(result.get("providerResponseServiceStreetAddress1")).isEqualTo(
                    new SingleField("providerResponseServiceStreetAddress1", "123 Main Street", null));
            assertThat(result.get("providerResponseServiceStreetAddress2")).isEqualTo(
                    new SingleField("providerResponseServiceStreetAddress2", "", null));
            assertThat(result.get("providerResponseServiceCity")).isEqualTo(
                    new SingleField("providerResponseServiceCity", "De Kalb", null));
            assertThat(result.get("providerResponseServiceState")).isEqualTo(
                    new SingleField("providerResponseServiceState", "IL", null));
            assertThat(result.get("providerResponseServiceZipCode")).isEqualTo(
                    new SingleField("providerResponseServiceZipCode", "60112", null));

            assertThat(result.get("providerMailingStreetAddress1")).isEqualTo(
                    new SingleField("providerMailingStreetAddress1", "888 Main Street", null));
            assertThat(result.get("providerMailingStreetAddress2")).isEqualTo(
                    new SingleField("providerMailingStreetAddress2", "Apt 55", null));
            assertThat(result.get("providerMailingCity")).isEqualTo(
                    new SingleField("providerMailingCity", "Chicago", null));
            assertThat(result.get("providerMailingState")).isEqualTo(
                    new SingleField("providerMailingState", "IL", null));
            assertThat(result.get("providerMailingZipCode")).isEqualTo(
                    new SingleField("providerMailingZipCode", "60115", null));

            assertThat(result.get("providerResponseContactPhoneNumber")).isEqualTo(
                    new SingleField("providerResponseContactPhoneNumber", "(111) 222-3333", null));
            assertThat(result.get("providerResponseContactEmail")).isEqualTo(
                    new SingleField("providerResponseContactEmail", "mail@daycareplace.org", null));
        }

        @Test
        void mapsCommonFieldsToPDFForProgram() {
            familySubmission = new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .withSubmittedAtDate(OffsetDateTime.now().minusDays(10))
                    .with("providers", List.of(programProvider))
                    .with("children", List.of(childWithCCAP))
                    .withChildcareScheduleForProvider(childWithCCAP.get("uuid").toString(),
                            programProvider.get("uuid").toString())
                    .build();

            providerSubmission.getInputData().put("currentProviderUuid", programProvider.get("uuid"));
            submissionRepositoryService.save(providerSubmission);
            submissionRepositoryService.save(familySubmission);

            programProvider.put("providerResponseSubmissionId", providerSubmission.getId());

            Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);

            assertThat(result.get("providerResponseFirstName")).isEqualTo(
                    new SingleField("providerResponseFirstName", "ProviderFirst", null));
            assertThat(result.get("providerResponseLastName")).isEqualTo(
                    new SingleField("providerResponseLastName", "ProviderLast", null));
            assertThat(result.get("providerResponseBusinessName")).isEqualTo(
                    new SingleField("providerResponseBusinessName", "DayCare Place", null));
            assertThat(result.get("providerResponseServiceStreetAddress1")).isEqualTo(
                    new SingleField("providerResponseServiceStreetAddress1", "123 Main Street", null));
            assertThat(result.get("providerResponseServiceStreetAddress2")).isEqualTo(
                    new SingleField("providerResponseServiceStreetAddress2", "", null));
            assertThat(result.get("providerResponseServiceCity")).isEqualTo(
                    new SingleField("providerResponseServiceCity", "De Kalb", null));
            assertThat(result.get("providerResponseServiceState")).isEqualTo(
                    new SingleField("providerResponseServiceState", "IL", null));
            assertThat(result.get("providerResponseServiceZipCode")).isEqualTo(
                    new SingleField("providerResponseServiceZipCode", "60112", null));

            assertThat(result.get("providerMailingStreetAddress1")).isEqualTo(
                    new SingleField("providerMailingStreetAddress1", "888 Main Street", null));
            assertThat(result.get("providerMailingStreetAddress2")).isEqualTo(
                    new SingleField("providerMailingStreetAddress2", "Apt 55", null));
            assertThat(result.get("providerMailingCity")).isEqualTo(
                    new SingleField("providerMailingCity", "Chicago", null));
            assertThat(result.get("providerMailingState")).isEqualTo(
                    new SingleField("providerMailingState", "IL", null));
            assertThat(result.get("providerMailingZipCode")).isEqualTo(
                    new SingleField("providerMailingZipCode", "60115", null));

            assertThat(result.get("providerResponseContactPhoneNumber")).isEqualTo(
                    new SingleField("providerResponseContactPhoneNumber", "(111) 222-3333", null));
            assertThat(result.get("providerResponseContactEmail")).isEqualTo(
                    new SingleField("providerResponseContactEmail", "mail@daycareplace.org", null));
        }

        @ParameterizedTest
        @MethodSource("providerDenialReasons")
        public void whenProviderDeclinesCareWithReason(ProviderDenialReason reason) {
            providerSubmission.getInputData().put("providerResponseAgreeToCare", "false");
            providerSubmission.getInputData().put("providerResponseDenyCareReason", reason.name());
            submissionRepositoryService.save(providerSubmission);

            Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);

            assertThat(result.get("providerResponse")).isEqualTo(
                    new SingleField("providerResponse", reason.getPdfValue(), null));
        }


        static Stream<Arguments> providerDenialReasons() {
            return Stream.of(ProviderDenialReason.values())
                    .map(reason -> Arguments.of(reason));
        }

        @Test
        public void whenProviderDeclinesCareWithoutReason() {
            providerSubmission.getInputData().put("providerResponseAgreeToCare", "false");
            submissionRepositoryService.save(providerSubmission);

            Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);

            assertThat(result.get("providerResponse")).isEqualTo(
                    new SingleField("providerResponse", "Provider declined", null));
        }

        @Nested
        class whenProviderIsRegistering {

            @BeforeEach
            void setup() {
                providerSubmission.getInputData().put("providerPaidCcap", "false");
                providerSubmission.getInputData().put("providerCareStartDate", "10/10/2025");
                submissionRepositoryService.save(providerSubmission);
            }

            @ParameterizedTest
            @MethodSource("providerTypeMaps")
            public void mapsProviderType(ProviderType providerType) {
                providerSubmission.getInputData().put("providerType", providerType.name());
                submissionRepositoryService.save(providerSubmission);

                Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);

                assertThat(result.get("providerType")).isEqualTo(
                        new SingleField("providerType", providerType.getPdfFieldName(), null));
            }


            static Stream<Arguments> providerTypeMaps() {
                return Stream.of(ProviderType.values())
                        .map(providerType -> Arguments.of(providerType));
            }

            @Test
            public void mapsSelectedProviderLanguagesToPDF() {
                String providerLanguagesOfferedOtherString = "test, double gibberish";
                providerSubmission.getInputData()
                        .put("providerLanguagesOffered[]", List.of("English", "Tagalog", "Spanish", "Hindi", "other"));
                providerSubmission.getInputData().put("providerLanguagesOffered_other", providerLanguagesOfferedOtherString);
                submissionRepositoryService.save(providerSubmission);

                Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
                assertThat(result.get("providerLanguageEnglish")).isEqualTo(
                        new SingleField("providerLanguageEnglish", "true", null));
                assertThat(result.get("providerLanguageSpanish")).isEqualTo(
                        new SingleField("providerLanguageSpanish", "true", null));
                assertThat(result.get("providerLanguageOther")).isEqualTo(new SingleField("providerLanguageOther", "true", null));
                assertThat(result.get("providerLanguageOtherDetail")).isEqualTo(new SingleField("providerLanguageOtherDetail",
                        String.format("%s, %s, %s", TAGALOG.getProviderLanguageOtherDetailPdfFieldValue(),
                                HINDI.getProviderLanguageOtherDetailPdfFieldValue(), providerLanguagesOfferedOtherString), null));
                assertThat(result.get("providerLanguageChinese")).isNotEqualTo(
                        new SingleField("providerLanguageChinese", "true", null));
            }

            @Test
            public void mapsNoSelectedProviderLanguagesToPDF() {
                providerSubmission.getInputData()
                        .put("providerLanguagesOffered[]", List.of("None"));
                submissionRepositoryService.save(providerSubmission);

                Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
                assertThat(result.get("providerLanguageEnglish")).isEqualTo(null);
                assertThat(result.get("providerLanguageSpanish")).isEqualTo(null);
                assertThat(result.get("providerLanguagePolish")).isEqualTo(null);
                assertThat(result.get("providerLanguageChinese")).isEqualTo(null);
                assertThat(result.get("providerLanguageOther")).isEqualTo(null);
                assertThat(result.get("providerLanguageOtherDetail")).isEqualTo(null);
            }

            @Test
            public void setsChildCareStartDateBasedOnProviderCareStartDate() {
                providerSubmission.getInputData().put("providerCareStartDate", "12/11/2023");

                submissionRepositoryService.save(providerSubmission);

                Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
                assertThat(result.get("childcareStartDate")).isEqualTo(new SingleField("childcareStartDate", "12/11/2023", null));
            }

            @Test
            public void mapsProviderHouseholdMembersWhenTheyExist() {
                providerSubmission = new SubmissionTestBuilder(providerSubmission)
                        .withProviderHouseholdMember("1-Iteration",
                                "One",
                                "30",
                                "10",
                                "2004",
                                "brother",
                                "555-55-5555")
                        .withProviderHouseholdMember("2-Iteration",
                                "Two",
                                "28",
                                "6",
                                "2000",
                                "Sister",
                                "444-55-5555")
                        .build();
                submissionRepositoryService.save(providerSubmission);

                Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
                assertThat(result.get("providerHouseholdMemberFirstName_1")).isEqualTo(
                        new SingleField("providerHouseholdMemberFirstName", "1-Iteration", 1));
                assertThat(result.get("providerHouseholdMemberLastName_1")).isEqualTo(
                        new SingleField("providerHouseholdMemberLastName", "One", 1));
                assertThat(result.get("providerHouseholdMemberRelationship_1")).isEqualTo(
                        new SingleField("providerHouseholdMemberRelationship", "brother", 1));
                assertThat(result.get("providerHouseholdMemberSSN_1")).isEqualTo(
                        new SingleField("providerHouseholdMemberSSN", "555-55-5555", 1));
                assertThat(result.get("providerHouseholdMemberDateOfBirth_1")).isEqualTo(
                        new SingleField("providerHouseholdMemberDateOfBirth", "10/30/2004", 1));

                assertThat(result.get("providerHouseholdMemberFirstName_2")).isEqualTo(
                        new SingleField("providerHouseholdMemberFirstName", "2-Iteration", 2));
                assertThat(result.get("providerHouseholdMemberLastName_2")).isEqualTo(
                        new SingleField("providerHouseholdMemberLastName", "Two", 2));
                assertThat(result.get("providerHouseholdMemberRelationship_2")).isEqualTo(
                        new SingleField("providerHouseholdMemberRelationship", "Sister", 2));
                assertThat(result.get("providerHouseholdMemberSSN_2")).isEqualTo(
                        new SingleField("providerHouseholdMemberSSN", "444-55-5555", 2));
                assertThat(result.get("providerHouseholdMemberDateOfBirth_2")).isEqualTo(
                        new SingleField("providerHouseholdMemberDateOfBirth", "6/28/2000", 2));
            }

            @Test
            public void mapsLicenseNumberWhenProviderIsLicensed() {
                providerSubmission = new SubmissionTestBuilder(providerSubmission)
                        .withProviderStateLicense()
                        .build();
                submissionRepositoryService.save(providerSubmission);

                Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);

                assertThat(result.get("providerLicenseNumber")).isEqualTo(
                        new SingleField("providerLicenseNumber", "123453646 (IL)", null));
            }

            @Test
            public void mapsProviderIdentityCheckSSNToProviderSSN() {
                providerSubmission.getInputData().put("providerIdentityCheckSSN", "445-32-6666");
                providerSubmission.getInputData().put("providerTaxIdSSN", "555-55-5555");
                providerSubmission.getInputData().put("providerITIN", "");

                submissionRepositoryService.save(providerSubmission);

                Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
                assertThat(result.get("providerSSN")).isEqualTo(new SingleField("providerSSN", "445-32-6666", null));
            }

            @Test
            public void mapsProviderTaxIdSSNToProviderSSN() {
                providerSubmission.getInputData().put("providerIdentityCheckSSN", "");
                providerSubmission.getInputData().put("providerTaxIdSSN", "555-55-5555");
                providerSubmission.getInputData().put("providerITIN", "");
                submissionRepositoryService.save(providerSubmission);

                Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
                assertThat(result.get("providerSSN")).isEqualTo(new SingleField("providerSSN", "555-55-5555", null));
            }

            @Test
            public void mapsITINToProviderSSN() {
                providerSubmission.getInputData().put("providerIdentityCheckSSN", "");
                providerSubmission.getInputData().put("providerTaxIdSSN", "555-55-5555");
                providerSubmission.getInputData().put("providerITIN", "123456789");
                submissionRepositoryService.save(providerSubmission);

                Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
                assertThat(result.get("providerSSN")).isEqualTo(new SingleField("providerSSN", "123456789", null));
            }
        }


        @Nested
        class whenExistingProvider {

            @Test
            public void isVerifiedByProviderIdAndAcceptsCare() {
                providerSubmission.getInputData().put("providerResponseAgreeToCare", "true");
                providerSubmission.getInputData().put("providerResponseProviderNumber", "123456789720008");
                submissionRepositoryService.save(providerSubmission);

                Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);

                assertThat(result.get("providerResponseProviderNumber")).isEqualTo(
                        new SingleField("providerResponseProviderNumber", "123456789720008", null));
                assertThat(result.get("providerResponse")).isEqualTo(new SingleField("providerResponse", "",
                        null));
            }


            @Test
            public void isVerifiedByFEINAndAcceptsCare() {
                providerSubmission.getInputData().put("providerTaxIdFEIN", "12-1234567");
                providerSubmission.getInputData().put("providerResponseAgreeToCare", "true");
                submissionRepositoryService.save(providerSubmission);

                Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);

                assertThat(result.get("providerTaxIdFEIN")).isEqualTo(
                        new SingleField("providerTaxIdFEIN", "12-1234567", null));
                assertThat(result.get("providerResponse")).isEqualTo(new SingleField("providerResponse", "",
                        null));
            }

            @Test
            public void isUnverifiedAndAcceptsCare() {
                Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);

                assertThat(result.get("providerResponseProviderNumber")).isEqualTo(
                        new SingleField("providerResponseProviderNumber", "", null));
                assertThat(result.get("providerTaxIdFEIN")).isEqualTo(
                        new SingleField("providerTaxIdFEIN", "", null));
                assertThat(result.get("providerResponse")).isEqualTo(
                        new SingleField("providerResponse", "Unable to identify provider - no response to care arrangement",
                                null));
            }

            @Test
            public void childCareStartDateIsSetByProviderPreparer() {
                Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
                assertThat(result.get("childcareStartDate")).isEqualTo(
                        new SingleField("childcareStartDate", "01/10/2025",
                                null));
            }
        }
    }

    private void validateChildData(Map<String, SubmissionField> result) {
        // Details about the child
        assertThat(result.get("childFirstName_1"))
                .isEqualTo(new SingleField("childFirstName", "First", 1));
        assertThat(result.get("childLastName_1"))
                .isEqualTo(new SingleField("childLastName", "Child", 1));

        assertThat(result.get("childRelationship_1"))
                .isEqualTo(new SingleField("childRelationship", null, 1));

        assertThat(result.get("childOtherEdHoursDescription_1"))
                .isEqualTo(new SingleField("childOtherEdHoursDescription", "", 1));

        assertThat(result.get("childCareScheduleMondayStart_1"))
                .isEqualTo(new SingleField("childCareScheduleMondayStart", "10:24", 1));

        assertThat(result.get("childCareScheduleMondayStartAmPm_1"))
                .isEqualTo(new SingleField("childCareScheduleMondayStartAmPm", "AM", 1));

        assertThat(result.get("childCareScheduleMondayEnd_1"))
                .isEqualTo(new SingleField("childCareScheduleMondayEnd", "10:21", 1));

        assertThat(result.get("childCareScheduleMondayEndAmPm_1"))
                .isEqualTo(new SingleField("childCareScheduleMondayEndAmPm", "PM", 1));

        assertThat(result.get("childCareScheduleTuesdayStart_1"))
                .isEqualTo(new SingleField("childCareScheduleTuesdayStart", "10:24", 1));

        assertThat(result.get("childCareScheduleTuesdayStartAmPm_1"))
                .isEqualTo(new SingleField("childCareScheduleTuesdayStartAmPm", "AM", 1));

        assertThat(result.get("childCareScheduleTuesdayEnd_1"))
                .isEqualTo(new SingleField("childCareScheduleTuesdayEnd", "10:21", 1));

        assertThat(result.get("childCareScheduleTuesdayEndAmPm_1"))
                .isEqualTo(new SingleField("childCareScheduleTuesdayEndAmPm", "PM", 1));

        assertThat(result.get("childCareChildInSchool_1"))
                .isEqualTo(new SingleField("childCareChildInSchool", "", 1));
    }
}
