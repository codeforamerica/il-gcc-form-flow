package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_FAMILY_CONFIRMATION_CODE;
import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_FAMILY_SUBMISSION_ID;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpSession;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class GenerateDummyFamilySubmissionForDev implements Action {

    private final SubmissionRepositoryService submissionRepositoryService;
    private final SubmissionRepository submissionRepository;
    private final HttpSession httpSession;

    private final Boolean enableMultipleProviders;

    @Autowired
    Environment env;

    public GenerateDummyFamilySubmissionForDev(SubmissionRepositoryService submissionRepositoryService,
            SubmissionRepository submissionRepository, HttpSession httpSession, @Value("${il-gcc.enable-multiple-providers}") boolean enableMultipleProviders) {
        this.submissionRepositoryService = submissionRepositoryService;
        this.submissionRepository = submissionRepository;
        this.httpSession = httpSession;
        this.enableMultipleProviders = enableMultipleProviders;
    }

    @Override
    public void run(Submission submission) {
        String[] activeProfiles = env.getActiveProfiles();
        boolean isDevProfile = Arrays.asList(activeProfiles).contains("dev");
        if (null == httpSession.getAttribute(SESSION_KEY_FAMILY_SUBMISSION_ID) && isDevProfile) {
            Optional<Submission> existingDummyFamilySubmision = submissionRepositoryService.findByShortCode("DEV-123ABC");
            existingDummyFamilySubmision.ifPresent(submissionRepository::delete);

            Map<String, Object> inputData = createFamilySubmission(submission);

            Submission dummyFamilySubmission = new Submission();
            dummyFamilySubmission.setSubmittedAt(OffsetDateTime.now().minusDays(1));
            dummyFamilySubmission.setFlow("gcc");
            dummyFamilySubmission.setShortCode("DEV-123ABC");
            dummyFamilySubmission.setInputData(inputData);

            dummyFamilySubmission = submissionRepositoryService.save(dummyFamilySubmission);

            httpSession.setAttribute(SESSION_KEY_FAMILY_SUBMISSION_ID, dummyFamilySubmission.getId());
            httpSession.setAttribute(SESSION_KEY_FAMILY_CONFIRMATION_CODE, dummyFamilySubmission.getShortCode());
        }
    }

    private @NotNull Map<String, Object> createFamilySubmission(Submission providerSubmission) {
        Map<String, Object> inputData = new HashMap<>();

        inputData.put("parentFirstName", "Devy");
        inputData.put("parentLastName", "McDeverson");
        inputData.put("parentBirthMonth", "12");
        inputData.put("parentBirthDay", "25");
        inputData.put("parentBirthYear", "1985");
        inputData.put("parentHomeStreetAddress1", "972 Mission St");
        inputData.put("parentEmail", "amedrano+test@codeforamerica.org");
        inputData.put("parentHomeStreetAddress2", "5th floor");
        inputData.put("parentHomeCity", "San Francisco");
        inputData.put("parentHomeState", "CA - California");
        inputData.put("parentHomeZipCode", "94103");
        inputData.put("parentHasPartner", "false");
        inputData.put("earliestChildcareStartDate", "01/10/2025");

        if (enableMultipleProviders) {
            inputData.putAll(createMultipleProviders());
        } else {
            inputData.putAll(createSingleProvider(1));
        }

        inputData.put("providerApplicationResponseStatus", SubmissionStatus.ACTIVE.name());
        inputData.put("providerResponseSubmissionId", providerSubmission.getId().toString());

        List<Map<String, Object>> children = new ArrayList<>();
        Map<String, Object> child1 = new HashMap<>();
        child1.put("uuid", "testerosa-testerson");
        child1.put("childFirstName", "Testerosa");
        child1.put("childLastName", "Testerson");
        child1.put("childDateOfBirthMonth", "10");
        child1.put("childDateOfBirthDay", "17");
        child1.put("childDateOfBirthYear", "2015");
        child1.put("childInCare", "true");
        child1.put("needFinancialAssistanceForChild", "true");
        child1.put("childIsUsCitizen", "Yes");
        child1.put("ccapStartDate", "01/10/2025");
        child1.put(Submission.ITERATION_IS_COMPLETE_KEY, true);

        Map<String, Object> child2 = new HashMap<>();
        child2.put("uuid", "testina-testerson");
        child2.put("childFirstName", "Testina");
        child2.put("childLastName", "Testerson");
        child2.put("childDateOfBirthMonth", "12");
        child2.put("childDateOfBirthDay", "25");
        child2.put("childDateOfBirthYear", "2017");
        child2.put("childInCare", "true");
        child2.put("needFinancialAssistanceForChild", "true");
        child2.put("childIsUsCitizen", "Yes");
        child2.put("ccapStartDate", "01/10/2025");
        child2.put(Submission.ITERATION_IS_COMPLETE_KEY, true);

        children.add(child1);
        children.add(child2);

        IntStream.range(0, children.size()).forEach(i -> {
            Map<String, Object> child = children.get(i);
            setTime(child, "childcare", "Start", "AllDays", "8", "00", "AM");
            setTime(child, "childcare", "End", "AllDays", "5", "00", "PM");

            setTime(child, "childcare", "Start", "Monday", "", "", "");
            setTime(child, "childcare", "End", "Monday", "", "", "");

            setTime(child, "childcare", "Start", "Wednesday", "", "", "");
            setTime(child, "childcare", "End", "Wednesday", "", "", "");

            setTime(child, "childcare", "Start", "Friday", "", "", "");
            setTime(child, "childcare", "End", "Friday", "", "", "");

            child.put("childcareWeeklySchedule[]", List.of("Monday", "Wednesday", "Friday"));
            child.put("childcareStartTimeWednesday", "");
            child.put("childcareHoursSameEveryDay[]", List.of("yes"));

            children.set(i, child);
        });
        inputData.put("children", children);

        List<Map<String, Object>> applicantJob = new ArrayList<>();
        Map<String, Object> job1 = new HashMap<>();
        job1.put("uuid", "job1");
        job1.put("companyName", "Test Co");
        job1.put("employerStreetAddress", "123 Test St");
        job1.put("employerCity", "Springfield");
        job1.put("employerState", "IL");
        job1.put("employerZipCode", "62629");
        job1.put("employerPhoneNumber", "217-555-1234");
        job1.put("isSelfEmployed", "false");
        job1.put(Submission.ITERATION_IS_COMPLETE_KEY, true);
        applicantJob.add(job1);
        inputData.put("jobs", applicantJob);

        List<Map<String, Object>> partnerJob = new ArrayList<>();
        Map<String, Object> partnerJob1 = new HashMap<>();
        partnerJob1.put("uuid", "partnerJob1");
        partnerJob1.put("companyName", "Testers Inc");
        partnerJob1.put("employerStreetAddress", "456 Test St");
        partnerJob1.put("employerCity", "Springfield");
        partnerJob1.put("employerState", "IL");
        partnerJob1.put("employerZipCode", "62629");
        partnerJob1.put("employerPhoneNumber", "217-555-5678");
        partnerJob1.put("isSelfEmployed", "false");
        partnerJob1.put(Submission.ITERATION_IS_COMPLETE_KEY, true);
        partnerJob.add(partnerJob1);
        inputData.put("partnerJobs", partnerJob);

        return inputData;
    }

    public void setTime(Map<String, Object> data, String inputPrefix, String startOrEndKey, String dayPostFix,
            String hour, String minute, String amOrPm) {
        data.put(inputPrefix + startOrEndKey + "Time" + dayPostFix + "Hour", hour);
        data.put(inputPrefix + startOrEndKey + "Time" + dayPostFix + "Minute", minute);
        data.put(inputPrefix + startOrEndKey + "Time" + dayPostFix + "AmPm", amOrPm);
    }

    public Map<String, Object> createMultipleProviders() {
        Map<String, Object> intendedFamilyData = new HashMap<>();

        List<Object> providers = new ArrayList<>();

        Map<String, Object> provider1 = createSingleProvider(1);
        provider1.put("iterationIsComplete", true);
        provider1.put("uuid", "first-provider-id");
        provider1.put("providerType", "Care Program");
        provider1.put("providerLastName", "Hanson");
        provider1.put("providerFirstName", "Philip");
        provider1.put("childCareProgramName", "Baby Children Day Care Center");
        provider1.put("familyIntendedProviderCity", "Chicago");
        provider1.put("familyIntendedProviderState", "IL");
        provider1.put("familyIntendedProviderAddress", "123 Main Street");

        providers.add(provider1);

        Map<String, Object> provider2 = createSingleProvider(2);
        provider2.put("iterationIsComplete", true);
        provider2.put("uuid", "second-provider-id");
        provider2.put("providerType", "Individual");
        provider2.put("providerLastName", "King");
        provider2.put("providerFirstName", "Brielle");
        provider2.put("childCareProgramName", "Baby Children Day Care Center");
        provider2.put("familyIntendedProviderName", "Baby Children Day Care Center");
        provider2.put("familyIntendedProviderCity", "Chicago");
        provider2.put("familyIntendedProviderState", "IL");
        provider2.put("familyIntendedProviderAddress", "223 Main Street");

        providers.add(provider2);

        Map<String, Object> provider3 = createSingleProvider(3);
        provider3.put("iterationIsComplete", true);
        provider3.put("uuid", "third-provider-id");
        provider3.put("providerType", "Care Program");
        provider3.put("providerLastName", "Holden");
        provider3.put("providerFirstName", "Callum");
        provider3.put("childCareProgramName", "");
        provider3.put("familyIntendedProviderName", "Holden Callum");
        provider3.put("familyIntendedProviderCity", "Chicago");
        provider3.put("familyIntendedProviderState", "IL");
        provider3.put("familyIntendedProviderAddress", "323 Main Street");

        providers.add(provider3);

        intendedFamilyData.put("providers", providers);

        return intendedFamilyData;
    }

    public Map<String, Object> createSingleProvider(int num){
        Map<String, Object> intendedFamilyData = new HashMap<>();
        intendedFamilyData.put("familyIntendedProviderName", String.format("Dev Provider%s", num));
        intendedFamilyData.put("familyIntendedProviderPhoneNumber", String.format("(510) 123-456%s", num));
        intendedFamilyData.put("familyIntendedProviderEmail", String.format("dev_provider_%s_email@mail.com", num));

        return intendedFamilyData;
    }
}
