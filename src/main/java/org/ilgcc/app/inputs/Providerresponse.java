package org.ilgcc.app.inputs;

import formflow.library.data.FlowInputs;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class Providerresponse extends FlowInputs {
    private String clientApplicationId;

    @NotBlank(message = "{errors.provide-provider-number}")
    private String providerResponseProviderNumber;

    @NotBlank(message = "{errors.provide-applicant-number}")
    private String providerResponseFamilyConfirmationCode;

    @NotBlank
    private String providerResponseAgreeToCare;
}
