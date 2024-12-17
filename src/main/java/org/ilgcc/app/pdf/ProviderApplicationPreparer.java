package org.ilgcc.app.pdf;

import static org.ilgcc.app.utils.SubmissionUtilities.formatToStringFromLocalDate;
import static org.ilgcc.app.utils.SubmissionUtilities.hasNotChosenProvider;
import static org.ilgcc.app.utils.SubmissionUtilities.hasProviderResponse;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ChildCareProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProviderApplicationPreparer implements SubmissionFieldPreparer {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    private final Boolean expandExistingProviderFlowFlag;

    public ProviderApplicationPreparer(
            @Value("${il-gcc.dts.expand-existing-provider-flow}") Boolean expandExistingProviderFlowFlag) {
        this.expandExistingProviderFlowFlag = expandExistingProviderFlowFlag;
    }

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        if (hasNotChosenProvider(submission)) {
            return prepareNoProviderData();
        }

        if (useProviderResponse(submission)) {
            return prepareProviderResponse(submission);
        } else {
            if (expandExistingProviderFlowFlag) {
                return prepareFamilyIntendedProviderData(submission);
            } else {
                return prepareFamilySelectedDayCareData(submission);
            }
        }
    }
    //Because we are printing the PDF from the GCC flow we need to get the provider submission then pull the responses values from the provdier submission
    private Map<String, SubmissionField> prepareProviderResponse(Submission submission) {
        var results = new HashMap<String, SubmissionField>();

        List<String> providerFields = List.of(
                "providerResponseProviderNumber",
                "providerResponseFirstName",
                "providerResponseLastName",
                "providerResponseBusinessName",
                "providerResponseContactPhoneNumber",
                "providerResponseContactEmail"
        );

        Submission providerSubmission = providerSubmissionFromId(submission).get();
        Map<String, Object> providerInputData = providerSubmission.getInputData();

        for (String fieldName : providerFields) {
            results.put(fieldName,
                    new SingleField(fieldName, providerInputData.getOrDefault(fieldName, "").toString(), null));
        }

        Map<String, String> client = (Map<String, String>) providerSubmission.getInputData().getOrDefault("clientResponse", new HashMap<String, String>());
        results.put("clientResponseConfirmationCode", new SingleField("clientResponseConfirmationCode", (String) client.getOrDefault("clientResponseConfirmationCode", ""), null));
        results.putAll(prepareProviderAddressData(providerInputData));
        results.putAll(prepareProviderMailingAddressData(providerInputData));

        results.put("providerLicenseNumber",
                new SingleField("providerLicenseNumber", providerLicense(providerInputData), null));
        results.put("providerSignature",
                new SingleField("providerSignature", providerSignature(providerInputData), null));
        try {
            Optional<LocalDate> providerSignatureDate = Optional.of(
                    LocalDate.from(providerSubmission.getSubmittedAt()));
            results.put("providerSignatureDate",
                    new SingleField("providerSignatureDate", formatToStringFromLocalDate(providerSignatureDate),
                            null));
        } catch (NullPointerException e) {
            log.error(String.format("Provider Application: %s, does not have a submittedAt date.",
                    providerSubmission.getId().toString()));
        }

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

        return results;
    }

    private Map<String, SubmissionField> prepareFamilySelectedDayCareData(Submission submission) {
        var results = new HashMap<String, SubmissionField>();

        var provider = ChildCareProvider.valueOf((String) submission.getInputData().get("dayCareChoice"));

        results.put("dayCareName", new SingleField("dayCareName", provider.getDisplayName(), null));
        results.put("dayCareIdNumber", new SingleField("dayCareIdNumber", provider.getIdNumber(), null));
        results.put("dayCareAddressStreet", new SingleField("dayCareAddressStreet", provider.getStreet(), null));
        results.put("dayCareAddressApt", new SingleField("dayCareAddressApt", provider.getApt(), null));
        results.put("dayCareAddressCity", new SingleField("dayCareAddressCity", provider.getCity(), null));
        results.put("dayCareAddressState", new SingleField("dayCareAddressState", provider.getState(), null));
        results.put("dayCareAddressZip", new SingleField("dayCareAddressZip", provider.getZipcode(), null));

        return results;
    }

    private Map<String, SubmissionField> prepareNoProviderData() {
        var results = new HashMap<String, SubmissionField>();
        results.put("providerNameCorporate",
                new SingleField("providerNameCorporate", "No qualified provider",
                        null));
        results.put("dayCareIdNumber",
                new SingleField("dayCareIdNumber", "460328258720008",
                        null));
        results.put("providerResponse",
                new SingleField("providerResponse", "No provider chosen", null));
        return results;
    }

    private Map<String, SubmissionField> prepareProviderAddressData(Map<String, Object> inputData) {
        var results = new HashMap<String, SubmissionField>();
        var useSuggestedParentAddress = inputData.getOrDefault("useSuggestedProviderAddress", "false").equals("true");

        String mailingAddressStreet1 = useSuggestedParentAddress ? "providerResponseServiceStreetAddress1_validated"
                : "providerResponseServiceStreetAddress1";
        String mailingAddressStreet2 = useSuggestedParentAddress ? "" : "providerResponseServiceStreetAddress2";
        String mailingCity = useSuggestedParentAddress ? "providerResponseServiceCity_validated" : "providerResponseServiceCity";
        String mailingState =
                useSuggestedParentAddress ? "providerResponseServiceState_validated" : "providerResponseServiceState";
        String mailingZipCode =
                useSuggestedParentAddress ? "providerResponseServiceZipCode_validated" : "providerResponseServiceZipCode";

        results.put("providerResponseServiceStreetAddress1", new SingleField("providerResponseServiceStreetAddress1",
                inputData.getOrDefault(mailingAddressStreet1, "").toString(), null));
        results.put("providerResponseServiceStreetAddress2", new SingleField("providerResponseServiceStreetAddress2",
                inputData.getOrDefault(mailingAddressStreet2, "").toString(), null));
        results.put("providerResponseServiceCity",
                new SingleField("providerResponseServiceCity", inputData.getOrDefault(mailingCity, "").toString(), null));
        results.put("providerResponseServiceState",
                new SingleField("providerResponseServiceState", inputData.getOrDefault(mailingState, "").toString(), null));
        results.put("providerResponseServiceZipCode",
                new SingleField("providerResponseServiceZipCode", inputData.getOrDefault(mailingZipCode, "").toString(), null));

        return results;
    }

    private Map<String, SubmissionField> prepareProviderMailingAddressData(Map<String, Object> inputData) {
        var results = new HashMap<String, SubmissionField>();
        var useSuggestedMailingAddress = inputData.getOrDefault("useSuggestedMailingAddress", "false").equals("true");

        String mailingAddressStreet1 = useSuggestedMailingAddress ? "providerMailingStreetAddress1_validated"
                : "providerMailingStreetAddress1";
        String mailingAddressStreet2 = useSuggestedMailingAddress ? "" : "providerMailingStreetAddress2";
        String mailingCity = useSuggestedMailingAddress ? "providerMailingCity_validated" : "providerMailingCity";
        String mailingState =
                useSuggestedMailingAddress ? "providerMailingState_validated" : "providerMailingState";
        String mailingZipCode =
                useSuggestedMailingAddress ? "providerMailingZipCode_validated" : "providerMailingZipCode";

        results.put("providerMailingStreetAddress1", new SingleField("providerMailingStreetAddress1",
                inputData.getOrDefault(mailingAddressStreet1, "").toString(), null));
        results.put("providerMailingStreetAddress2", new SingleField("providerMailingStreetAddress2",
                inputData.getOrDefault(mailingAddressStreet2, "").toString(), null));
        results.put("providerMailingCity",
                new SingleField("providerMailingCity", inputData.getOrDefault(mailingCity, "").toString(), null));
        results.put("providerMailingState",
                new SingleField("providerMailingState", inputData.getOrDefault(mailingState, "").toString(), null));
        results.put("providerMailingZipCode",
                new SingleField("providerMailingZipCode", inputData.getOrDefault(mailingZipCode, "").toString(), null));

        return results;
    }

    private String providerSignature(Map<String, Object> providerInputData) {
        String firstname = (String) providerInputData.getOrDefault("providerResponseFirstName", "");
        String lastName = (String) providerInputData.getOrDefault("providerResponseLastName", "");
        String businessName = (String) providerInputData.getOrDefault("providerResponseBusinessName", "");

        if (businessName.isEmpty()) {
            return String.format("%s %s", firstname, lastName);
        } else {
            return String.format("%s %s, %s", firstname, lastName, businessName);
        }
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

    private Optional<Submission> providerSubmissionFromId(Submission submission) {
        if (submission.getInputData().containsKey("providerResponseSubmissionId")) {
            UUID providerId = UUID.fromString(submission.getInputData().get("providerResponseSubmissionId").toString());
            return submissionRepositoryService.findById(providerId);
        }

        return Optional.empty();
    }

    private boolean useProviderResponse(Submission familySubmission) {
        if (hasProviderResponse(familySubmission) && providerSubmissionFromId(familySubmission).isPresent()) {
            Submission providerSubmission = providerSubmissionFromId(familySubmission).get();
            Map<String, Object> providerInputData = providerSubmission.getInputData();
            if(providerInputData.containsKey("providerResponseAgreeToCare")){
                return providerInputData.get("providerResponseAgreeToCare").equals("true");
            }

            return true;
        }
        return false;
    }

    private String providerResponse(Submission familySubmission) {
        Optional<Submission> providerSubmission = providerSubmissionFromId(familySubmission);
        if (providerSubmission.isPresent()) {
            Map<String, Object> providerInputData = providerSubmission.get().getInputData();
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
