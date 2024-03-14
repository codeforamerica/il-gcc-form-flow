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

    results.put("dayCareName", new SingleField("PROVIDER_NAME_CORPORATE", provider.getDisplayName(), null));
    results.put("dayCareIdNumber", new SingleField("PROVIDER_ID_NUMBER", provider.getIdNumber(), null));
    results.put("dayCareAddressStreet", new SingleField("PROVIDER_ADDRESSS_STREET", provider.getStreet(), null));
    results.put("dayCareAddressApt", new SingleField("PROVIDER_ADDRESS_APT", provider.getApt(), null));
    results.put("dayCareAddressCity", new SingleField("PROVIDER_ADDRESS_CITY", provider.getCity(), null));
    results.put("dayCareAddressState", new SingleField("PROVIDER_ADDRESS_STATE", provider.getState(), null));
    results.put("dayCareAddressZip", new SingleField("PROVIDER_ADDRESS_ZIP", provider.getZipcode(), null));

    return results;
  }
}
