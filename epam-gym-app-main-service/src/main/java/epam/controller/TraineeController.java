package epam.controller;

import epam.dto.request_dto.UpdateTraineeRequestDTO;
import epam.dto.response_dto.TraineeResponseDTO;
import epam.dto.response_dto.TrainingResponseDTO;
import epam.service.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping("api/v1/trainees")
@RequiredArgsConstructor
@Tag(name = "Trainee", description = "Operations related to trainees")
public class TraineeController {

    private final TraineeService traineeService;


    @Operation(summary = "Get trainee details by username")
    @ApiResponse(responseCode = "200", description = "Trainee details retrieved successfully",
            content = @Content(schema = @Schema(implementation = TraineeResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    @GetMapping(value = "/profile", produces = "application/json")
    public ResponseEntity<TraineeResponseDTO> getTraineeProfile(Authentication connectedUser) {

        return ResponseEntity.ok(traineeService.getTraineeProfile(connectedUser));
    }

    @Operation(summary = "Get trainings for a trainee by username with optional filters")
    @ApiResponse(responseCode = "200", description = "List of trainings retrieved",
            content = @Content(schema = @Schema(implementation = TrainingResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    @GetMapping(value = "/trainings", produces = "application/json")
    public ResponseEntity<List<TrainingResponseDTO>> getTrainingByUsername(
            @RequestParam(value = "periodFrom", required = false) String periodFrom,
            @RequestParam(value = "periodTo", required = false) String periodTo,
            @RequestParam(value = "trainerName", required = false) String trainerName,
            @RequestParam(value = "trainingType", required = false) String trainingType,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(traineeService.getTraineeTrainings(periodFrom, periodTo, trainerName, trainingType, connectedUser));
    }

    @Operation(summary = "Update trainee details")
    @ApiResponse(responseCode = "200", description = "Trainee updated successfully",
            content = @Content(schema = @Schema(implementation = TraineeResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    @PutMapping(value = "/update", produces = "application/json")
    public ResponseEntity<TraineeResponseDTO> update(
            @RequestBody @Valid UpdateTraineeRequestDTO requestDTO,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(traineeService.updateTrainee(connectedUser, requestDTO));
    }

    @Operation(summary = "Delete a trainee by username")
    @ApiResponse(responseCode = "200", description = "Trainee deleted successfully")
    @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    @DeleteMapping(value = "/delete", produces = "application/json")
    public ResponseEntity<Void> delete(Authentication connectedUser) {
        traineeService.deleteTrainee(connectedUser);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update active status of a trainee")
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid status update request", content = @Content)
    @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    @PatchMapping(value = "/status")
    public ResponseEntity<Void> updateTraineeStatus(
            @RequestParam(value = "isActive") @NotNull(message = "isActive status must be provided") Boolean isActive,
            Authentication connectedUser) {

        traineeService.updateTraineeStatus(connectedUser, isActive);

        return ResponseEntity.ok().build();
    }
}
