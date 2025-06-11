package epam.client.messaging.consumer;

import epam.client.dto.TrainerWorkloadSummaryResponseDTO;

public interface TrainerWorkloadSummaryMessageConsumer {
    TrainerWorkloadSummaryResponseDTO getTrainerWorkloadSummary(String trainerUsername, Integer year, Integer month);

}
