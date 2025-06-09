package epam.controller;

import epam.dto.TrainerWorkloadSummaryResponseDTO;
import epam.service.TrainerWorkloadSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/trainer-workload-summary")
public class TrainerWorkloadSummaryController {
    private final TrainerWorkloadSummaryService trainerWorkloadSummaryService;

    @GetMapping
    public ResponseEntity<TrainerWorkloadSummaryResponseDTO> getTrainerWorkloadSummary(
            @RequestParam(value = "trainerUsername") String trainerUsername,
            @RequestParam(value = "year") Integer year,
            @RequestParam(value = "month") Integer month
    ) {
        return ResponseEntity.ok(trainerWorkloadSummaryService.getTrainerWorkloadSummary(trainerUsername, year, month));
    }
}
