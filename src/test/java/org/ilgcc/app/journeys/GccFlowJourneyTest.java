package org.ilgcc.app.journeys;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import formflow.library.data.SubmissionRepository;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.awaitility.Awaitility.await;

public class GccFlowJourneyTest extends AbstractBasePageTest {

  @Autowired
  SubmissionRepository repository;

  @Test
  void fullGccFlow() throws IOException {
    // Home page
    assertThat(testPage.getTitle()).isEqualTo("Get help paying for child care.");
    testPage.clickButton("Apply now");
    // onboarding-getting-started
    assertThat(testPage.getTitle()).isEqualTo("Getting started");
    testPage.clickContinue();
    //onboarding-choose-provider
    assertThat(testPage.getTitle()).isEqualTo("Choose Provider");
    testPage.clickElementById("dayCareChoice-OPEN_SESAME-label");
    testPage.clickContinue();
    //onboarding-confirm-provider
    assertThat(testPage.getTitle()).isEqualTo("Confirm provider");
    testPage.clickLink("Yes, I confirm");
    //onboarding-language-preference
    assertThat(testPage.getTitle()).isEqualTo("Language Preference");
    testPage.selectFromDropdown("languageRead", "English");
    testPage.selectFromDropdown("languageSpeak", "Español");
    testPage.clickContinue();

    // parent-info-intro
    assertThat(testPage.getTitle()).isEqualTo("Parent and Guardian Information");
    testPage.clickContinue();
    // parent-info-basic-1
    assertThat(testPage.getTitle()).isEqualTo("Tell us about yourself");
    testPage.enter("parentFirstName", "parent first");
    testPage.enter("parentLastName", "parent last");
    testPage.enter("parentBirthMonth", "12");
    testPage.enter("parentBirthDay", "25");
    testPage.enter("parentBirthYear", "1985");
    testPage.clickContinue();
    // parent-info-basic-2
    assertThat(testPage.getTitle()).isEqualTo("Parent info basic");
    testPage.clickContinue();
    // parent-info-service
    assertThat(testPage.getTitle()).isEqualTo("Parent info service");
    testPage.clickContinue();
    // parent-home-address
    assertThat(testPage.getTitle()).isEqualTo("What is your home address?");
    testPage.enter("parentHomeStreetAddress1", "972 Mission St");
    testPage.enter("parentHomeStreetAddress2", "5th floor");
    testPage.enter("parentHomeCity", "San Francisco");
    testPage.selectFromDropdown("parentHomeState", "CA - California");
    testPage.enter("parentHomeZipCode", "94103");
    testPage.clickContinue();
    // parent-mailing-address
    assertThat(testPage.getTitle()).isEqualTo("Parent Mailing Address");
    testPage.goBack();
    //parent-home-address
    assertThat(testPage.getTitle()).isEqualTo("What is your home address?");
    testPage.clickElementById("parentHomeExperiencingHomelessness-yes");
    testPage.clickContinue();
    //parent-home-address
    assertThat(testPage.getTitle()).isEqualTo("Parent");
    testPage.clickButton("I have a place to get mail");
    // parent-mailing-address
    assertThat(testPage.getTitle()).isEqualTo("Parent Mailing Address");
    testPage.enter("parentMailingStreetAddress1", "972 Mission St");
    testPage.enter("parentMailingStreetAddress2", "5th floor");
    testPage.enter("parentMailingCity", "San Francisco");
    testPage.selectFromDropdown("parentMailingState", "CA - California");
    testPage.enter("parentMailingZipCode", "94103");
    testPage.clickContinue();
    // parent-confirm-address
    assertThat(testPage.getHeader()).isEqualTo("Confirm your address");
    testPage.clickButton("Use this address");
    // parent-contact
    assertThat(testPage.getTitle()).isEqualTo("Parent Contact");
    testPage.clickElementById("parentContactPreferCommunicate-mail-label");
    assertThat(testPage.getElementText("parentContactPreferCommunicate-mail-label")).isEqualTo(
        "It's okay to send me mail about my case.");
    testPage.clickContinue();

    //parent-info-review
    assertThat(testPage.getTitle()).isEqualTo("Review Info");
    testPage.clickContinue();

    assertThat(testPage.getHeader()).isEqualTo("Do you have a partner or spouse?");
    testPage.clickButton("Yes");
    // parent-qualifying-partner
    assertThat(testPage.getHeader()).isEqualTo("Your partner or spouse");
    testPage.selectRadio("parentSpouseIsStepParent", "Yes");
    testPage.selectRadio("parentSpouseShareChildren", "Yes");
    testPage.selectRadio("parentSpouseLiveTogether", "Yes");
    testPage.clickContinue();
    //parent-partner-info-basic
    assertThat(testPage.getHeader()).isEqualTo("Tell us about your partner");
    testPage.enter("parentPartnerFirstName", "partner");
    testPage.enter("parentPartnerLastName", "parent");
    testPage.enter("parentPartnerBirthMonth", "12");
    testPage.enter("parentPartnerBirthDay", "25");
    testPage.enter("parentPartnerBirthYear", "2018");
    testPage.clickContinue();

    // parent-partner-contact
    assertThat(testPage.getTitle()).isEqualTo("How can we contact them?");
    testPage.clickContinue();
    // parent-partner-info-service
    assertThat(testPage.getTitle()).isEqualTo("Are they a service member?");
    testPage.clickContinue();
    // parent-partner-info-disability
    assertThat(testPage.getTitle()).isEqualTo("Do they have a disability?");
    testPage.clickButton("Yes");
    // parent-other-family
    assertThat(testPage.getHeader()).isEqualTo("Do you live with any other adult family members who you financially support?");
    testPage.clickButton("Yes");
    // parent-add-adults
    assertThat(testPage.getHeader()).isEqualTo("Add adult family members who you financially support.");
    testPage.clickButton("Add family member");
    // parent-add-adult-details
    assertThat(testPage.getHeader()).isEqualTo("Add family member");
    testPage.enter("adultDependentFirstName", "ada");
    testPage.enter("adultDependentLastName", "dolt");
    testPage.clickContinue();
    // delete-person
    testPage.clickLink("delete");
    assertThat(testPage.getTitle()).isEqualTo("Delete person");
    testPage.clickButton("Yes, delete");
    testPage.clickButton("Add family member");
    testPage.enter("adultDependentFirstName", "adaa");
    testPage.enter("adultDependentLastName", "doltt");
    testPage.clickContinue();
    testPage.clickButton("I'm done");
    // parent-intro-family-info
    assertThat(testPage.getTitle()).isEqualTo("Parent Intro Family Info");
    testPage.clickButton("Continue to next section");
    //children-info-intro
    assertThat(testPage.getTitle()).isEqualTo("Your Children");
    testPage.clickContinue();
    // children-add
    assertThat(testPage.getTitle()).isEqualTo("Children add");
    testPage.clickButton("Add child");
    //children-info-basic
    assertThat(testPage.getTitle()).isEqualTo("Children Info");
    testPage.enter("childFirstName", "child");
    testPage.enter("childLastName", "mcchild");
    testPage.enter("childDateOfBirthMonth", "12");
    testPage.enter("childDateOfBirthDay", "25");
    testPage.enter("childDateOfBirthYear", "2018");
    testPage.selectRadio("needFinancialAssistanceForChild", "Yes");
    testPage.clickButton("Continue");
    //children-ccap-info
    assertThat(testPage.getTitle()).isEqualTo("CCAP Info");
    testPage.clickContinue();
    //children-ccap-in-care
    assertThat(testPage.getTitle()).isEqualTo("CCAP in care");
    testPage.clickButton("No");
    //children-ccap-start-date (Test No logic)
    assertThat(testPage.getTitle()).isEqualTo("CCAP Start Date");
    assertThat((testPage.getHeader())).isEqualTo("When will child start care at your chosen provider?");
    testPage.goBack();
    //children-ccap-start-date (Test Yes Logic)
    testPage.clickButton("Yes");
    assertThat(testPage.getTitle()).isEqualTo("CCAP Start Date");
    assertThat((testPage.getHeader())).isEqualTo("When did child start care at your chosen provider?");
    testPage.enter("ccapStartMonth", "11");
    testPage.enter("ccapStartDay", "1");
    testPage.enter("ccapStartYear", "2010");
    testPage.clickContinue();
    //children-ccap-weekly-schedule
    assertThat(testPage.getTitle()).isEqualTo("CCAP Childcare Weekly Schedule");
    testPage.clickElementById("childcareWeeklySchedule-Thursday");
    testPage.clickElementById("childcareWeeklySchedule-Friday");
    testPage.clickContinue();
    //children-childcare-hourly-schedule
    assertThat(testPage.getTitle()).isEqualTo("CCAP Childcare Hourly Schedule");
    testPage.clickContinue();
    //children-ccap-child-other-ed
    assertThat(testPage.getTitle()).isEqualTo("CCAP Child Other");
    testPage.clickButton("Yes");
    //children-add (with children listed)
    assertThat(testPage.getTitle()).isEqualTo("Children add");
    List<String> li = testPage.getTextBySelector(".child-name");
    assertThat(li).containsExactly("child mcchild");
    testPage.clickButton("That is all my children");

    //activities-add-ed-program
    assertThat(testPage.getTitle()).isEqualTo("Tell us about your school or training program.");
    testPage.clickContinue();

    //activities-ed-program-type
    assertThat(testPage.getElementText("educationType-highSchool-label")).isEqualTo("High School or GED");
    testPage.clickElementById("educationType-highSchool-label");
    assertThat(testPage.getTitle()).isEqualTo("What type of school or training are you enrolled in?");
    testPage.clickContinue();

    //activities-ed-program-name
    testPage.enter("schoolName", "World");
    assertThat(testPage.getTitle()).isEqualTo("What is the school or training program name?*");
    testPage.clickContinue();

    //activities-ed-program-info
    assertThat(testPage.getTitle()).isEqualTo("Program information");
    testPage.clickContinue();

    //activities-ed-program-method
    assertThat(testPage.getTitle()).isEqualTo("How is the program taught?");
    testPage.clickElementById("programTaught-Online-label");
    testPage.clickElementById("programSchedule-No-label");
    testPage.clickContinue();

    //activities-next-class-schedule
    assertThat(testPage.getTitle()).isEqualTo("Next, we'll ask about your class schedule.");
    testPage.clickContinue();

    //activities-class-weekly-schedule
    assertThat(testPage.getTitle()).isEqualTo("Weekly Class Schedule");
    testPage.clickElementById("weeklySchedule-Monday");
    testPage.clickContinue();

    //activities-class-hourly-schedule
    assertThat(testPage.getTitle()).isEqualTo("Hourly Class Schedule");
    assertThat(testPage.getElementText("activitiesClassHoursSameEveryDay-Yes-label")).isEqualTo("My class hours are the same every day.");
    testPage.clickContinue();

    //activities-ed-program-dates
    assertThat(testPage.getTitle()).isEqualTo("Time of Program");
    testPage.clickContinue();
    //unearned-income-intro
    assertThat(testPage.getTitle()).isEqualTo("Unearned Income Intro");
    testPage.clickContinue();

    //unearned-income-source
    assertThat(testPage.getTitle()).isEqualTo("Income Source");
    testPage.clickElementById("unearnedIncomeSource-ROYALTIES-label");
    testPage.clickContinue();

    //unearned-income-assets
    assertThat(testPage.getTitle()).isEqualTo("Unearned Income Assets");
    testPage.clickElementById("unearnedIncomeAssetsMoreThanOneMillionDollars-true");
    //unearned-income-programs
    assertThat(testPage.getTitle()).isEqualTo("Unearned Income Programs");
    assertThat(testPage.getHeader()).isEqualTo("Does anyone in your household participate in any of these programs?");
    testPage.clickElementById("unearnedIncomePrograms-CASH_ASSISTANCE");
    testPage.clickElementById("unearnedIncomePrograms-SNAP");

    // Download PDF and verify fields
    verifyPDF();
  }

  private void verifyPDF() throws IOException {
    File pdfFile = getDownloadedPDF();
    try (FileInputStream actualIn = new FileInputStream(pdfFile);
         PdfReader actualReader = new PdfReader(actualIn);
         FileInputStream expectedIn = new FileInputStream("src/test/resources/output/test_filled_ccap.pdf");
         PdfReader expectedReader = new PdfReader(expectedIn)) {
      AcroFields actualAcroFields = actualReader.getAcroFields();
      AcroFields expectedAcroFields = expectedReader.getAcroFields();

      assertThat(actualAcroFields.getAllFields().size()).isEqualTo(expectedAcroFields.getAllFields().size());
      for (String expectedField : expectedAcroFields.getAllFields().keySet()) {
        assertThat(actualAcroFields.getField(expectedField)).isEqualTo(expectedAcroFields.getField(expectedField));
      }
    } catch (IOException e) {
      fail("Failed to generate PDF: %s", e);
      throw new RuntimeException(e);
    }
  }

  private File getDownloadedPDF() throws IOException {
    // There should only be one
    String downloadUrl = repository.findAll().stream()
        .findFirst()
        .map(submission -> "%s/download/gcc/%s".formatted(baseUrl, submission.getId()))
        .orElseThrow(() -> new RuntimeException("Couldn't get url for pdf download"));
    driver.get(downloadUrl);
    await().until(pdfDownloadCompletes());
    return getLatestDownloadedFile(path);
  }
}
