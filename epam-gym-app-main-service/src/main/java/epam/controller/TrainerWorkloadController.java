package epam.controller;

import epam.client.dto.TrainerWorkloadSummaryResponseDTO;
import epam.client.service.TrainerWorkloadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/trainer-workload")
@Tag(name = "Trainer-workload", description = "Operations related to trainers workload")
public class TrainerWorkloadController {
    private final TrainerWorkloadService trainerWorkloadService;

    @GetMapping
    public ResponseEntity<?> getTrainerWorkloadSummary(
            @RequestParam(value = "year") Integer year,
            @RequestParam(value = "month") Integer month,
            Authentication connectedUser
    ) {
        TrainerWorkloadSummaryResponseDTO trainerWorkloadSummary =
                trainerWorkloadService.getTrainerWorkloadSummary(year, month, connectedUser);
        return trainerWorkloadSummary == null
                ? ResponseEntity.ok("Trainer workload summary not found")
                : ResponseEntity.ok(trainerWorkloadSummary);
    }
}
