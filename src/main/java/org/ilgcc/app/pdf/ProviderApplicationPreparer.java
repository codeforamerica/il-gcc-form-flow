package org.ilgcc.app.pdf;

import static org.ilgcc.app.utils.SubmissionUtilities.formatToStringFromLocalDate;

import formflow.library.data.Submission;
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
import org.ilgcc.app.utils.PreparerUtilities;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name="il-gcc.dts.expand-existing-provider-flow", havingValue = "true")
@Component
public class ProviderApplicationPreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();
        var inputData = submission.getInputData();

        boolean hasProviderResponse = inputData.containsKey("providerResponseSubmissionId");

        if(!hasProviderResponse){
            results.put("providerNameCorporate",
                    new SingleField("providerNameCorporate", inputData.getOrDefault("familyIntendedProviderName", "").toString(), null));
            results.put("providerPhoneNumber",
                    new SingleField("providerPhoneNumber", inputData.getOrDefault("familyIntendedProviderPhoneNumber", "").toString(), null));
            results.put("providerEmail",
                    new SingleField("providerEmail", inputData.getOrDefault("familyIntendedProviderEmail", "").toString(), null));
            results.put("providerResponse",
                    new SingleField("providerResponse", "No response from provider", null));
        }

        return results;
    }
}
