package org.ilgcc.app.inputs;

import formflow.library.data.FlowInputs;
import formflow.library.data.annotations.Phone;
import formflow.library.utils.RegexUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class Providerresponse extends FlowInputs {
    private String familySubmissionId;

    @NotBlank(message = "{errors.provide-provider-number}")
    private String providerResponseProviderNumber;

    @NotBlank(message = "{errors.provide-applicant-number}")
    private String providerResponseFamilyConfirmationCode;

    @NotBlank
    private String providerResponseAgreeToCare;
    
    private String providerResponseBusinessName;
    
    @NotBlank
    private String providerResponseFirstName;
    
    @NotBlank
    private String providerResponseLastName;
    
    @NotBlank
    private String providerResponseServiceStreetAddress1;
    
    private String providerResponseServiceStreetAddress2;
    
    @NotBlank
    private String providerResponseServiceCity;

    @NotBlank
    private String providerResponseServiceState;
    
    @NotBlank
    private String providerResponseServiceZipCode;
    
    private String useSuggestedProviderAddress;
    
    @Phone(message = "{errors.invalid-phone-number}")
    private String providerResponseContactPhoneNumber;

    @Email(regexp = RegexUtils.EMAIL_REGEX, message = "{errors.invalid-email}")
    private String providerResponseContactEmail;
}
