package epam.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordGeneratorTest {

    private PasswordGenerator passwordGenerator;

    @BeforeEach
    void setUp() {
        passwordGenerator = new PasswordGenerator();
    }

    @Test
    void testGeneratedPasswordLength() {
        String password = passwordGenerator.generatePassword();
        assertEquals(10, password.length(), "Password length should be exactly 10 characters.");
    }

    @Test
    void testGeneratedPasswordNotNullOrEmpty() {
        String password = passwordGenerator.generatePassword();
        assertNotNull(password, "Generated password should not be null.");
        assertFalse(password.isEmpty(), "Generated password should not be empty.");
    }

    @Test
    void testGeneratedPasswordsAreRandom() {
        String password1 = passwordGenerator.generatePassword();
        String password2 = passwordGenerator.generatePassword();
        assertNotEquals(password1, password2, "Consecutive generated passwords should not be the same.");
    }

    @Test
    void testBCryptEncodingAndMatching() {
        String rawPassword = passwordGenerator.generatePassword();
        String encodedPassword = passwordGenerator.encode(rawPassword);

        assertNotNull(encodedPassword, "Encoded password should not be null.");
        assertTrue(passwordGenerator.matches(rawPassword, encodedPassword), "BCrypt should match raw and encoded password.");
    }
}
