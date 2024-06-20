package org.ilgcc.app.utils;

import com.google.common.collect.Iterables;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.UserFileRepository;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
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
import static org.assertj.core.api.Fail.fail;
import static org.awaitility.Awaitility.await;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import({WebDriverConfiguration.class})
@ActiveProfiles("test")
@Slf4j
public abstract class AbstractBasePageTest {
  
  private static final String UPLOADED_JPG_FILE_NAME = "test.jpeg";
  
  @Autowired
  protected RemoteWebDriver driver;

  @Autowired
  SubmissionRepository repo;

  @Autowired
  UserFileRepository userFileRepository;

  @Autowired
  protected Path path;

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
    repo.deleteAll();
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

  protected void uploadJpgFile(){
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

  protected void verifyPDF(Submission submission, String testName) throws IOException{
    File pdfFile = getDownloadedPDF(submission);

    regenerateExpectedPDF(pdfFile, testName);

    try (FileInputStream actualIn = new FileInputStream(pdfFile);
        PdfReader actualReader = new PdfReader(actualIn);
//        Rather than using the filled out PDF, we can consider using the name of the TEST OR Generating the list from
        FileInputStream expectedIn = new FileInputStream("src/test/resources/output/%s_FILLED".formatted(testName));
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

  private File getDownloadedPDF(Submission submission) throws IOException {
    String downloadUrl = "%s/download/gcc/%s".formatted(baseUrl, submission.getId());
    driver.get(downloadUrl);
    await().until(pdfDownloadCompletes());
    return getLatestDownloadedFile(path);
  }

  private static void regenerateExpectedPDF(File pdfFile, String testName) {
    try (FileInputStream regeneratedPDF = new FileInputStream(pdfFile);
        FileOutputStream testPDF = new FileOutputStream("src/test/resources/output/%s_FILLED.pdf".formatted(testName))) {
      testPDF.write(regeneratedPDF.readAllBytes());
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
