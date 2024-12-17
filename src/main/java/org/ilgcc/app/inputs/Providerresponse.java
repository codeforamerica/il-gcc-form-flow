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

    @NotBlank(message = "{provider-response.errors.first-name}")
    private String providerResponseFirstName;

    @NotBlank(message = "{provider-response.errors.last-name}")
    private String providerResponseLastName;

    @NotBlank(message = "{provider-response.errors.street}")
    private String providerResponseServiceStreetAddress1;

    private String providerResponseServiceStreetAddress2;

    @NotBlank(message = "{provider-response.errors.city}")
    private String providerResponseServiceCity;

    @NotBlank(message = "{provider-response.errors.state}")
    private String providerResponseServiceState;

    @NotBlank(message = "{provider-response.errors.zip}")
    private String providerResponseServiceZipCode;

    private String useSuggestedProviderAddress;

    @Phone(message = "{errors.invalid-phone-number}")
    private String providerResponseContactPhoneNumber;

    @NotBlank(message = "{provider-response.errors.street}")
    private String providerMailingStreetAddress1;
    
    private String providerMailingStreetAddress2;
    @NotBlank(message = "{provider-response.errors.city}")
    private String providerMailingCity;
    @NotBlank(message = "{provider-response.errors.state}")
    private String providerMailingState;
    @NotBlank(message = "{provider-response.errors.zip}")
    private String providerMailingZipCode;

    private String providerMailingAddressSameAsServiceAddress;

    private String useSuggestedMailingAddress;

    private String providerPaidCcap;

    private String providerCurrentlyLicensed;

    @NotBlank(message = "{registration-licensing-info.errors.license-number}")
    private String providerLicenseNumber;

    @NotBlank(message = "{registration-licensing-info.errors.license-state}")
    private String providerLicenseState;

    @NotBlank(message = "{registration-licensed-care-location.error}")
    private String providerLicensedCareLocation;
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
