package org.ilgcc.app.utils;

import formflow.library.data.Submission;
import jakarta.validation.constraints.NotBlank;
import jakarta.websocket.OnClose;
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

    public SubmissionTestBuilder withParentPartnerDetails() {
        submission.getInputData().put("parentSpouseIsStepParent", "Yes");
        submission.getInputData().put("parentSpouseShareChildren", "Yes");
        submission.getInputData().put("parentSpouseLiveTogether", "Yes");
        submission.getInputData().put("parentPartnerFirstName", "partner");
        submission.getInputData().put("parentPartnerLastName", "parent");
        submission.getInputData().put("parentPartnerBirthMonth", "12");
        submission.getInputData().put("parentPartnerBirthDay", "25");
        submission.getInputData().put("parentPartnerBirthYear", "2018");
        submission.getInputData().put("parentHasPartner", "true");
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

    public SubmissionTestBuilder withChild(String firstName, String lastName) {
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
        child.put("needFinancialAssistanceForChild", "Yes");
        child.put("childIsUsCitizen", "Yes");
        child.put(Submission.ITERATION_IS_COMPLETE_KEY, true);
        children.add(child);
        submission.getInputData().put("children", children);
        return this;
    }

}
