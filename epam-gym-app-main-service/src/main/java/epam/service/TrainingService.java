package epam.service;


import epam.dto.response_dto.TrainingRequestDTO;
import epam.dto.response_dto.TrainingResponseDTO;
import epam.entity.Training;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface TrainingService {

    TrainingResponseDTO createTraining(TrainingRequestDTO training);

    String deleteTraining(UUID trainingId);

    String deleteTraining(Training training, String username);


    List<Training> getTrainerTrainings(String username);
}
