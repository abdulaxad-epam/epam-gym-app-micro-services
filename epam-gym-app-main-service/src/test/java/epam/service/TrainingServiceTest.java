package epam.service;

import epam.client.dto.TrainerWorkloadResponseDTO;
import epam.client.service.TrainerWorkloadService;
import epam.dto.response_dto.TrainingRequestDTO;
import epam.dto.response_dto.TrainingResponseDTO;
import epam.entity.Trainee;
import epam.entity.Trainer;
import epam.entity.Training;
import epam.entity.User;
import epam.exception.exception.TraineeNotFoundException;
import epam.exception.exception.TrainerNotFoundException;
import epam.exception.exception.TrainingNotFoundException;
import epam.mapper.TrainingMapper;
import epam.repository.TraineeRepository;
import epam.repository.TrainerRepository;
import epam.repository.TrainingRepository;
import epam.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class TrainingServiceTest {

    private TrainingServiceImpl trainingService;

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TrainingMapper trainingMapper;
    @Mock
    private TrainingTypeService trainingTypeService;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainerWorkloadService trainerWorkloadService;
    @Mock
    private TraineeTrainerService traineeTrainerService;

    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;

    private final String trainerUsername = "trainerUser";
    private final String traineeUsername = "traineeUser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainingService = new TrainingServiceImpl(
                trainingRepository,
                trainingMapper,
                trainingTypeService,
                traineeRepository,
                trainerRepository,
                trainerWorkloadService,
                traineeTrainerService
        );
    }

    @Test
    public void testCreateTraining_ShouldReturnTrainingResponseDTO() {
        TrainingRequestDTO requestDTO = new TrainingRequestDTO();
        requestDTO.setTraineeUsername(traineeUsername);
        requestDTO.setTrainerUsername(trainerUsername);
        requestDTO.setTrainingType("Yoga");

        Trainer trainer = new Trainer();
        trainer.setUser(new User());
        Trainee trainee = new Trainee();
        trainee.setUser(new User());
        Training training = new Training();
        TrainingResponseDTO responseDTO = new TrainingResponseDTO();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(trainerUsername);

        when(trainerRepository.findTrainerByUser_Username(trainerUsername)).thenReturn(Optional.of(trainer));
        when(traineeRepository.findTraineeByUser_Username(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainingMapper.toTraining(eq(requestDTO), any(), eq(trainer), eq(trainee))).thenReturn(training);
        when(trainerWorkloadService.actionOnADD(training)).thenReturn(new TrainerWorkloadResponseDTO());
        when(trainingMapper.toTrainingResponseDTO(training)).thenReturn(responseDTO);

        TrainingResponseDTO result = trainingService.createTraining(requestDTO);

        assertEquals(responseDTO, result);
        verify(trainingRepository).save(training);
        verify(traineeTrainerService).assignTrainerToTrainee(traineeUsername, trainerUsername);
    }

    @Test
    public void testDeleteTraining_WithValidId_ShouldReturnSuccessMessage() {
        UUID trainingId = UUID.randomUUID();
        Training training = new Training();
        training.setTrainingId(trainingId);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(trainerUsername);
        when(trainingRepository.findTrainingByTrainingId(trainingId))
                .thenReturn(Optional.of(training));

        when(trainerWorkloadService.actionOnDELETE(training)).thenReturn(new TrainerWorkloadResponseDTO());

        String result = trainingService.deleteTraining(trainingId);

        assertEquals("Training with id " + trainingId + " deleted successfully", result);
        verify(traineeTrainerService).unassignTrainerFromTrainee(training);
        verify(trainingRepository).deleteTrainingByTrainingId(trainingId);
    }

    @Test
    public void testDeleteTraining_WithInvalidId_ShouldThrowException() {
        UUID trainingId = UUID.randomUUID();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(trainerUsername);
        when(trainingRepository.findByTrainingIdAndTrainer_User_Username(trainingId, trainerUsername))
                .thenReturn(Optional.empty());

        assertThrows(TrainingNotFoundException.class, () ->
                trainingService.deleteTraining(trainingId));
    }

    @Test
    public void testDeleteTraining_ByEntity_ShouldSucceed() {
        Training training = new Training();
        training.setTrainingId(UUID.randomUUID());

        when(trainerWorkloadService.actionOnDELETE(training)).thenReturn(new TrainerWorkloadResponseDTO());

        String result = trainingService.deleteTraining(training, trainerUsername);

        assertEquals("Training with id " + training.getTrainingId() + " deleted successfully", result);
        verify(traineeTrainerService).unassignTrainerFromTrainee(training);
        verify(trainingRepository).deleteTrainingByTrainingId(training.getTrainingId());
    }

    @Test
    public void testGetTrainerTrainings_ShouldReturnList() {
        List<Training> trainings = List.of(new Training());

        when(trainingRepository.findTrainingsByTrainer_User_Username(trainerUsername)).thenReturn(trainings);

        List<Training> result = trainingService.getTrainerTrainings(trainerUsername);

        assertEquals(trainings, result);
    }


    @Test
    public void testCreateTraining_ShouldThrowTrainerNotFoundException() {
        TrainingRequestDTO requestDTO = new TrainingRequestDTO();
        requestDTO.setTraineeUsername("traineeUser");
        requestDTO.setTrainerUsername(trainerUsername);
        requestDTO.setTrainingType("Yoga");

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(trainerUsername);
        when(trainerRepository.findTrainerByUser_Username(trainerUsername)).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class, () ->
                trainingService.createTraining(requestDTO)
        );

        verify(trainerRepository).findTrainerByUser_Username(trainerUsername);
        verifyNoInteractions(traineeRepository, trainingMapper, trainingRepository, trainerWorkloadService);
    }

    @Test
    public void testCreateTraining_ShouldThrowTraineeNotFoundException() {
        TrainingRequestDTO requestDTO = new TrainingRequestDTO();
        requestDTO.setTraineeUsername(traineeUsername);
        requestDTO.setTrainerUsername(trainerUsername);
        requestDTO.setTrainingType("Yoga");

        Trainer trainer = new Trainer();
        trainer.setUser(new User());

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(trainerUsername);
        when(trainerRepository.findTrainerByUser_Username(trainerUsername)).thenReturn(Optional.of(trainer));
        when(traineeRepository.findTraineeByUser_Username(traineeUsername)).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class, () ->
                trainingService.createTraining(requestDTO)
        );

        verify(trainerRepository).findTrainerByUser_Username(trainerUsername);
        verifyNoInteractions(trainingMapper, trainingRepository, trainerWorkloadService);
    }
}
