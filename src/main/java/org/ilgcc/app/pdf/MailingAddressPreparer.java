package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.ChildCareProvider;
import org.springframework.stereotype.Component;

@Component
public class MailingAddressPreparer implements SubmissionFieldPreparer {

  @Override
  public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
    var results = new HashMap<String, SubmissionField>();
    var inputData = submission.getInputData();
    Boolean homeAddressSameAsMailingAddress = (Boolean) inputData.getOrDefault("parentMailingAddressSameAsHomeAddress[]", "no").equals(List.of("yes"));

    var hasMailingAddress = !inputData.getOrDefault("parentMailingStreetAddress1", "").equals("");
    if (hasMailingAddress && !homeAddressSameAsMailingAddress){
      results.put("APPLICANT_ADDRESS_MAILING_STREET", new SingleField("parentMailingAddressForPDFStreet", (String) inputData.get("parentMailingStreetAddress1"), null));
      results.put("APPLICANT_ADDRESS_MAILING_APT", new SingleField("parentMailingAddressForPDFApt", (String) inputData.get("parentMailingStreetAddress2"), null));
      results.put("APPLICANT_ADDRESS_MAILING_CITY", new SingleField("parentMailingAddressForPDFCity", (String) inputData.get("parentMailingCity"), null));
      results.put("APPLICANT_ADDRESS_MAILING_STATE", new SingleField("parentMailingAddressForPDFState", (String) inputData.get("parentMailingState"), null));
      results.put("APPLICANT_ADDRESS_MAILING_ZIP", new SingleField("parentMailingAddressForPDFZip", (String) inputData.get("parentMailingZipcode"), null));
    }
    if (homeAddressSameAsMailingAddress){
      results.put("APPLICANT_ADDRESS_MAILING_STREET", new SingleField("parentMailingAddressForPDFStreet", "", null));
      results.put("APPLICANT_ADDRESS_MAILING_APT", new SingleField("parentMailingAddressForPDFApt", "", null));
      results.put("APPLICANT_ADDRESS_MAILING_CITY", new SingleField("parentMailingAddressForPDFCity", "", null));
      results.put("APPLICANT_ADDRESS_MAILING_STATE", new SingleField("parentMailingAddressForPDFState", "", null));
      results.put("APPLICANT_ADDRESS_MAILING_ZIP", new SingleField("parentMailingAddressForPDFZipCode", "", null));
    }

    return results;
  }
}
