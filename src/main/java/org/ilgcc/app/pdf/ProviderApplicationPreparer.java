package org.ilgcc.app.pdf;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getProviderApplicationResponseStatus;
import static org.ilgcc.app.utils.SubmissionUtilities.formatToStringFromLocalDate;
import static org.ilgcc.app.utils.SubmissionUtilities.hasNotChosenProvider;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.enums.ProviderDenialReason;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProviderApplicationPreparer extends ProviderSubmissionFieldPreparer {

    Submission providerSubmission;

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission familySubmission, PdfMap pdfMap) {
        if (hasNotChosenProvider(familySubmission)) {
            return prepareNoProviderData(familySubmission.getShortCode());
        }

        Optional<Submission> providerSubmissionOptional = getProviderSubmission(familySubmission);

        if (providerSubmissionOptional.isPresent()) {
            providerSubmission = providerSubmissionOptional.get();
            return prepareProviderResponse();
        } else {
            Optional<SubmissionStatus> submissionStatus = getProviderApplicationResponseStatus(familySubmission);
            Boolean hasExpired = submissionStatus.isPresent() && submissionStatus.get().equals(SubmissionStatus.EXPIRED);
            return prepareFamilyIntendedProviderData(familySubmission, hasExpired);
        }
    }

    //Because we are printing the PDF from the GCC flow we need to get the provider submission then pull the responses values from the provider submission
    private Map<String, SubmissionField> prepareProviderResponse() {
        var results = new HashMap<String, SubmissionField>();

        Map<String, Object> providerInputData = providerSubmission.getInputData();

        List<String> providerFields = new ArrayList<>(Arrays.asList(
                "providerResponseFirstName",
                "providerResponseLastName",
                "providerResponseBusinessName",
                "providerResponseContactPhoneNumber",
                "providerResponseContactEmail",
                "providerConviction",
                "providerConvictionExplanation",
                "providerIdentityCheckDateOfBirthDate",
                "providerResponseProviderNumber",
                "providerTaxIdFEIN",
                "providerResponseServiceStreetAddress1",
                "providerResponseServiceStreetAddress2",
                "providerResponseServiceCity",
                "providerResponseServiceState",
                "providerResponseServiceZipCode",
                "providerMailingStreetAddress1",
                "providerMailingStreetAddress2",
                "providerMailingCity",
                "providerMailingState",
                "providerMailingZipCode"
        ));

        for (
                String fieldName : providerFields) {
            results.put(fieldName,
                    new SingleField(fieldName, providerInputData.getOrDefault(fieldName, "").toString(), null));
        }

        Map<String, String> client = (Map<String, String>) providerInputData.getOrDefault("clientResponse",
                new HashMap<String, String>());
        results.put("clientResponseConfirmationCode", new SingleField("clientResponseConfirmationCode",
                client.getOrDefault("clientResponseConfirmationCode", ""), null));

        results.put("providerLicenseNumber",
                new SingleField("providerLicenseNumber", providerLicense(providerInputData), null));
        results.put("providerSignature",
                new SingleField("providerSignature", providerSignature(providerInputData), null));
        results.put("providerSignatureDate",
                new SingleField("providerSignatureDate", providerSignatureDate(providerSubmission.getSubmittedAt()),
                        null));
        results.put("providerResponse", new SingleField("providerResponse", providerResponse(providerInputData), null));

        return results;
    }

    private Map<String, SubmissionField> prepareFamilyIntendedProviderData(Submission submission,
            Boolean providerApplicationExpired) {
        var results = new HashMap<String, SubmissionField>();
        Map<String, Object> inputData = submission.getInputData();


        results.put("providerResponseBusinessName",
                new SingleField("providerResponseBusinessName", inputData.getOrDefault("familyIntendedProviderName", "").toString(),
                        null));
        results.put("providerPhoneNumber",
                new SingleField("providerPhoneNumber",
                        inputData.getOrDefault("familyIntendedProviderPhoneNumber", "").toString(), null));
        results.put("providerEmail",
                new SingleField("providerEmail", inputData.getOrDefault("familyIntendedProviderEmail", "").toString(), null));

        results.put("clientResponseConfirmationCode", new SingleField("clientResponseConfirmationCode",
                submission.getShortCode(), null));

        if (providerApplicationExpired) {
            results.put("providerResponse",
                    new SingleField("providerResponse", "No response from provider", null));
        }

        return results;
    }

    private Map<String, SubmissionField> prepareNoProviderData(String shortCode) {
        var results = new HashMap<String, SubmissionField>();
        results.put("providerResponseBusinessName",
                new SingleField("providerResponseBusinessName", "No qualified provider",
                        null));
        results.put("providerResponseProviderNumber",
                new SingleField("providerResponseProviderNumber", "460328258720008",
                        null));
        results.put("providerResponse",
                new SingleField("providerResponse", "No provider chosen", null));

        results.put("clientResponseConfirmationCode", new SingleField("clientResponseConfirmationCode",
                shortCode, null));

        return results;
    }


    private String providerSignature(Map<String, Object> providerInputData) {
        String providerSignature = (String) providerInputData.getOrDefault("providerSignedName", "");
        if (!providerSignature.isEmpty()) {
            return providerSignature;
        }
        String firstname = (String) providerInputData.getOrDefault("providerResponseFirstName", "");
        String lastName = (String) providerInputData.getOrDefault("providerResponseLastName", "");
        String businessName = (String) providerInputData.getOrDefault("providerResponseBusinessName", "");

        if (businessName.isEmpty()) {
            return String.format("%s %s", firstname, lastName);
        } else {
            return String.format("%s %s, %s", firstname, lastName, businessName);
        }
    }

    private String providerSignatureDate(OffsetDateTime submittedAt) {
        if (submittedAt != null) {
            Optional<LocalDate> providerSignatureDate = Optional.of(
                    LocalDate.from(submittedAt));
            return formatToStringFromLocalDate(providerSignatureDate);
        }
        return "";
    }

    private String providerLicense(Map<String, Object> providerInputData) {
        String providerHasStateLicense = (String) providerInputData.getOrDefault("providerCurrentlyLicensed", "false");
        String providerLicenseNumber = (String) providerInputData.getOrDefault("providerLicenseNumber", "");
        String providerLicenseState = (String) providerInputData.getOrDefault("providerLicenseState", "");
        if (providerHasStateLicense.equalsIgnoreCase("true")) {
            if (providerLicenseState.isEmpty()) {
                return providerLicenseNumber;
            } else {
                return String.format("%s (%s)", providerLicenseNumber, providerLicenseState);
            }
        }
        return "";

    }


    private String providerResponse(Map<String, Object> providerInputData) {
        boolean returningProvider = providerInputData.getOrDefault("providerPaidCcap", "true").toString().equals("true");
        boolean hasFEIN = providerInputData.containsKey("providerTaxIdFEIN");
        boolean hasProviderNumber = providerInputData.containsKey("providerResponseProviderNumber");
        if ("false".equals(providerInputData.get("providerResponseAgreeToCare"))) {
            if (providerInputData.get("providerResponseDenyCareReason") != null && !providerInputData.get("providerResponseDenyCareReason").toString().isEmpty()) {
                return ProviderDenialReason.valueOf(
                        providerInputData.get("providerResponseDenyCareReason").toString()).getPdfValue();
            } else {
                return ProviderDenialReason.NO_REASON_SELECTED.getPdfValue();
            }
        }

        if (returningProvider && !hasFEIN && !hasProviderNumber) {
            return "Unable to identify provider - no response to care arrangement";
        }

        return "";
    }
}
