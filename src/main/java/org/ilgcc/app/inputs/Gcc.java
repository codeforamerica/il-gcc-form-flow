package org.ilgcc.app.inputs;

import formflow.library.data.FlowInputs;
import formflow.library.data.annotations.Money;
import formflow.library.data.annotations.Phone;
import formflow.library.inputs.Encrypted;
import formflow.library.utils.RegexUtils;
import jakarta.validation.constraints.*;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class Gcc extends FlowInputs {

    private String current_uuid;

    MultipartFile uploadDocuments;
    private String lang;

    // onboarding-choose-provider

    @NotBlank(message = "{errors.choose-provider}")
    private String dayCareChoice;
    private String languageRead;
    private String languageSpeak;

    // parent-info-basic-1
    @NotBlank(message = "{errors.provide-first-name}")
    private String parentFirstName;
    @NotBlank(message = "{errors.provide-last-name}")
    private String parentLastName;
    private String parentPreferredName;
    private String parentOtherLegalName;
    private String parentBirthDay;
    private String parentBirthMonth;
    private String parentBirthYear;
    private String parentBirthDate;
    @Pattern(regexp = "\\d{3}-\\d{2}-\\d{4}", message = "{errors.invalid-ssn}")
    @Encrypted
    private String parentSsn;
    private List<String> parentGender;

    // parent-info-service
    private String parentIsServing;
    private String parentInMilitaryReserveOrNationalGuard;

    // parent-home-address
    private Boolean parentHomeExperiencingHomelessness;
    @NotBlank(message = "{errors.provide-street}")
    private String parentHomeStreetAddress1;
    private String parentHomeStreetAddress2;
    @NotBlank(message = "{errors.provide-city}")
    private String parentHomeCity;
    @NotBlank(message = "{errors.provide-state}")
    private String parentHomeState;
    @NotBlank(message = "{errors.provide-zip}")
    private String parentHomeZipCode;

    // parent-mailing-address
    private Boolean parentMailingAddressSameAsHomeAddress;
    @NotBlank(message = "{errors.provide-street}")
    private String parentMailingStreetAddress1;
    private String parentMailingStreetAddress2;
    @NotBlank(message = "{errors.provide-city}")
    private String parentMailingCity;
    @NotBlank(message = "{errors.provide-state}")
    private String parentMailingState;
    @NotBlank(message = "{errors.provide-zip}")
    private String parentMailingZipCode;
    private String useSuggestedParentAddress;
    private String parentConfirmSuggestedAddress;
    // parent-comm-preference
    @NotBlank(message = "{errors.invalid-communication-preference}")
    private String parentContactPreferredCommunicationMethod;

    // parent-contact-info
    @Phone(message = "{errors.invalid-phone-number}")
    private String parentContactPhoneNumber;
    private String parentContactEmail;

    // parent-has-a-partner
    private String parentHasPartner;
    private String parentHasQualifyingPartner;

    // parent-partner-info-basic
    @NotBlank(message = "{errors.provide-first-name}")
    private String parentPartnerFirstName;
    @NotBlank(message = "{errors.provide-last-name}")
    private String parentPartnerLastName;
    private String parentPartnerBirthDay;
    private String parentPartnerBirthMonth;
    private String parentPartnerBirthYear;
    private String parentPartnerBirthDate;
    @Pattern(regexp = "\\d{3}-\\d{2}-\\d{4}", message = "{errors.invalid-ssn}")
    @Encrypted
    private String parentPartnerSSN;

    // parent-partner-contact
    @Phone(message = "{errors.invalid-phone-number}")
    private String parentPartnerPhoneNumber;
    @Email(regexp = RegexUtils.EMAIL_REGEX, message = "{errors.invalid-email}")
    private String parentPartnerEmail;

    // parent-partner-info-service
    private String parentPartnerIsServing;
    private String parentPartnerInMilitaryReserveOrNationalGuard;

    // parent-partner-info-disability
    private String parentPartnerHasDisability;

    // parent-other-family
    private String hasAdultDependents;

    // parent-add-adults-detail
    @NotBlank(message = "{errors.provide-first-name}")
    private String adultDependentFirstName;
    @NotBlank(message = "{errors.provide-last-name}")
    private String adultDependentLastName;
    private String adultDependentBirthdateDay;
    private String adultDependentBirthdateMonth;
    private String adultDependentBirthdateYear;
    private String adultDependentBirthdateDate;

    // children-info-basic
    @NotBlank(message = "{errors.provide-first-name}")
    private String childFirstName;
    @NotBlank(message = "{errors.provide-last-name}")
    private String childLastName;
    private String childDateOfBirthDay;
    private String childDateOfBirthMonth;
    private String childDateOfBirthYear;
    private String childDateOfBirthDate;
    @NotBlank(message = "{errors.required-financial-assistance}")
    private String needFinancialAssistanceForChild;

    // children-ccap-info
    @NotEmpty(message = "{errors.select-child-relationship}")
    private String childRelationship;
    private List<String> childGender;
    private String childHasDisability;
    private String childIsUsCitizen;
    private List<String> childRaceEthnicity;

    // children-ccap-in-care
    private String childInCare;

    // children-ccap-start-date
    private String ccapStartDay;
    private String ccapStartMonth;
    private String ccapStartYear;
    private String ccapStartDate;

    // children-childcare-weekly-schedule
    @NotEmpty(message = "{errors.select-at-least-one-day}")
    private List<String> childcareWeeklySchedule;

    // children-childcare-hourly-schedule
    private Boolean childcareHoursSameEveryDay;
    @NotBlank(message = "{errors.validate.start.time}")
    private String childcareStartTimeAllDays;
    @NotBlank(message = "{errors.validate.end.time}")
    private String childcareEndTimeAllDays;
    @NotBlank(message = "{errors.validate.start.time}")
    private String childcareStartTimeMonday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String childcareEndTimeMonday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String childcareStartTimeTuesday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String childcareEndTimeTuesday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String childcareStartTimeWednesday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String childcareEndTimeWednesday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String childcareStartTimeThursday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String childcareEndTimeThursday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String childcareStartTimeFriday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String childcareEndTimeFriday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String childcareStartTimeSaturday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String childcareEndTimeSaturday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String childcareStartTimeSunday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String childcareEndTimeSunday;

    // children-ccap-child-other-ed
    private String childAttendsOtherEd;

    // activities-parent-type
    private List<String> activitiesParentChildcareReason;
    private String activitiesParentChildcareReason_other;
    private List<String> activitiesParentPartnerChildcareReason;
    private String activitiesParentPartnerChildcareReason_other;

    // activities-employer-name
    @NotBlank(message = "{errors.require-company-name}")
    private String companyName;

    // activities-employer-address
    @Phone(message = "{errors.invalid-phone-number}")
    private String employerPhoneNumber;
    private String employerStreetAddress;
    private String employerCity;
    private String employerState;
    @Pattern(regexp = "^\\d{5}(?:-\\d{4})?$", message = "{errors.invalid-zipcode}")
    private String employerZipCode;

    //activities-self-employment
    private String isSelfEmployed;

    // activities-work-schedule-vary
    private String activitiesWorkVary;

    // activities-job-weekly-schedule
    @NotEmpty(message = "{activities-job-weekly-schedule.validation}")
    private List<String> activitiesJobWeeklySchedule;

    // activities-job-hourly-schedule
    private String activitiesJobHoursSameEveryDay;
    @NotBlank(message = "{errors.validate.start.time}")
    private String activitiesJobStartTimeAllDays;
    @NotBlank(message = "{errors.validate.end.time}")
    private String activitiesJobEndTimeAllDays;
    @NotBlank(message = "{errors.validate.start.time}")
    private String activitiesJobStartTimeMonday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String activitiesJobEndTimeMonday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String activitiesJobStartTimeTuesday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String activitiesJobEndTimeTuesday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String activitiesJobStartTimeWednesday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String activitiesJobEndTimeWednesday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String activitiesJobStartTimeThursday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String activitiesJobEndTimeThursday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String activitiesJobStartTimeFriday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String activitiesJobEndTimeFriday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String activitiesJobStartTimeSaturday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String activitiesJobEndTimeSaturday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String activitiesJobStartTimeSunday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String activitiesJobEndTimeSunday;

    // activities-work-commute-time
    private String activitiesJobCommuteTime;

    // activities-ed-program-type
    @NotBlank(message = "{errors.select-one-option}")
    private String educationType;

    // activities-ed-program-name
    @NotBlank(message = "{errors.provide-program-name}")
    private String applicantSchoolName;

    // activities-ed-program-info
    @Phone(message = "{errors.invalid-phone-number}")
    private String applicantSchoolPhoneNumber;
    private String applicantSchoolStreetAddress;
    private String applicantSchoolCity;
    private String applicantSchoolState;
    @Pattern(regexp = "^\\d{5}(?:-\\d{4})?$", message = "{errors.invalid-zipcode}")
    private String applicantSchoolZipCode;

    // activities-ed-program-method
    private String programTaught;
    @NotBlank(message = "{errors.select-yes-or-no}")
    private String programSchedule;

    // activities-class-weekly-schedule
    @NotEmpty(message = "{errors.select-at-least-one-day}")
    private List<String> weeklySchedule;

    // activities-class-hourly-schedule
    private String activitiesClassHoursSameEveryDay;
    @NotBlank(message = "{errors.validate.start.time}")
    private String activitiesClassStartTimeAllDays;
    @NotBlank(message = "{errors.validate.end.time}")
    private String activitiesClassEndTimeAllDays;
    @NotBlank(message = "{errors.validate.start.time}")
    private String activitiesClassStartTimeMonday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String activitiesClassEndTimeMonday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String activitiesClassStartTimeTuesday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String activitiesClassEndTimeTuesday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String activitiesClassStartTimeWednesday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String activitiesClassEndTimeWednesday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String activitiesClassStartTimeThursday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String activitiesClassEndTimeThursday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String activitiesClassStartTimeFriday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String activitiesClassEndTimeFriday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String activitiesClassStartTimeSaturday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String activitiesClassEndTimeSaturday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String activitiesClassStartTimeSunday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String activitiesClassEndTimeSunday;

    // activities-ed-program-dates
    private String activitiesProgramStartDay;
    private String activitiesProgramStartMonth;
    private String activitiesProgramStartYear;
    private String activitiesProgramEndDay;
    private String activitiesProgramEndMonth;
    private String activitiesProgramEndYear;

    // activities-partner-employer-name
    @NotBlank(message = "{errors.require-company-name}")
    private String partnerCompanyName;

    // activities-partner-employer-address
    @Phone(message = "{errors.invalid-phone-number}")
    private String partnerEmployerPhoneNumber;
    private String partnerEmployerStreetAddress;
    private String partnerEmployerCity;
    private String partnerEmployerState;
    @Pattern(regexp = "^\\d{5}(?:-\\d{4})?$", message = "{errors.invalid-zipcode}")
    private String partnerEmployerZipCode;

    // activities-partner-ed-program-type
    @NotBlank(message = "{errors.select-one-option}")
    private String partnerEducationType;

    // activities-partner-ed-program-name
    @NotBlank(message = "{errors.provide-program-name}")
    private String partnerProgramName;

    // activities-partner-ed-program-info
    @Phone(message = "{errors.invalid-phone-number}")
    private String partnerEdPhoneNumber;
    private String partnerEdStreetAddress;
    private String partnerEdCity;
    private String partnerEdState;
    @Pattern(regexp = "^\\d{5}(?:-\\d{4})?$", message = "{errors.invalid-zipcode}")
    private String partnerEdZipCode;

    // activities-partner-ed-program-method
    private String partnerProgramTaught;
    @NotBlank(message = "{errors.select-yes-or-no}")
    private String partnerProgramSchedule;

    // activities-partner-class-weekly-schedule
    @NotEmpty(message = "{errors.select-at-least-one-day}")
    private List<String> partnerClassWeeklySchedule;

    // activities-partner-class-hourly-schedule
    private String partnerClassHoursSameEveryDay;
    @NotBlank(message = "{errors.validate.start.time}")
    private String partnerClassStartTimeAllDays;
    @NotBlank(message = "{errors.validate.end.time}")
    private String partnerClassEndTimeAllDays;
    @NotBlank(message = "{errors.validate.start.time}")
    private String partnerClassStartTimeMonday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String partnerClassEndTimeMonday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String partnerClassStartTimeTuesday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String partnerClassEndTimeTuesday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String partnerClassStartTimeWednesday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String partnerClassEndTimeWednesday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String partnerClassStartTimeThursday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String partnerClassEndTimeThursday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String partnerClassStartTimeFriday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String partnerClassEndTimeFriday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String partnerClassStartTimeSaturday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String partnerClassEndTimeSaturday;
    @NotBlank(message = "{errors.validate.start.time}")
    private String partnerClassStartTimeSunday;
    @NotBlank(message = "{errors.validate.end.time}")
    private String partnerClassEndTimeSunday;

    // activities-partner-ed-program-dates
    private String partnerProgramStartDay;
    private String partnerProgramStartMonth;
    private String partnerProgramStartYear;
    private String partnerProgramEndDay;
    private String partnerProgramEndMonth;
    private String partnerProgramEndYear;

    // unearned-income-source
    private List<String> unearnedIncomeSource;

    // unearned-income-amount
    @Money(message="{errors.invalid-dollar-amount}")
    private String unearnedIncomeRental;
    @Money(message="{errors.invalid-dollar-amount}")
    private String unearnedIncomeDividends;

    @Money(message="{errors.invalid-dollar-amount}")
    private String unearnedIncomeRoyalties;
    @Money(message="{errors.invalid-dollar-amount}")
    private String unearnedIncomePension;
    @Money(message="{errors.invalid-dollar-amount}")
    private String unearnedIncomeWorkers;

    // unearned-income-assets
    private String unearnedIncomeAssetsMoreThanOneMillionDollars;

    // unearned-income-child-support
    private String doesAnyoneInHouseholdPayChildSupport;

    // unearned-income-child-support-amount
    @Money(message="{errors.invalid-dollar-amount}")
    private String amountYourHouseholdPaysInChildSupport;

    // unearned-income-programs
    private List<String> unearnedIncomePrograms;

    // submit-ccap-terms
    @NotEmpty(message = "{errors.validate.legal-terms}")
    private List<String> agreesToLegalTerms;

    // submit-sign-name
    @NotBlank(message = "{errors.validate.signed-name}")
    private String signedName;
    @NotBlank(message = "{errors.validate.signed-name}")
    private String partnerSignedName;

    // submit-confirmation
    private String surveyDifficulty;
    private String surveyAdditionalComments;
}
