package epam.controller;

import epam.dto.request_dto.TrainingRequestDTO;
import epam.dto.response_dto.TrainingResponseDTO;
import epam.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/trainings")
@Tag(name = "Training", description = "Operations related to training sessions")
public class TrainingController {

    private final TrainingService trainingService;

    @Operation(summary = "Create a new training session")
    @ApiResponse(responseCode = "200", description = "Training created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TrainingResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid training data", content = @Content)
    @PostMapping
    public ResponseEntity<TrainingResponseDTO> createTraining(
            @Parameter(description = "Training creation request body", required = true)
            @Valid @RequestBody TrainingRequestDTO trainingRequestDTO, Authentication connectedUser) {
        return ResponseEntity.ok(trainingService.createTraining(trainingRequestDTO, connectedUser));
    }

    @Operation(summary = "Delete training session")
    @ApiResponse(responseCode = "200", description = "Training removed successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TrainingResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid training data", content = @Content)
    @DeleteMapping("/{trainingId}")
    public ResponseEntity<String> deleteTraining(
            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "Invalid UUID format for trainingId. Must be 32 hexadecimal digits with 4 hyphens (e.g., xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx).")
            String trainingId, Authentication connectedUser) {
        return ResponseEntity.ok(trainingService.deleteTraining(UUID.fromString(trainingId), connectedUser));
    }
}
