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
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProviderSubmissionFieldPreparerService implements SubmissionFieldPreparer {
    
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

    //Because we are printing the PDF from the GCC flow we need to get the provider submission then pull the responses values from the provider submission
    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission familySubmission, PdfMap pdfMap) {
        Map<String, SubmissionField> results = new HashMap<>();
        
        // TODO: We were using enableMultipleProviders but we can just use the key from the multiprovider data structure
        // When enable multi provider in prod, the else of this code can be removed
        if (familySubmission.getInputData().containsKey("providers")) {
            Map<String, List<Map<String, Object>>> mergedChildrenAndSchedules =
                    getRelatedChildrenSchedulesForProvider(familySubmission.getInputData());

            String providerUuid = mergedChildrenAndSchedules.keySet().stream().toList().get(0);

            if (null != providerUuid) {
                Map<String, Object> firstProviderObject = new HashMap<>();
                if (providerUuid.equals("NO_PROVIDER")) {
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

                Optional<Submission> providerSubmissionOptional = Optional.empty();

                if (firstProviderObject.containsKey("providerResponseSubmissionId")) {
                    UUID providerUUID = UUID.fromString(firstProviderObject.get(
                            "providerResponseSubmissionId").toString());
                    providerSubmissionOptional =
                            submissionRepositoryService.findById(providerUUID);
                }

                if (providerSubmissionOptional.isPresent()) {
                    Submission providerSubmission = providerSubmissionOptional.get();
                    if (ProviderSubmissionUtilities.isProviderRegistering(providerSubmission)) {
                        results.putAll(mapProviderRegistrationData(providerSubmission.getInputData()));
                    }
                    results.putAll(
                            providerApplicationPreparerHelper.prepareSubmissionFields(
                                    providerSubmission.getInputData()));

                    results.putAll(setProviderSignatureAndDate(providerSubmissionOptional.get()));
                } else {
                    results.putAll(familyIntendedProviderPreparerHelper.prepareSubmissionFields(familySubmission,
                            firstProviderObject));
                }
            }

        } else {
            Optional<Submission> providerSubmissionOptional = getProviderSubmission(familySubmission);

            if (providerSubmissionOptional.isPresent()) {
                Submission providerSubmission = providerSubmissionOptional.get();
                if (ProviderSubmissionUtilities.isProviderRegistering(providerSubmission)) {
                    results.putAll(mapProviderRegistrationData(providerSubmission.getInputData()));
                }
                results.putAll(
                        providerApplicationPreparerHelper.prepareSubmissionFields(
                                providerSubmission.getInputData()));

                results.putAll(setProviderSignatureAndDate(providerSubmission));
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

    private  Map<String, SubmissionField> setProviderSignatureAndDate(Submission providerSubmission){
        Map<String, SubmissionField> fields = new HashMap<>();

        if (providerSubmission.getInputData().getOrDefault("providerResponseAgreeToCare", "false").equals("true")) {
            fields.put("providerSignature", new SingleField("providerSignature", providerSignature(providerSubmission.getInputData()), null));
            fields.put("providerSignatureDate",
                    new SingleField("providerSignatureDate",
                            providerSignatureDate(providerSubmission.getSubmittedAt()), null));
        }

        return fields;
    }

    private String providerSignatureDate(OffsetDateTime submittedAt) {
        if (submittedAt != null) {
            Optional<LocalDate> providerSignatureDate = Optional.of(LocalDate.from(submittedAt));
            return formatToStringFromLocalDate(providerSignatureDate);
        }
        return "";
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
}
