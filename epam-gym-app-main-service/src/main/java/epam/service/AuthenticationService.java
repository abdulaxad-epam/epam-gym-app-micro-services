package epam.service;

import epam.dto.request_dto.AuthenticateRequestDTO;
import epam.dto.request_dto.ChangePasswordRequestDTO;
import epam.dto.request_dto.RegisterTraineeRequestDTO;
import epam.dto.request_dto.RegisterTrainerRequestDTO;
import epam.dto.response_dto.AuthenticationResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;


public interface AuthenticationService {

    AuthenticationResponseDTO register(RegisterTrainerRequestDTO userRequestDTO);

    AuthenticationResponseDTO register(RegisterTraineeRequestDTO userRequestDTO);

    AuthenticationResponseDTO authenticate(AuthenticateRequestDTO authenticateRequestDTO);

    Boolean changePassword(ChangePasswordRequestDTO changePasswordRequestDTO, Authentication authentication);

    void logout(HttpServletRequest request);

    AuthenticationResponseDTO refreshToken(HttpServletRequest request);
}
