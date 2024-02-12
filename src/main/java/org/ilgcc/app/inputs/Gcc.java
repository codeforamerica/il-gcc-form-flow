package org.ilgcc.app.inputs;

import formflow.library.data.FlowInputs;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;

public class Gcc extends FlowInputs {

    private String lang;

    @NotBlank
    private String schoolName;

    @NotBlank(message = "{activities-add-ed-program-type.validationMessage}")
    private String educationType;

    @NotBlank
    private String dayCareChoice;

    private String languageRead;
    private String languageSpeak;
    private ArrayList<String> weeklySchedule;
}
