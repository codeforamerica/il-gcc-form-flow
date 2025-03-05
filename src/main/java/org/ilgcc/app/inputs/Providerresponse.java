package org.ilgcc.app.inputs;

import formflow.library.data.FlowInputs;
import formflow.library.data.annotations.Phone;
import formflow.library.data.annotations.SSN;
import formflow.library.inputs.Encrypted;
import formflow.library.utils.RegexUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class Providerresponse extends FlowInputs {

    private String familySubmissionId;

    MultipartFile providerUploadDocuments;

    @NotNull(message = "{errors.provide-provider-number}")
    private BigInteger providerResponseProviderNumber;

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

    // registration-start-date
    private String providerCareStartDay;
    private String providerCareStartMonth;
    private String providerCareStartYear;
    private String providerCareStartDate;

    private String providerConviction;

    @NotBlank(message = "{registration-convictions-info.error}")
    private String providerConvictionExplanation;

    private String providerHouseholdLiveWithOthers;

    private String providerIdentityCheckDateOfBirthDay;
    private String providerIdentityCheckDateOfBirthMonth;
    private String providerIdentityCheckDateOfBirthYear;
    private String providerIdentityCheckDateOfBirthDate;

    @NotBlank
    private String providerTaxIdType;
    @SSN(message="{registration-tax-id-ssn.error}")
    @Encrypted
    private String providerTaxIdSSN;
    @Pattern(regexp = "\\d{9}", message = "{registration-tax-id-ein.error}")
    private String providerTaxIdEIN;

    // registration terms
    @NotEmpty(message = "{errors.validate.provider-agrees-to-legal-terms}")
    private List<String> providerAgreesToLegalTerms;

    @NotEmpty(message = "{registration-service-languages.error}")
    private List<String> providerLanguagesOffered;
    private String providerLanguagesOffered_other;

    // registration-household-add-person
    @NotBlank(message = "{registration-household-add-person-info.error.first-name}")
    private String providerHouseholdMemberFirstName;
    @NotBlank(message = "{registration-household-add-person-info.error.last-name}")
    private String providerHouseholdMemberLastName;

    private String providerHouseholdMemberDateOfBirthDay;
    private String providerHouseholdMemberDateOfBirthMonth;
    private String providerHouseholdMemberDateOfBirthYear;
    @NotBlank(message = "{registration-household-add-person-info.error.relationship}")
    private String providerHouseholdMemberRelationship;
    @Encrypted
    @SSN(message="{registration-household-add-person-info.error.ssn}")
    private String providerHouseholdMemberSSN;

    // registration-signature
    @NotBlank(message = "{errors.validate.provider-signed-name}")
    private String providerSignedName;

    // registration-submit-confirmation
    private String providerSurveyFamilyDifficulty;
    private String providerSurveyProviderDifficulty;
    private String providerSurveyAdditionalComments;

    private String providerConfirmationEmailSent;
}
