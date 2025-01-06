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

    @NotBlank(message = "{errors.provide-first-name}")
    private String providerResponseFirstName;

    @NotBlank(message = "{errors.provide-last-name}")
    private String providerResponseLastName;

    @NotBlank(message = "{errors.provide-street}")
    private String providerResponseServiceStreetAddress1;

    private String providerResponseServiceStreetAddress2;

    @NotBlank(message = "{errors.provide-city}")
    private String providerResponseServiceCity;

    @NotBlank(message = "{errors.provide-state}")
    private String providerResponseServiceState;

    @NotBlank(message = "{errors.provide-zip}")
    private String providerResponseServiceZipCode;

    private String useSuggestedProviderResponseServiceAddress;

    @Phone(message = "{errors.invalid-phone-number}")
    private String providerResponseContactPhoneNumber;

    @NotBlank(message = "{errors.provide-street}")
    private String providerMailingStreetAddress1;
    
    private String providerMailingStreetAddress2;
    @NotBlank(message = "{errors.provide-city}")
    private String providerMailingCity;
    @NotBlank(message = "{errors.provide-state}")
    private String providerMailingState;
    @NotBlank(message = "{errors.provide-zip}")
    private String providerMailingZipCode;

    private String providerMailingAddressSameAsServiceAddress;

    private String useSuggestedProviderMailingAddress;

    private String providerPaidCcap;

    private String providerCurrentlyLicensed;

    @NotBlank(message = "{registration-licensing-info.errors.license-number}")
    private String providerLicenseNumber;

    @NotBlank(message = "{errors.provide-state}")
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

    private String providerIdentityCheckSSN;
    
    private String providerType;

    private String providerConviction;

    @NotBlank(message = "{registration-convictions-info.error}")
    private String providerConvictionExplanation;

    private String providerHouseholdLiveWithOthers;

    private String providerIdentityCheckDateOfBirthDay;
    private String providerIdentityCheckDateOfBirthMonth;
    private String providerIdentityCheckDateOfBirthYear;
    private String providerIdentityCheckDateOfBirthDate;

    private String providerTaxIdType;

    private String providerTaxIdSSN;
}
