package epam.controller;

import epam.dto.request_dto.TrainerRequestDTO;
import epam.dto.response_dto.TrainerResponseDTO;
import epam.dto.response_dto.TrainingResponseDTO;
import epam.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/trainers")
@Tag(name = "Trainer", description = "Operations related to trainers")
public class TrainerController {

    private final TrainerService trainerService;


    @Operation(summary = "Get trainer details by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer details retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainerResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    @GetMapping(value = "/profile", produces = "application/json")
    public ResponseEntity<TrainerResponseDTO> getTrainer(Authentication connectedUser) {
        return ResponseEntity.ok(trainerService.getTrainerByUsername(connectedUser));
    }

    @Operation(summary = "Update trainer profile by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer updated successfully",
                    content = @Content(schema = @Schema(implementation = TrainerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    @PutMapping(value = "/update", produces = "application/json")
    public ResponseEntity<TrainerResponseDTO> updateTrainer(
            @Parameter(description = "Updated trainer data", required = true)
            @Valid @RequestBody TrainerRequestDTO traineeRequestDTO, Authentication connectedUser) {

        return ResponseEntity.ok(trainerService.updateTrainer(connectedUser, traineeRequestDTO));
    }

    @Operation(summary = "Get trainings conducted by a trainer with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings list retrieved",
                    content = @Content(schema = @Schema(implementation = TrainingResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    @GetMapping(value = "/trainings", produces = "application/json")
    public ResponseEntity<List<TrainingResponseDTO>> getTrainings(
            @Parameter(description = "Filter by start date (yyyy-MM-dd)")
            @RequestParam(value = "periodFrom", required = false) String periodFrom,

            @Parameter(description = "Filter by end date (yyyy-MM-dd)")
            @RequestParam(value = "periodTo", required = false) String periodTo,

            @Parameter(description = "Filter by trainee name")
            @RequestParam(value = "traineeName", required = false) String traineeName,
            Authentication connectedUser) {

        return ResponseEntity.ok(trainerService.getTrainerTrainings(connectedUser, periodFrom, periodTo, traineeName));
    }

    @Operation(summary = "Update trainer's active status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer status updated"),
            @ApiResponse(responseCode = "400", description = "Invalid status update request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    @PatchMapping(value = "/status")
    public ResponseEntity<Void> trainerStatus(
            @Parameter(description = "New active status (true/false)", required = true)
            @RequestParam(value = "isActive") @NotNull(message = "isActive status must be provided") Boolean isActive,
            Authentication connectedUser) {

        trainerService.updateTrainerStatus(connectedUser, isActive);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete a trainer by username")
    @ApiResponse(responseCode = "200", description = "Trainer deleted successfully")
    @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    @DeleteMapping(value = "/delete", produces = "application/json")
    public ResponseEntity<Void> delete(Authentication connectedUser) {
        trainerService.deleteTrainer(connectedUser);

        return ResponseEntity.noContent().build();
    }

}
