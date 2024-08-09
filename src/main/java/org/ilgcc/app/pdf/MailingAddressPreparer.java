package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MailingAddressPreparer implements SubmissionFieldPreparer {

  @Override
  public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
    var results = new HashMap<String, SubmissionField>();
    var inputData = submission.getInputData();
    Boolean homeAddressSameAsMailingAddress = (Boolean) inputData.getOrDefault("parentMailingAddressSameAsHomeAddress[]", "no").equals(List.of("yes"));
    Boolean useSuggestedParentAddress = (Boolean) inputData.getOrDefault("useSuggestedParentAddress", "false").equals("true");

    String mailingAddressStreet1 = useSuggestedParentAddress ? "parentMailingStreetAddress1_validated" : "parentMailingStreetAddress1";
    String mailingAddressStreet2 = useSuggestedParentAddress ? "parentMailingStreetAddress2_validated" : "parentMailingStreetAddress2";
    String mailingCity = useSuggestedParentAddress ? "parentMailingCity_validated" : "parentMailingCity";
    String mailingState = useSuggestedParentAddress ? "parentMailingState_validated" : "parentMailingState";
    String mailingZipCode = useSuggestedParentAddress ? "parentMailingZipCode_validated" : "parentMailingZipCode";

    results.put("parentMailingAddressForPDFStreet", new SingleField("parentMailingAddressForPDFStreet", inputData.getOrDefault(mailingAddressStreet1, "").toString(), null));
    results.put("parentMailingAddressForPDFApt", new SingleField("parentMailingAddressForPDFApt", inputData.getOrDefault(mailingAddressStreet2, "").toString(), null));
    results.put("parentMailingAddressForPDFCity", new SingleField("parentMailingAddressForPDFCity", inputData.getOrDefault(mailingCity, "").toString(), null));
    results.put("parentMailingAddressForPDFState", new SingleField("parentMailingAddressForPDFState", inputData.getOrDefault(mailingState, "").toString(), null));
    results.put("parentMailingAddressForPDFZipCode", new SingleField("parentMailingAddressForPDFZipCode", inputData.getOrDefault(mailingZipCode, "").toString(), null));

    results.put("parentHomeStreetAddress1ForPDF", new SingleField("parentHomeStreetAddress1ForPDF", homeAddressSameAsMailingAddress ? inputData.getOrDefault(mailingAddressStreet1, "").toString() : inputData.getOrDefault("parentHomeStreetAddress1" , "").toString() , null));
    results.put("parentHomeStreetAddress2ForPDF", new SingleField("parentHomeStreetAddress2ForPDF", homeAddressSameAsMailingAddress ? inputData.getOrDefault(mailingAddressStreet2, "").toString() : inputData.getOrDefault( "parentHomeStreetAddress2", "").toString() , null));
    results.put("parentHomeCityForPDF", new SingleField("parentHomeCityForPDF", homeAddressSameAsMailingAddress ? inputData.getOrDefault(mailingCity, "").toString() : inputData.getOrDefault("parentHomeCity", "").toString() , null));
    results.put("parentHomeStateForPDF", new SingleField("parentHomeStateForPDF", homeAddressSameAsMailingAddress ? inputData.getOrDefault(mailingState, "").toString() : inputData.getOrDefault("parentHomeState" , "").toString() , null));
    results.put("parentHomeZipCodeForPDF", new SingleField("parentHomeZipCodeForPDF", homeAddressSameAsMailingAddress ? inputData.getOrDefault(mailingZipCode, "").toString() : inputData.getOrDefault( "parentHomeZipCode", "").toString(), null));
    return results;
  }
}
