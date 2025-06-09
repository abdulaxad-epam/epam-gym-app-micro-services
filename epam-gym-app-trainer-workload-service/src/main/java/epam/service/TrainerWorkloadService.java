package epam.service;

import epam.dto.TrainerWorkloadRequestDTO;
import epam.dto.TrainerWorkloadResponseDTO;
import epam.entity.TrainerWorkload;

import java.util.List;

public interface TrainerWorkloadService {
    TrainerWorkloadResponseDTO actionOn(TrainerWorkloadRequestDTO trainerWorkloadRequestDTO);

    List<TrainerWorkload> getTrainerWorkload(String trainerUsername, Integer year, Integer month);
}
