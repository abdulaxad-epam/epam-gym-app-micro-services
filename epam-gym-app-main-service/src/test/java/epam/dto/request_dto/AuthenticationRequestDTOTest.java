package epam.dto.request_dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationRequestDTOTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    public void testValidAuthenticateRequest() {
        AuthenticateRequestDTO request = AuthenticateRequestDTO.builder()
                .username("testfd.fdUser")
                .password("1234567890")
                .build();

        Set<ConstraintViolation<AuthenticateRequestDTO>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testNullUsername() {
        AuthenticateRequestDTO request = AuthenticateRequestDTO.builder()
                .username(null)
                .password("1234567890")
                .build();

        Set<ConstraintViolation<AuthenticateRequestDTO>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Trainer username is required", violations.iterator().next().getMessage());
    }

    @Test
    public void testNullPassword() {
        AuthenticateRequestDTO request = AuthenticateRequestDTO.builder()
                .username("testre.eUser")
                .password(null)
                .build();

        Set<ConstraintViolation<AuthenticateRequestDTO>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Password is required", violations.iterator().next().getMessage());
    }
}
