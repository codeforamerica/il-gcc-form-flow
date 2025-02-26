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
    
    // onboarding-chosen-provider
    private String hasChosenProvider;

    private String languageRead;
    private String languageSpeak;

    @NotBlank(message = "{errors.provide-county}")
    private String applicationCounty;

    private String applicationZipCode;
    
    private String ccrrName;
    
    // onboarding-provider-info
    @NotBlank(message = "{errors.provide-provider-name}")
    private String familyIntendedProviderName;
    @Email(regexp = RegexUtils.EMAIL_REGEX, message = "{errors.invalid-email}")
    private String familyIntendedProviderEmail;
    @Phone(message = "{errors.invalid-phone-number}")
    private String familyIntendedProviderPhoneNumber;

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

    private String organizationId;

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
    @Email(regexp = RegexUtils.EMAIL_REGEX, message = "{errors.invalid-email}")
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
    
    // parent-info-disability
    private String parentHasDisability;

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

    private String childcareStartTimeAllDaysHour;
    private String childcareStartTimeAllDaysMinute;
    private String childcareStartTimeAllDaysAmPm;

    private String childcareEndTimeAllDaysHour;
    private String childcareEndTimeAllDaysMinute;
    private String childcareEndTimeAllDaysAmPm;

    private String childcareStartTimeMondayHour;
    private String childcareStartTimeMondayMinute;
    private String childcareStartTimeMondayAmPm;

    private String childcareEndTimeMondayHour;
    private String childcareEndTimeMondayMinute;
    private String childcareEndTimeMondayAmPm;

    private String childcareStartTimeTuesdayHour;
    private String childcareStartTimeTuesdayMinute;
    private String childcareStartTimeTuesdayAmPm;

    private String childcareEndTimeTuesdayHour;
    private String childcareEndTimeTuesdayMinute;
    private String childcareEndTimeTuesdayAmPm;

    private String childcareStartTimeWednesdayHour;
    private String childcareStartTimeWednesdayMinute;
    private String childcareStartTimeWednesdayAmPm;

    private String childcareEndTimeWednesdayHour;
    private String childcareEndTimeWednesdayMinute;
    private String childcareEndTimeWednesdayAmPm;

    private String childcareStartTimeThursdayHour;
    private String childcareStartTimeThursdayMinute;
    private String childcareStartTimeThursdayAmPm;

    private String childcareEndTimeThursdayHour;
    private String childcareEndTimeThursdayMinute;
    private String childcareEndTimeThursdayAmPm;

    private String childcareStartTimeFridayHour;
    private String childcareStartTimeFridayMinute;
    private String childcareStartTimeFridayAmPm;

    private String childcareEndTimeFridayHour;
    private String childcareEndTimeFridayMinute;
    private String childcareEndTimeFridayAmPm;

    private String childcareStartTimeSaturdayHour;
    private String childcareStartTimeSaturdayMinute;
    private String childcareStartTimeSaturdayAmPm;

    private String childcareEndTimeSaturdayHour;
    private String childcareEndTimeSaturdayMinute;
    private String childcareEndTimeSaturdayAmPm;

    private String childcareStartTimeSundayHour;
    private String childcareStartTimeSundayMinute;
    private String childcareStartTimeSundayAmPm;

    private String childcareEndTimeSundayHour;
    private String childcareEndTimeSundayMinute;
    private String childcareEndTimeSundayAmPm;
    

    // children-ccap-child-other-ed
    private String childAttendsOtherEd;

    // children-school-weekly-schedule
    private String childOtherEdHoursDescription;


    private String earliestChildcareStartDate;

    // activities-parent-type
    @NotEmpty(message = "{activities-type.error.required}")
    private List<String> activitiesParentChildcareReason;
    private String activitiesParentChildcareReason_other;
    @NotEmpty(message = "{activities-type.error.required}")
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

    //activities-employer-start-date
    private String activitiesJobStartDay;
    private String activitiesJobStartMonth;
    private String activitiesJobStartYear;
    //activities-self-employment
    private String isSelfEmployed;

    // activities-work-schedule-vary
    private String activitiesWorkVary;


    // activities-job-weekly-schedule
    @NotEmpty(message = "{activities-job-weekly-schedule.validation}")
    private List<String> activitiesJobWeeklySchedule;

    // activities-job-hourly-schedule
    private String activitiesJobHoursSameEveryDay;

    private String activitiesJobStartTimeAllDaysHour;
    private String activitiesJobStartTimeAllDaysMinute;
    private String activitiesJobStartTimeAllDaysAmPm;

    private String activitiesJobEndTimeAllDaysHour;
    private String activitiesJobEndTimeAllDaysMinute;
    private String activitiesJobEndTimeAllDaysAmPm;

    private String activitiesJobStartTimeMondayHour;
    private String activitiesJobStartTimeMondayMinute;
    private String activitiesJobStartTimeMondayAmPm;

    private String activitiesJobEndTimeMondayHour;
    private String activitiesJobEndTimeMondayMinute;
    private String activitiesJobEndTimeMondayAmPm;

    private String activitiesJobStartTimeTuesdayHour;
    private String activitiesJobStartTimeTuesdayMinute;
    private String activitiesJobStartTimeTuesdayAmPm;

    private String activitiesJobEndTimeTuesdayHour;
    private String activitiesJobEndTimeTuesdayMinute;
    private String activitiesJobEndTimeTuesdayAmPm;

    private String activitiesJobStartTimeWednesdayHour;
    private String activitiesJobStartTimeWednesdayMinute;
    private String activitiesJobStartTimeWednesdayAmPm;

    private String activitiesJobEndTimeWednesdayHour;
    private String activitiesJobEndTimeWednesdayMinute;
    private String activitiesJobEndTimeWednesdayAmPm;

    private String activitiesJobStartTimeThursdayHour;
    private String activitiesJobStartTimeThursdayMinute;
    private String activitiesJobStartTimeThursdayAmPm;

    private String activitiesJobEndTimeThursdayHour;
    private String activitiesJobEndTimeThursdayMinute;
    private String activitiesJobEndTimeThursdayAmPm;

    private String activitiesJobStartTimeFridayHour;
    private String activitiesJobStartTimeFridayMinute;
    private String activitiesJobStartTimeFridayAmPm;

    private String activitiesJobEndTimeFridayHour;
    private String activitiesJobEndTimeFridayMinute;
    private String activitiesJobEndTimeFridayAmPm;

    private String activitiesJobStartTimeSaturdayHour;
    private String activitiesJobStartTimeSaturdayMinute;
    private String activitiesJobStartTimeSaturdayAmPm;

    private String activitiesJobEndTimeSaturdayHour;
    private String activitiesJobEndTimeSaturdayMinute;
    private String activitiesJobEndTimeSaturdayAmPm;

    private String activitiesJobStartTimeSundayHour;
    private String activitiesJobStartTimeSundayMinute;
    private String activitiesJobStartTimeSundayAmPm;

    private String activitiesJobEndTimeSundayHour;
    private String activitiesJobEndTimeSundayMinute;
    private String activitiesJobEndTimeSundayAmPm;

    // activities-work-commute-time
    private String activitiesJobCommuteTime;

    // parent-info-bachelors
    private String applicantHasBachelorsDegree;

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

    private String activitiesClassStartTimeAllDaysHour;
    private String activitiesClassStartTimeAllDaysMinute;
    private String activitiesClassStartTimeAllDaysAmPm;

    private String activitiesClassEndTimeAllDaysHour;
    private String activitiesClassEndTimeAllDaysMinute;
    private String activitiesClassEndTimeAllDaysAmPm;

    private String activitiesClassStartTimeMondayHour;
    private String activitiesClassStartTimeMondayMinute;
    private String activitiesClassStartTimeMondayAmPm;

    private String activitiesClassEndTimeMondayHour;
    private String activitiesClassEndTimeMondayMinute;
    private String activitiesClassEndTimeMondayAmPm;

    private String activitiesClassStartTimeTuesdayHour;
    private String activitiesClassStartTimeTuesdayMinute;
    private String activitiesClassStartTimeTuesdayAmPm;

    private String activitiesClassEndTimeTuesdayHour;
    private String activitiesClassEndTimeTuesdayMinute;
    private String activitiesClassEndTimeTuesdayAmPm;

    private String activitiesClassStartTimeWednesdayHour;
    private String activitiesClassStartTimeWednesdayMinute;
    private String activitiesClassStartTimeWednesdayAmPm;

    private String activitiesClassEndTimeWednesdayHour;
    private String activitiesClassEndTimeWednesdayMinute;
    private String activitiesClassEndTimeWednesdayAmPm;

    private String activitiesClassStartTimeThursdayHour;
    private String activitiesClassStartTimeThursdayMinute;
    private String activitiesClassStartTimeThursdayAmPm;

    private String activitiesClassEndTimeThursdayHour;
    private String activitiesClassEndTimeThursdayMinute;
    private String activitiesClassEndTimeThursdayAmPm;

    private String activitiesClassStartTimeFridayHour;
    private String activitiesClassStartTimeFridayMinute;
    private String activitiesClassStartTimeFridayAmPm;

    private String activitiesClassEndTimeFridayHour;
    private String activitiesClassEndTimeFridayMinute;
    private String activitiesClassEndTimeFridayAmPm;

    private String activitiesClassStartTimeSaturdayHour;
    private String activitiesClassStartTimeSaturdayMinute;
    private String activitiesClassStartTimeSaturdayAmPm;

    private String activitiesClassEndTimeSaturdayHour;
    private String activitiesClassEndTimeSaturdayMinute;
    private String activitiesClassEndTimeSaturdayAmPm;

    private String activitiesClassStartTimeSundayHour;
    private String activitiesClassStartTimeSundayMinute;
    private String activitiesClassStartTimeSundayAmPm;

    private String activitiesClassEndTimeSundayHour;
    private String activitiesClassEndTimeSundayMinute;
    private String activitiesClassEndTimeSundayAmPm;

    // activities-class-commute-time
    private String activitiesEdCommuteTime;

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

    //activities-partner-employer-start-date
    private String activitiesPartnerJobStartDay;
    private String activitiesPartnerJobStartMonth;
    private String activitiesPartnerJobStartYear;

    // parent-partner-info-bachelors
    private String partnerHasBachelorsDegree;

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

    private String partnerClassStartTimeAllDaysHour;
    private String partnerClassStartTimeAllDaysMinute;
    private String partnerClassStartTimeAllDaysAmPm;

    private String partnerClassEndTimeAllDaysHour;
    private String partnerClassEndTimeAllDaysMinute;
    private String partnerClassEndTimeAllDaysAmPm;

    private String partnerClassStartTimeMondayHour;
    private String partnerClassStartTimeMondayMinute;
    private String partnerClassStartTimeMondayAmPm;

    private String partnerClassEndTimeMondayHour;
    private String partnerClassEndTimeMondayMinute;
    private String partnerClassEndTimeMondayAmPm;

    private String partnerClassStartTimeTuesdayHour;
    private String partnerClassStartTimeTuesdayMinute;
    private String partnerClassStartTimeTuesdayAmPm;

    private String partnerClassEndTimeTuesdayHour;
    private String partnerClassEndTimeTuesdayMinute;
    private String partnerClassEndTimeTuesdayAmPm;

    private String partnerClassStartTimeWednesdayHour;
    private String partnerClassStartTimeWednesdayMinute;
    private String partnerClassStartTimeWednesdayAmPm;

    private String partnerClassEndTimeWednesdayHour;
    private String partnerClassEndTimeWednesdayMinute;
    private String partnerClassEndTimeWednesdayAmPm;

    private String partnerClassStartTimeThursdayHour;
    private String partnerClassStartTimeThursdayMinute;
    private String partnerClassStartTimeThursdayAmPm;

    private String partnerClassEndTimeThursdayHour;
    private String partnerClassEndTimeThursdayMinute;
    private String partnerClassEndTimeThursdayAmPm;

    private String partnerClassStartTimeFridayHour;
    private String partnerClassStartTimeFridayMinute;
    private String partnerClassStartTimeFridayAmPm;

    private String partnerClassEndTimeFridayHour;
    private String partnerClassEndTimeFridayMinute;
    private String partnerClassEndTimeFridayAmPm;

    private String partnerClassStartTimeSaturdayHour;
    private String partnerClassStartTimeSaturdayMinute;
    private String partnerClassStartTimeSaturdayAmPm;

    private String partnerClassEndTimeSaturdayHour;
    private String partnerClassEndTimeSaturdayMinute;
    private String partnerClassEndTimeSaturdayAmPm;

    private String partnerClassStartTimeSundayHour;
    private String partnerClassStartTimeSundayMinute;
    private String partnerClassStartTimeSundayAmPm;

    private String partnerClassEndTimeSundayHour;
    private String partnerClassEndTimeSundayMinute;
    private String partnerClassEndTimeSundayAmPm;


    // activities-partner-ed-commute-time
    private String partnerProgramCommuteTime;

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

    // unearned-income-referral-services
    private List<String> unearnedIncomeReferralServices;

    // submit-ccap-terms
    @NotEmpty(message = "{errors.validate.legal-terms}")
    private List<String> agreesToLegalTerms;

    // submit-sign-name
    @NotBlank(message = "{errors.validate.signed-name}")
    private String signedName;
    @NotBlank(message = "{errors.validate.signed-name}")
    private String partnerSignedName;

    // complete-submit-confirmation
    private String surveyDifficulty;
    private String surveyAdditionalComments;
    private String providerResponseSubmissionId;
}
