package org.ilgcc.app.pdf;

import static org.ilgcc.app.utils.SubmissionUtilities.formatToStringFromLocalDate;
import static org.ilgcc.app.utils.SubmissionUtilities.hasNotChosenProvider;
import static org.ilgcc.app.utils.SubmissionUtilities.hasProviderResponse;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProviderApplicationPreparer extends ProviderSubmissionFieldPreparer {

    Submission providerSubmission;

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        if (hasNotChosenProvider(submission)) {
            return prepareNoProviderData(submission.getShortCode());
        }

        if (useProviderResponse(submission)) {
            return prepareProviderResponse();
        } else {
            return prepareFamilyIntendedProviderData(submission);
        }
    }

    //Because we are printing the PDF from the GCC flow we need to get the provider submission then pull the responses values from the provider submission
    private Map<String, SubmissionField> prepareProviderResponse() {
        var results = new HashMap<String, SubmissionField>();

        Map<String, Object> providerInputData = providerSubmission.getInputData();

        List<String> providerFields = List.of(
                "providerResponseProviderNumber",
                "providerResponseFirstName",
                "providerResponseLastName",
                "providerResponseBusinessName",
                "providerResponseContactPhoneNumber",
                "providerResponseContactEmail",
                "providerConviction",
                "providerConvictionExplanation",
                "providerIdentityCheckDateOfBirthDate",
                "providerTaxIdEIN",
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
        );

        for (String fieldName : providerFields) {
            results.put(fieldName,
                    new SingleField(fieldName, providerInputData.getOrDefault(fieldName, "").toString(), null));
        }

        Map<String, String> client = (Map<String, String>) providerInputData.getOrDefault("clientResponse",
                new HashMap<String, String>());
        results.put("clientResponseConfirmationCode", new SingleField("clientResponseConfirmationCode",
                (String) client.getOrDefault("clientResponseConfirmationCode", ""), null));

        results.put("providerLicenseNumber",
                new SingleField("providerLicenseNumber", providerLicense(providerInputData), null));

        results.put("providerSignature",
                new SingleField("providerSignature", providerSignature(providerInputData), null));
        results.put("providerSignatureDate",
                new SingleField("providerSignatureDate", providerSignatureDate(providerSubmission.getSubmittedAt()),
                        null));

        return results;
    }

    private Map<String, SubmissionField> prepareFamilyIntendedProviderData(Submission submission) {
        var results = new HashMap<String, SubmissionField>();
        Map<String, Object> inputData = submission.getInputData();

        results.put("providerNameCorporate",
                new SingleField("providerNameCorporate", inputData.getOrDefault("familyIntendedProviderName", "").toString(),
                        null));
        results.put("providerPhoneNumber",
                new SingleField("providerPhoneNumber",
                        inputData.getOrDefault("familyIntendedProviderPhoneNumber", "").toString(), null));
        results.put("providerEmail",
                new SingleField("providerEmail", inputData.getOrDefault("familyIntendedProviderEmail", "").toString(), null));
        results.put("providerResponse",
                new SingleField("providerResponse", providerResponse(submission), null));

        results.put("clientResponseConfirmationCode", new SingleField("clientResponseConfirmationCode",
                submission.getShortCode(), null));

        return results;
    }

    private Map<String, SubmissionField> prepareNoProviderData(String shortCode) {
        var results = new HashMap<String, SubmissionField>();
        results.put("providerNameCorporate",
                new SingleField("providerNameCorporate", "No qualified provider",
                        null));
        results.put("dayCareIdNumber",
                new SingleField("dayCareIdNumber", "460328258720008",
                        null));
        results.put("providerResponse",
                new SingleField("providerResponse", "No provider chosen", null));

        results.put("clientResponseConfirmationCode", new SingleField("clientResponseConfirmationCode",
                shortCode, null));

        return results;
    }



    private String providerSignature(Map<String, Object> providerInputData) {
        String providerSignature =  (String) providerInputData.getOrDefault("providerSignedName", "");
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

    private boolean useProviderResponse(Submission familySubmission) {
        Optional<Submission> providerSubmissionOptional = getProviderSubmission(familySubmission);
        if (providerSubmissionOptional.isPresent()) {
            providerSubmission = providerSubmissionOptional.get();
            Map<String, Object> providerInputData = providerSubmission.getInputData();
            if (providerInputData.containsKey("providerResponseAgreeToCare")) {
                return providerInputData.get("providerResponseAgreeToCare").equals("true");
            }

            return true;
        }
        return false;
    }

    private String providerResponse(Submission familySubmission) {
        if (hasProviderResponse(familySubmission)) {
            Map<String, Object> providerInputData = providerSubmission.getInputData();
            if (providerInputData.containsKey("providerResponseAgreeToCare")) {
                if (providerInputData.get("providerResponseAgreeToCare").equals("false")) {
                    return "Provider declined";
                } else {
                    return "true";
                }
            }
        }
        ZoneId chicagoTimeZone = ZoneId.of("America/Chicago");
        ZonedDateTime todaysDate = OffsetDateTime.now().atZoneSameInstant(chicagoTimeZone);
        if (ProviderSubmissionUtilities.providerApplicationHasExpired(familySubmission, todaysDate)) {
            return "No response from provider";
        }
        return "";
    }
}
