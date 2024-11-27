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

    private String providerResponseFamilyShortCode;

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

    private String providerPaidCcap;

    private String providerCurrentlyLicensed;

    @Email(regexp = RegexUtils.EMAIL_REGEX, message = "{errors.invalid-email}")
    private String providerResponseContactEmail;
    
    // registration-applicant
    @NotBlank
    private String providerLicenseExemptType;
    
    // registration-unlicensed-care-location
    @NotBlank
    private String providerLicenseExemptCareLocation;
    
    // registration-unlicensed-relationship
    
    @NotBlank
    private String providerLicenseExemptRelationship;
}
