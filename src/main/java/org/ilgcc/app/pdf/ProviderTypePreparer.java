package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ProviderTypePreparer implements SubmissionFieldPreparer {
  @Autowired
  SubmissionRepositoryService submissionRepositoryService;
  @Autowired
  private ProviderSubmissionUtilities providerSubmissionUtilities;

  @Override
  public Map<String, SubmissionField> prepareSubmissionFields(Submission familySubmission, PdfMap pdfMap) {
    var results = new HashMap<String, SubmissionField>();
    Optional<Submission> providerSubmission = providerSubmissionUtilities.getProviderSubmissionFromId(submissionRepositoryService, familySubmission);
    if (providerSubmission.isEmpty()) {
      return results;
    }

    var providerInputData = providerSubmission.get().getInputData();

    String providerCurrentlyLicensed = (String) providerInputData.getOrDefault("providerCurrentlyLicensed", "");
    if(providerCurrentlyLicensed.equals("true")) {
      String providerLicensedCareLocation = (String) providerInputData.getOrDefault("providerLicensedCareLocation", "");
      switch (providerLicensedCareLocation) {
        case "childCareCenter":
          results.put("providerType", new SingleField("providerType", "LICENSED_DAY_CARE_CENTER_760", null));
          break;
        case "childCareHome":
          results.put("providerType", new SingleField("providerType", "LICENSED_DAY_CARE_HOME_762", null));
          break;
        case "groupChildCareHome":
          results.put("providerType", new SingleField("providerType", "LICENSED_GROUP_DAY_CARE_HOME_763", null));
          break;
        default:
          break;
      }
    }

    if(providerCurrentlyLicensed.equals("false")){
      String providerLicenseExemptType = (String) providerInputData.getOrDefault("providerLicenseExemptType", "");
      if (providerLicenseExemptType.equals("License-exempt")) {
        results.put("providerType", new SingleField("providerType", "DAY_CARE_CENTER_EXEMPT_FROM_LICENSING_761", null));
      }

      if (providerLicenseExemptType.equals("Self")) {
        String providerLicenseExemptCareLocation = (String) providerInputData.getOrDefault("providerLicenseExemptCareLocation", "");
        String providerLicenseExemptRelationship = (String) providerInputData.getOrDefault("providerLicenseExemptRelationship", "");

        if(providerLicenseExemptCareLocation.equals("Providers home")) {
          if(providerLicenseExemptRelationship.equals("Relative")) {
            results.put("providerType", new SingleField("providerType", "CARE_BY_RELATIVE_IN_CHILD_CARE_PROVIDERS_HOME_765", null));
          }else{
            results.put("providerType", new SingleField("providerType", "CARE_BY_NON_RELATIVE_IN_CHILD_CARE_PROVIDERS_HOME_764", null));
          }
        }
        if(providerLicenseExemptCareLocation.equals("Childs home")){
          if(providerLicenseExemptRelationship.equals("Relative")) {
            results.put("providerType", new SingleField("providerType", "CARE_BY_RELATIVE_IN_CHILDS_HOME_767", null));
          }else{
            results.put("providerType", new SingleField("providerType", "CARE_BY_NON_RELATIVE_IN_CHILDS_HOME_766", null));
          }
        }
      }
    }
    return results;
  }
}
