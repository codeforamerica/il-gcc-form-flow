package org.ilgcc.app.inputs;

import formflow.library.data.FlowInputs;
import jakarta.validation.constraints.NotBlank;

public class Gcc extends FlowInputs {
    @NotBlank
    private String schoolName;
}
