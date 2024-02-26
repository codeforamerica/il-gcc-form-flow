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
    private String phoneNumber;
    private String streetAddress;
    private String city;
    private String state;
    @Pattern(regexp = "^\\d{5,}$", message = "{activities-ed-program-info.validationMessage}")
    private String zipCode;
    @NotBlank(message = "{errors.provide-first-name}")
    private String childFirstName;
    @NotBlank(message = "{errors.provide-last-name}")
    private String childLastName;

    private String childDateOfBirthDay;
    private String childDateOfBirthMonth;
    private String childDateOfBirthYear;
    private String needFinancialAssistanceForChild;

    private String childGender;
    private String childRaceEthnicity;
    private String childHasDisability;
    private String childIsUScitizen;
    private String childInIntactFamily;

    private String childInCare;

    private List<String> weeklySchedule;


    private String ccapStartDay;
    private String ccapStartMonth;
    private String ccapStartYear;

    private String ccapStartDate;

    private List<String> childcareWeeklySchedule;

    private String current_uuid;
}
