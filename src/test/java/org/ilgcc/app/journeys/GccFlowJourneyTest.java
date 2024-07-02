package org.ilgcc.app.journeys;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import formflow.library.data.SubmissionRepository;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.awaitility.Awaitility.await;

@Slf4j
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
        // parent-comm-preference
        assertThat(testPage.getHeader()).isEqualTo("How do you prefer to get updates about your application?");
        testPage.selectRadio("parentContactPreferredCommunicationMethod", "email");
        testPage.clickContinue();
        // parent-contact-info
        assertThat(testPage.getTitle()).isEqualTo("Parent Contact Info");
        testPage.enter("parentContactEmail", "test@email.org");
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
        testPage.enter("parentPartnerSSN", "123-456-7890");
        testPage.clickContinue();

        // parent-partner-contact
        assertThat(testPage.getTitle()).isEqualTo("How can we contact them?");
        testPage.enter("parentPartnerPhoneNumber", "3333333333");
        testPage.clickContinue();
        // parent-partner-info-service
        assertThat(testPage.getTitle()).isEqualTo("Are they a service member?");
        testPage.selectRadio("parentPartnerIsServing", "No");
        testPage.selectRadio("parentPartnerInMilitaryReserveOrNationalGuard", "Yes");
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
        testPage.enter("childFirstName", "mugully");
        testPage.enter("childLastName", "glopklin");
        testPage.enter("childDateOfBirthMonth", "1");
        testPage.enter("childDateOfBirthDay", "1");
        testPage.enter("childDateOfBirthYear", "2022");
        testPage.selectRadio("needFinancialAssistanceForChild", "No");
        testPage.clickButton("Continue");
        testPage.clickButton("Add child");
        testPage.enter("childFirstName", "child");
        testPage.enter("childLastName", "mcchild");
        testPage.enter("childDateOfBirthMonth", "12");
        testPage.enter("childDateOfBirthDay", "25");
        testPage.enter("childDateOfBirthYear", "2018");
        testPage.selectRadio("needFinancialAssistanceForChild", "Yes");
        testPage.clickButton("Continue");
        //children-ccap-info
        assertThat(testPage.getTitle()).isEqualTo("CCAP Info");
        testPage.selectFromDropdown("childRelationship", "My child");
        testPage.selectRadio("childHasDisability", "No");
        testPage.selectRadio("childIsUsCitizen", "Yes");
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
        testPage.enter("childcareStartTimeThursday", "1000AM");
        testPage.enter("childcareEndTimeThursday", "0100PM");
        testPage.enter("childcareStartTimeFriday", "0900AM");
        testPage.enter("childcareEndTimeFriday", "1200PM");
        testPage.clickContinue();
        //children-ccap-child-other-ed
        assertThat(testPage.getTitle()).isEqualTo("CCAP Child Other");
        testPage.clickButton("Yes");
        //children-add (with children listed)
        assertThat(testPage.getTitle()).isEqualTo("Children add");
        List<String> li = testPage.getTextBySelector(".child-name");
        assertThat(li).containsExactly("mugully glopklin", "child mcchild");
        testPage.clickButton("That is all my children");

        //activities-parent-intro
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Intro");
        testPage.clickContinue();
        //activities-parent-type
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Type");
        testPage.clickElementById("activitiesParentChildcareReason-other");
        testPage.clickElementById("activitiesParentChildcareReason-SCHOOL");
        testPage.enter("activitiesParentChildcareReason_other", "test");
        testPage.clickElementById("activitiesParentPartnerChildcareReason-TANF_TRAINING");
        testPage.clickElementById("activitiesParentPartnerChildcareReason-WORKING");
        testPage.clickElementById("activitiesParentPartnerChildcareReason-SCHOOL");
        testPage.clickContinue();
        //activities-add-ed-program (client should be directed to this page if working is not checked)
        assertThat(testPage.getTitle()).isEqualTo("School or training program");
        testPage.goBack();
        assertThat(testPage.getTitle()).isEqualTo("Activities Parent Type");
        testPage.clickElementById("activitiesParentChildcareReason-WORKING");
        testPage.clickContinue();
        //activities-add-jobs
        assertThat(testPage.getTitle()).isEqualTo("Activities Add Jobs");
        testPage.clickButton("Add a job");
        //activities-employer-name
        assertThat(testPage.getTitle()).isEqualTo("Activities Employer Name");
        testPage.enter("companyName", "testCompany");
        testPage.clickContinue();
        //activities-employer-address
        assertThat(testPage.getTitle()).isEqualTo("Activities Employer Address");
        testPage.enter("employerPhoneNumber", "3333333");
        testPage.enter("employerCity", "Chicago");
        testPage.enter("employerStreetAddress", "123 Test Me");
        testPage.enter("employerState", "IL - Illinois");
        testPage.enter("employerZipCode", "6042");
        testPage.clickContinue();
        assertThat(testPage.hasErrorText("Make sure the phone number you entered includes 10 digits.")).isTrue();
        assertThat(testPage.hasErrorText("Make sure the zip code you entered follows the right format.")).isTrue();
        testPage.enter("employerPhoneNumber", "3333333333");
        testPage.enter("employerZipCode", "60423");
        testPage.clickContinue();
        //activities-self-employment
        assertThat(testPage.getTitle()).isEqualTo("Activities Self Employment");
        testPage.clickButton("Yes");

        //activities-work-schedule-vary
        assertThat(testPage.getTitle()).isEqualTo("Work schedule vary");
        testPage.clickButton("Yes");

        //activities-next-work-schedule
        assertThat(testPage.getTitle()).isEqualTo("Work Schedule");
        testPage.clickContinue();

        //activities-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Weekly Schedule");
        testPage.clickElementById("activitiesJobWeeklySchedule-Monday");
        testPage.clickElementById("activitiesJobWeeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-job-hourly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Job hourly schedule");
        testPage.enter("activitiesJobStartTimeMonday", "1200PM");
        testPage.enter("activitiesJobEndTimeMonday", "0100PM");
        testPage.enter("activitiesJobStartTimeSunday", "0200PM");
        testPage.enter("activitiesJobEndTimeSunday", "0300PM");
        testPage.clickContinue();

        //activities-work-commute-time
        assertThat(testPage.getTitle()).isEqualTo("Work commute time");
        testPage.selectFromDropdown("activitiesJobCommuteTime", "1 hour");
        testPage.clickContinue();

        //activities-add-jobs (list)
        assertThat(testPage.getTitle()).isEqualTo("Activities Add Jobs");
        testPage.clickButton("That is all my jobs");

        //activities-add-ed-program
        assertThat(testPage.getHeader()).isEqualTo("Tell us about your school or training program.");
        testPage.clickContinue();

        //activities-ed-program-type
        assertThat(testPage.getElementText("educationType-highSchoolOrGed-label")).isEqualTo("High School or GED");
        testPage.clickElementById("educationType-highSchoolOrGed-label");
        assertThat(testPage.getHeader()).isEqualTo("What type of school or training are you enrolled in?");
        testPage.clickContinue();

        //activities-ed-program-name
        testPage.enter("schoolName", "World Training Program");
        assertThat(testPage.getHeader()).isEqualTo("What is the school or training program name?*");
        testPage.clickContinue();

        //activities-ed-program-info
        assertThat(testPage.getTitle()).isEqualTo("School or training program");
        testPage.enter("phoneNumber", "(217) 123-1233");
        testPage.enter("streetAddress", "123 Main St");
        testPage.enter("city", "Springfield");
        testPage.enter("state", "IL - Illinois");
        testPage.enter("zipCode", "62629");
        testPage.clickContinue();

        //activities-ed-program-method
        assertThat(testPage.getTitle()).isEqualTo("Learning style");
        testPage.clickElementById("programTaught-Online-label");
        testPage.clickElementById("programSchedule-No-label");
        testPage.clickContinue();

        //activities-next-class-schedule
        assertThat(testPage.getHeader()).isEqualTo("Next, we'll ask about your class schedule.");
        testPage.clickContinue();

        //activities-class-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Weekly Class Schedule");
        testPage.clickElementById("weeklySchedule-Monday");
        testPage.clickElementById("weeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-class-hourly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Hourly Class Schedule");
        testPage.clickElementById("activitiesClassHoursSameEveryDay-Yes");
        testPage.enter("activitiesClassStartTimeAllDays", "0900AM");
        testPage.enter("activitiesClassEndTimeAllDays", "13:00");
        testPage.clickContinue();

        //activities-ed-program-dates
        assertThat(testPage.getTitle()).isEqualTo("Time of Program");
        testPage.enter("activitiesProgramStartYear", "2024");
        testPage.enter("activitiesProgramStartMonth", "12");
        testPage.enter("activitiesProgramStartDay", "10");

        testPage.enter("activitiesProgramEndYear", "2025");
        testPage.enter("activitiesProgramEndMonth", "02");
        testPage.enter("activitiesProgramEndDay", "12");
        testPage.clickContinue();
        //activities-partner-add-job
        assertThat(testPage.getTitle()).isEqualTo("Activities Partner Add Jobs");
        testPage.clickButton("Add a job");
        //activities-partner-employer-name
        assertThat(testPage.getTitle()).isEqualTo("Activities Partner Employer Name");
        testPage.enter("partnerCompanyName", "testPartnerCompany");
        testPage.clickContinue();
        //activities--partner-employer-address
        assertThat(testPage.getTitle()).isEqualTo("Activities Partner Employer Address");
        testPage.enter("partnerEmployerPhoneNumber", "4444");
        testPage.enter("partnerEmployerCity", "Oakland");
        testPage.enter("partnerEmployerStreetAddress", "123 Partner Employer Address");
        testPage.enter("partnerEmployerZipCode", "6042");
        testPage.clickContinue();
        assertThat(testPage.hasErrorText("Make sure the phone number you entered includes 10 digits.")).isTrue();
        assertThat(testPage.hasErrorText("Make sure the zip code you entered follows the right format.")).isTrue();
        testPage.enter("partnerEmployerPhoneNumber", "4333333333");
        testPage.enter("partnerEmployerZipCode", "92453");
        testPage.clickContinue();
        //activities-partner-self-employment
        assertThat(testPage.getTitle()).isEqualTo("Activities Partner Self Employment");
        testPage.clickButton("No");
        //activities-partner-work-schedule-vary
        assertThat(testPage.getTitle()).isEqualTo("Partner Work Schedule Varies");
        assertThat(testPage.getHeader()).isEqualTo("Do partner's work days or hours vary at this job?");
        testPage.clickButton("No");

        //activities-partner-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Partner Weekly Schedule");
        testPage.goBack();

        //activities-partner-work-schedule-vary
        assertThat(testPage.getTitle()).isEqualTo("Partner Work Schedule Varies");
        testPage.clickButton("Yes");

        //activities-partner-next-work-schedule
        assertThat(testPage.getTitle()).isEqualTo("Notice Partner Work Schedule Varies");
        assertThat(testPage.getHeader()).isEqualTo("Next, we'll ask about partner's work schedule");
        testPage.clickContinue();
        //activities-partner-job-weekly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Partner Weekly Schedule");
        testPage.clickElementById("activitiesJobWeeklySchedule-Monday");
        testPage.clickElementById("activitiesJobWeeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-partner-job-hourly-schedule
        assertThat(testPage.getTitle()).isEqualTo("Partner Hourly Schedule");
        testPage.enter("activitiesJobStartTimeMonday", "1200PM");
        testPage.enter("activitiesJobEndTimeMonday", "0100PM");
        testPage.enter("activitiesJobStartTimeSunday", "0200AM");
        testPage.enter("activitiesJobEndTimeSunday", "0300PM");
        testPage.clickContinue();

        //activities-partner-commute-time
        assertThat(testPage.getTitle()).isEqualTo("Activities Partner Commute Time");
        testPage.selectFromDropdown("activitiesJobCommuteTime", "1.5 hours");
        testPage.clickContinue();

        //activities-partner-add-job
        assertThat(testPage.getHeader()).isEqualTo("Does partner have any other jobs?");
        testPage.clickButton("That is all my jobs");
        // activities-partner-ed
        assertThat(testPage.getHeader()).isEqualTo("Now tell us about partner's school or training program.");
        testPage.clickContinue();
        testPage.clickElementById("partnerEducationType-twoYearCollege-label");
        assertThat(testPage.getHeader()).isEqualTo("What type of school or training is partner enrolled in?");
        testPage.clickContinue();
        testPage.enter("partnerProgramName", "World University");
        assertThat(testPage.getHeader()).isEqualTo("What is the school or training program name?*");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("School or training program");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("Learning style");
        testPage.clickElementById("partnerProgramTaught-In-Person-label");
        testPage.clickElementById("partnerProgramSchedule-Yes-label");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("Weekly Class Schedule");
        testPage.clickElementById("partnerClassWeeklySchedule-Monday");
        testPage.clickElementById("partnerClassWeeklySchedule-Wednesday");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("Hourly Class Schedule");
        testPage.enter("partnerClassStartTimeMonday", "1100AM");
        testPage.enter("partnerClassEndTimeMonday", "16:00");
        testPage.enter("partnerClassStartTimeWednesday", "14:00");
        testPage.enter("partnerClassEndTimeWednesday", "15:00");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("Time of Program");
        testPage.clickContinue();

        //unearned-income-intro
        assertThat(testPage.getTitle()).isEqualTo("Unearned Income Intro");
        testPage.clickContinue();

        //unearned-income-source
        assertThat(testPage.getTitle()).isEqualTo("Income Source");
        testPage.clickElementById("unearnedIncomeSource-ROYALTIES-label");
        testPage.clickElementById("unearnedIncomeSource-RENTAL-label");
        testPage.clickElementById("unearnedIncomeSource-UNEMPLOYMENT-label");
        testPage.clickContinue();

        //unearned-income-amount
        assertThat(testPage.getTitle()).isEqualTo("Unearned Income Amount");
        testPage.enter("unearnedIncomeRoyalties", "35");
        testPage.enter("unearnedIncomeUnemployment", "175");
        testPage.clickContinue();

        //unearned-income-assets
        assertThat(testPage.getTitle()).isEqualTo("Unearned Income Assets");
        testPage.clickElementById("unearnedIncomeAssetsMoreThanOneMillionDollars-true");
        //unearned-income-child-support
        assertThat(testPage.getTitle()).isEqualTo("Child Support");
        testPage.clickElementById("doesAnyoneInHouseholdPayChildSupport-true");
        //unearned-income-child-support-amount
        assertThat(testPage.getTitle()).isEqualTo("Child Support Paid");
        testPage.enter("amountYourHouseholdPaysInChildSupport", "1453");
        testPage.clickContinue();
        //unearned-income-programs
        assertThat(testPage.getTitle()).isEqualTo("Unearned Income Programs");
        assertThat(testPage.getHeader()).isEqualTo("Does anyone in your household participate in any of these programs?");
        testPage.clickElementById("unearnedIncomePrograms-CASH_ASSISTANCE");
        testPage.clickElementById("unearnedIncomePrograms-SNAP");
        testPage.clickContinue();

        // submit-intro
        assertThat(testPage.getTitle()).isEqualTo("Sign and Submit");
        testPage.clickContinue();

        // submit-ccap-terms
        assertThat(testPage.getTitle()).isEqualTo("Parent Agreement and Terms");
        testPage.clickElementById("agreesToLegalTerms-true");
        testPage.clickContinue();

        // submit-sign-name
        assertThat(testPage.getTitle()).isEqualTo("Sign Application");
        testPage.enter("signedName", "parent first parent last");
        testPage.enter("partnerSignedName", "partner parent");
        testPage.clickContinue();

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo("Your application has been submitted!");
        testPage.clickButton("Submit documents now");

        // doc-upload-recommended docs
        assertThat(testPage.getTitle()).isEqualTo("Recommended documents");
        testPage.clickButton("Submit documents now");

        // doc-upload-add-files
        assertThat(testPage.getTitle()).isEqualTo("Add Files");
        assertThat(testPage.findElementById("form-submit-button").getAttribute("class").contains("display-none")).isTrue();
        uploadJpgFile();
        // The submit button is hidden unless a file has been uploaded. The await gives the system time to remove the "display-none" class.
        await().atMost(5, TimeUnit.SECONDS).until(
                () -> !(testPage.findElementById("form-submit-button").getAttribute("class").contains("display-none"))
        );

        testPage.clickButton("I'm finished uploading");
        assertThat(testPage.getTitle()).isEqualTo("Submit Confirmation");

        testPage.goBack();
        testPage.goBack();
        testPage.clickButton("Skip and finish");

        // submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo("Next Steps");
        testPage.clickButton("Finish application");

        // submit-confirmation
        assertThat(testPage.getTitle()).startsWith("Your application has been sent to");
        testPage.clickElementById("surveyDifficulty-very-easy");
        testPage.clickButton("Submit feedback");
        assertThat(testPage.getTitle()).startsWith("Your application has been sent to");
        assertThat(testPage.getCssSelectorText(".notice--success")).isEqualTo("Thank you for your feedback.");

        // Download PDF and verify fields
        verifyPDF();
    }

    /**
     * This compares the pdf fields in the generated pdf and our expected test pdf, "test_filled_ccap.pdf".
     * If there are updates to the template pdf (used to generate the client pdf), the test pdf should be updated to have the expected fields and values.
     */
    private void verifyPDF() throws IOException {
        File pdfFile = getDownloadedPDF();

//         regenerateExpectedPDF(pdfFile); // uncomment and run test to regenerate the test pdf

        try (FileInputStream actualIn = new FileInputStream(pdfFile);
            PdfReader actualReader = new PdfReader(actualIn);
            FileInputStream expectedIn = new FileInputStream("src/test/resources/output/test_filled_ccap.pdf");
            PdfReader expectedReader = new PdfReader(expectedIn)) {
            AcroFields actualAcroFields = actualReader.getAcroFields();
            AcroFields expectedAcroFields = expectedReader.getAcroFields();

            // Get all failures at once and log them
            List<String> missMatches = getMissMatches(expectedAcroFields, actualAcroFields);

            // Do actual assertions
            for (String expectedField : missMatches) {
                var actual = actualAcroFields.getField(expectedField);
                var expected = expectedAcroFields.getField(expectedField);
                assertThat(actual)
                        .withFailMessage("Expected %s to be %s but was %s".formatted(expectedField, expected, actual))
                        .isEqualTo(expected);
            }
            assertThat(actualAcroFields.getAllFields().size()).isEqualTo(expectedAcroFields.getAllFields().size());
        } catch (IOException e) {
            fail("Failed to generate PDF: %s", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * If there are changes to the expected PDF, it can be regenerated with this method.
     */
    private static void regenerateExpectedPDF(File pdfFile) {
        try (FileInputStream regeneratedPDF = new FileInputStream(pdfFile);
             FileOutputStream testPDF = new FileOutputStream("src/test/resources/output/test_filled_ccap.pdf")) {
            testPDF.write(regeneratedPDF.readAllBytes());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @NotNull
    private static List<String> getMissMatches(AcroFields expectedAcroFields, AcroFields actualAcroFields) {
        List<String> missMatches = new ArrayList<>();
//        These fields are dynamic and untestable with the current PDF approach
        List<String> UNTESTABLE_FIELDS = List.of("PARTNER_SIGNATURE_DATE", "APPLICANT_SIGNATURE_DATE");
        for (String expectedField : expectedAcroFields.getAllFields().keySet()) {
            if(!UNTESTABLE_FIELDS.contains(expectedField)){
                var actual = actualAcroFields.getField(expectedField);
                var expected = expectedAcroFields.getField(expectedField);
                if (!expected.equals(actual)) {
                    missMatches.add(expectedField);
                    log.info("Expected %s to be %s but was %s".formatted(expectedField, expected, actual));
                }
            }
        }
        return missMatches;
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
