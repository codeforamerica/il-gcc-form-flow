package org.ilgcc.app.utils;

import com.google.common.collect.Iterables;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.UserFileRepository;
import java.util.Locale;
import org.ilgcc.app.data.TransmissionRepository;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import({WebDriverConfiguration.class})
@ActiveProfiles({"test", "selenium-test"})
public abstract class AbstractBasePageTest {

    private static final String UPLOADED_JPG_FILE_NAME = "test.jpeg";

    @Autowired
    protected RemoteWebDriver driver;

    @Autowired
    SubmissionRepository repo;

    @Autowired
    UserFileRepository userFileRepository;
    
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

    public Submission getSessionSubmission() {
        // We're hoping that there's only one submission per session
        // If 0 or >1, an error will be thrown
        return Iterables.getOnlyElement(repo.findAll());
    }

    public SubmissionTestBuilder getSessionSubmissionTestBuilder() {
        return new SubmissionTestBuilder(getSessionSubmission());
    }

    public void saveSubmission(Submission submission) {
        repo.save(submission);
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
}
