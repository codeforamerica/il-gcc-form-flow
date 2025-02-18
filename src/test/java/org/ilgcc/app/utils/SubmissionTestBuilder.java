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

    public SubmissionTestBuilder withCCRR() {
        submission.getInputData().put("ccrrPhoneNumber", "(603) 555-1244");
        submission.getInputData().put("ccrrName", "Sample Test CCRRR");
        return this;
    }

    public SubmissionTestBuilder withFlow(String flow) {
        submission.setFlow(flow);
        return this;
    }

    public SubmissionTestBuilder withShortCode(String shortCode) {
        submission.setShortCode(shortCode);
        return this;
    }

    public SubmissionTestBuilder withProviderSubmissionData(){
        submission.getInputData().put("providerResponseFirstName", "Provider");
        submission.getInputData().put("providerResponseLastName", "LastName");
        submission.getInputData().put("providerResponseBusinessName", "DayCare Place");
        submission.getInputData().put("providerResponseServiceStreetAddress1", "123 Main St");
        submission.getInputData().put("providerResponseServiceStreetAddress2", "Unit 10");
        submission.getInputData().put("providerResponseServiceCity", "DeKalb");
        submission.getInputData().put("providerResponseServiceState", "IL");
        submission.getInputData().put("providerResponseServiceZipCode", "60112");
        submission.getInputData().put("providerResponseContactPhoneNumber", "(111) 222-3333");
        submission.getInputData().put("providerResponseContactEmail", "mail@daycareplace.org");

        return this;
    }

    public SubmissionTestBuilder withProviderStateLicense(){
        submission.getInputData().put("providerCurrentlyLicensed", "true");
        submission.getInputData().put("providerLicenseNumber", "123453646");
        submission.getInputData().put("providerLicenseState", "IL");

        return this;
    }
    public SubmissionTestBuilder withDayCareProvider() {
        submission.getInputData().put("familyIntendedProviderName", "Open Sesame");
        return this;
    }
    public SubmissionTestBuilder withClientResponseConfirmationCode(String confirmationCode) {
        Map<String, String> clientResponse = new HashMap<>();
        clientResponse.put("clientResponseConfirmationCode", confirmationCode);
        submission.getInputData().put("clientResponse", clientResponse);
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
            String parentHomeZipCode) {
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
            String parentMailingZipCode) {
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
            String parentMailingZipCode_validated) {
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
        child.put("childDateOfBirthMonth", "10");
        child.put("childDateOfBirthDay", "11");
        child.put("childDateOfBirthYear", "2002");
        child.put("needFinancialAssistanceForChild", needFinancialAssistanceForChild);
        child.put("childIsUsCitizen", "Yes");
        child.put("ccapStartDate", "01/10/2025");
        child.put(Submission.ITERATION_IS_COMPLETE_KEY, true);
        children.add(child);
        submission.getInputData().put("children", children);
        return this;
    }
    public SubmissionTestBuilder withProviderHouseholdMember(
        String firstName,
        String lastName,
        String dateOfBirthDay,
        String dateOfBirthMonth,
        String dateOfBirthYear,
        String relationship,
        String SSN) {
        List<Map<String, Object>> providerHouseholdMembers = (List<Map<String, Object>>) submission.getInputData().getOrDefault("providerHouseholdMembers", new ArrayList<>());

        Map<String, Object> providerHouseholdMember = new HashMap<>();
        String uuid = "%s-%s".formatted(firstName, lastName).toLowerCase();
        providerHouseholdMember.put("uuid", uuid);
        providerHouseholdMember.put("providerHouseholdMemberFirstName", firstName);
        providerHouseholdMember.put("providerHouseholdMemberLastName", lastName);
        providerHouseholdMember.put("providerHouseholdMemberDateOfBirthMonth", dateOfBirthMonth);
        providerHouseholdMember.put("providerHouseholdMemberDateOfBirthDay", dateOfBirthDay);
        providerHouseholdMember.put("providerHouseholdMemberDateOfBirthYear", dateOfBirthYear);
        providerHouseholdMember.put("providerHouseholdMemberRelationship", relationship);
        providerHouseholdMember.put("providerHouseholdMemberSSN", SSN);
        providerHouseholdMember.put(Submission.ITERATION_IS_COMPLETE_KEY, true);
        providerHouseholdMembers.add(providerHouseholdMember);
        submission.getInputData().put("providerHouseholdMembers", providerHouseholdMembers);
        return this;
    }

    public SubmissionTestBuilder withJob(String subflow, String companyName, String employerStreetAddress, String employerCity,
            String employerState, String employerZipCode, String employerPhoneNumber, String isSelfEmployed) {
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
        job.put("isSelfEmployed", isSelfEmployed);
        job.put(Submission.ITERATION_IS_COMPLETE_KEY, true);
        jobs.add(job);
        submission.getInputData().put(subflow, jobs);
        return this;
    }
    public SubmissionTestBuilder withJob(String subflow, HashMap<String, Object> fields) {
        List<Map<String, Object>> jobs = (List<Map<String, Object>>) submission.getInputData().get(subflow);
        if (jobs == null) {
            jobs = new ArrayList<>();
        }
        if (!fields.isEmpty()) {
            jobs.add(fields);
        }
        submission.getInputData().put(subflow, jobs);
        return this;
    }

    public SubmissionTestBuilder addJobWithStartDate(String activitiesJobStartDay, String activitiesJobStartMonth, String activitiesJobStartYear) {
        var job = new HashMap<String, Object>();
        job.put("activitiesJobStartDay", activitiesJobStartDay);
        job.put("activitiesJobStartMonth", activitiesJobStartMonth);
        job.put("activitiesJobStartYear", activitiesJobStartYear);
        return withJob("jobs", job);
    }
    public SubmissionTestBuilder addPartnerJobWithStartDate(String activitiesPartnerJobStartDay, String activitiesPartnerJobStartMonth, String activitiesPartnerJobStartYear) {
        var job = new HashMap<String, Object>();
        job.put("activitiesPartnerJobStartDay", activitiesPartnerJobStartDay);
        job.put("activitiesPartnerJobStartMonth", activitiesPartnerJobStartMonth);
        job.put("activitiesPartnerJobStartYear", activitiesPartnerJobStartYear);
        return withJob("partnerJobs", job);
    }

    public SubmissionTestBuilder withPartnerJob(String subflow, String companyName, String employerStreetAddress,
            String employerCity, String employerState, String employerZipCode, String employerPhoneNumber,
            String isSelfEmployed) {
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
        job.put("isSelfEmployed", isSelfEmployed);
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

        setTime(child, "childcare", "Start", "AllDays", TimeOption.TIME9AM);
        setTime(child, "childcare", "End", "AllDays", TimeOption.TIME5PM);

        setTime(child, "childcare", "Start", "Monday", TimeOption.NOTIME);
        setTime(child, "childcare", "End", "Monday", TimeOption.NOTIME);

        setTime(child, "childcare", "Start", "Wednesday", TimeOption.NOTIME);
        setTime(child, "childcare", "End", "Wednesday", TimeOption.NOTIME);

        setTime(child, "childcare", "Start", "Friday", TimeOption.NOTIME);
        setTime(child, "childcare", "End", "Friday", TimeOption.NOTIME);

        child.put("childcareWeeklySchedule[]", List.of("Monday", "Wednesday", "Friday"));
        child.put("childcareStartTimeWednesday", "");
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

        setTime(child, "childcare", "Start", "AllDays", TimeOption.NOTIME);
        setTime(child, "childcare", "End", "AllDays", TimeOption.NOTIME);

        setTime(child, "childcare", "Start", "Tuesday", TimeOption.TIME9AM);
        setTime(child, "childcare", "End", "Tuesday", TimeOption.TIME12PM);

        setTime(child, "childcare", "Start", "Wednesday", TimeOption.TIME1PM);
        setTime(child, "childcare", "End", "Wednesday", TimeOption.TIME3PM);

        setTime(child, "childcare", "Start", "Saturday", TimeOption.TIME113PM);
        setTime(child, "childcare", "End", "Saturday", TimeOption.TIME310PM);

        child.put("childcareWeeklySchedule[]", List.of("Tuesday", "Wednesday", "Saturday"));
        child.put("childcareHoursSameEveryDay[]", List.of());

        children.set(childPosition, child);

        return this;
    }

    public SubmissionTestBuilder withRegularSchoolSchedule(String inputNamePrefix, String weeklyScheduleName, List days) {
        if (!childcareReasonKey(inputNamePrefix).isBlank()) {
            submission.getInputData().put(childcareReasonKey(inputNamePrefix), List.of("SCHOOL"));
        }

        setTime(submission.getInputData(), inputNamePrefix, "Start", "AllDays", TimeOption.TIME10AM);
        setTime(submission.getInputData(), inputNamePrefix, "End", "AllDays", TimeOption.TIME345PM);

        submission.getInputData().put(inputNamePrefix + "HoursSameEveryDay[]", List.of("Yes"));
        submission.getInputData().put(weeklyScheduleName, days);
        return this;

    }

    public SubmissionTestBuilder withSchoolScheduleByDay(String inputNamePrefix, String day,
            TimeOption startTime, TimeOption endTime) {
        if (!childcareReasonKey(inputNamePrefix).isBlank()) {
            submission.getInputData().put(childcareReasonKey(inputNamePrefix), List.of("SCHOOL"));
        }
        submission.getInputData().put(inputNamePrefix + "HoursSameEveryDay[]", List.of());

        setTime(submission.getInputData(), inputNamePrefix, "Start", day, startTime);
        setTime(submission.getInputData(), inputNamePrefix, "End", day, endTime);

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
        withJob("jobs", "Regular Schedule Job", "123 Main Str", "", "", "", "", "false");

        List<Map<String, Object>> jobs = (List<Map<String, Object>>) submission.getInputData().get("jobs");
        if (jobs == null) {
            return this;
        }

        Map<String, Object> job = jobs.get(jobs.size() - 1);

        setTime(job, "activitiesJob", "Start", "AllDays", TimeOption.TIME10AM);
        setTime(job, "activitiesJob", "End", "AllDays", TimeOption.TIME345PM);

        job.put("activitiesJobHoursSameEveryDay[]", List.of("Yes"));
        job.put("activitiesJobWeeklySchedule[]", days);
        submission.getInputData().put("activitiesParentChildcareReason[]", List.of("WORKING"));
        return this;
    }

    public SubmissionTestBuilder withRegularWorkScheduleAddHour(List days, TimeOption startTime, TimeOption endTime) {
        withJob("jobs", "Regular Schedule Job", "123 Main Str", "", "", "", "", "false");
        List<Map<String, Object>> jobs = (List<Map<String, Object>>) submission.getInputData().get("jobs");
        if (jobs == null) {
            return this;
        }

        Map<String, Object> job = jobs.get(jobs.size() - 1);

        setTime(job, "activitiesJob", "Start", "AllDays", startTime);
        setTime(job, "activitiesJob", "End", "AllDays", endTime);

        job.put("activitiesJobHoursSameEveryDay[]", List.of("Yes"));
        job.put("activitiesJobWeeklySchedule[]", days);
        submission.getInputData().put("activitiesParentChildcareReason[]", List.of("WORKING"));
        return this;
    }

    public SubmissionTestBuilder withWorkScheduleByDay(String day, TimeOption startTime, TimeOption endTime) {
        withJob("jobs", "Regular Mixed Schedule Job", "123 Main Str", "", "", "", "", "false");

        List<Map<String, Object>> jobs = (List<Map<String, Object>>) submission.getInputData().get("jobs");
        if (jobs == null) {
            return this;
        }

        Map<String, Object> job = jobs.get(0);

        job.put("activitiesJobHoursSameEveryDay[]", List.of());
        setTime(job, "activitiesJob", "Start", day, startTime);
        setTime(job, "activitiesJob", "End", day, endTime);

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
        withJob("partnerJobs", "Regular Schedule Job", "123 Main Str", "", "", "", "", "false");

        List<Map<String, Object>> jobs = (List<Map<String, Object>>) submission.getInputData().get("partnerJobs");
        if (jobs == null) {
            return this;
        }

        Map<String, Object> job = jobs.get(jobs.size() - 1);

        setTime(job, "activitiesJob", "Start", "AllDays", TimeOption.TIME10AM);
        setTime(job, "activitiesJob", "End", "AllDays", TimeOption.TIME345PM);

        job.put("activitiesJobHoursSameEveryDay[]", List.of("Yes"));
        job.put("activitiesJobWeeklySchedule[]", days);
        submission.getInputData().put("activitiesParentChildcareReason[]", List.of("WORKING"));
        return this;
    }

    public SubmissionTestBuilder withPartnerRegularWorkScheduleAddHour(List days, TimeOption startTime, TimeOption endTime) {
        withJob("partnerJobs", "Regular Schedule Job", "123 Main Str", "", "", "", "", "false");
        List<Map<String, Object>> jobs = (List<Map<String, Object>>) submission.getInputData().get("partnerJobs");
        if (jobs == null) {
            return this;
        }

        Map<String, Object> job = jobs.get(jobs.size() - 1);

        setTime(job, "activitiesJob", "Start", "AllDays", startTime);
        setTime(job, "activitiesJob", "End", "AllDays", endTime);

        job.put("activitiesJobHoursSameEveryDay[]", List.of("Yes"));
        job.put("activitiesJobWeeklySchedule[]", days);
        submission.getInputData().put("activitiesParentChildcareReason[]", List.of("WORKING"));
        return this;
    }

    public SubmissionTestBuilder withPartnerWorkScheduleByDay(String day, TimeOption startTime, TimeOption endTime) {
        withParentPartnerDetails();
        withJob("partnerJobs", "Regular Mixed Schedule Job", "123 Main Str", "", "", "", "", "false");
        List<Map<String, Object>> partnerJobs = (List<Map<String, Object>>) submission.getInputData().get("partnerJobs");
        if (partnerJobs == null) {
            return this;
        }

        Map<String, Object> job = partnerJobs.get(0);
        job.put("activitiesJobHoursSameEveryDay[]", List.of());

        setTime(job, "activitiesJob", "Start", day, startTime);
        setTime(job, "activitiesJob", "End", day, endTime);

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

    public SubmissionTestBuilder addChildDataArray(int childIterationIndex, String inputName, List value) {
        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData()
                .getOrDefault("children", emptyList());
        if (children.isEmpty()) {
            return this;
        }

        Map<String, Object> child = children.get(childIterationIndex);

        child.put(inputName + "[]", value);
        return this;
    }

    public SubmissionTestBuilder withChildAttendsSchoolDuringTheDay(int childIterationPosition, String childAttendsOtherEd) {
        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData()
                .getOrDefault("children", emptyList());
        if (children.isEmpty()) {
            return this;
        }

        Map<String, Object> child = children.get(childIterationPosition);

        child.put("childAttendsOtherEd", (childAttendsOtherEd));
        return this;
    }

    public SubmissionTestBuilder withChildIsAUSCitizen(int childIterationPosition, String childIsUsCitizen) {
        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData()
                .getOrDefault("children", emptyList());
        if (children.isEmpty()) {
            return this;
        }

        Map<String, Object> child = children.get(childIterationPosition);
        child.put("childIsUsCitizen", (childIsUsCitizen));
        return this;
    }

    public SubmissionTestBuilder withChildHasSpecialNeeds(int childIterationPosition, String childHasSpecialNeeds) {
        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData()
                .getOrDefault("children", emptyList());
        if (children.isEmpty()) {
            return this;
        }

        Map<String, Object> child = children.get(childIterationPosition);

        child.put("childHasDisability", (childHasSpecialNeeds));
        return this;
    }

    public SubmissionTestBuilder addChildCareStartDate(int childIterationPosition, String year, String month, String day) {
        List<Map<String, Object>> children = (List<Map<String, Object>>) submission.getInputData()
                .getOrDefault("children", emptyList());
        if (children.isEmpty()) {
            return this;
        }
        Map<String, Object> child = children.get(childIterationPosition);
        child.put("ccapStartYear", year);
        child.put("ccapStartMonth", month);
        child.put("ccapStartDay", day);

        child.put("ccapStartDate", DateUtilities.getFormattedDateFromMonthDateYearInputs("ccapStart", child));

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

    public SubmissionTestBuilder withEducationType(String educationType, String parentOrPartner) {
        if (parentOrPartner.equalsIgnoreCase("parent")) {
            submission.getInputData().put("educationType", educationType);
        }
        if (parentOrPartner.equalsIgnoreCase("partner")) {
            submission.getInputData().put("partnerEducationType", educationType);
        }
        return this;
    }

    public SubmissionTestBuilder setTime(Map<String, Object> data, String inputPrefix, String startOrEndKey, String dayPostFix,
            TimeOption timeOption) {
        data.put(inputPrefix + startOrEndKey + "Time" + dayPostFix + "Hour", timeOption.getHour());
        data.put(inputPrefix + startOrEndKey + "Time" + dayPostFix + "Minute", timeOption.getMinute());
        data.put(inputPrefix + startOrEndKey + "Time" + dayPostFix + "AmPm", timeOption.getAmOrPm());

        return this;
    }
}
