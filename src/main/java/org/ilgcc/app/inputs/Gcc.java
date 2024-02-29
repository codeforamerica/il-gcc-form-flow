package org.ilgcc.app.inputs;

import formflow.library.data.FlowInputs;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

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

    @NotBlank(message = "{errors.provide-first-name}")
    private String childFirstName;
    @NotBlank(message = "{errors.provide-last-name}")
    private String childLastName;

    private String childDateOfBirthDay;
    private String childDateOfBirthMonth;
    private String childDateOfBirthYear;
    @NotBlank(message = "{errors.required-financial-assistance}")
    private String needFinancialAssistanceForChild;

    private String childGender;
    private String childRaceEthnicity;
    private String childHasDisability;
    private String childIsUsCitizen;
    private String childInIntactFamily;

    private String childInCare;

    private List<String> weeklySchedule;


    private String ccapStartDay;
    private String ccapStartMonth;
    private String ccapStartYear;

    private String ccapStartDate;

    private List<String> childcareWeeklySchedule;

    private String childAttendsOtherEd;

    private String current_uuid;
}
