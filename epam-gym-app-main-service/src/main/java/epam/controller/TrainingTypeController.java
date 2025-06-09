package epam.controller;

import epam.dto.response_dto.TrainingTypeResponseDTO;
import epam.service.TrainingTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/training-types")
@Tag(name = "Training Types", description = "Endpoints for retrieving available training types")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    @Operation(summary = "Get all available training types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of training types retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingTypeResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<TrainingTypeResponseDTO>> getTrainingTypes() {
        return ResponseEntity.ok(trainingTypeService.findAll());
    }
}
