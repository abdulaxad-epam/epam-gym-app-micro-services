package epam.service;

import epam.dto.TrainerWorkloadRequestDTO;
import epam.dto.TrainerWorkloadResponseDTO;
import epam.enums.ActionType;

public interface TrainerWorkloadService {
    TrainerWorkloadResponseDTO actionOn(TrainerWorkloadRequestDTO trainerWorkloadRequestDTO);

    TrainerWorkloadResponseDTO actionOnADD(TrainerWorkloadRequestDTO trainerWorkloadRequestDTO);

    TrainerWorkloadResponseDTO actionOnDELETE(TrainerWorkloadRequestDTO requestDTO);

    TrainerWorkloadResponseDTO updateExistingTrainerWorkload(TrainerWorkloadRequestDTO trainerWorkloadRequestDTO, ActionType actionType);

    TrainerWorkloadResponseDTO updateTrainer();
}
