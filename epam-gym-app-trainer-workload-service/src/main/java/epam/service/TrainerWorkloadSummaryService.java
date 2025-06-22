package epam.service;

import epam.dto.TrainerWorkloadResponseDTO;
import epam.entity.TrainerWorkload;

public interface TrainerWorkloadSummaryService {

    void produce(TrainerWorkloadResponseDTO trainerWorkloadResponseDTO, boolean status);
}
