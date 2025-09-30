package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.Map;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
        classes = IlGCCApplication.class,
        properties = "il-gcc.enable-multiple-providers=true"

)
@ActiveProfiles("test")
public class EmployerStartDatePreparerTest {
  private final EmployerStartDatePreparer preparer = new EmployerStartDatePreparer();

  private Submission submission;

  @Test
  public void activitiesJobStartDateIsCompleteWhenValidMonthDayAndValidYearAreProvided() {
    submission = new SubmissionTestBuilder()
        .addJobWithStartDate("09", "10", "2024")
        .addJobWithStartDate("1", "8", "2010")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("activitiesJobStart_1")).isEqualTo(new SingleField("activitiesJobStart", "10/09/2024", 1));
    assertThat(result.get("activitiesJobStart_2")).isEqualTo(new SingleField("activitiesJobStart", "08/01/2010", 2));
  }

  @Test
  public void partnerJobStartDateIsCompleteWhenValidMonthDayAndValidYearAreProvided(){
    submission = new SubmissionTestBuilder()
        .addPartnerJobWithStartDate("09", "10", "2024")
        .addPartnerJobWithStartDate("1", "8", "2010")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("activitiesPartnerJobStart_1")).isEqualTo(new SingleField("activitiesPartnerJobStart", "10/09/2024", 1));
    assertThat(result.get("activitiesPartnerJobStart_2")).isEqualTo(new SingleField("activitiesPartnerJobStart", "08/01/2010", 2));

  }

  @Test
  public void jobStartDatesArePrintedIfMonthAndDayArePresent(){
    submission = new SubmissionTestBuilder()
        .addJobWithStartDate("", "10", "2024")
        .addPartnerJobWithStartDate("", "5", "1922")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("activitiesJobStart_1")).isEqualTo(new SingleField("activitiesJobStart", "10/2024", 1));
    assertThat(result.get("activitiesPartnerJobStart_1")).isEqualTo(new SingleField("activitiesPartnerJobStart", "5/1922", 1));
  }
}