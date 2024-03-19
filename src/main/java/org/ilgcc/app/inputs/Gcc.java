package org.ilgcc.app.inputs;

import formflow.library.data.FlowInputs;
import formflow.library.utils.RegexUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public class Gcc extends FlowInputs {

    private String lang;
    @NotBlank
    private String schoolName;
    @NotBlank(message = "{activities-add-ed-program-type.validationMessage}")
    private String educationType;
    @NotBlank
    private String dayCareChoice;
    @NotBlank(message = "{activities-ed-program-method.validationMessage}")
    private String programTaught;
    @NotBlank(message = "{activities-ed-program-method.validationMessage}")
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
    private String phoneNumber;
    private String streetAddress;
    private String city;
    private String state;
    @Pattern(regexp = "^\\d{5,}$", message = "{activities-ed-program-info.validationMessage}")
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

    @Pattern(regexp = "\\d{3}-\\d{2}-\\d{4}", message = "{errors.invalid-ssn}")
    private String parentSsn;
    private List<String> parentGender;
    private List<String> parentRaceEthnicity;

    private String parentConfirmSuggestedAddress;

    private String hasAdultDependents;

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

    private String childAttendsOtherEd;

    private String current_uuid;
}
