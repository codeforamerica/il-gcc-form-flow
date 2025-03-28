package org.ilgcc.app.pdf;

import static org.ilgcc.app.utils.SubmissionUtilities.getProviderSubmissionId;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProviderSubmissionFieldPreparer implements SubmissionFieldPreparer {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission familySubmission, PdfMap pdfMap) {
        return new HashMap<String, SubmissionField>();
    }

    protected Optional<Map<String, Object>> getProviderInputData(Submission familySubmission) {
        Optional<Submission> providerSubmission = getProviderSubmission(familySubmission);

        if (providerSubmission.isPresent()) {
            return Optional.of(providerSubmission.get().getInputData());
        } else {
            return Optional.empty();
        }
    }

    protected Optional<Submission> getProviderSubmission(Submission familySubmission) {
        Optional<UUID> providerResponseUUID = getProviderSubmissionId(familySubmission);
        if (providerResponseUUID.isPresent()) {
            return submissionRepositoryService.findById(providerResponseUUID.get());
        }
        return Optional.empty();

    }
}
