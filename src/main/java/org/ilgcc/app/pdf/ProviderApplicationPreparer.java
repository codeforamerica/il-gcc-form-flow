package org.ilgcc.app.pdf;

import static org.ilgcc.app.utils.SubmissionUtilities.formatToStringFromLocalDate;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.swing.text.html.Option;
import org.ilgcc.app.utils.PreparerUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "il-gcc.dts.wait-for-provider-response", havingValue = "true")
@Component
public class ProviderApplicationPreparer implements SubmissionFieldPreparer {

    SubmissionRepositoryService submissionRepositoryService;

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();
        var inputData = submission.getInputData();

        boolean hasProviderResponse = inputData.containsKey("providerResponseSubmissionId");
        List<String> providerFields = List.of(
                "providerResponseFirstName",
                "providerResponseLastName",
                "providerResponseBusinessName",
                "providerResponseServiceAddress1",
                "providerResponseServiceAddress2",
                "providerResponseServiceCity",
                "providerResponseServiceState",
                "providerResponseServiceZipCode",
                "providerResponseContactPhoneNumber",
                "providerResponseContactEmail"
        );

        if (hasProviderResponse) {
            UUID providerId = (UUID) inputData.get("providerResponseSubmissionId");
            Optional<Submission> providerSubmission = submissionRepositoryService.findById(providerId);
            if (providerSubmission.isPresent()) {
                Map<String, Object> providerInputData = providerSubmission.get().getInputData();
                for (String fieldName : providerFields) {
                    results.put(fieldName,
                            new SingleField(fieldName, providerInputData.getOrDefault(fieldName, "").toString(), null));
                }

                results.put("providerSignature", new SingleField("providerSignature", providerSignature(providerInputData), null));
            }
        }

        if (!hasProviderResponse) {
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

    private String providerSignature(Map<String, Object> providerInputData){
        String firstname = (String) providerInputData.getOrDefault("providerResponseFirstName", "");
        String lastName = (String) providerInputData.getOrDefault("providerResponseLastName", "");
        String businessName = (String) providerInputData.getOrDefault("providerResponseBusinessName", "");

        if(businessName.isEmpty()){
            return String.format("%s %s", firstname, lastName);
        } else {
            return String.format("%s %s, %s", firstname, lastName, businessName);
        }
    }
}
