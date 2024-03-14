package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import org.ilgcc.app.utils.ChildCareProvider;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DayCareProviderPreparer implements SubmissionFieldPreparer {

  @Override
  public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
    var results = new HashMap<String, SubmissionField>();

    var provider = ChildCareProvider.valueOf((String) submission.getInputData().get("dayCareChoice"));

    results.put("PROVIDER_NAME_CORPORATE", new SingleField("dayCareName", provider.getDisplayName(), null));
    results.put("PROVIDER_ID_NUMBER", new SingleField("dayCareIdNumber", provider.getIdNumber(), null));
    results.put("PROVIDER_ADDRESSS_STREET", new SingleField("dayCareAddressStreet", provider.getStreet(), null));
    results.put("PROVIDER_ADDRESS_APT", new SingleField("dayCareAddressApt", provider.getApt(), null));
    results.put("PROVIDER_ADDRESS_CITY", new SingleField("dayCareAddressCity", provider.getCity(), null));
    results.put("PROVIDER_ADDRESS_STATE", new SingleField("dayCareAddressState", provider.getState(), null));
    results.put("PROVIDER_ADDRESS_ZIP", new SingleField("dayCareAddressZip", provider.getZipcode(), null));

    return results;
  }
}
