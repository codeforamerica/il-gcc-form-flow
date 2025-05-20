package org.ilgcc.app.pdf;

import static java.util.Collections.emptyList;
import static org.ilgcc.app.utils.SubmissionUtilities.formatToStringFromLocalDate;
import static org.ilgcc.app.utils.SubmissionUtilities.getProviderSubmissionId;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.ilgcc.app.pdf.helpers.FamilyIntendedProviderPreparerHelper;
import org.ilgcc.app.pdf.helpers.ProviderApplicationPreparerHelper;
import org.ilgcc.app.pdf.helpers.ProviderHouseholdMemberPreparerHelper;
import org.ilgcc.app.pdf.helpers.ProviderLanguagesPreparerHelper;
import org.ilgcc.app.pdf.helpers.ProviderSSNPreparerHelper;
import org.ilgcc.app.pdf.helpers.ProviderTypePreparerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProviderSubmissionFieldPreparerService implements SubmissionFieldPreparer {

    private boolean enableMultipleProviders;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    ProviderApplicationPreparerHelper providerApplicationPreparerHelper;

    @Autowired
    ProviderHouseholdMemberPreparerHelper providerHouseholdMemberPreparer;

    @Autowired
    ProviderLanguagesPreparerHelper providerLanguagesPreparerHelper;

    @Autowired
    ProviderSSNPreparerHelper providerSSNPreparerHelper;

    @Autowired
    ProviderTypePreparerHelper providerTypePreparerHelper;

    @Autowired
    FamilyIntendedProviderPreparerHelper familyIntendedProviderPreparerHelper;

    public ProviderSubmissionFieldPreparerService(
            @Value("${il-gcc.enable-multiple-providers}") boolean enableMultipleProviders) {
        this.enableMultipleProviders = enableMultipleProviders;
    }

    //Because we are printing the PDF from the GCC flow we need to get the provider submission then pull the responses values from the provider submission
    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission familySubmission, PdfMap pdfMap) {
        Map<String, SubmissionField> results = new HashMap<>();

        Optional<Submission> providerSubmissionOptional = getProviderSubmission(familySubmission);
        if (providerSubmissionOptional.isPresent()) {
            results.putAll(mapProviderInputDataToSubmissionFields(providerSubmissionOptional.get().getInputData()));
            results.put("providerSignatureDate",
                    new SingleField("providerSignatureDate",
                            providerSignatureDate(providerSubmissionOptional.get().getSubmittedAt()), null));
        } else {
            if (enableMultipleProviders) {
                List<Map<String, Object>> providers = (List<Map<String, Object>>) familySubmission.getInputData()
                        .getOrDefault("providers[]", emptyList());

                Map<String, Object> firstProvider = providers.getFirst();
                results.putAll(familyIntendedProviderPreparerHelper.prepareSubmissionFields(firstProvider));
            } else {
                results.putAll(familyIntendedProviderPreparerHelper.prepareSubmissionFields(familySubmission.getInputData()));
            }
        }

        return results;
    }

    public Map<String, SubmissionField> mapProviderInputDataToSubmissionFields(Map<String, Object> providerInputData) {
        Map<String, SubmissionField> providerSubmissionFields = new HashMap<>();
        providerSubmissionFields.putAll(providerApplicationPreparerHelper.prepareSubmissionFields(providerInputData));
        providerSubmissionFields.putAll(providerHouseholdMemberPreparer.prepareSubmissionFields(providerInputData));
        providerSubmissionFields.putAll(providerLanguagesPreparerHelper.prepareSubmissionFields(providerInputData));
        providerSubmissionFields.putAll(providerTypePreparerHelper.prepareSubmissionFields(providerInputData));
        providerSubmissionFields.putAll(providerSSNPreparerHelper.prepareSubmissionFields(providerInputData));

        return providerSubmissionFields;
    }

    protected Optional<Submission> getProviderSubmission(Submission familySubmission) {
        Optional<UUID> providerResponseUUID = getProviderSubmissionId(familySubmission);
        if (providerResponseUUID.isPresent()) {
            return submissionRepositoryService.findById(providerResponseUUID.get());
        }
        return Optional.empty();

    }

    private String providerSignatureDate(OffsetDateTime submittedAt) {
        if (submittedAt != null) {
            Optional<LocalDate> providerSignatureDate = Optional.of(LocalDate.from(submittedAt));
            return formatToStringFromLocalDate(providerSignatureDate);
        }
        return "";
    }
}
