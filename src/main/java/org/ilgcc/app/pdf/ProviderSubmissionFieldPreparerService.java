package org.ilgcc.app.pdf;

import static org.ilgcc.app.utils.SchedulePreparerUtility.getRelatedChildrenSchedulesForProvider;
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
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.pdf.helpers.FamilyIntendedProviderPreparerHelper;
import org.ilgcc.app.pdf.helpers.ProviderApplicationPreparerHelper;
import org.ilgcc.app.pdf.helpers.ProviderHouseholdMemberPreparerHelper;
import org.ilgcc.app.pdf.helpers.ProviderLanguagesPreparerHelper;
import org.ilgcc.app.pdf.helpers.ProviderRegistrationPreparer;
import org.ilgcc.app.pdf.helpers.ProviderSSNPreparerHelper;
import org.ilgcc.app.pdf.helpers.ProviderTypePreparerHelper;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
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

    @Autowired
    ProviderRegistrationPreparer providerRegistrationPreparer;

    @Autowired
    NeedChildcareChildrenPreparer needChildcareChildrenPreparer;

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
            if ("false".equals(providerSubmissionOptional.get().getInputData().get("providerPaidCcap"))) {
                results.putAll(mapProviderRegistrationData(providerSubmissionOptional.get().getInputData()));
            }
            results.putAll(
                    providerApplicationPreparerHelper.prepareSubmissionFields(providerSubmissionOptional.get().getInputData()));
            results.put("providerSignatureDate",
                    new SingleField("providerSignatureDate",
                            providerSignatureDate(providerSubmissionOptional.get().getSubmittedAt()), null));
        } else {
            if (enableMultipleProviders) {
                Map<String, List<Map<String, Object>>> mergedChildrenAndSchedules =
                        getRelatedChildrenSchedulesForProvider(familySubmission.getInputData());

                String providerUuid = mergedChildrenAndSchedules.keySet().stream().toList().get(0);

                if (null != providerUuid) {
                    Map<String, Object> firstProviderObject = new HashMap<>();
                    if (providerUuid == "NO_PROVIDER") {
                        firstProviderObject.put("uuid", "NO_PROVIDER");
                    } else {
                        firstProviderObject = SubmissionUtilities.getCurrentProvider(familySubmission.getInputData(),
                                providerUuid);
                    }

                    List<Map<String, Object>> listOfChildcareSchedulesForCurrentProvider =
                            mergedChildrenAndSchedules.get(providerUuid);

                    for (int j = 0; j < listOfChildcareSchedulesForCurrentProvider.size(); j++) {
                        results.putAll(
                                needChildcareChildrenPreparer.prepareChildCareSchedule(
                                        listOfChildcareSchedulesForCurrentProvider.get(j),
                                        j + 1));
                    }

                    results.putAll(familyIntendedProviderPreparerHelper.prepareSubmissionFields(familySubmission,
                            firstProviderObject));
                }
            } else {
                results.putAll(familyIntendedProviderPreparerHelper.prepareSubmissionFields(familySubmission.getInputData()));
            }
        }

        return results;
    }

    public Map<String, SubmissionField> mapProviderRegistrationData(Map<String, Object> registeringProviderInputData) {
        Map<String, SubmissionField> providerSubmissionFields = new HashMap<>();
        providerSubmissionFields.putAll(providerRegistrationPreparer.prepareSubmissionFields(registeringProviderInputData));
        providerSubmissionFields.putAll(providerHouseholdMemberPreparer.prepareSubmissionFields(registeringProviderInputData));
        providerSubmissionFields.putAll(providerLanguagesPreparerHelper.prepareSubmissionFields(registeringProviderInputData));
        providerSubmissionFields.putAll(providerTypePreparerHelper.prepareSubmissionFields(registeringProviderInputData));
        providerSubmissionFields.putAll(providerSSNPreparerHelper.prepareSubmissionFields(registeringProviderInputData));

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
