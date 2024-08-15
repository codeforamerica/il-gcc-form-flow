package org.ilgcc.app.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import formflow.library.data.Submission;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import org.ilgcc.app.utils.enums.FileNameUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class FileNameUtilityTest {
    
    @ParameterizedTest
    @CsvSource({
            "Gonzalez Alex, Gonzalez-Alex",
            "Mc'Donald Paul, McDonald-Paul",
            "Cortés Alejandro, Cortes-Alejandro",
            "Gabriel-McKig Ana, Gabriel-McKig-Ana",
            "De Sandoval Jessica, De-Sandoval-Jessica",
            "José González María, Jose-Gonzalez-Maria",
            "Picard Jean-Luc, Picard-Jean-Luc",
            "Al-Saud Abdul Rahman , Al-Saud-Abdul-Rahman",
            "Müller Jürgen, Muller-Jurgen",
            "L'Écuyer François, LEcuyer-Francois"
    })
    void getApplicantNameForFileNameShouldReturnValidString(String input, String expected) {
        assertEquals(expected, FileNameUtility.formatApplicantNameForFileName(input));
    }
    
    @Test
    void getFileNameForPdfShouldReturnValidString() {
        Submission submission = new Submission();
        submission.setSubmittedAt(OffsetDateTime.parse("2024-02-07T12:00:00Z"));
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("parentFirstName", "Alex");
        inputData.put("parentLastName", "Gonzalez");
        submission.setInputData(inputData);
        assertEquals("Gonzalez-Alex-2024-02-07-CCAP-Application-Form.pdf", FileNameUtility.getFileNameForPdf(submission));
    }
}
