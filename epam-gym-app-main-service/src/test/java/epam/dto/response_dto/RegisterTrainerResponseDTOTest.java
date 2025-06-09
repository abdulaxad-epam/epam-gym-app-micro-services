package epam.dto.response_dto;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(InstancioExtension.class)
public class RegisterTrainerResponseDTOTest {

    @Test
    public void testValidRegisterTrainerResponse() {
        RegisterTrainerResponseDTO response = Instancio.create(RegisterTrainerResponseDTO.class);

        assertNotNull(response);
        assertNotNull(response.getUser());
        assertNotNull(response.getTrainerSpecialization());

        assertFalse(response.getTrainerSpecialization().isEmpty());
    }

    @Test
    @InstancioSource
    public void testRegisterTrainerResponseWithGeneratedData() {
        RegisterTrainerResponseDTO response = Instancio.ofBlank(RegisterTrainerResponseDTO.class).create();
        assertNotNull(response);
        assertNotNull(response.getUser());
        assertNull(response.getUser().getUsername());
        assertNull(response.getUser().getFirstName());
        assertNull(response.getUser().getLastName());
        assertNull(response.getTrainerSpecialization());

    }
}