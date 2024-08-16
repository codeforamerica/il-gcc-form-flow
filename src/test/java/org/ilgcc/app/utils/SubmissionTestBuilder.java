package org.ilgcc.app.utils;

import static java.util.Collections.emptyList;
import formflow.library.data.Submission;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmissionTestBuilder {

    private final Submission submission;

    public SubmissionTestBuilder() {
        this.submission = new Submission();
    }

    public SubmissionTestBuilder(Submission submission) {
        this.submission = submission;
    }

    public Submission build() {
        return submission;
    }


    public SubmissionTestBuilder with(String key, Object value) {
        submission.getInputData().put(key, value);
        return this;
    }

    public SubmissionTestBuilder withSubmittedAtDate(OffsetDateTime date) {
        submission.setSubmittedAt(date);
        return this;
    }

    public SubmissionTestBuilder withDayCareProvider() {
        submission.getInputData().put("dayCareChoice", "OPEN_SESAME");
        return this;
    }

    public SubmissionTestBuilder withParentDetails() {
        submission.getInputData().put("parentFirstName", "parent first");
        submission.getInputData().put("parentLastName", "parent last");
        submission.getInputData().put("parentBirthMonth", "12");
        submission.getInputData().put("parentBirthDay", "25");
        submission.getInputData().put("parentBirthYear", "1985");

        submission.getInputData().put("parentHomeStreetAddress1", "972 Mission St");
        submission.getInputData().put("parentHomeStreetAddress2", "5th floor");
        submission.getInputData().put("parentHomeCity", "San Francisco");
        submission.getInputData().put("parentHomeState", "CA - California");
        submission.getInputData().put("parentHomeZipCode", "94103");
        submission.getInputData().put("parentHasPartner", "false");
        return this;
    }

    public SubmissionTestBuilder withHomelessDetails() {
        submission.getInputData().put("parentFirstName", "HP_first");
        submission.getInputData().put("parentLastName", "HP_last");
        submission.getInputData().put("parentBirthMonth", "10");
        submission.getInputData().put("parentBirthDay", "20");
        submission.getInputData().put("parentBirthYear", "1922");
        return this;
    }

    public SubmissionTestBuilder withParentPartnerDetails() {
        submission.getInputData().put("parentPartnerFirstName", "partner");
        submission.getInputData().put("parentPartnerLastName", "parent");
        submission.getInputData().put("parentPartnerBirthMonth", "12");
        submission.getInputData().put("parentPartnerBirthDay", "25");
        submission.getInputData().put("parentPartnerBirthYear", "2018");
        submission.getInputData().put("parentHasQualifyingPartner", "true");
        submission.getInputData().put("parentHasPartner", "true");
        return this;
    }

    public SubmissionTestBuilder withHomeAddress(
        String parentHomeStreetAddress1,
        String parentHomeStreetAddress2,
        String parentHomeCity,
        String parentHomeState,
        String parentHomeZipCode){
        submission.getInputData().put("parentHomeStreetAddress1", parentHomeStreetAddress1);
        submission.getInputData().put("parentHomeStreetAddress2", parentHomeStreetAddress2);
        submission.getInputData().put("parentHomeCity", parentHomeCity);
        submission.getInputData().put("parentHomeState", parentHomeState);
        submission.getInputData().put("parentHomeZipCode", parentHomeZipCode);
        return this;
    }

    public SubmissionTestBuilder withMailingAddress(
        String parentMailingStreetAddress1,
        String parentMailingStreetAddress2,
        String parentMailingCity,
        String parentMailingState,
        String parentMailingZipCode){
        submission.getInputData().put("parentMailingStreetAddress1", parentMailingStreetAddress1);
        submission.getInputData().put("parentMailingStreetAddress2", parentMailingStreetAddress2);
        submission.getInputData().put("parentMailingCity", parentMailingCity);
        submission.getInputData().put("parentMailingState", parentMailingState);
        submission.getInputData().put("parentMailingZipCode", parentMailingZipCode);
        return this;
    }

    public SubmissionTestBuilder withValidatedMailingAddress(
        String parentMailingStreetAddress1_validated,
        String parentMailingStreetAddress2_validated,
        String parentMailingCity_validated,
        String parentMailingState_validated,
        String parentMailingZipCode_validated){
        submission.getInputData().put("parentMailingStreetAddress1_validated", parentMailingStreetAddress1_validated);
        submission.getInputData().put("parentMailingStreetAddress2_validated", parentMailingStreetAddress2_validated);
        submission.getInputData().put("parentMailingCity_validated", parentMailingCity_validated);
        submission.getInputData().put("parentMailingState_validated", parentMailingState_validated);
        submission.getInputData().put("parentMailingZipCode_validated", parentMailingZipCode_validated);
        return this;
    }


    public SubmissionTestBuilder withAdultDependent(String firstName, String lastName) {
        List<Map<String, Object>> adultDependents = (List<Map<String, Object>>) submission.getInputData().get("adultDependents");
        if (adultDependents == null) {
            adultDependents = new ArrayList<>();
        }

        Map<String, Object> adultDependent = new HashMap<>();
        String uuid = "%s-%s".formatted(firstName, lastName).toLowerCase();
        adultDependent.put("uuid", uuid);
        adultDependent.put("adultDependentFirstName", firstName);
        adultDependent.put("adultDependentLastName", lastName);
        adultDependent.put("adultDependentBirthdateDay", "10");
        adultDependent.put("adultDependentBirthdateMonth", "11");
        adultDependent.put("adultDependentBirthdateYear", "2001");
        adultDependent.put(Submission.ITERATION_IS_COMPLETE_KEY, true);
        adultDependents.add(adultDependent);
        submission.getInputData().put("adultDependents", adultDependents);
        return this;
    }

    public SubmissionTestBuilder withChild(String firstName, String lastName, String needFinancialAssistanceForChild) {
        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData().get("children");
        if (children == null) {
            children = new ArrayList<>();
        }

        Map<String, Object> child = new HashMap<>();
        String uuid = "%s-%s".formatted(firstName, lastName).toLowerCase();
        child.put("uuid", uuid);
        child.put("childFirstName", firstName);
        child.put("childLastName", lastName);
        child.put("childInCare", "true");
        child.put("childBirthdateDay", "10");
        child.put("childBirthdateMonth", "11");
        child.put("childBirthdateYear", "2001");
        child.put("needFinancialAssistanceForChild", needFinancialAssistanceForChild);
        child.put("childIsUsCitizen", "Yes");
        child.put(Submission.ITERATION_IS_COMPLETE_KEY, true);
        children.add(child);
        submission.getInputData().put("children", children);
        return this;
    }

    public SubmissionTestBuilder withJob(String subflow, String companyName, String employerStreetAddress, String employerCity,
        String employerState, String employerZipCode, String employerPhoneNumber) {
        List<Map<String, Object>> jobs = (List<Map<String, Object>>) submission.getInputData().get(subflow);
        if (jobs == null) {
            jobs = new ArrayList<>();
        }

        Map<String, Object> job = new HashMap<>();
        String uuid = companyName.toLowerCase();
        job.put("uuid", uuid);
        job.put("companyName", companyName);
        job.put("employerStreetAddress", employerStreetAddress);
        job.put("employerCity", employerCity);
        job.put("employerState", employerState);
        job.put("employerZipCode", employerZipCode);
        job.put("employerPhoneNumber", employerPhoneNumber);
        job.put(Submission.ITERATION_IS_COMPLETE_KEY, true);
        jobs.add(job);
        submission.getInputData().put(subflow, jobs);
        return this;
    }

    public SubmissionTestBuilder withPartnerJob(String subflow, String companyName, String employerStreetAddress,
        String employerCity, String employerState, String employerZipCode, String employerPhoneNumber) {
        List<Map<String, Object>> jobs = (List<Map<String, Object>>) submission.getInputData().get(subflow);
        if (jobs == null) {
            jobs = new ArrayList<>();
        }

        Map<String, Object> job = new HashMap<>();
        String uuid = companyName.toLowerCase();
        job.put("uuid", uuid);
        job.put("partnerCompanyName", companyName);
        job.put("partnerEmployerStreetAddress", employerStreetAddress);
        job.put("partnerEmployerCity", employerCity);
        job.put("partnerEmployerState", employerState);
        job.put("partnerEmployerZipCode", employerZipCode);
        job.put("partnerEmployerPhoneNumber", employerPhoneNumber);
        job.put(Submission.ITERATION_IS_COMPLETE_KEY, true);
        jobs.add(job);
        submission.getInputData().put(subflow, jobs);
        return this;
    }

    public SubmissionTestBuilder withConstantChildcareSchedule(int childPosition) {

        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData().get("children");
        if (children == null) {
            return this;
        }

        Map<String, Object> child = children.get(childPosition);

        setTime(child, "childcare", "Start", "AllDays", "9", "0", "AM");
        setTime(child, "childcare", "End", "AllDays", "5", "0", "PM");

        setTime(child, "childcare", "Start", "Monday", "", "", "");
        setTime(child, "childcare", "End", "Monday", "", "", "");

        setTime(child, "childcare", "Start", "Wednesday", "", "", "");
        setTime(child, "childcare", "End", "Wednesday", "", "", "");

        setTime(child, "childcare", "Start", "Friday", "", "", "");
        setTime(child, "childcare", "End", "Friday", "", "", "");

        child.put("childcareWeeklySchedule[]", List.of("Monday", "Wednesday", "Friday"));
        child.put("childcareHoursSameEveryDay[]", List.of("yes"));

        children.set(childPosition, child);

        return this;
    }

    public SubmissionTestBuilder withVaryingChildcareSchedule(int childPosition) {
        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData().get("children");
        if (children == null) {
            return this;
        }

        Map<String, Object> child = children.get(childPosition);

        setTime(child, "childcare", "Start", "AllDays", "", "", "");
        setTime(child, "childcare", "End", "AllDays", "", "", "");

        setTime(child, "childcare", "Start", "Tuesday", "9", "0", "AM");
        setTime(child, "childcare", "End", "Tuesday", "12", "0", "PM");

        setTime(child, "childcare", "Start", "Wednesday", "1", "0", "PM");
        setTime(child, "childcare", "End", "Wednesday", "3", "0", "PM");

        setTime(child, "childcare", "Start", "Saturday", "1", "13", "PM");
        setTime(child, "childcare", "End", "Saturday", "3", "10", "PM");

        child.put("childcareWeeklySchedule[]", List.of("Tuesday", "Wednesday", "Saturday"));
        child.put("childcareHoursSameEveryDay[]", List.of());

        children.set(childPosition, child);

        return this;
    }

    public SubmissionTestBuilder withRegularSchoolSchedule(String inputNamePrefix, String weeklyScheduleName, List days) {
        if (!childcareReasonKey(inputNamePrefix).isBlank()) {
            submission.getInputData().put(childcareReasonKey(inputNamePrefix), List.of("SCHOOL"));
        }

        setTime(submission.getInputData(), inputNamePrefix, "Start", "AllDays", "10", "0", "AM");
        setTime(submission.getInputData(), inputNamePrefix, "End", "AllDays", "3", "45", "PM");

        submission.getInputData().put(inputNamePrefix + "HoursSameEveryDay[]", List.of("Yes"));
        submission.getInputData().put(weeklyScheduleName, days);
        return this;

    }

    public SubmissionTestBuilder withSchoolScheduleByDay(String inputNamePrefix, String day,
        String startHour, String startMinute, String startAmPm, String endHour, String endMinute, String endAmPm) {
        if (!childcareReasonKey(inputNamePrefix).isBlank()) {
            submission.getInputData().put(childcareReasonKey(inputNamePrefix), List.of("SCHOOL"));
        }
        submission.getInputData().put(inputNamePrefix + "HoursSameEveryDay[]", List.of());

        setTime(submission.getInputData(), inputNamePrefix, "Start", day, startHour, startMinute, startAmPm);
        setTime(submission.getInputData(), inputNamePrefix, "End", day, endHour, endMinute, endAmPm);

        return this;
    }

    public SubmissionTestBuilder regularWorkScheduleWithCommuteTime(String commuteLength) {
        withRegularWorkSchedule(List.of("Monday", "Thursday", "Sunday"));

        List<Map<String, Object>> jobs = (List<Map<String, Object>>) submission.getInputData().get("jobs");

        if (jobs == null) {
            return this;
        }

        Map<String, Object> job = jobs.get(jobs.size() - 1);

        job.put("activitiesJobCommuteTime", commuteLength);

        return this;
    }

    public SubmissionTestBuilder withRegularWorkSchedule(List days) {
        withJob("jobs", "Regular Schedule Job", "123 Main Str", "", "", "", "");
        List<Map<String, Object>> jobs = (List<Map<String, Object>>) submission.getInputData().get("jobs");
        if (jobs == null) {
            return this;
        }

        Map<String, Object> job = jobs.get(jobs.size() - 1);

        setTime(job, "activitiesJob", "Start", "AllDays", "10", "0", "AM");
        setTime(job, "activitiesJob", "End", "AllDays", "3", "45", "PM");

        job.put("activitiesJobHoursSameEveryDay[]", List.of("Yes"));
        job.put("activitiesJobWeeklySchedule[]", days);
        submission.getInputData().put("activitiesParentChildcareReason[]", List.of("WORKING"));
        return this;
    }

    public SubmissionTestBuilder withRegularWorkScheduleAddHour(List days, String startHour, String startMinute, String startAmPm, String endHour, String endMinute, String endAmPm) {
        withJob("jobs", "Regular Schedule Job", "123 Main Str", "", "", "", "");
        List<Map<String, Object>> jobs = (List<Map<String, Object>>) submission.getInputData().get("jobs");
        if (jobs == null) {
            return this;
        }

        Map<String, Object> job = jobs.get(jobs.size() - 1);

        setTime(job, "activitiesJob", "Start", "AllDays", startHour, startMinute, startAmPm);
        setTime(job, "activitiesJob", "End", "AllDays", endHour, endMinute, endAmPm);

        job.put("activitiesJobHoursSameEveryDay[]", List.of("Yes"));
        job.put("activitiesJobWeeklySchedule[]", days);
        submission.getInputData().put("activitiesParentChildcareReason[]", List.of("WORKING"));
        return this;
    }

    public SubmissionTestBuilder withWorkScheduleByDay(String day, String startHour, String startMinute, String startAmPm, String endHour, String endMinute, String endAmPm) {
        withJob("jobs", "Regular Mixed Schedule Job", "123 Main Str", "", "", "", "");
        List<Map<String, Object>> jobs = (List<Map<String, Object>>) submission.getInputData().get("jobs");
        if (jobs == null) {
            return this;
        }

        Map<String, Object> job = jobs.get(0);

        job.put("activitiesJobHoursSameEveryDay[]", List.of());
        setTime(job, "activitiesJob", "Start", day, startHour, startMinute, startAmPm);
        setTime(job, "activitiesJob", "End", day, endHour, endMinute, endAmPm);

        ArrayList<String> jobList = new ArrayList<>();
        if (job.containsKey("activitiesJobWeeklySchedule[]")) {
            List jobSchedule = (List) job.getOrDefault("activitiesJobWeeklySchedule[]", List.of());
            jobList.addAll(jobSchedule);
        }
        jobList.add(day);
        job.put("activitiesJobWeeklySchedule[]", jobList);

        submission.getInputData().put("activitiesParentChildcareReason[]", List.of("WORKING"));
        return this;
    }

    public SubmissionTestBuilder withPartnerRegularWorkSchedule(List days) {
        withJob("partnerJobs", "Regular Schedule Job", "123 Main Str", "", "", "", "");
        List<Map<String, Object>> jobs = (List<Map<String, Object>>) submission.getInputData().get("partnerJobs");
        if (jobs == null) {
            return this;
        }

        Map<String, Object> job = jobs.get(jobs.size() - 1);

        setTime(job, "activitiesJob", "Start", "AllDays", "10", "0", "AM");
        setTime(job, "activitiesJob", "End", "AllDays", "3", "45", "PM");

        job.put("activitiesJobHoursSameEveryDay[]", List.of("Yes"));
        job.put("activitiesJobWeeklySchedule[]", days);
        submission.getInputData().put("activitiesParentChildcareReason[]", List.of("WORKING"));
        return this;
    }
    public SubmissionTestBuilder withPartnerRegularWorkScheduleAddHour(List days, String startHour, String startMinute, String startAmPm, String endHour, String endMinute, String endAmPm) {
        withJob("partnerJobs", "Regular Schedule Job", "123 Main Str", "", "", "", "");
        List<Map<String, Object>> jobs = (List<Map<String, Object>>) submission.getInputData().get("partnerJobs");
        if (jobs == null) {
            return this;
        }

        Map<String, Object> job = jobs.get(jobs.size() - 1);

        setTime(job, "activitiesJob", "Start", "AllDays", startHour, startMinute, startAmPm);
        setTime(job, "activitiesJob", "End", "AllDays", endHour, endMinute, endAmPm);

        job.put("activitiesJobHoursSameEveryDay[]", List.of("Yes"));
        job.put("activitiesJobWeeklySchedule[]", days);
        submission.getInputData().put("activitiesParentChildcareReason[]", List.of("WORKING"));
        return this;
    }

    public SubmissionTestBuilder withPartnerWorkScheduleByDay(String day, String startHour, String startMinute, String startAmPm, String endHour, String endMinute, String endAmPm) {
        withParentPartnerDetails();
        withJob("partnerJobs", "Regular Mixed Schedule Job", "123 Main Str", "", "", "", "");
        List<Map<String, Object>> partnerJobs = (List<Map<String, Object>>) submission.getInputData().get("partnerJobs");
        if (partnerJobs == null) {
            return this;
        }

        Map<String, Object> job = partnerJobs.get(0);
        job.put("activitiesJobHoursSameEveryDay[]", List.of());

        setTime(job, "activitiesJob", "Start", day, startHour, startMinute, startAmPm);
        setTime(job, "activitiesJob", "End", day, endHour, endMinute, endAmPm);

        ArrayList<String> jobList = new ArrayList<>();
        if (job.containsKey("activitiesJobWeeklySchedule[]")) {
            List jobSchedule = (List) job.getOrDefault("activitiesJobWeeklySchedule[]", List.of());
            jobList.addAll(jobSchedule);
        }
        jobList.add(day);
        job.put("activitiesJobWeeklySchedule[]", jobList);

        submission.getInputData().put("activitiesParentPartnerChildcareReason[]", List.of("WORKING"));
        return this;
    }

    public SubmissionTestBuilder regularPartnerScheduleWithCommuteTime(String commuteLength) {
        withPartnerRegularWorkSchedule(List.of("Monday", "Thursday", "Sunday"));

        List<Map<String, Object>> jobs = (List<Map<String, Object>>) submission.getInputData().get("partnerJobs");

        if (jobs == null) {
            return this;
        }

        Map<String, Object> job = jobs.get(jobs.size() - 1);

        job.put("activitiesJobCommuteTime", commuteLength);

        return this;
    }

    public SubmissionTestBuilder addChildDataArray(int childIterationIndex, String inputName, List value){
        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData().getOrDefault("children", emptyList());
        if (children.isEmpty()) {
            return this;
        }

        Map<String, Object> child = children.get(childIterationIndex);

        child.put(inputName+"[]", value);
        return this;
    }
    public SubmissionTestBuilder withChildAttendsSchoolDuringTheDay(int childIterationPosition, String childAttendsOtherEd){
        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData().getOrDefault("children", emptyList());
        if (children.isEmpty()) {
            return this;
        }

        Map<String, Object> child = children.get(childIterationPosition);

        child.put("childAttendsOtherEd", (childAttendsOtherEd));
        return this;
    }
    public SubmissionTestBuilder withChildIsAUSCitizen(int childIterationPosition, String childIsUsCitizen){
        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData().getOrDefault("children", emptyList());
        if (children.isEmpty()) {
            return this;
        }

        Map<String, Object> child = children.get(childIterationPosition);
        child.put("childIsUsCitizen", (childIsUsCitizen));
        return this;
    }
    public SubmissionTestBuilder withChildHasSpecialNeeds(int childIterationPosition, String childHasSpecialNeeds){
        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData().getOrDefault("children", emptyList());
        if (children.isEmpty()) {
            return this;
        }

        Map<String, Object> child = children.get(childIterationPosition);

        child.put("childHasDisability", (childHasSpecialNeeds));
        return this;
    }
    public SubmissionTestBuilder addChildCareStartDate(int childIterationPosition, String ccapStartDate){
        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData().getOrDefault("children", emptyList());
        if (children.isEmpty()) {
            return this;
        }
        Map<String, Object> child = children.get(childIterationPosition);
        child.put("ccapStartDate", (ccapStartDate));
        return this;
    }

    private String childcareReasonKey(String inputNamePrefix) {
        if (inputNamePrefix.equals("activitiesClass")) {
            return "activitiesParentChildcareReason[]";
        }

        if (inputNamePrefix.equals("partnerClass")) {
            return "activitiesParentPartnerChildcareReason[]";
        }

        return "";

    }

    public SubmissionTestBuilder withEducationType(String educationType, String parentOrPartner){
        if (parentOrPartner.equalsIgnoreCase("parent")){
            submission.getInputData().put("educationType", educationType);
        }
        if (parentOrPartner.equalsIgnoreCase("partner")){
            submission.getInputData().put("partnerEducationType", educationType);
        }
        return this;
    }

    public SubmissionTestBuilder setTime(Map<String, Object> data, String inputPrefix, String startOrEndKey, String dayPostFix, String hour, String minute, String amPm){
        data.put(inputPrefix + startOrEndKey + "Time" + dayPostFix + "Hour", hour);
        data.put(inputPrefix + startOrEndKey + "Time" + dayPostFix + "Minute", minute);
        data.put(inputPrefix + startOrEndKey + "Time" + dayPostFix + "AmPm", amPm);

        return this;

    }
}
