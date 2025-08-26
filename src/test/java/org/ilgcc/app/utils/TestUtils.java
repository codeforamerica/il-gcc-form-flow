package org.ilgcc.app.utils;

import formflow.library.data.Submission;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TestUtils {

  public static Path getAbsoluteFilepath(String resourceFilename) {
    return Paths.get(getAbsoluteFilepathString(resourceFilename));
  }

  public static String getAbsoluteFilepathString(String resourceFilename) {
    URL resource = TestUtils.class.getClassLoader().getResource(resourceFilename);
    if (resource != null) {
      return (new File(resource.getFile())).getAbsolutePath();
    }
    return "";
  }

  public static byte[] getFileContentsAsByteArray(String filename) throws IOException {
    return Files.readAllBytes(getAbsoluteFilepath(filename));
  }

  public static void resetSubmission() {
    Submission submission = new Submission();
    submission.setId(null);
    submission.setInputData(new HashMap<>());
    submission.setFlow(null);
  }

  public static Map<String, Object> createCareSchedule(String providerUUID, String childUUID, String ccapStartDate) {
    Map<String, Object> careSchedule = new HashMap<>();

    careSchedule.put("uuid", String.format("%s-%s", providerUUID, childUUID));
    careSchedule.put("childInCare", true);
    careSchedule.put("ccapStartDate", ccapStartDate);
    careSchedule.put("repeatForValue", providerUUID);
    careSchedule.put("iterationIsComplete", true);

    return careSchedule;
  }
}
