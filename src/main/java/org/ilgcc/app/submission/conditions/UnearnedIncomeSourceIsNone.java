package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.ilgcc.app.utils.IncomeOption;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.ilgcc.app.utils.SubmissionUtilities.MM_DD_YYYY;

@Component
public class UnearnedIncomeSourceIsNone implements Condition {

    private static final String UNEARNED_INCOME_SOURCE = "unearnedIncomeSource[]";

    @Override
    public Boolean run(Submission submission) {
        var incomeSource = (List<String>) submission.getInputData().getOrDefault(UNEARNED_INCOME_SOURCE, emptyList());

        if (incomeSource.contains("NONE")) {
            return true;
        }

        return false;
    }
}