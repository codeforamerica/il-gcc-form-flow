package org.ilgcc.app.pdf.helpers;

import formflow.library.pdf.SubmissionField;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class InputDataPreparerHelper {
    public Map<String, SubmissionField> prepareSubmissionFields(Map<String, Object> inputData) {
        return new HashMap<>();
    }
}
