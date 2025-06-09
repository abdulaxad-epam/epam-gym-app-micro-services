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
public class RegisterTraineeResponseDTOTest {

    @Test
    public void testValidRegisterTraineeResponse() {
        RegisterTraineeResponseDTO response = Instancio.create(RegisterTraineeResponseDTO.class);

        assertNotNull(response);
        assertNotNull(response.getUser());
        assertNotNull(response.getTraineeDateOfBirth());
        assertNotNull(response.getAddress());
        assertFalse(response.getTraineeDateOfBirth().isEmpty());
        assertFalse(response.getAddress().isEmpty());
    }

    @Test
    @InstancioSource
    public void testRegisterTraineeResponseWithGeneratedData() {
        RegisterTraineeResponseDTO response = Instancio.ofBlank(RegisterTraineeResponseDTO.class).create();

        assertNotNull(response);
        assertNotNull(response.getUser());
        assertNull(response.getUser().getFirstName());
        assertNull(response.getUser().getLastName());
        assertNull(response.getUser().getIsActive());
        assertNull(response.getUser().getUsername());
        assertNull(response.getTraineeDateOfBirth());
        assertNull(response.getAddress());

    }
}
