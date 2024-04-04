package org.ilgcc.app.inputs;

import formflow.library.data.FlowInputs;
import formflow.library.data.annotations.Phone;
import formflow.library.utils.RegexUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public class Gcc extends FlowInputs {
  private String current_uuid;
    private String lang;
    private String languageRead;
    private String languageSpeak;
    @NotBlank
    private String dayCareChoice;
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
    @Length(min = 9, message = "{errors.invalid-phone-number}")
    private String parentContactPhoneNumber;
    @Email(regexp = RegexUtils.EMAIL_REGEX, message = "{errors.invalid-email}")
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
    private List<String> parentContactPreferCommunicate;
    private String parentHasPartner;
    @NotBlank(message = "{errors.require-yes-no}")
    private String parentSpouseIsStepParent;
    @NotBlank(message = "{errors.require-yes-no}")
    private String parentSpouseShareChildren;
    @NotBlank(message = "{errors.require-yes-no}")
    private String parentSpouseLiveTogether;
    @Length(min = 9, message = "{errors.invalid-phone-number}")
    private String parentPartnerPhoneNumber;
    @Email(regexp = RegexUtils.EMAIL_REGEX, message = "{errors.invalid-email}")
    private String parentPartnerEmail;
    private String parentPartnerIsServing;
    private String parentPartnerInMilitaryReserveOrNationalGuard;
    private String parentPartnerHasDisability;
    @NotBlank(message = "{general.indicates-required}")
    private String parentPartnerFirstName;
    @NotBlank(message = "{general.indicates-required}")
    private String parentPartnerLastName;

    private String parentPartnerSSN;
    private String parentPartnerBirthDay;
    private String parentPartnerBirthMonth;
    private String parentPartnerBirthYear;
    private List<String> parentPartnerGender;
    @NotBlank(message = "{errors.provide-program-name}")
    private String schoolName;
  @NotBlank(message = "{errors.select-one-option}")
    private String educationType;
    private String programTaught;
  @NotBlank(message = "{errors.select-yes-or-no}")
    private String programSchedule;
    private String phoneNumber;
    private String streetAddress;
    private String city;
    private String state;
    @Pattern(regexp = "^\\d{5,}$", message = "{errors.invalid-zipcode}")
    private String zipCode;
    private String activitiesClassHoursSameEveryDay;
    private String activitiesClassStartTimeAllDays;
    private String activitiesClassEndTimeAllDays;
    private String activitiesClassStartTimeMonday;
    private String activitiesClassEndTimeMonday;

    private String activitiesClassStartTimeTuesday;
    private String activitiesClassEndTimeTuesday;
    private String activitiesClassStartTimeWednesday;
    private String activitiesClassEndTimeWednesday;

    private String activitiesClassStartTimeThursday;
    private String activitiesClassEndTimeThursday;
    private String activitiesClassStartTimeFriday;
    private String activitiesClassEndTimeFriday;
    private String activitiesClassStartTimeSaturday;
    private String activitiesClassEndTimeSaturday;
    private String activitiesClassStartTimeSunday;
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

    private List<String> childGender;
    private List<String> childRaceEthnicity;
    private String childHasDisability;
    private String childIsUsCitizen;
    private String childInCare;
  @NotEmpty(message = "{errors.select-at-least-one-day}")
    private List<String> weeklySchedule;
    @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])$", message = "{general.day.validation}")
    private String activitiesProgramStartDay;
    @Pattern(regexp = "^(0?[1-9]|1[0-2])$", message = "{general.month.validation}")
    private String activitiesProgramStartMonth;
    @Pattern(regexp = "^(19|20)\\d{2}$", message = "{general.year.validation}")
    private String activitiesProgramStartYear;
    @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])$", message = "{general.day.validation}")
    private String activitiesProgramEndDay;
    @Pattern(regexp = "^(0?[1-9]|1[0-2])$", message = "{general.month.validation}")
    private String activitiesProgramEndMonth;
    @Pattern(regexp = "^(19|20)\\d{2}$", message = "{general.year.validation}")
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

  @NotBlank(message = "{errors.select-one-option}")
  private String partnerEducationType;
  @NotBlank(message = "{errors.provide-program-name}")
  private String partnerProgramName;
  private String partnerEdPhoneNumber;
  private String partnerEdStreetAddress;
  private String partnerEdCity;
  private String partnerEdState;
  @Pattern(regexp = "^\\d{5,}$", message = "{errors.invalid-zipcode}")
  private String partnerEdZipCode;
  private String partnerProgramTaught;
  @NotBlank(message = "{errors.select-yes-or-no}")
  private String partnerProgramSchedule;

  @NotEmpty(message = "{errors.select-at-least-one-day}")
  private List<String> partnerClassWeeklySchedule;
  private String partnerClassHoursSameEveryDay;
  @NotBlank(message = "{errors.provide-start-time}")
  private String partnerClassStartTimeAllDays;
  @NotBlank(message = "{errors.provide-end-time}")
  private String partnerClassEndTimeAllDays;
  @NotBlank(message = "{errors.provide-start-time}")
  private String partnerClassStartTimeMonday;
  @NotBlank(message = "{errors.provide-end-time}")
  private String partnerClassEndTimeMonday;
  @NotBlank(message = "{errors.provide-start-time}")
  private String partnerClassStartTimeTuesday;
  @NotBlank(message = "{errors.provide-end-time}")
  private String partnerClassEndTimeTuesday;
  @NotBlank(message = "{errors.provide-start-time}")
  private String partnerClassStartTimeWednesday;
  @NotBlank(message = "{errors.provide-end-time}")
  private String partnerClassEndTimeWednesday;
  @NotBlank(message = "{errors.provide-start-time}")
  private String partnerClassStartTimeThursday;
  @NotBlank(message = "{errors.provide-end-time}")
  private String partnerClassEndTimeThursday;
  @NotBlank(message = "{errors.provide-start-time}")
  private String partnerClassStartTimeFriday;
  @NotBlank(message = "{errors.provide-end-time}")
  private String partnerClassEndTimeFriday;
  @NotBlank(message = "{errors.provide-start-time}")
  private String partnerClassStartTimeSaturday;
  @NotBlank(message = "{errors.provide-end-time}")
  private String partnerClassEndTimeSaturday;
  @NotBlank(message = "{errors.provide-start-time}")
  private String partnerClassStartTimeSunday;
  @NotBlank(message = "{errors.provide-end-time}")
  private String partnerClassEndTimeSunday;
  @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])$", message = "{general.day.validation}")
  private String partnerProgramStartDay;
  @Pattern(regexp = "^(0?[1-9]|1[0-2])$", message = "{general.month.validation}")
  private String partnerProgramStartMonth;
  @Pattern(regexp = "^(19|20)\\d{2}$", message = "{general.year.validation}")
  private String partnerProgramStartYear;
  @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])$", message = "{general.day.validation}")
  private String partnerProgramEndDay;
  @Pattern(regexp = "^(0?[1-9]|1[0-2])$", message = "{general.month.validation}")
  private String partnerProgramEndMonth;
  @Pattern(regexp = "^(19|20)\\d{2}$", message = "{general.year.validation}")
  private String partnerProgramEndYear;
    private List<String> unearnedIncomeSource;
    private String unearnedIncomeRental;
    private String unearnedIncomeDividends;
    private String unearnedIncomeUnemployment;
    private String unearnedIncomeRoyalties;
    private String unearnedIncomePension;
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
    @Pattern(regexp = "^\\d{5,}$", message = "{errors.invalid-zipcode}")
    private String employerZipCode;

    private String isSelfEmployed;
}
