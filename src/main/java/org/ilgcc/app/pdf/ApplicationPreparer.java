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
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.utils.PreparerUtilities;
import org.springframework.stereotype.Component;

@Component
public class ApplicationPreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();
        var inputData = submission.getInputData();
        var receivedTimestampFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy, h:mm a zzz");
        ZoneId chicagoTimeZone = ZoneId.of("America/Chicago");

        Optional<OffsetDateTime> submittedAt = Optional.ofNullable(submission.getSubmittedAt());
        submittedAt.ifPresent(offsetDateTime -> {
            offsetDateTime.atZoneSameInstant(chicagoTimeZone);
            String formattedSubmittedAtDate = offsetDateTime.atZoneSameInstant(chicagoTimeZone).format(receivedTimestampFormat);
            results.put("receivedTimestamp", new SingleField("receivedTimestamp", formattedSubmittedAtDate, null));
        });

        var partnerSignature = inputData.getOrDefault("partnerSignedName", "");

        if (!partnerSignature.equals("")) {
            Optional<LocalDate> partnerSignatureDate = Optional.of(LocalDate.from(submission.getSubmittedAt()));
            results.put("partnerSignedAt",
                    new SingleField("partnerSignedAt", formatToStringFromLocalDate(partnerSignatureDate), null));
        }

        var parentFirstName = inputData.getOrDefault("parentFirstName", "");
        var parentLastName = inputData.getOrDefault("parentLastName", "");
        results.put("parentFullName",
                new SingleField("parentFullName", String.format("%s, %s", parentLastName, parentFirstName), null));

        String rentalIncome = inputData.getOrDefault("unearnedIncomeRental", "").toString();
        String dividendIncome = inputData.getOrDefault("unearnedIncomeDividends", "").toString();
        String unemploymentIncome = inputData.getOrDefault("unearnedIncomeUnemployment", "").toString();
        String royaltiesIncome = inputData.getOrDefault("unearnedIncomeRoyalties", "0").toString();
        String pensionIncome = inputData.getOrDefault("unearnedIncomePension", "0").toString();
        String workersIncome = inputData.getOrDefault("unearnedIncomeWorkers", "0").toString();

        var totalExpenses = PreparerUtilities.numberValueOf(rentalIncome) + PreparerUtilities.numberValueOf(dividendIncome)
                + PreparerUtilities.numberValueOf(unemploymentIncome) + PreparerUtilities.numberValueOf(royaltiesIncome)
                + PreparerUtilities.numberValueOf(pensionIncome) + PreparerUtilities.numberValueOf(workersIncome);

        results.put("otherMonthlyIncomeApplicant",
                new SingleField("otherMonthlyIncomeApplicant", String.format("%.0f", Math.floor(totalExpenses)), null));

        return results;
    }
}
