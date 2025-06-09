package epam.service;

import epam.dto.request_dto.AuthenticateRequestDTO;
import epam.dto.request_dto.ChangePasswordRequestDTO;
import epam.dto.request_dto.RegisterTraineeRequestDTO;
import epam.dto.request_dto.RegisterTrainerRequestDTO;
import epam.dto.request_dto.TrainerRequestDTO;
import epam.dto.request_dto.UserRequestDTO;
import epam.dto.response_dto.AuthenticationResponseDTO;
import epam.dto.response_dto.RegisterTraineeResponseDTO;
import epam.dto.response_dto.RegisterTrainerResponseDTO;
import epam.dto.response_dto.UserResponseDTO;
import epam.entity.User;
import epam.exception.exception.InvalidTokenType;
import epam.exception.exception.UserNotAuthenticated;
import epam.exception.exception.UserNotFoundException;
import epam.service.impl.AuthenticationServiceImpl;
import epam.service.impl.BruteForceProtectionService;
import epam.util.PasswordGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthenticationServiceTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UserService userService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TraineeService traineeService;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private BruteForceProtectionService bruteForceProtectionService;
    @Mock
    private Authentication authentication;
    @Mock
    private epam.service.impl.UserDetailsService userDetailsServiceImpl;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterTrainee() {
        RegisterTraineeRequestDTO dto = new RegisterTraineeRequestDTO();
        UserRequestDTO userDTO = new UserRequestDTO();
        dto.setUser(userDTO);

        when(passwordGenerator.generatePassword()).thenReturn("plainPass");
        when(passwordGenerator.encode("plainPass")).thenReturn("encodedPass");

        RegisterTraineeResponseDTO response = new RegisterTraineeResponseDTO();
        UserResponseDTO user = new UserResponseDTO();
        user.setUsername("trainee");
        user.setIsActive(true);
        response.setUser(user);

        when(traineeService.createTrainee(any())).thenReturn(response);
        when(userDetailsService.loadUserByUsername("trainee")).thenReturn(mock(UserDetails.class));
        when(jwtService.generateAccessToken(any())).thenReturn("access");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh");

        AuthenticationResponseDTO result = authenticationService.register(dto);

        assertNotNull(result.getToken());
        assertEquals("access", result.getToken().getAccessToken());
        assertEquals("refresh", result.getToken().getRefreshToken());
        assertEquals("trainee", result.getUser().getUsername());
    }


    @Test
    void registerTrainer_shouldReturnAuthResponse() {
        // Given
        var trainerUser = new UserResponseDTO("firstname", "lastname", "username", true);
        var trainerResponseDTO = RegisterTrainerResponseDTO.builder().user(trainerUser).build();

        var trainerRequestDTO = RegisterTrainerRequestDTO.builder()
                .specialization("Strength")
                .user(new epam.dto.request_dto.UserRequestDTO())
                .build();

        String rawPassword = "plainPass";
        String encodedPassword = "encodedPass";

        when(passwordGenerator.generatePassword()).thenReturn(rawPassword);
        when(passwordGenerator.encode(rawPassword)).thenReturn(encodedPassword);
        when(trainerService.createTrainer(any(TrainerRequestDTO.class))).thenReturn(trainerResponseDTO);
        when(userDetailsService.loadUserByUsername("trainer1")).thenReturn(mock(UserDetails.class));
        when(jwtService.generateAccessToken(any())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");

        // When
        AuthenticationResponseDTO response = authenticationService.register(trainerRequestDTO);

        // Then
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("access-token", response.getToken().getAccessToken());
        assertEquals("refresh-token", response.getToken().getRefreshToken());
    }

    @Test
    void testAuthenticate_ValidCredentials() {
        AuthenticateRequestDTO dto = new AuthenticateRequestDTO();
        dto.setUsername("user");
        dto.setPassword("password");

        User user = User.builder()
                .username("user")
                .isActive(true)
                .password("hashed")
                .build();

        when(bruteForceProtectionService.isBlocked("user")).thenReturn(false);
        when(userService.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordGenerator.matches("password", "hashed")).thenReturn(true);
        when(userDetailsService.loadUserByUsername("user")).thenReturn(mock(UserDetails.class));
        when(jwtService.generateAccessToken(any())).thenReturn("access");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh");

        AuthenticationResponseDTO response = authenticationService.authenticate(dto);

        assertNotNull(response.getToken());
        assertEquals("access", response.getToken().getAccessToken());
        assertEquals("refresh", response.getToken().getRefreshToken());
    }

    @Test
    void testAuthenticate_UserBlocked() {
        AuthenticateRequestDTO dto = new AuthenticateRequestDTO();
        dto.setUsername("blocked");
        dto.setPassword("password");

        when(bruteForceProtectionService.isBlocked("blocked")).thenReturn(true);

        assertThrows(LockedException.class, () -> authenticationService.authenticate(dto));
    }

    @Test
    void testChangePassword_Success() throws AccessDeniedException {
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO("old", "new");

        Authentication auth = mock(Authentication.class);
        UserDetails details = mock(UserDetails.class);

        when(auth.getPrincipal()).thenReturn(details);
        when(details.getUsername()).thenReturn("user");

        User user = User.builder()
                .username("user")
                .isActive(true)
                .password("oldHashed")
                .build();

        when(userService.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordGenerator.matches("old", "oldHashed")).thenReturn(true);
        when(passwordGenerator.encode("new")).thenReturn("newHashed");

        assertTrue(authenticationService.changePassword(request, auth));
    }

    @Test
    void testChangePassword_Thrown() {
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO("old", "new");

        User user = User.builder()
                .username("user")
                .isActive(true)
                .password("oldHashed")
                .build();

        when(userService.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordGenerator.matches("old", "oldHashed")).thenReturn(true);
        when(passwordGenerator.encode("new")).thenReturn("newHashed");

        assertThrows(UserNotAuthenticated.class, () -> authenticationService.changePassword(request, null));
    }

    @Test
    void testChangePassword_Failed() {
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO("old", "new");

        Authentication auth = mock(Authentication.class);
        UserDetails details = mock(UserDetails.class);

        when(auth.getPrincipal()).thenReturn(details);
        when(details.getUsername()).thenReturn("user");

        User user = User.builder()
                .username("user")
                .isActive(true)
                .build();

        when(userService.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordGenerator.matches("old", "oldHashed")).thenReturn(true);
        when(passwordGenerator.encode("new")).thenReturn("newHashed");

        assertThrows(UserNotFoundException.class, () -> authenticationService.changePassword(request, auth));
    }

    @Test
    void testRefreshToken_Valid() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");

        when(jwtService.isTokenNotExpired("validToken")).thenReturn(true);
        when(jwtService.extractUsername("validToken")).thenReturn("user");
        when(userDetailsService.loadUserByUsername("user")).thenReturn(mock(UserDetails.class));
        when(jwtService.validateRefreshToken(eq("validToken"), any())).thenReturn(true);
        when(jwtService.generateAccessToken(any())).thenReturn("newAccess");

        AuthenticationResponseDTO response = authenticationService.refreshToken(request);
        assertEquals("newAccess", response.getToken().getAccessToken());
    }

    @Test
    void testRefreshToken_InvalidHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        assertThrows(InvalidTokenType.class, () -> authenticationService.refreshToken(request));
    }

    @Test
    void testRefreshToken_InvalidToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(jwtService.isTokenNotExpired("expiredToken")).thenReturn(false);
        when(jwtService.validateRefreshToken("invalidToken", mock(UserDetails.class))).thenReturn(false);

        assertThrows(InvalidTokenType.class, () -> authenticationService.refreshToken(request));
    }


    @Test
    void logout_shouldCallJwtServiceBlackList() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        authenticationService.logout(request);

        verify(jwtService, times(1)).blackList(request);
    }
}
