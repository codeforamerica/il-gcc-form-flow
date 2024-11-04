package org.ilgcc.app.pdf;

import static org.ilgcc.app.utils.SubmissionUtilities.formatToStringFromLocalDate;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
        if (expandExistingProviderFlowFlag) {
            return prepareSubmittedDayCareData(submission);
        } else {
            return prepareSelectedDayCareData(submission);
        }

    }
    //Because we are printing the PDF from the GCC flow we need to get the provider submission then pull the responses values from the provdier submission

    private Map<String, SubmissionField> prepareSubmittedDayCareData(Submission submission) {
        var results = new HashMap<String, SubmissionField>();
        Map<String, Object> inputData = submission.getInputData();
        boolean hasProviderResponse = inputData.containsKey("providerResponseSubmissionId");
        List<String> providerFields = List.of(
                "providerResponseProviderNumber",
                "providerResponseFirstName",
                "providerResponseLastName",
                "providerResponseBusinessName",
                "providerResponseContactPhoneNumber",
                "providerResponseContactEmail"
        );

        if (hasProviderResponse) {
            UUID providerId = UUID.fromString(inputData.get("providerResponseSubmissionId").toString());
            Optional<Submission> providerSubmission = submissionRepositoryService.findById(providerId);
            if (providerSubmission.isPresent()) {
                boolean providerAgreesToCare = providerSubmission.get().getInputData().getOrDefault("providerResponseAgreeToCare", "false").equals("true");
                if(providerAgreesToCare){
                    Map<String, Object> providerInputData = providerSubmission.get().getInputData();
                    for (String fieldName : providerFields) {
                        results.put(fieldName,
                            new SingleField(fieldName, providerInputData.getOrDefault(fieldName, "").toString(), null));
                    }

                    results.putAll(prepareProviderAddressData(providerInputData));

                    results.put("providerSignature",
                        new SingleField("providerSignature", providerSignature(providerInputData), null));
                    try {
                        Optional<LocalDate> providerSignatureDate = Optional.of(LocalDate.from(providerSubmission.get().getSubmittedAt()));
                        results.put("providerSignatureDate",
                            new SingleField("providerSignatureDate", formatToStringFromLocalDate(providerSignatureDate), null));
                    } catch (NullPointerException e){
                        log.error(String.format("Provider Application: %s, does not have a submittedAt date.", providerSubmission.get().getId().toString()));
                    }
                }else{
                    results.put("providerNameCorporate",
                        new SingleField("providerNameCorporate", inputData.getOrDefault("familyIntendedProviderName", "").toString(),
                            null));
                    results.put("providerPhoneNumber",
                        new SingleField("providerPhoneNumber",
                            inputData.getOrDefault("familyIntendedProviderPhoneNumber", "").toString(), null));
                    results.put("providerEmail",
                        new SingleField("providerEmail", inputData.getOrDefault("familyIntendedProviderEmail", "").toString(), null));
                    results.put("providerResponse",
                        new SingleField("providerResponse", "Provider declined", null));
                }
            } else {
                log.error(String.format("Family Application (%s) does not have corresponding provider application for id: %s",
                        submission.getId(), providerId));
            }
        } else {
            results.put("providerNameCorporate",
                    new SingleField("providerNameCorporate", inputData.getOrDefault("familyIntendedProviderName", "").toString(),
                            null));
            results.put("providerPhoneNumber",
                    new SingleField("providerPhoneNumber",
                            inputData.getOrDefault("familyIntendedProviderPhoneNumber", "").toString(), null));
            results.put("providerEmail",
                    new SingleField("providerEmail", inputData.getOrDefault("familyIntendedProviderEmail", "").toString(), null));
            results.put("providerResponse",
                    new SingleField("providerResponse", "No response from provider", null));
        }

        return results;
    }

    private Map<String, SubmissionField> prepareSelectedDayCareData(Submission submission) {
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

    private Map<String, SubmissionField> prepareProviderAddressData(Map<String, Object> inputData) {
        var results = new HashMap<String, SubmissionField>();
        var useSuggestedParentAddress = inputData.getOrDefault("useSuggestedProviderAddress", "false").equals("true");

        String mailingAddressStreet1 = useSuggestedParentAddress ? "providerResponseServiceStreetAddress1_validated" : "providerResponseServiceStreetAddress1";
        String mailingAddressStreet2 = useSuggestedParentAddress ? "" : "providerResponseServiceStreetAddress2";
        String mailingCity = useSuggestedParentAddress ? "providerResponseServiceCity_validated" : "providerResponseServiceCity";
        String mailingState = useSuggestedParentAddress ? "providerResponseServiceState_validated" : "providerResponseServiceState";
        String mailingZipCode = useSuggestedParentAddress ? "providerResponseServiceZipCode_validated" : "providerResponseServiceZipCode";

        results.put("providerResponseServiceStreetAddress1", new SingleField("providerResponseServiceStreetAddress1", inputData.getOrDefault(mailingAddressStreet1, "").toString(), null));
        results.put("providerResponseServiceStreetAddress2", new SingleField("providerResponseServiceStreetAddress2", inputData.getOrDefault(mailingAddressStreet2, "").toString(), null));
        results.put("providerResponseServiceCity", new SingleField("providerResponseServiceCity", inputData.getOrDefault(mailingCity, "").toString(), null));
        results.put("providerResponseServiceState", new SingleField("providerResponseServiceState", inputData.getOrDefault(mailingState, "").toString(), null));
        results.put("providerResponseServiceZipCode", new SingleField("providerResponseServiceZipCode", inputData.getOrDefault(mailingZipCode, "").toString(), null));

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
}
