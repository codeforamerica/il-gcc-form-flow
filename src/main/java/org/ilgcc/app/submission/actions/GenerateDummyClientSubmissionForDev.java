package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
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
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class GenerateDummyClientSubmissionForDev implements Action {
    
    private final SubmissionRepositoryService submissionRepositoryService;
    private final SubmissionRepository submissionRepository;
    private final HttpSession httpSession;
    
    @Autowired
    Environment env;
    
    public GenerateDummyClientSubmissionForDev(SubmissionRepositoryService submissionRepositoryService,
            SubmissionRepository submissionRepository, HttpSession httpSession) {
        this.submissionRepositoryService = submissionRepositoryService;
        this.submissionRepository = submissionRepository;
        this.httpSession = httpSession;
    }

    @Override
    public void run(FormSubmission formSubmission, Submission submission) {
        String[] activeProfiles = env.getActiveProfiles();
        boolean isDevProfile = Arrays.asList(activeProfiles).contains("dev");
        if (isDevProfile) {
            Optional<Submission> existingDummyClientSubmision = submissionRepositoryService.findByShortCode("DEV-123ABC");
            existingDummyClientSubmision.ifPresent(submissionRepository::delete);

            Map<String, Object> inputData = createFamilySubmission();

            Submission dummyClientSubmission = new Submission();
            dummyClientSubmission.setSubmittedAt(OffsetDateTime.now().minusDays(1));
            dummyClientSubmission.setFlow("gcc");
            dummyClientSubmission.setShortCode("DEV-123ABC");
            dummyClientSubmission.setInputData(inputData);
            
            submissionRepositoryService.save(dummyClientSubmission);

            httpSession.setAttribute("clientSubmissionId", dummyClientSubmission.getId());
        }
    }

    private @NotNull Map<String, Object> createFamilySubmission() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("familyIntendedProviderName", "Dev Provider");
        inputData.put("parentFirstName", "Devy");
        inputData.put("parentLastName", "McDeverson");
        inputData.put("parentBirthMonth", "12");
        inputData.put("parentBirthDay", "25");
        inputData.put("parentBirthYear", "1985");
        inputData.put("parentHomeStreetAddress1", "972 Mission St");
        inputData.put("parentHomeStreetAddress2", "5th floor");
        inputData.put("parentHomeCity", "San Francisco");
        inputData.put("parentHomeState", "CA - California");
        inputData.put("parentHomeZipCode", "94103");
        inputData.put("parentHasPartner", "false");
        
        List<Map<String, Object>> children = new ArrayList<>();
        Map<String, Object> child1 = new HashMap<>();
        child1.put("uuid", "testerosa-testerson");
        child1.put("childFirstName", "Testerosa");
        child1.put("childLastName", "Testerson");
        child1.put("childDateOfBirthMonth", "10");
        child1.put("childDateOfBirthDay", "17");
        child1.put("childDateOfBirthYear", "2015");
        child1.put("childInCare", "true");
        child1.put("needFinancialAssistanceForChild", "Yes");
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
        child2.put("needFinancialAssistanceForChild", "Yes");
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
}
