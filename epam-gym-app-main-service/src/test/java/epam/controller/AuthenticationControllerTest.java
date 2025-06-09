package epam.controller;

import epam.dto.request_dto.AuthenticateRequestDTO;
import epam.dto.request_dto.ChangePasswordRequestDTO;
import epam.dto.request_dto.RegisterTraineeRequestDTO;
import epam.dto.request_dto.RegisterTrainerRequestDTO;
import epam.dto.response_dto.AuthenticationResponseDTO;
import epam.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.nio.file.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateTrainer_ShouldReturnOkResponse() {
        RegisterTrainerRequestDTO requestDTO = new RegisterTrainerRequestDTO();
        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO();

        when(authenticationService.register(requestDTO)).thenReturn(responseDTO);

        ResponseEntity<AuthenticationResponseDTO> response = authenticationController.createTrainer(requestDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    public void testCreateTrainee_ShouldReturnOkResponse() {
        RegisterTraineeRequestDTO requestDTO = new RegisterTraineeRequestDTO();
        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO();

        when(authenticationService.register(requestDTO)).thenReturn(responseDTO);

        ResponseEntity<AuthenticationResponseDTO> response = authenticationController.createTrainee(requestDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    public void testAuthenticateTraining_ShouldReturnOkResponse() {
        AuthenticateRequestDTO requestDTO = new AuthenticateRequestDTO();
        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO();

        when(authenticationService.authenticate(requestDTO)).thenReturn(responseDTO);

        ResponseEntity<AuthenticationResponseDTO> response = authenticationController.authenticateTraining(requestDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    public void testChangePassword_ShouldReturnAccepted() throws AccessDeniedException {
        ChangePasswordRequestDTO requestDTO = new ChangePasswordRequestDTO();
        Authentication auth = mock(Authentication.class);

        when(authenticationService.changePassword(requestDTO, auth)).thenReturn(true);

        ResponseEntity<Boolean> response = authenticationController.changePassword(requestDTO, auth);

        assertEquals(202, response.getStatusCode().value());
    }

    @Test
    public void testLogout_ShouldReturnNoContent() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        doNothing().when(authenticationService).logout(request);

        ResponseEntity<Void> response = authenticationController.logout(request);

        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    public void testRefreshToken_ShouldReturnOkResponse() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO();

        when(authenticationService.refreshToken(request)).thenReturn(responseDTO);

        ResponseEntity<AuthenticationResponseDTO> response = authenticationController.refreshToken(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(responseDTO, response.getBody());
    }
}
