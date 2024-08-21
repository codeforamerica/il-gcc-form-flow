package org.ilgcc.app.utils;

import static java.util.Collections.emptyList;

import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;

public class RecommendedDocumentsUtilities {

  //create a function that checks homelessness
  public static boolean showRequiredHomelessnessDocuments(Submission submission) {
    return submission.getInputData().getOrDefault("parentHomeExperiencingHomelessness[]", List.of("no")).equals(List.of("yes"));
  }

  public static boolean showTANFDocumentation(Submission submission) {
    Map<String, Object> inputData = submission.getInputData();
    List<String> parentNeedForChildcareReasons = (List<String>) inputData.getOrDefault("activitiesParentChildcareReason[]",
        List.of());
    boolean tanfISReasonForParentChildcareNeed = parentNeedForChildcareReasons.stream().anyMatch(
        reason -> reason.equals(ChildcareReasonOption.TANF_TRAINING.toString())
    );
    boolean parentHasAPartner = inputData.getOrDefault("parentHasPartner", "false").equals("true");
    if (parentHasAPartner) {
      List<String> partnerNeedForChildcareReasons = (List<String>) inputData.getOrDefault(
          "activitiesParentPartnerChildcareReason[]", List.of());
      boolean tanfISReasonForPartnerChildcareNeed = partnerNeedForChildcareReasons.stream().anyMatch(
          reason -> reason.equals(ChildcareReasonOption.TANF_TRAINING.toString())
      );
      return (tanfISReasonForPartnerChildcareNeed || tanfISReasonForParentChildcareNeed);
    }
    return tanfISReasonForParentChildcareNeed;
  }

  public static boolean showWorkingDocumentation(Submission submission) {
    Map<String, Object> inputData = submission.getInputData();
    List<String> parentNeedForChildcareReasons = (List<String>) inputData.getOrDefault("activitiesParentChildcareReason[]",
        List.of());
    boolean workingISReasonForParentChildcareNeed = parentNeedForChildcareReasons.stream().anyMatch(
        reason -> reason.equals(ChildcareReasonOption.WORKING.toString())
    );
    boolean parentHasAPartner = inputData.getOrDefault("parentHasPartner", "false").equals("true");
    if (parentHasAPartner) {
      List<String> partnerNeedForChildcareReasons = (List<String>) inputData.getOrDefault(
          "activitiesParentPartnerChildcareReason[]", List.of());
      boolean workingISReasonForPartnerChildcareNeed = partnerNeedForChildcareReasons.stream().anyMatch(
          reason -> reason.equals(ChildcareReasonOption.WORKING.toString())
      );
      return (workingISReasonForPartnerChildcareNeed || workingISReasonForParentChildcareNeed);
    }
    return workingISReasonForParentChildcareNeed;
  }

  public static boolean showSchoolOrTrainingDocumentation(Submission submission) {
    Map<String, Object> inputData = submission.getInputData();
    List<String> parentNeedForChildcareReasons = (List<String>) inputData.getOrDefault("activitiesParentChildcareReason[]",
        List.of());
    boolean schoolISReasonForParentChildcareNeed = parentNeedForChildcareReasons.stream().anyMatch(
        reason -> reason.equals(ChildcareReasonOption.SCHOOL.toString())
    );
    boolean parentHasAPartner = inputData.getOrDefault("parentHasPartner", "false").equals("true");
    if (parentHasAPartner) {
      List<String> partnerNeedForChildcareReasons = (List<String>) inputData.getOrDefault(
          "activitiesParentPartnerChildcareReason[]", List.of());
      boolean schoolISReasonForPartnerChildcareNeed = partnerNeedForChildcareReasons.stream().anyMatch(
          reason -> reason.equals(ChildcareReasonOption.SCHOOL.toString())
      );
      return (schoolISReasonForPartnerChildcareNeed || schoolISReasonForParentChildcareNeed);
    }
    return schoolISReasonForParentChildcareNeed;
  }

  public static boolean showSelfEmploymentDocumentation(Submission submission) {
    //get jobs for parent if parent has a job
    List<Map> jobs = (List<Map>) submission.getInputData().getOrDefault("jobs", emptyList());
    if (!jobs.isEmpty()) {
      for (var job : jobs) {
        if (job.getOrDefault("isSelfEmployed", "false").equals("true")) {
          return true;
        }
      }
    }
    List<Map> partnerJobs = (List<Map>) submission.getInputData().getOrDefault("partnerJobs", emptyList());
    if (!partnerJobs.isEmpty()) {
      for (var job : partnerJobs) {
        if (job.getOrDefault("isSelfEmployed", "false").equals("true")) {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean shouldDisplayRecommendedDocumentScreens(Submission submission){
    return (showSchoolOrTrainingDocumentation(submission) ||
        showTANFDocumentation(submission) ||
        showWorkingDocumentation(submission) ||
        showSelfEmploymentDocumentation(submission) ||
        showSelfEmploymentDocumentation(submission)) ||
        showRequiredHomelessnessDocuments(submission);
  }
}