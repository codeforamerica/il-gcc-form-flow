package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.Map;
import org.ilgcc.app.utils.ChildCareProvider;
import org.springframework.stereotype.Component;

@Component
public class SSNPreparer implements SubmissionFieldPreparer {

  @Override
  public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {


    var results = new HashMap<String, SubmissionField>();
    String parentSSNInput = (String) submission.getInputData().getOrDefault("parentSsn", "");
    if(!parentSSNInput.isEmpty()){
      results.put("APPLICANT_SSN", new SingleField("parentSsn", parentSSNInput.replace("-", ""), null));
    }
    return results;
  }
}
