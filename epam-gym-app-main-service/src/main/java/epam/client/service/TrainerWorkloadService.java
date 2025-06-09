package epam.client.service;

import epam.client.dto.TrainerWorkloadResponseDTO;
import epam.client.dto.TrainerWorkloadSummaryResponseDTO;
import epam.entity.Training;
import org.springframework.security.core.Authentication;

public interface TrainerWorkloadService {
    TrainerWorkloadResponseDTO actionOnADD(Training training);


    TrainerWorkloadResponseDTO actionOnDELETE(Training training);

    TrainerWorkloadSummaryResponseDTO getTrainerWorkloadSummary(Integer year, Integer month, Authentication connectedUser);
}
