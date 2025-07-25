package org.ilgcc.app.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.awaitility.Awaitility.await;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.google.common.collect.Iterables;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.data.UserFileRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.TransmissionRepository;
import org.ilgcc.app.data.importer.FakeResourceOrganizationAndCountyData;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Slf4j
@Import({WebDriverConfiguration.class, FakeResourceOrganizationAndCountyData.class})
@TestPropertySource(properties = {
        "ACTIVE_CASELOAD_CODES=BB,QQ"
})
@ActiveProfiles({"test", "selenium-test"})
public abstract class AbstractBasePageTest {

    private static final String UPLOADED_JPG_FILE_NAME = "test.jpeg";

    @Autowired
    protected RemoteWebDriver driver;

    @Autowired
    protected SubmissionRepository repo;

    @Autowired
    protected SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    protected UserFileRepository userFileRepository;

    @Autowired
    TransmissionRepository transmissionRepository;

    @Autowired
    protected Path path;

    @Autowired
    MessageSource messageSource;
    protected String baseUrl;

    @LocalServerPort
    protected String localServerPort;

    protected Page testPage;

    // These fields are dynamic and untestable with the current PDF approach
    protected List<String> UNTESTABLE_FIELDS = List.of(
            "PARTNER_SIGNATURE_DATE",
            "APPLICANT_SIGNATURE_DATE",
            "APPLICANT_NAME_FULL",
            "RECEIVED_TIMESTAMP",
            "APPLICATION_CONFIRMATION_CODE",
            "PROVIDER_SIGNATURE_DATE");

    public Submission getSessionSubmission() {
        // We're hoping that there's only one submission per session
        // If 0 or >1, an error will be thrown
        return Iterables.getOnlyElement(repo.findAll());
    }

    public SubmissionTestBuilder getSessionSubmissionTestBuilder() {
        return new SubmissionTestBuilder(getSessionSubmission());
    }

    public Submission saveSubmission(Submission submission) {
        Submission s = repo.save(submission);
        if (s.getShortCode() == null) {
            submissionRepositoryService.generateAndSetUniqueShortCode(s);
        }
        return s;
    }

    @BeforeEach
    protected void setUp() throws IOException {
        initTestPage();
        baseUrl = "http://localhost:%s".formatted(localServerPort);

        driver.navigate().to(baseUrl);
    }

    @AfterEach
    protected void clearSubmissions() {
        userFileRepository.deleteAll();
        transmissionRepository.deleteAll();
        repo.deleteAll();
    }

    public String getEnMessage(String key) {
        return messageSource.getMessage(key, null, Locale.ENGLISH);
    }

    public String getEnMessageWithParams(String key, Object[] args) {
        return messageSource.getMessage(key, args, Locale.ENGLISH);
    }

    public String getRequiredEnMessage(String key) {
        return String.format("%s %s", getEnMessage(key), getEnMessage("general.required-field"));
    }

    public String getRequiredEnMessageWithParams(String key, Object[] args) {
        return String.format("%s %s", getEnMessageWithParams(key, args), getEnMessage("general.required-field"));
    }

    protected void initTestPage() {
        testPage = new Page(driver, localServerPort);
    }

    @SuppressWarnings("unused")
    public void takeSnapShot(String fileWithPath) {
        TakesScreenshot screenshot = driver;
        Path sourceFile = screenshot.getScreenshotAs(OutputType.FILE).toPath();
        Path destinationFile = new File(fileWithPath).toPath();
        try {
            Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void uploadFile(String filepath, String dzName) {
        testPage.clickElementById("drag-and-drop-box-" + dzName); // is this needed?
        WebElement upload = driver.findElement(By.className("dz-hidden-input"));
        upload.sendKeys(TestUtils.getAbsoluteFilepathString(filepath));
        await().until(
                () -> !driver.findElements(By.className("file-details")).get(0).getAttribute("innerHTML")
                        .isBlank());
    }

    protected void uploadFile(String filepath) {
        WebElement upload = driver.findElement(By.className("dz-hidden-input"));
        upload.sendKeys(TestUtils.getAbsoluteFilepathString(filepath));
        waitUntilFileIsUploaded();
    }

    protected void uploadJpgFile() {
        uploadFile(UPLOADED_JPG_FILE_NAME);
        assertThat(driver.findElement(By.id("file-preview-template-uploadDocuments")).getText().replace("\n", ""))
                .contains(UPLOADED_JPG_FILE_NAME);
    }

    protected void uploadJpgFile(String dzName) {
        uploadFile(UPLOADED_JPG_FILE_NAME, dzName);
        assertThat(driver.findElement(By.id("dropzone-" + dzName)).getText().replace("\n", ""))
                .contains(UPLOADED_JPG_FILE_NAME);
        // The submit button is hidden unless a file has been uploaded. The await gives the system time to remove the "display-none" class.
        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> !(testPage.findElementById("form-submit-button").getAttribute("class").contains("display-none")));
    }

    private void waitUntilFileIsUploaded() {
        await().until(
                () -> !driver.findElements(By.className("file-details")).get(0).getAttribute("innerHTML")
                        .isBlank());
    }

    protected File getLatestDownloadedFile(Path path) throws IOException {
        return Files.list(path)
                .filter(f -> !Files.isDirectory(f))
                .max(Comparator.comparingLong(f -> f.toFile().lastModified())).get()
                .toFile();
    }


    protected List<File> getAllFiles() {
        return Arrays.stream(Objects.requireNonNull(path.toFile().listFiles()))
                .filter(file -> file.getName().endsWith(".pdf"))
                .toList();
    }

    protected Callable<Boolean> pdfDownloadCompletes() {
        return () -> getAllFiles().size() > 0;
    }

    protected void addPrimaryParentJob(String postFix) {
        //activities-employer-name
        testPage.enter("companyName", "testCompany" + postFix);
        testPage.clickContinue();
        //activities-employer-address
        testPage.enter("employerCity", "Chicago");
        testPage.enter("employerStreetAddress", "123 Test Me" + postFix);
        testPage.enter("employerState", "IL - Illinois");
        testPage.enter("employerPhoneNumber", "333333333" + postFix);
        testPage.enter("employerZipCode", "6042" + postFix);
        testPage.clickContinue();
        //activities-employer-start-date
        testPage.enter("activitiesJobStartDay", "05");
        testPage.enter("activitiesJobStartMonth", "04");
        testPage.enter("activitiesJobStartYear", "2025");
        testPage.clickContinue();
        //activities-self-employment
        testPage.clickNo();

        //activities-work-schedule-vary
        testPage.clickNo();
    }

    protected void addPrimaryParentJobSchedule(String postFix) {

        //activities-job-weekly-schedule
        testPage.clickElementById("activitiesJobWeeklySchedule-Monday");
        testPage.clickElementById("activitiesJobWeeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-job-hourly-schedule
        testPage.clickElementById("activitiesJobHoursSameEveryDay-Yes");
        testPage.selectFromDropdown("activitiesJobStartTimeAllDaysHour", "9");
        testPage.enter("activitiesJobStartTimeAllDaysMinute", postFix);
        testPage.selectFromDropdown("activitiesJobStartTimeAllDaysAmPm", "AM");

        testPage.selectFromDropdown("activitiesJobEndTimeAllDaysHour", "1");
        testPage.enter("activitiesJobEndTimeAllDaysMinute", postFix);
        testPage.selectFromDropdown("activitiesJobEndTimeAllDaysAmPm", "PM");

        testPage.clickContinue();

        //activities-work-commute-time
        testPage.selectFromDropdown("activitiesJobCommuteTime", getEnMessage("general.hours.1.hour"));
        testPage.clickContinue();
    }

    protected void addParentPartnerJob(String postFix) {
        testPage.enter("partnerCompanyName", "testPartnerCompany" + postFix);
        testPage.clickContinue();
        //activities--partner-employer-address
        testPage.enter("partnerEmployerPhoneNumber", "433333333" + postFix);
        testPage.enter("partnerEmployerCity", "Oakland");
        testPage.enter("partnerEmployerState", "IL - Illinois");
        testPage.enter("partnerEmployerStreetAddress", "123 Partner Employer Address");
        testPage.enter("partnerEmployerZipCode", "6042" + postFix);
        testPage.clickContinue();
        //activities-partner-employer-start-date
        testPage.clickContinue();
        //activities-partner-self-employment
        testPage.clickNo();
        //activities-partner-work-schedule-vary
        testPage.clickNo();
    }

    protected void addParentPartnerJobSchedule(String postFix) {

        //activities-partner-job-weekly-schedule
        testPage.clickElementById("activitiesJobWeeklySchedule-Monday");
        testPage.clickElementById("activitiesJobWeeklySchedule-Sunday");
        testPage.clickContinue();

        //activities-partner-job-hourly-schedule
        testPage.clickElementById("activitiesJobHoursSameEveryDay-Yes");
        testPage.selectFromDropdown("activitiesJobStartTimeAllDaysHour", "9");
        testPage.enter("activitiesJobStartTimeAllDaysMinute", postFix);
        testPage.selectFromDropdown("activitiesJobStartTimeAllDaysAmPm", "PM");

        testPage.selectFromDropdown("activitiesJobEndTimeAllDaysHour", "1");
        testPage.enter("activitiesJobEndTimeAllDaysMinute", postFix);
        testPage.selectFromDropdown("activitiesJobEndTimeAllDaysAmPm", "PM");

        testPage.clickContinue();

        //activities-partner-commute-time
        testPage.selectFromDropdown("activitiesJobCommuteTime", getEnMessage("general.hours.1.5.hours"));
        testPage.clickContinue();
    }

    protected String createAValidLink() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        Submission s = saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider().withParentDetails()
                .with("parentPreferredName", "FirstName").withChild("First", "Child", "true")
                .withChild("Second", "Child", "false").withChild("NoAssistance", "Child", "false")
                .withConstantChildcareSchedule(0).with("earliestChildcareStartDate", "10/10/2011")
                .withSubmittedAtDate(OffsetDateTime.now()).build());

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/s/%s".formatted(localServerPort, s.getShortCode()));

        return s.getShortCode();
    }

    protected void setupRegistration() {
        String confirmationCode = createAValidLink();
        // submit-start
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // confirmation-code
        assertThat(testPage.findElementById("providerResponseFamilyShortCode").getAttribute("value")).isEqualTo(confirmationCode);
        testPage.clickContinue();

        // paid-by-ccap
        testPage.clickNo();

        // registration-start
        testPage.clickButton(getEnMessage("registration-start.button"));

        // registration-getting-started
        testPage.clickContinue();

        // registration-provide-care-intro
        testPage.clickContinue();
    }

    /**
     * This compares the pdf fields in the generated pdf and our expected test pdf, "test_filled_ccap.pdf". If there are updates
     * to the template pdf (used to generate the client pdf), the test pdf should be updated to have the expected fields and
     * values.
     */
    protected void verifyPDF(String pdfPath, List<String> untestableFields, String flow) throws IOException {
        File pdfFile = getDownloadedPDF(flow);

//        regenerateExpectedPDF(pdfFile, pdfPath); // uncomment and run test to regenerate the test pdf

        try (FileInputStream actualIn = new FileInputStream(pdfFile); PdfReader actualReader = new PdfReader(
                actualIn); FileInputStream expectedIn = new FileInputStream(
                pdfPath); PdfReader expectedReader = new PdfReader(expectedIn)) {
            AcroFields actualAcroFields = actualReader.getAcroFields();
            AcroFields expectedAcroFields = expectedReader.getAcroFields();

            // Get all failures at once and log them
            List<String> missMatches = getMissMatches(expectedAcroFields, actualAcroFields, untestableFields);

            // Do actual assertions
            for (String expectedField : missMatches) {
                var actual = actualAcroFields.getField(expectedField);
                var expected = expectedAcroFields.getField(expectedField);
                assertThat(actual).withFailMessage("Expected %s to be %s but was %s".formatted(expectedField, expected, actual))
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
    protected static void regenerateExpectedPDF(File pdfFile, String pdfPath) {
        try (FileInputStream regeneratedPDF = new FileInputStream(pdfFile); FileOutputStream testPDF = new FileOutputStream(
                pdfPath)) {
            testPDF.write(regeneratedPDF.readAllBytes());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @NotNull
    protected static List<String> getMissMatches(AcroFields expectedAcroFields, AcroFields actualAcroFields,
            List<String> untestableFields) {
        List<String> missMatches = new ArrayList<>();
//
        for (String expectedField : expectedAcroFields.getAllFields().keySet()) {
            if (!untestableFields.contains(expectedField)) {
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

    protected File getDownloadedPDF(String flow) throws IOException {
        Optional<Submission> submissionOptional =
                repo.findAll().stream().filter(submission -> submission.getFlow().equals(flow)).findFirst();

        String downloadUrl = "";
        if (submissionOptional.isPresent()) {

            String baseString = flow.equals("gcc") ? "%s/download/%s/%s" : "%s/provider-response-download/%s/%s";

            downloadUrl = baseString.formatted(baseUrl, flow, submissionOptional.get().getId());

        }

        driver.get(downloadUrl);
        await().until(pdfDownloadCompletes());
        return getLatestDownloadedFile(path);
    }
}
