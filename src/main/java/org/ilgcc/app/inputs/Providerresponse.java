package org.ilgcc.app.inputs;

import formflow.library.data.FlowInputs;
import jakarta.validation.constraints.NotBlank;

public class Providerresponse extends FlowInputs {
    @NotBlank(message = "{errors.provide-provider-number}")
    private String providerNumber;
}
