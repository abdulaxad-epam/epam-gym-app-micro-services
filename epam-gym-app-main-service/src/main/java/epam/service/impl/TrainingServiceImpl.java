package epam.service.impl;

import epam.client.dto.TrainerWorkloadResponseDTO;
import epam.client.service.TrainerWorkloadService;
import epam.dto.request_dto.TrainingRequestDTO;
import epam.dto.response_dto.TrainingResponseDTO;
import epam.entity.Training;
import epam.exception.exception.DailyTrainingDurationExceededException;
import epam.exception.exception.TraineeNotFoundException;
import epam.exception.exception.TrainerNotFoundException;
import epam.exception.exception.TrainingNotFoundException;
import epam.mapper.TrainingMapper;
import epam.repository.TraineeRepository;
import epam.repository.TrainerRepository;
import epam.repository.TrainingRepository;
import epam.service.TraineeTrainerService;
import epam.service.TrainingService;
import epam.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainingMapper trainingMapper;
    private final TrainingTypeService trainingTypeService;

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    private final TrainerWorkloadService trainerWorkloadService;

    private final TraineeTrainerService traineeTrainerService;

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
//    @PreAuthorize("hasRole('TRAINER')")
    public TrainingResponseDTO createTraining(TrainingRequestDTO trainingRequestDTO) {

        Training training = trainingMapper.toTraining(
                trainingRequestDTO,
                trainingTypeService.getTrainingByTrainingName(trainingRequestDTO.getTrainingType()),
                trainerRepository.findTrainerByUser_Username(trainingRequestDTO.getTrainerUsername())
                        .orElseThrow(
                                () -> new TrainerNotFoundException("Trainer with username " + trainingRequestDTO.getTrainerUsername() + " not found")
                        ),
                traineeRepository.findTraineeByUser_Username(trainingRequestDTO.getTraineeUsername())
                        .orElseThrow(
                                () -> new TraineeNotFoundException("Trainee with username " + trainingRequestDTO.getTraineeUsername() + " not found")
                        )
        );

        traineeTrainerService.assignTrainerToTrainee(trainingRequestDTO.getTraineeUsername(), trainingRequestDTO.getTrainerUsername());

        TrainerWorkloadResponseDTO trainerWorkloadResponseDTO = trainerWorkloadService.actionOnADD(training);
        log.info("Trainer workload response on ADD: {}", trainerWorkloadResponseDTO);
        trainingRepository.save(training);

        return trainingMapper.toTrainingResponseDTO(training);
    }

    @Override
    @Transactional
//    @PreAuthorize("hasRole('TRAINER')")
    public String deleteTraining(UUID trainingId) {

        return trainingRepository.findTrainingByTrainingId(trainingId)
                .map(this::tripleDeletion).orElseThrow(
                        () -> new TrainingNotFoundException("Training with training id " + trainingId + " not found")
                );
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('TRAINER')")
    public String deleteTraining(Training training, String username) {
        return tripleDeletion(training);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('TRAINER')")
    @Override
    public List<Training> getTrainerTrainings(String username) {
        return trainingRepository.findTrainingsByTrainer_User_Username(username);
    }

    private String tripleDeletion(Training training) {
        TrainerWorkloadResponseDTO trainerWorkloadResponseDTO = trainerWorkloadService.actionOnDELETE(training);
        log.info("Trainer workload response on DELETE: {}", trainerWorkloadResponseDTO);
        traineeTrainerService.unassignTrainerFromTrainee(training);
        trainingRepository.deleteTrainingByTrainingId(training.getTrainingId());
        return "Training with id " + training.getTrainingId() + " deleted successfully";
    }
}