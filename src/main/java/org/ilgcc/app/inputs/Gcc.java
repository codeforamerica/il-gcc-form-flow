package org.ilgcc.app.inputs;

import formflow.library.data.FlowInputs;
import formflow.library.utils.RegexUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.NumberFormat;

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
    @Length(min = 9, message = "{errors.invalid-phone-number}")
    private String parentContactPhoneNumber;
    @Email(message = "{errors.invalid-email}", regexp = RegexUtils.EMAIL_REGEX)
    private String parentContactEmail;

    private List<String> parentContactPreferCommunicate;

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

    @Pattern(regexp = "^(1[0-2]|[1-9])$", message = "{general.month.validation}")
    private String activitiesProgramStartMonth;
    @Pattern(regexp = "^(19|20)\\d{2}$", message = "{general.year.validation}")
    private String activitiesProgramStartYear;
    @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])$", message = "{general.day.validation}")
    private String activitiesProgramEndDay;
    @Pattern(regexp = "^(1[0-2]|[1-9])$", message = "{general.month.validation}")
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
