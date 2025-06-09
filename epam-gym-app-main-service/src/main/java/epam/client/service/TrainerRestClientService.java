package epam.client.service;

import epam.client.dto.TrainerWorkloadRequestDTO;
import epam.client.dto.TrainerWorkloadResponseDTO;
import epam.client.dto.TrainerWorkloadSummaryResponseDTO;
import org.springframework.http.ResponseEntity;

public interface TrainerRestClientService {


    ResponseEntity<TrainerWorkloadResponseDTO> postTrainingAction(String url, TrainerWorkloadRequestDTO dto, String token);

    ResponseEntity<TrainerWorkloadSummaryResponseDTO> getTrainingSummary(String url, String token);
}
