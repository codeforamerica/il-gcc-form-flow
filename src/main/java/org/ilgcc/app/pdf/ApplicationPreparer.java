package org.ilgcc.app.pdf;

import static java.util.Collections.emptyList;
import static java.util.Collections.list;
import static org.ilgcc.app.utils.SubmissionUtilities.formatToStringFromLocalDate;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.bytebuddy.asm.Advice.Local;
import org.springframework.stereotype.Component;

@Component
public class ApplicationPreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();

        var inputData = submission.getInputData();

        var partnerSignature = inputData.getOrDefault("partnerSignedName", "");

        if (!partnerSignature.equals("")) {
            Optional<LocalDate> partnerSignatureDate = Optional.of(LocalDate.from(submission.getSubmittedAt()));
            results.put("partnerSignedAt",
                new SingleField("partnerSignedAt", formatToStringFromLocalDate(partnerSignatureDate), null));
        }

        Integer rentalIncome = Integer.parseInt(inputData.getOrDefault("unearnedIncomeRental", "0").toString());
        Integer dividendIncome = Integer.parseInt(inputData.getOrDefault("unearnedIncomeDividends", "0").toString());
        Integer unemploymentIncome = Integer.parseInt(inputData.getOrDefault("unearnedIncomeUnemployment", "0").toString());
        Integer royaltiesIncome = Integer.parseInt(inputData.getOrDefault("unearnedIncomeRoyalties", "0").toString());
        Integer pensionIncome = Integer.parseInt(inputData.getOrDefault("unearnedIncomePension", "0").toString());
        Integer workersIncome = Integer.parseInt(inputData.getOrDefault("unearnedIncomeWorkers", "0").toString());

        var totalExpenses = rentalIncome + dividendIncome + unemploymentIncome + royaltiesIncome + pensionIncome + workersIncome;

        results.put("otherMonthlyIncomeApplicant",
            new SingleField("otherMonthlyIncomeApplicant", Integer.toString(totalExpenses), null));

        return results;
    }
}