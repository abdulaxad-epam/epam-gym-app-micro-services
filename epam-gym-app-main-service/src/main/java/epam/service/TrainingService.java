package epam.service;


import epam.dto.request_dto.TrainingRequestDTO;
import epam.dto.response_dto.TrainingResponseDTO;
import epam.entity.Training;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface TrainingService {

    TrainingResponseDTO createTraining(TrainingRequestDTO training, Authentication authentication);

    String deleteTraining(UUID trainingId, Authentication authentication);

    String deleteTraining(Training training, String username);


    List<Training> getTrainerTrainings(String username);
}
