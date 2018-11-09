package com.a2sidorov.mychatTest


import org.junit.jupiter.api.Test;

import com.a2sidorov.mychat.InputValidation

import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertTrue

class InputValidationTest {

    @Test
    void empty() {
        assertFalse(InputValidation.isValidNickname(""))
    }

    @Test
    void onlyWhitespace() {
        assertFalse(InputValidation.isValidNickname(" "))
    }

    @Test
    void onlySymbols() {
        assertFalse(InputValidation.isValidNickname("!@#\$"));
    }

    @Test
    void charNum1() {
        assertTrue(InputValidation.isValidNickname("1"));
    }

    @Test
    void charNum15() {
        assertTrue(InputValidation.isValidNickname("123456789012345"));
    }

    @Test
    void charNum16() {
        assertFalse(InputValidation.isValidNickname("1234567890123456"));
    }

    @Test
    void valid() {
        assertTrue(InputValidation.isValidNickname("Nomad"));
    }

}

