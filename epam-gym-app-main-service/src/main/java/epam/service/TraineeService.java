package epam.service;

import epam.dto.request_dto.TraineeRequestDTO;
import epam.dto.request_dto.UpdateTraineeRequestDTO;
import epam.dto.response_dto.RegisterTraineeResponseDTO;
import epam.dto.response_dto.TraineeResponseDTO;
import epam.dto.response_dto.TrainingResponseDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface TraineeService {

    RegisterTraineeResponseDTO createTrainee(TraineeRequestDTO trainee);

    void deleteTrainee(Authentication connectedUser);

    TraineeResponseDTO getTraineeProfile(Authentication connectedUser);

    List<TrainingResponseDTO> getTraineeTrainings(String periodFrom, String periodTo, String trainerName, String trainingType, Authentication connectedUser);

    void updateTraineeStatus(Authentication connectedUser, Boolean isActive);

    TraineeResponseDTO updateTrainee(Authentication connectedUser, UpdateTraineeRequestDTO updateTrainee);
}
