package epam.mapper;

import epam.dto.request_dto.UserRequestDTO;
import epam.dto.response_dto.UserResponseDTO;
import epam.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = UserMapper.INSTANCE;
    }

    @Test
    void testToUserResponseDTO() {
        User user = new User();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setIsActive(true);
        user.setUsername("johndoe");

        UserResponseDTO responseDTO = userMapper.toUserResponseDTO(user);

        assertNotNull(responseDTO);
        assertEquals("John", responseDTO.getFirstName());
        assertEquals("Doe", responseDTO.getLastName());
        assertTrue(responseDTO.getIsActive());
        assertEquals("johndoe", responseDTO.getUsername());
    }

    @Test
    void testToUser() {
        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setFirstName("Jane");
        requestDTO.setLastName("Smith");
        requestDTO.setIsActive(false);

        User user = userMapper.toUser(requestDTO);

        assertNotNull(user);
        assertEquals("Jane", user.getFirstname());
        assertEquals("Smith", user.getLastname());
        assertFalse(user.getIsActive());
    }
}
