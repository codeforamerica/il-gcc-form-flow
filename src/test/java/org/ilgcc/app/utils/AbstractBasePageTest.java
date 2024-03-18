package org.ilgcc.app.utils;

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
import static org.awaitility.Awaitility.await;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import({WebDriverConfiguration.class})
@ActiveProfiles("test")
public abstract class AbstractBasePageTest {
  
  private static final String UPLOADED_JPG_FILE_NAME = "test.jpeg";
  
  @Autowired
  protected RemoteWebDriver driver;

  @Autowired
  protected Path path;

  protected String baseUrl;

  @LocalServerPort
  protected String localServerPort;

  protected Page testPage;

  @BeforeEach
  protected void setUp() throws IOException {
    initTestPage();
    baseUrl = "http://localhost:%s".formatted(localServerPort);

    driver.navigate().to(baseUrl);
  }

  protected void initTestPage() {
    testPage = new Page(driver);
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

  protected void uploadJpgFile(String dzName) {
    uploadFile(UPLOADED_JPG_FILE_NAME, dzName);
    assertThat(driver.findElement(By.id("dropzone-" + dzName)).getText().replace("\n", ""))
        .contains(UPLOADED_JPG_FILE_NAME);
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
}
