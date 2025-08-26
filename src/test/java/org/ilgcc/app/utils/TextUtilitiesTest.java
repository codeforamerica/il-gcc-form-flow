package org.ilgcc.app.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class TextUtilitiesTest {

    @Test
    void sanitize_nullInput_returnsNull() {
        assertNull(TextUtilities.sanitize(null));
        assertNull(TextUtilities.sanitize(null, "_"));
    }

    @Test
    void sanitize_validCharacters_unchanged() {
        String input = "abcXYZ123-_";
        String result = TextUtilities.sanitize(input);
        assertEquals(input, result);
    }

    @Test
    void sanitize_invalidCharacters_removed() {
        String input = "abc!@#123";
        String result = TextUtilities.sanitize(input);
        assertEquals("abc123", result);
    }

    @Test
    void sanitize_invalidCharacters_replaced() {
        String input = "a b$c";
        String result = TextUtilities.sanitize(input, "_");
        assertEquals("a_b_c", result);
    }

    @Test
    void sanitize_newlineAndCarriageReturn_removed() {
        String input = "abc\ndef\rghi";
        String result = TextUtilities.sanitize(input);
        assertEquals("abcdefghi", result);
    }

    @Test
    void sanitize_newlineAndCarriageReturn_replaced() {
        String input = "abc\ndef\rghi";
        String result = TextUtilities.sanitize(input, "_");
        assertEquals("abc_def_ghi", result);
    }

    @Test
    void sanitize_emptyString_returnsEmpty() {
        assertEquals("", TextUtilities.sanitize(""));
        assertEquals("", TextUtilities.sanitize("", "_"));
    }

    @Test
    void sanitize_onlyInvalidCharacters() {
        String input = "!@#$";
        assertEquals("", TextUtilities.sanitize(input));
        assertEquals("____", TextUtilities.sanitize(input, "_"));
    }
}
