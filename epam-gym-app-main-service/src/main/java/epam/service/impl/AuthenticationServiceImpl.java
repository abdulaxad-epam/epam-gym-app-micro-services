package epam.service.impl;

import epam.dto.request_dto.AuthenticateRequestDTO;
import epam.dto.request_dto.ChangePasswordRequestDTO;
import epam.dto.request_dto.RegisterTraineeRequestDTO;
import epam.dto.request_dto.RegisterTrainerRequestDTO;
import epam.dto.request_dto.TraineeRequestDTO;
import epam.dto.request_dto.TrainerRequestDTO;
import epam.dto.response_dto.AuthenticationResponseDTO;
import epam.dto.response_dto.RegisterTraineeResponseDTO;
import epam.dto.response_dto.RegisterTrainerResponseDTO;
import epam.dto.response_dto.TokenDTO;
import epam.dto.response_dto.UserAuthenticationResponseDTO;
import epam.dto.response_dto.UserResponseDTO;
import epam.exception.exception.InvalidTokenType;
import epam.exception.exception.UserNotAuthenticated;
import epam.exception.exception.UserNotFoundException;
import epam.service.AuthenticationService;
import epam.service.JwtService;
import epam.service.TraineeService;
import epam.service.TrainerService;
import epam.service.UserService;
import epam.util.PasswordGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final PasswordGenerator passwordGenerator;
    private final UserDetailsService userDetailsService;
    private final BruteForceProtectionService bruteForceProtectionService;

    @Override
    public AuthenticationResponseDTO register(RegisterTraineeRequestDTO userRequestDTO) {

        String password = passwordGenerator.generatePassword();
        String encryptedPassword = passwordGenerator.encode(password);

        userRequestDTO.getUser().setPassword(encryptedPassword);
        userRequestDTO.getUser().setRole("TRAINEE");

        TraineeRequestDTO traineeRequestDTO = TraineeRequestDTO.builder()
                .dateOfBirth(userRequestDTO.getDateOfBirth())
                .address(userRequestDTO.getAddress())
                .user(userRequestDTO.getUser())
                .build();

        RegisterTraineeResponseDTO trainee = traineeService.createTrainee(traineeRequestDTO);

        return generateToken(trainee.getUser(), password);
    }

    @Override
    public AuthenticationResponseDTO register(RegisterTrainerRequestDTO userRequestDTO) {

        String password = passwordGenerator.generatePassword();
        String encryptedPassword = passwordGenerator.encode(password);

        userRequestDTO.getUser().setPassword(encryptedPassword);
        userRequestDTO.getUser().setRole("TRAINER");

        TrainerRequestDTO trainerRequestDTO = TrainerRequestDTO.builder()
                .specialization(userRequestDTO.getSpecialization())
                .user(userRequestDTO.getUser())
                .build();

        RegisterTrainerResponseDTO trainer = trainerService.createTrainer(trainerRequestDTO);

        return generateToken(trainer.getUser(), password);
    }

    @Override
    public AuthenticationResponseDTO authenticate(AuthenticateRequestDTO dto) {
        String username = dto.getUsername();

        if (bruteForceProtectionService.isBlocked(username)) {
            throw new LockedException("Too many failed attempts. Try again later.");
        }

        var userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            bruteForceProtectionService.loginFailed(username);
            throw new UserNotFoundException("Invalid username or password");
        }

        var user = userOptional.get();

        if (!passwordGenerator.matches(dto.getPassword(), user.getPassword())) {
            bruteForceProtectionService.loginFailed(username);
            throw new UserNotFoundException("Invalid username or password");
        }

        bruteForceProtectionService.loginSucceeded(username);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        var accessToken = jwtService.generateAccessToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthenticationResponseDTO.builder()
                .token(
                        TokenDTO.builder()
                                .refreshToken(refreshToken)
                                .accessToken(accessToken)
                                .build())
                .user(null)
                .build();
    }

    @Override
    @Transactional
    public Boolean changePassword(ChangePasswordRequestDTO changePasswordRequestDTO, Authentication authentication) {
        if (authentication == null) {
            throw new UserNotAuthenticated("Access denied");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        var userOptional = userService.findByUsername(userDetails.getUsername());

        if (userOptional.isPresent() && passwordGenerator.matches(changePasswordRequestDTO.getOldPassword(), userOptional.get().getPassword())) {

            userOptional.get().setPassword(passwordGenerator.encode(changePasswordRequestDTO.getNewPassword()));

            return true;
        }

        throw new UserNotFoundException("User not found wrong old password");
    }

    @Override
    public void logout(HttpServletRequest request) {
        jwtService.blackList(request);
    }

    @Override
    public AuthenticationResponseDTO refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String refreshToken;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenType("Missing or invalid Authorization header");
        }

        refreshToken = authHeader.substring(7);

        if (!jwtService.isTokenNotExpired(refreshToken)) {
            throw new InvalidTokenType("Refresh token is expired");
        }

        String username = jwtService.extractUsername(refreshToken);
        UserDetails user = userDetailsService.loadUserByUsername(username);

        if (!jwtService.validateRefreshToken(refreshToken, user)) {
            throw new InvalidTokenType("Refresh token is not valid");
        }

        String accessToken = jwtService.generateAccessToken(user);
        return AuthenticationResponseDTO.builder()
                .token(TokenDTO.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build()).build();
    }

    private AuthenticationResponseDTO generateToken(UserResponseDTO userResponseDTO, String password) {
        var user = userDetailsService.loadUserByUsername(userResponseDTO.getUsername());

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        var userResponse = UserAuthenticationResponseDTO.builder()
                .username(userResponseDTO.getUsername())
                .password(password)
                .isActive(userResponseDTO.getIsActive())
                .build();

        return AuthenticationResponseDTO.builder()
                .token(TokenDTO.builder()
                        .refreshToken(refreshToken)
                        .accessToken(accessToken)
                        .build())
                .user(userResponse)
                .build();
    }


}
