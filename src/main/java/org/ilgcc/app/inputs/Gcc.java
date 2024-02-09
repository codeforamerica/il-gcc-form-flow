package org.ilgcc.app.inputs;

import formflow.library.data.FlowInputs;
import jakarta.validation.constraints.NotBlank;

public class Gcc extends FlowInputs {
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

}
