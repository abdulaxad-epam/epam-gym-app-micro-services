package epam.controller;

import epam.dto.request_dto.AuthenticateRequestDTO;
import epam.dto.request_dto.ChangePasswordRequestDTO;
import epam.dto.request_dto.RegisterTraineeRequestDTO;
import epam.dto.request_dto.RegisterTrainerRequestDTO;
import epam.dto.response_dto.AuthenticationResponseDTO;
import epam.dto.response_dto.RegisterTraineeResponseDTO;
import epam.dto.response_dto.RegisterTrainerResponseDTO;
import epam.exception.exception.TraineeNotFoundException;
import epam.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    @Operation(summary = "Register a new trainer")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Trainer registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterTrainerResponseDTO.class))), @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)})
    @PostMapping(value = "/register/trainer", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthenticationResponseDTO> createTrainer(@Valid @RequestBody RegisterTrainerRequestDTO trainerRequestDTO) {
        return ResponseEntity.ok(authenticationService.register(trainerRequestDTO));
    }

    @Operation(summary = "Register a new trainee")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Trainee registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterTraineeResponseDTO.class))), @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)})
    @PostMapping(value = "/register/trainee", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AuthenticationResponseDTO> createTrainee(@Valid @RequestBody RegisterTraineeRequestDTO traineeRequestDTO) {
        return ResponseEntity.ok(authenticationService.register(traineeRequestDTO));
    }

    @Operation(summary = "Authenticate a user (trainee or trainer)")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Authentication successful"), @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials", content = @Content), @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)})
    @PostMapping(value = "/authenticate", consumes = "application/json")
    public ResponseEntity<AuthenticationResponseDTO> authenticateTraining(@Valid @RequestBody AuthenticateRequestDTO authenticateRequestDTO) throws TraineeNotFoundException {
        return ResponseEntity.ok(authenticationService.authenticate(authenticateRequestDTO));
    }

    @Operation(summary = "Change password for a user")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Password changed successfully"), @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content), @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)})
    @PutMapping(value = "/changePassword", consumes = "application/json")
    public ResponseEntity<Boolean> changePassword(@Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO, Authentication authentication) throws TraineeNotFoundException {
        authenticationService.changePassword(changePasswordRequestDTO, authentication);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authenticationService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/refresh-token")
    public ResponseEntity<AuthenticationResponseDTO> refreshToken(HttpServletRequest request) {

        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }

}
