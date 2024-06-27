package org.ilgcc.app.inputs;

import formflow.library.data.FlowInputs;
import formflow.library.data.annotations.Money;
import formflow.library.data.annotations.Phone;
import formflow.library.utils.RegexUtils;
import jakarta.validation.constraints.*;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class Gcc extends FlowInputs {
    MultipartFile uploadDocuments;
    private String lang;
    @NotBlank(message = "{errors.provide-program-name}")
    private String schoolName;
    @NotBlank(message = "{errors.select-one-option}")
    private String educationType;
    @NotBlank(message = "{errors.choose-provider}")
    private String dayCareChoice;
    private String programTaught;
    @NotBlank(message = "{errors.select-yes-or-no}")
    private String programSchedule;
    private String languageRead;
    private String languageSpeak;
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
    private String parentIsServing;
    private String parentInMilitaryReserveOrNationalGuard;
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

    @Phone(message = "{errors.invalid-phone-number}")
    private String parentContactPhoneNumber;

    private String parentContactEmail;
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
    @NotBlank(message = "{errors.invalid-communication-preference}")
    private String parentContactPreferredCommunicationMethod;
    private String parentHasPartner;
    @NotBlank(message = "{errors.require-yes-no}")
    private String parentSpouseIsStepParent;
    @NotBlank(message = "{errors.require-yes-no}")
    private String parentSpouseShareChildren;
    @NotBlank(message = "{errors.require-yes-no}")
    private String parentSpouseLiveTogether;
    @Phone(message="{errors.invalid-phone-number}")
    private String parentPartnerPhoneNumber;
    @Email(regexp = RegexUtils.EMAIL_REGEX, message = "{errors.invalid-email}")
    private String parentPartnerEmail;
    private String parentPartnerIsServing;
    private String parentPartnerInMilitaryReserveOrNationalGuard;
    private String parentPartnerHasDisability;
    @NotBlank(message = "{errors.provide-first-name}")
    private String parentPartnerFirstName;
    @NotBlank(message = "{errors.provide-last-name}")
    private String parentPartnerLastName;

    private String parentPartnerSSN;
    private String parentPartnerBirthDay;
    private String parentPartnerBirthMonth;
    private String parentPartnerBirthYear;
    private List<String> parentPartnerGender;
    @Phone(message = "{errors.invalid-phone-number}")
    private String phoneNumber;
    private String streetAddress;
    private String city;
    private String state;
    @Pattern(regexp = "^\\d{5}(?:-\\d{4})?$", message = "{errors.invalid-zipcode}")
    private String zipCode;
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

    @Pattern(regexp = "\\d{3}-\\d{2}-\\d{4}", message = "{errors.invalid-ssn}")
    private String parentSsn;
    private List<String> parentGender;
    private List<String> parentRaceEthnicity;

    private String parentConfirmSuggestedAddress;

    private String hasAdultDependents;

    @NotBlank(message = "{errors.provide-first-name}")
    private String adultDependentFirstName;

    @NotBlank(message = "{errors.provide-last-name}")
    private String adultDependentLastName;

    private String adultDependentBirthdateDay;
    private String adultDependentBirthdateMonth;
    private String adultDependentBirthdateYear;

    @NotBlank(message = "{errors.provide-first-name}")
    private String childFirstName;
    @NotBlank(message = "{errors.provide-last-name}")
    private String childLastName;

    private String childDateOfBirthDay;
    private String childDateOfBirthMonth;
    private String childDateOfBirthYear;
    @NotBlank(message = "{errors.required-financial-assistance}")
    private String needFinancialAssistanceForChild;

    @NotEmpty(message = "{errors.select-child-relationship}")
    private String childRelationship;
    private List<String> childGender;
    private List<String> childRaceEthnicity;
    private String childHasDisability;
    private String childIsUsCitizen;
    private String childInCare;
    @NotEmpty(message = "{errors.select-at-least-one-day}")
    private List<String> weeklySchedule;
    private String activitiesProgramStartDay;
    private String activitiesProgramStartMonth;
    private String activitiesProgramStartYear;
    private String activitiesProgramEndDay;
    private String activitiesProgramEndMonth;
    private String activitiesProgramEndYear;
    private String ccapStartDay;
    private String ccapStartMonth;
    private String ccapStartYear;

    private String ccapStartDate;

    private List<String> childcareWeeklySchedule;
    private Boolean childcareHoursSameEveryDay;
    private String childcareStartTimeAllDays;
    private String childcareEndTimeAllDays;
    private String childcareStartTimeMonday;
    private String childcareEndTimeMonday;

    private String childcareStartTimeTuesday;
    private String childcareEndTimeTuesday;
    private String childcareStartTimeWednesday;
    private String childcareEndTimeWednesday;

    private String childcareStartTimeThursday;
    private String childcareEndTimeThursday;

    private String childcareStartTimeFriday;

    private String childcareEndTimeFriday;
    private String childcareStartTimeSaturday;

    private String childcareEndTimeSaturday;
    private String childcareStartTimeSunday;

    private String childcareEndTimeSunday;
    private String childAttendsOtherEd;

    private List<String> unearnedIncomePrograms;
    private String unearnedIncomeAssetsMoreThanOneMillionDollars;
    private String current_uuid;

    @NotBlank(message = "{errors.select-one-option}")
    private String partnerEducationType;
    @NotBlank(message = "{errors.provide-program-name}")
    private String partnerProgramName;
    @Phone(message = "{errors.invalid-phone-number}")
    private String partnerEdPhoneNumber;
    private String partnerEdStreetAddress;
    private String partnerEdCity;
    private String partnerEdState;
    @Pattern(regexp = "^\\d{5}(?:-\\d{4})?$", message = "{errors.invalid-zipcode}")
    private String partnerEdZipCode;
    private String partnerProgramTaught;
    @NotBlank(message = "{errors.select-yes-or-no}")
    private String partnerProgramSchedule;

    @NotEmpty(message = "{errors.select-at-least-one-day}")
    private List<String> partnerClassWeeklySchedule;
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
    private String partnerProgramStartDay;
    private String partnerProgramStartMonth;
    private String partnerProgramStartYear;
    private String partnerProgramEndDay;
    private String partnerProgramEndMonth;
    private String partnerProgramEndYear;
    private List<String> unearnedIncomeSource;
    @Money
    private String unearnedIncomeRental;
    @Money
    private String unearnedIncomeDividends;
    @Money
    private String unearnedIncomeUnemployment;
    @Money
    private String unearnedIncomeRoyalties;
    @Money
    private String unearnedIncomePension;
    @Money
    private String unearnedIncomeWorkers;

    private List<String> activitiesParentChildcareReason;
    private String activitiesParentChildcareReason_other;
    private List<String> activitiesParentPartnerChildcareReason;
    private String activitiesParentPartnerChildcareReason_other;
    @NotBlank(message = "{errors.require-company-name}")
    private String companyName;
    @Phone(message = "{errors.invalid-phone-number}")
    private String employerPhoneNumber;
    private String employerStreetAddress;
    private String employerCity;
    private String employerState;
    @Pattern(regexp = "^\\d{5}(?:-\\d{4})?$", message = "{errors.invalid-zipcode}")
    private String employerZipCode;

    private String partnerCompanyName;
    @Phone(message = "{errors.invalid-phone-number}")
    private String partnerEmployerPhoneNumber;
    private String partnerEmployerStreetAddress;
    private String partnerEmployerCity;
    private String partnerEmployerState;
    @Pattern(regexp = "^\\d{5}(?:-\\d{4})?$", message = "{errors.invalid-zipcode}")
    private String partnerEmployerZipCode;
    private String isSelfEmployed;

    private String activitiesWorkVary;
    @Size(min = 1, message = "{activities-job-weekly-schedule.validation}")
    private List<String> activitiesJobWeeklySchedule;
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

    private String activitiesJobCommuteTime;

    @NotEmpty(message = "{errors.validate.legal-terms}")
    private List<String> agreesToLegalTerms;
    @NotBlank(message = "{errors.validate.signed-name}")
    private String signedName;
    @NotBlank(message = "{errors.validate.signed-name}")
    private String partnerSignedName;
    private String surveyDifficulty;
    private String surveyAdditionalComments;
}
