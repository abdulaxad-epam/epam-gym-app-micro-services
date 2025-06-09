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

public class ChangePasswordRequestDTOTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidChangePasswordRequest() {
        ChangePasswordRequestDTO request = ChangePasswordRequestDTO.builder()
                .oldPassword("1234567890")
                .newPassword("0987654321")
                .build();

        Set<ConstraintViolation<ChangePasswordRequestDTO>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testShortOldPassword() {
        ChangePasswordRequestDTO request = ChangePasswordRequestDTO.builder()
                .oldPassword("1234512341234")
                .newPassword("0987")
                .build();

        Set<ConstraintViolation<ChangePasswordRequestDTO>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testLongOldPassword() {
        ChangePasswordRequestDTO request = ChangePasswordRequestDTO.builder()
                .oldPassword("1234567")
                .newPassword("098765432112")
                .build();

        Set<ConstraintViolation<ChangePasswordRequestDTO>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testShortNewPassword() {
        ChangePasswordRequestDTO request = ChangePasswordRequestDTO.builder()
                .oldPassword("1234567890")
                .newPassword("09876")
                .build();

        Set<ConstraintViolation<ChangePasswordRequestDTO>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testLongNewPassword() {
        ChangePasswordRequestDTO request = ChangePasswordRequestDTO.builder()
                .oldPassword("1234567890")
                .newPassword("0987654321123")
                .build();

        Set<ConstraintViolation<ChangePasswordRequestDTO>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testNullOldPassword() {
        ChangePasswordRequestDTO request = ChangePasswordRequestDTO.builder()
                .oldPassword(null)
                .newPassword("0987654321")
                .build();

        Set<ConstraintViolation<ChangePasswordRequestDTO>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Old password is required", violations.iterator().next().getMessage());
    }

    @Test
    public void testNullNewPassword() {
        ChangePasswordRequestDTO request = ChangePasswordRequestDTO.builder()
                .oldPassword("1234567890")
                .newPassword(null)
                .build();

        Set<ConstraintViolation<ChangePasswordRequestDTO>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("New password is required", violations.iterator().next().getMessage());
    }
}
