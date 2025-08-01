package org.ilgcc.app.pdf;

import static org.ilgcc.app.utils.PreparerUtilities.getApplicantFamilySize;
import static org.ilgcc.app.utils.SubmissionUtilities.formatToStringFromLocalDate;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
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
            ZonedDateTime chicagoSubmittedAt = offsetDateTime.atZoneSameInstant(chicagoTimeZone);
            results.put("receivedTimestamp", new SingleField("receivedTimestamp",
                    chicagoSubmittedAt.format(receivedTimestampFormat), null));

            Optional<LocalDate> signatureDate = Optional.of(LocalDate.from(chicagoSubmittedAt));

            String applicantSignature = (String) inputData.getOrDefault("signedName", "");
            if (!applicantSignature.isBlank()) {
                results.put("applicantSignedAt",
                        new SingleField("applicantSignedAt", formatToStringFromLocalDate(signatureDate), null));
            }

            String partnerSignature = (String) inputData.getOrDefault("partnerSignedName", "");
            if (!partnerSignature.isEmpty()) {
                results.put("partnerSignedAt",
                        new SingleField("partnerSignedAt", formatToStringFromLocalDate(signatureDate), null));
            }
        });

        var parentFirstName = inputData.getOrDefault("parentFirstName", "");
        var parentLastName = inputData.getOrDefault("parentLastName", "");
        results.put("parentFullName",
                new SingleField("parentFullName", String.format("%s, %s", parentLastName, parentFirstName), null));
        results.put("parentHomeStreetAddress1", setAddressData("parentHome", inputData));
        results.put("parentMailingStreetAddress1", setAddressData("parentMailing", inputData));
        String partnerFirstName = inputData.getOrDefault("parentPartnerFirstName", "").toString();
        String partnerLastName = inputData.getOrDefault("parentPartnerLastName", "").toString();
        results.put("partnerFullName",
                new SingleField("partnerFullName", String.format("%s, %s", partnerLastName, partnerFirstName), null));

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

        List<String> unearnedIncomePrograms = (List<String>) inputData.getOrDefault("unearnedIncomePrograms[]", List.of());
        if (!unearnedIncomePrograms.isEmpty()) {
            if (unearnedIncomePrograms.contains("SNAP")) {
                results.put("unearnedIncomePrograms-snap", new SingleField("unearnedIncomePrograms-snap", "true", null));
            }
            if (unearnedIncomePrograms.contains("HOMELESS_SHELTER_OR_PREVENTION_PROGRAMS")) {
                results.put("unearnedIncomePrograms-homeless-shelters",
                        new SingleField("unearnedIncomePrograms-homeless-shelters", "true", null));
            }
            if (unearnedIncomePrograms.contains("CASH_ASSISTANCE")) {
                results.put("unearnedIncomePrograms-tanf", new SingleField("unearnedIncomePrograms-tanf", "true", null));
            }
            if (unearnedIncomePrograms.contains("HOUSING_VOUCHERS")) {
                results.put("unearnedIncomePrograms-housing-vouchers",
                        new SingleField("unearnedIncomePrograms-housing-vouchers", "true", null));
            }
        }

        List<String> unearnedIncomeReferral = (List<String>) inputData.getOrDefault("unearnedIncomeReferralServices[]",
                List.of());
        if (unearnedIncomeReferral.contains("SAFE_SUPPORT")) {
            results.put("referralServicesDomesticViolence", new SingleField("referralServicesDomesticViolence", "true", null));
        }
        if (unearnedIncomeReferral.contains("HOUSING_SUPPORT")) {
            results.put("referralServicesHomelessness", new SingleField("referralServicesHomelessness", "true", null));
        }
        if (unearnedIncomeReferral.contains("DISABILITY_SUPPORT")) {
            results.put("referralServicesPhysicalOrMentalDisability",
                    new SingleField("referralServicesPhysicalOrMentalDisability", "true", null));
        }

        results.put("clientResponseConfirmationCode",
                new SingleField("clientResponseConfirmationCode", submission.getShortCode(), null));

        results.put("childcareStartDate",
                new SingleField("childcareStartDate",
                        inputData.getOrDefault("earliestChildcareStartDate", "").toString(), null));
        results.put("applicantFamilySize",
            new SingleField("applicantFamilySize", Integer.toString(getApplicantFamilySize(inputData)), null));
        return results;
    }

    private SingleField setAddressData(String addressPrefix, Map<String, Object> inputData) {
        String street1Key = String.format("%sStreetAddress1", addressPrefix);
        String street2Key = String.format("%sStreetAddress2", addressPrefix);
        String streetAddress2 = (String) inputData.getOrDefault(street2Key, "");
        String streetAddress1 = (String) inputData.getOrDefault(street1Key, "");

        String address = streetAddress1 + (!streetAddress2.isBlank() ? ", " + streetAddress2 : "");
        return new SingleField(street1Key, address, null);
    }
}
