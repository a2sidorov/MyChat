package com.a2sidorov.mychat.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testing InputValidation class")
class InputValidationTest {

    @Nested
    @DisplayName("Testing isAddressValid method")
    class isAddressValidTest {

        @DisplayName("when the string is empty then return false")
        @Test
        void isAddressValidTest1() {
            assertFalse(InputValidation.isAddressValid(""));
        }

        @DisplayName("when the string is a white space then return false")
        @Test
        void isAddressValidTest2() {
            assertFalse(InputValidation.isAddressValid(" "));
        }

        @DisplayName("when the string is a white space then return false")
        @Test
        void isAddressValidTest3() {
            assertFalse(InputValidation.isAddressValid(" "));
        }

        @DisplayName("when the string contains a white space then return false")
        @Test
        void isAddressValidTest4() {
            assertFalse(InputValidation.isAddressValid("192.168.0. 1"));
        }

        @DisplayName("when the string contains a character then return false")
        @Test
        void isAddressValidTest5() {
            assertFalse(InputValidation.isPortValid("192.168.o.1"));
        }

        @DisplayName("when the address is 30.168.1.255.1 then return false")
        @Test
        void isAddressValidTest6() {
            assertFalse(InputValidation.isPortValid("30.168.1.255.1"));
        }

        @DisplayName("when the address is 127.1 then return false")
        @Test
        void isAddressValidTest7() {
            assertFalse(InputValidation.isPortValid("127.1"));
        }

        @DisplayName("when the address is -1.2.3.4 then return false")
        @Test
        void isAddressValidTest8() {
            assertFalse(InputValidation.isPortValid("-1.2.3.4"));
        }

        @DisplayName("when the address is -1.2.3.4 then return false")
        @Test
        void isAddressValidTest9() {
            assertFalse(InputValidation.isPortValid("3...3"));
        }


    }

    @Nested
    @DisplayName("Testing isPortValid method")
    class isPortValidTest {

        @DisplayName("when the string is empty then return false")
        @Test
        void isPortValidTest1() {
            assertFalse(InputValidation.isPortValid(""));
        }

        @DisplayName("when the string is a white space then return false")
        @Test
        void isPortValidTest2() {
            assertFalse(InputValidation.isPortValid(" "));
        }

        @DisplayName("when the string contains a character then return false")
        @Test
        void isPortValidTest3() {
            assertFalse(InputValidation.isPortValid("1a50"));
        }

        @DisplayName("when the string contains a symbol then return false")
        @Test
        void isPortValidTest4() {
            assertFalse(InputValidation.isPortValid("1@50"));
        }

        @DisplayName("when the string is a negative number then return false")
        @Test
        void isPortValidTest5() {
            assertFalse(InputValidation.isPortValid("-1050"));
        }

        @DisplayName("when the string is longer than 5 characters then return false")
        @Test
        void isPortValidTest6() {
            assertFalse(InputValidation.isPortValid("123456"));
        }

        @DisplayName("when the string is a valid port number then return true")
        @Test
        void isPortValidTest7() {
            assertTrue(InputValidation.isPortValid("1050"));
        }
    }

    @Nested
    @DisplayName("Testing isNicknameValid method")
    class isNicknameValidTest {
        @DisplayName("when the string is empty then return false")
        @Test
        void isNicknameValidTest1() {
            assertFalse(InputValidation.isNicknameValid(""));
        }

        @DisplayName("when the string is a white space then return false")
        @Test
        void isNicknameValidTest2() {
            assertFalse(InputValidation.isNicknameValid(" "));
        }

        @DisplayName("when the string is '!@#$' then return false")
        @Test
        void isNicknameValidTest3() {
            assertFalse(InputValidation.isNicknameValid("!@#$"));
        }

        @DisplayName("when the string is a digit then return true")
        @Test
        void isNicknameValidTest4() {
            assertTrue(InputValidation.isNicknameValid("1"));
        }

        @DisplayName("when the string is 15 character long then return true")
        @Test
        void isNicknameValidTest5() {
            assertTrue(InputValidation.isNicknameValid("123456789012345"));
        }

        @DisplayName("when the string is more than 15 character long then return false")
        @Test
        void isNicknameValidTest6() {
            assertFalse(InputValidation.isNicknameValid("1234567890123456"));
        }

        @DisplayName("when the string is valid then return true")
        @Test
        void isNicknameValidTest7() {
            assertTrue(InputValidation.isNicknameValid("No_m$d"));
        }
    }




}
