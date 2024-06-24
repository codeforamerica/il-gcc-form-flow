package org.ilgcc.app.submission.actions;

import static java.util.Collections.emptyList;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.List;
import org.ilgcc.app.utils.IncomeOption;
import org.springframework.stereotype.Component;

@Component
public class RemoveOutdatedIncomeSources implements Action {
    @Override
    public void run(FormSubmission formSubmission, Submission submission) {
        var incomeSource = (List<String>) formSubmission.getFormData().getOrDefault("unearnedIncomeSource[]", emptyList());

        for (IncomeOption incomeOption : IncomeOption.values()) {
            var incomeOptionInputKey = incomeOption.getInputFieldName();
            if (!incomeSource.contains(incomeOption.toString()) && submission.getInputData().containsKey(incomeOptionInputKey)) {
                submission.getInputData().replace(incomeOptionInputKey, "");
            }
        }
    }
}