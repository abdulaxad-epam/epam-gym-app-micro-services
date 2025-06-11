package epam.service;

import epam.dto.TrainerWorkloadSummaryResponseDTO;

public interface TrainerWorkloadSummaryService {
    TrainerWorkloadSummaryResponseDTO getTrainerWorkloadSummary(String trainerUsername, Integer year, Integer month);

    void produce(String username, Integer year, Integer month);
}
