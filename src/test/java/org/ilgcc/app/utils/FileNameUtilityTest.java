package org.ilgcc.app.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.ilgcc.app.utils.enums.FileNameUtility;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class FileNameUtilityTest {
    
    @ParameterizedTest
    @CsvSource({
            "Alex Gonzalez, Alex-Gonzalez",
            "Paul Mc'Donald, Paul-McDonald",
            "Alejandro Cortés, Alejandro-Cortes",
            "Ana Gabriel-McKig, Ana-Gabriel-McKig",
            "Jessica De Sandoval, Jessica-De-Sandoval",
            "María José González, Maria-Jose-Gonzalez",
            "Jean-Luc Picard, Jean-Luc-Picard",
            "Abdul Rahman Al-Saud, Abdul-Rahman-Al-Saud",
            "Jürgen Müller, Jurgen-Muller",
            "François L'Écuyer, Francois-LEcuyer"
    })
    void getApplicantNameForFileNameShouldReturnValidString(String input, String expected) {
        assertEquals(expected, FileNameUtility.getApplicantNameForFileName(input));
    }
}
