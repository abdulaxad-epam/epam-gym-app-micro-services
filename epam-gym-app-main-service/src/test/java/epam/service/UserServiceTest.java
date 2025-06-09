package epam.service;

import epam.entity.User;
import epam.repository.UserRepository;
import epam.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserRepository userRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void existsByUsernameAndPassword_shouldReturnTrue_whenUserExists() {
        String username = "testuser";
        String password = "password";

        when(userRepository.existsByUsernameAndPassword(username.toLowerCase(), password)).thenReturn(true);

        boolean result = userService.existsByUsernameAndPassword(username, password);

        assertTrue(result);
        verify(userRepository).existsByUsernameAndPassword(username.toLowerCase(), password);
    }

    @Test
    void existsByUsernameAndPassword_shouldReturnFalse_whenUserDoesNotExist() {
        String username = "testuser";
        String password = "wrongpassword";

        when(userRepository.existsByUsernameAndPassword(username.toLowerCase(), password)).thenReturn(false);

        boolean result = userService.existsByUsernameAndPassword(username, password);

        assertFalse(result);
        verify(userRepository).existsByUsernameAndPassword(username.toLowerCase(), password);
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenUsernameExists() {
        String username = "testuser";

        when(userRepository.existsByUsername(username.toLowerCase())).thenReturn(true);

        boolean result = userService.existsByUsername(username);

        assertTrue(result);
        verify(userRepository).existsByUsername(username.toLowerCase());
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUsernameDoesNotExist() {
        String username = "nonexistent";

        when(userRepository.existsByUsername(username.toLowerCase())).thenReturn(false);

        boolean result = userService.existsByUsername(username);

        assertFalse(result);
        verify(userRepository).existsByUsername(username.toLowerCase());
    }

    @Test
    void findByUsername_shouldReturnUser_whenUserExists() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenUserDoesNotExist() {
        String username = "unknown";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername(username);

        assertTrue(result.isEmpty());
        verify(userRepository).findByUsername(username);
    }
}
