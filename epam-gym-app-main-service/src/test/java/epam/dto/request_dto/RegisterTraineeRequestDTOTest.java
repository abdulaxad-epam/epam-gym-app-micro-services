package epam.dto.request_dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.Set;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(InstancioExtension.class)
public class RegisterTraineeRequestDTOTest {

    private Validator validator;

    @WithSettings
    public static final Settings settings = Settings.create()
            .set(Keys.STRING_MIN_LENGTH, 10).lock();

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private UserRequestDTO getValidUserRequest() {
        return Instancio.of(UserRequestDTO.class)
                .withSettings(settings)
                .set(field(UserRequestDTO::getRole), null)
                .set(field(UserRequestDTO::getPassword), null)
                .create();
    }

    @Test
    void testValidTraineeRequest() {
        RegisterTraineeRequestDTO dto = RegisterTraineeRequestDTO.builder()
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .user(getValidUserRequest())
                .build();

        Set<ConstraintViolation<RegisterTraineeRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "DTO should be valid");
    }

    @Test
    void testMissingDateOfBirth() {
        RegisterTraineeRequestDTO dto = RegisterTraineeRequestDTO.builder()
                .address("Some Address")
                .user(getValidUserRequest())
                .build();

        Set<ConstraintViolation<RegisterTraineeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Date of birth is required")));
    }

    @Test
    void testFutureDateOfBirth() {
        RegisterTraineeRequestDTO dto = RegisterTraineeRequestDTO.builder()
                .dateOfBirth(LocalDate.now().plusDays(1))
                .address("Some Address")
                .user(getValidUserRequest())
                .build();

        Set<ConstraintViolation<RegisterTraineeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Date of birth must be in the past")));
    }

    @Test
    void testBlankAddress() {
        RegisterTraineeRequestDTO dto = RegisterTraineeRequestDTO.builder()
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .address("   ")
                .user(getValidUserRequest())
                .build();

        Set<ConstraintViolation<RegisterTraineeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Address is required")));
    }

    @Test
    void testNullUserObject() {
        RegisterTraineeRequestDTO dto = RegisterTraineeRequestDTO.builder()
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .address("Some Address")
                .user(null)
                .build();

        Set<ConstraintViolation<RegisterTraineeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("User details are required")));
    }
}
