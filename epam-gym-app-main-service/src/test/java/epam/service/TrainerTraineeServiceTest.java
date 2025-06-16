package epam.service;

import epam.dto.response_dto.TrainerResponseDTO;
import epam.entity.Trainee;
import epam.entity.Trainer;
import epam.entity.TrainerTrainee;
import epam.entity.User;
import epam.exception.exception.TraineeHasAssignedBeforeException;
import epam.exception.exception.TraineeNotFoundException;
import epam.mapper.TrainerMapper;
import epam.repository.TraineeRepository;
import epam.repository.TrainerRepository;
import epam.repository.TrainerTraineeRepository;
import epam.service.impl.TrainerTraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrainerTraineeServiceTest {

    private TrainerTraineeRepository trainerTraineeRepository;
    private TraineeRepository traineeRepository;
    private TrainerRepository trainerRepository;
    private TrainerMapper trainerMapper;
    private TrainerTraineeServiceImpl service;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        trainerTraineeRepository = mock(TrainerTraineeRepository.class);
        traineeRepository = mock(TraineeRepository.class);
        trainerRepository = mock(TrainerRepository.class);
        trainerMapper = mock(TrainerMapper.class);
        userDetails = mock(UserDetails.class);

        service = new TrainerTraineeServiceImpl(trainerTraineeRepository, traineeRepository, trainerRepository, trainerMapper);
    }

    @Test
    void assignTrainerToTrainee_shouldAssignSuccessfully() {
        String traineeUsername = "trainee1";
        String trainerUsername = "trainer1";

        Trainee trainee = new Trainee();
        trainee.setTraineeId(UUID.randomUUID());
        User traineeUser = new User();
        traineeUser.setUsername(traineeUsername);
        trainee.setUser(traineeUser);

        Trainer trainer = new Trainer();
        trainer.setTrainerId(UUID.randomUUID());
        User trainerUser = new User();
        trainerUser.setUsername(trainerUsername);
        trainer.setUser(trainerUser);

        when(traineeRepository.findTraineeByUser_Username(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findTrainerByUser_Username(trainerUsername)).thenReturn(Optional.of(trainer));
        when(trainerTraineeRepository.existsById_TrainerIdAndId_TraineeId(any(UUID.class), any(UUID.class))).thenReturn(false);

        service.assignTrainerToTrainee(traineeUsername, trainerUsername);

        verify(trainerTraineeRepository).save(any(TrainerTrainee.class));
    }

    @Test
    void assignTrainerToTrainee_shouldThrowIfAlreadyAssigned() {
        String traineeUsername = "trainee1";
        String trainerUsername = "trainer1";

        Trainee trainee = new Trainee();
        trainee.setTraineeId(UUID.randomUUID());
        User traineeUser = new User();
        traineeUser.setUsername(traineeUsername);
        trainee.setUser(traineeUser);

        Trainer trainer = new Trainer();
        trainer.setTrainerId(UUID.randomUUID());
        User trainerUser = new User();
        trainerUser.setUsername(trainerUsername);
        trainer.setUser(trainerUser);

        when(traineeRepository.findTraineeByUser_Username(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findTrainerByUser_Username(trainerUsername)).thenReturn(Optional.of(trainer));
        when(trainerTraineeRepository.existsById_TrainerIdAndId_TraineeId(any(UUID.class), any(UUID.class))).thenReturn(true);

        assertThrows(TraineeHasAssignedBeforeException.class,
                () -> service.assignTrainerToTrainee(traineeUsername, trainerUsername));
    }

    @Test
    void updateTraineeTrainer_shouldReplaceAssignments() {
        Authentication auth = mock(Authentication.class);
        User user = new User();
        user.setUsername("trainee1");

        when(auth.getPrincipal()).thenReturn(user);

        Trainee trainee = new Trainee();
        trainee.setTraineeId(UUID.randomUUID());
        trainee.setUser(user);

        Trainer trainer = new Trainer();
        trainer.setTrainerId(UUID.randomUUID());

        User trainerUser = User.builder().username("trainer1").build();
        trainer.setUser(trainerUser);


        when(userDetails.getUsername()).thenReturn("trainee1");
        when(auth.getPrincipal()).thenReturn(userDetails);

        when(traineeRepository.findTraineeByUser_Username("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findTrainerByUser_Username("trainer1")).thenReturn(Optional.of(trainer));

        TrainerResponseDTO dto = new TrainerResponseDTO();
        when(trainerMapper.toTrainerResponseDTO(trainer)).thenReturn(dto);

        List<TrainerResponseDTO> response = service.updateTraineeTrainer(auth, List.of("trainer1"));

        assertEquals(1, response.size());
        verify(trainerTraineeRepository).removeTrainerTraineeByTrainee_User_Username("trainee1");
        verify(trainerTraineeRepository).save(any(TrainerTrainee.class));
    }

    @Test
    void getAllNotAssignedTrainers_shouldReturnMappedList() {
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("trainee1");
        when(traineeRepository.existsTraineeByUser_Username("trainee1")).thenReturn(true);

        Trainer trainer = new Trainer();
        when(trainerTraineeRepository.findByUsernameNotAssignedToTrainee("trainee1")).thenReturn(List.of(trainer));

        TrainerResponseDTO dto = new TrainerResponseDTO();
        when(trainerMapper.toTrainerResponseDTO(trainer)).thenReturn(dto);

        List<TrainerResponseDTO> result = service.getAllNotAssignedTrainers(auth);

        assertEquals(1, result.size());
        verify(trainerMapper).toTrainerResponseDTO(trainer);
    }

    @Test
    void getAllNotAssignedTrainers_shouldThrowWhenTraineeNotFound() {
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("trainee1");
        when(traineeRepository.existsTraineeByUser_Username("trainee1")).thenReturn(false);

        assertThrows(TraineeNotFoundException.class,
                () -> service.getAllNotAssignedTrainers(auth));
    }
}
