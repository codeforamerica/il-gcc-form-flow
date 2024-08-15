package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

public class MailingAddressPreparerTest {
  private final MailingAddressPreparer preparer = new MailingAddressPreparer();

  private Submission submission;
  @Test
  public void shouldMapNonValidatedMailingAddressToPdf(){
    submission = new SubmissionTestBuilder()
        .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
        .build();
    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
    assertThat(result.get("parentMailingAddressForPDFStreet")).isEqualTo(new SingleField("parentMailingAddressForPDFStreet", "972 Mission St", null));
    assertThat(result.get("parentMailingAddressForPDFApt")).isEqualTo(new SingleField("parentMailingAddressForPDFApt", "5", null));
    assertThat(result.get("parentMailingAddressForPDFCity")).isEqualTo(new SingleField("parentMailingAddressForPDFCity", "San Francisco", null));
    assertThat(result.get("parentMailingAddressForPDFState")).isEqualTo(new SingleField("parentMailingAddressForPDFState", "CA", null));
    assertThat(result.get("parentMailingAddressForPDFZipCode")).isEqualTo(new SingleField("parentMailingAddressForPDFZipCode", "94103", null));
  }
  @Test
  public void shouldNotMapSuggestedMailingAddressToPDFIfClientDoesNotChooseUseSuggestedAddress(){
    submission = new SubmissionTestBuilder()
        .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
        .with("useSuggestedParentAddress", "false")
        .withValidatedMailingAddress("972 Mission St [Validated]", "5 [Validated]", "San Francisco [Validated]", "CA [Validated]", "94103 [Validated]")
        .build();
    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
    assertThat(result.get("parentMailingAddressForPDFStreet")).isEqualTo(new SingleField("parentMailingAddressForPDFStreet", "972 Mission St", null));
    assertThat(result.get("parentMailingAddressForPDFApt")).isEqualTo(new SingleField("parentMailingAddressForPDFApt", "5", null));
    assertThat(result.get("parentMailingAddressForPDFCity")).isEqualTo(new SingleField("parentMailingAddressForPDFCity", "San Francisco", null));
    assertThat(result.get("parentMailingAddressForPDFState")).isEqualTo(new SingleField("parentMailingAddressForPDFState", "CA", null));
    assertThat(result.get("parentMailingAddressForPDFZipCode")).isEqualTo(new SingleField("parentMailingAddressForPDFZipCode", "94103", null));
  }
  @Test
  public void shouldMapSuggestedMailingAddressToMailingAddressFieldsOfPDFIfClientChoosesToUseSuggestedAddress(){
    submission = new SubmissionTestBuilder()
        .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
        .with("useSuggestedParentAddress", "true")
        .withValidatedMailingAddress("972 Mission St [Validated]", "5 [Validated]", "San Francisco [Validated]", "CA [Validated]", "94103 [Validated]")
        .build();
    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
    assertThat(result.get("parentMailingAddressForPDFStreet")).isEqualTo(new SingleField("parentMailingAddressForPDFStreet", "972 Mission St [Validated]", null));
    assertThat(result.get("parentMailingAddressForPDFApt")).isEqualTo(new SingleField("parentMailingAddressForPDFApt", "", null));
    assertThat(result.get("parentMailingAddressForPDFCity")).isEqualTo(new SingleField("parentMailingAddressForPDFCity", "San Francisco [Validated]", null));
    assertThat(result.get("parentMailingAddressForPDFState")).isEqualTo(new SingleField("parentMailingAddressForPDFState", "CA [Validated]", null));
    assertThat(result.get("parentMailingAddressForPDFZipCode")).isEqualTo(new SingleField("parentMailingAddressForPDFZipCode", "94103 [Validated]", null));
  }
  @Test
  public void shouldMapStreetAddress2ToAnEmptyStringWhenASuggestedAddressIsUsed(){
    submission = new SubmissionTestBuilder()
        .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
        .withHomeAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
        .with("useSuggestedParentAddress", "true")
        .with("parentMailingAddressSameAsHomeAddress[]", List.of("yes"))
        .withValidatedMailingAddress("972 Mission St [Validated]", "5 [Validated]", "San Francisco [Validated]", "CA [Validated]", "94103 [Validated]")
        .build();
    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("parentHomeStreetAddress1ForPDF")).isEqualTo(new SingleField("parentHomeStreetAddress1ForPDF", "972 Mission St [Validated]", null));
    assertThat(result.get("parentHomeStreetAddress2ForPDF")).isEqualTo(new SingleField("parentHomeStreetAddress2ForPDF", "", null));
    assertThat(result.get("parentMailingAddressForPDFStreet")).isEqualTo(new SingleField("parentMailingAddressForPDFStreet", "972 Mission St [Validated]", null));
    assertThat(result.get("parentMailingAddressForPDFApt")).isEqualTo(new SingleField("parentMailingAddressForPDFApt", "", null));

  }

  @Test
  public void shouldMapSuggestedMailingAddressToHomeAddressIfClientSelectsHomeAddressIsSameAsMailingAddressAndChoosesToUseSuggestedAddress(){
    submission = new SubmissionTestBuilder()
        .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
        .with("useSuggestedParentAddress", "true")
        .with("parentMailingAddressSameAsHomeAddress[]", List.of("yes"))
        .withHomeAddress("Home Street", "10","Home_city", "HomeState", "11111")
        .withValidatedMailingAddress("972 Mission St [Validated]", "5 [Validated]", "San Francisco [Validated]", "CA [Validated]", "94103 [Validated]")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("parentHomeStreetAddress1ForPDF")).isEqualTo(new SingleField("parentHomeStreetAddress1ForPDF", "972 Mission St [Validated]", null));
    assertThat(result.get("parentHomeCityForPDF")).isEqualTo(new SingleField("parentHomeCityForPDF", "San Francisco [Validated]", null));
    assertThat(result.get("parentHomeStateForPDF")).isEqualTo(new SingleField("parentHomeStateForPDF", "CA [Validated]", null));
    assertThat(result.get("parentHomeZipCodeForPDF")).isEqualTo(new SingleField("parentHomeZipCodeForPDF", "94103 [Validated]", null));
  }
  @Test
  public void shouldMapMailingAddressToHomeAddressIfClientSelectsHomeAddressIsSameAsMailingAddressAndDoesNotChooseToUseSuggestedAddress(){
    submission = new SubmissionTestBuilder()
        .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
        .with("useSuggestedParentAddress", "false")
        .with("parentMailingAddressSameAsHomeAddress[]", List.of("yes"))
        .withHomeAddress("Home Street", "10","Home_city", "HomeState", "11111")
        .withValidatedMailingAddress("972 Mission St [Validated]", "5 [Validated]", "San Francisco [Validated]", "CA [Validated]", "94103 [Validated]")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("parentHomeStreetAddress1ForPDF")).isEqualTo(new SingleField("parentHomeStreetAddress1ForPDF", "972 Mission St", null));
    assertThat(result.get("parentHomeStreetAddress2ForPDF")).isEqualTo(new SingleField("parentHomeStreetAddress2ForPDF", "5", null));
    assertThat(result.get("parentHomeCityForPDF")).isEqualTo(new SingleField("parentHomeCityForPDF", "San Francisco", null));
    assertThat(result.get("parentHomeStateForPDF")).isEqualTo(new SingleField("parentHomeStateForPDF", "CA", null));
    assertThat(result.get("parentHomeZipCodeForPDF")).isEqualTo(new SingleField("parentHomeZipCodeForPDF", "94103", null));
  }
  @Test
  public void shouldMapToHomeAddressIfClientDoesNotChooseHomeAddressIsSameAsMailingAddress(){
    submission = new SubmissionTestBuilder()
        .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
        .with("useSuggestedParentAddress", "false")
        .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
        .withHomeAddress("Home Street", "10","Home_city", "HomeState", "11111")
        .withValidatedMailingAddress("972 Mission St [Validated]", "5 [Validated]", "San Francisco [Validated]", "CA [Validated]", "94103 [Validated]")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("parentHomeStreetAddress1ForPDF")).isEqualTo(new SingleField("parentHomeStreetAddress1ForPDF", "Home Street", null));
    assertThat(result.get("parentHomeStreetAddress2ForPDF")).isEqualTo(new SingleField("parentHomeStreetAddress2ForPDF", "10", null));
    assertThat(result.get("parentHomeCityForPDF")).isEqualTo(new SingleField("parentHomeCityForPDF", "Home_city", null));
    assertThat(result.get("parentHomeStateForPDF")).isEqualTo(new SingleField("parentHomeStateForPDF", "HomeState", null));
    assertThat(result.get("parentHomeZipCodeForPDF")).isEqualTo(new SingleField("parentHomeZipCodeForPDF", "11111", null));
  }
}