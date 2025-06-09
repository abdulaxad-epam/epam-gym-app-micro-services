package epam.service;

import epam.dto.request_dto.TraineeRequestDTO;
import epam.dto.request_dto.UpdateTraineeRequestDTO;
import epam.dto.response_dto.RegisterTraineeResponseDTO;
import epam.dto.response_dto.TraineeResponseDTO;
import epam.dto.response_dto.TrainingResponseDTO;
import epam.entity.Trainee;
import epam.entity.Training;
import epam.entity.User;
import epam.exception.exception.TraineeNotFoundException;
import epam.mapper.TraineeMapper;
import epam.mapper.TrainingMapper;
import epam.repository.TraineeRepository;
import epam.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainingService trainingService;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateTraineeSuccessfully() {
        TraineeRequestDTO dto = new TraineeRequestDTO();
        Trainee trainee = new Trainee();
        RegisterTraineeResponseDTO response = new RegisterTraineeResponseDTO();

        when(traineeMapper.toTrainee(dto)).thenReturn(trainee);
        when(traineeRepository.save(trainee)).thenReturn(trainee);
        when(traineeMapper.toRegisterTraineeResponseDTO(trainee)).thenReturn(response);

        RegisterTraineeResponseDTO result = traineeService.createTrainee(dto);

        assertNotNull(result);
        verify(traineeRepository).save(trainee);
    }

    @Test
    void shouldUpdateTraineeSuccessfully() {
        UpdateTraineeRequestDTO dto = new UpdateTraineeRequestDTO();
        dto.setFirstname("John");
        dto.setLastname("Doe");
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        dto.setAddress("Some Address");
        dto.setIsActive(true);

        User user = new User();
        user.setUsername("john");
        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("john");
        when(traineeRepository.findTraineeByUser_Username("john")).thenReturn(Optional.of(trainee));

        TraineeResponseDTO responseDTO = new TraineeResponseDTO();
        when(traineeMapper.toTraineeResponseDTO(trainee)).thenReturn(responseDTO);

        TraineeResponseDTO result = traineeService.updateTrainee(authentication, dto);

        assertNotNull(result);
        assertEquals(responseDTO, result);
    }

    @Test
    void shouldThrowWhenTraineeNotFoundInUpdate() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("john");
        when(traineeRepository.findTraineeByUser_Username("john")).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class,
                () -> traineeService.updateTrainee(authentication, new UpdateTraineeRequestDTO()));
    }

    @Test
    void shouldDeleteTraineeSuccessfully() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("john");
        when(traineeRepository.existsTraineeByUser_Username("john")).thenReturn(true);

        doNothing().when(traineeRepository).deleteTraineeByUser_Username("john");

        traineeService.deleteTrainee(authentication);

        verify(traineeRepository).deleteTraineeByUser_Username("john");
    }

    @Test
    void shouldThrowWhenDeletingNonExistingTrainee() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("john");
        when(traineeRepository.existsTraineeByUser_Username("john")).thenReturn(false);

        assertThrows(TraineeNotFoundException.class, () -> traineeService.deleteTrainee(authentication));
    }

    @Test
    void shouldGetTraineeProfileSuccessfully() {
        User user = new User();
        user.setUsername("john");
        Trainee trainee = new Trainee();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("john");
        when(traineeRepository.findTraineeByUser_Username("john")).thenReturn(Optional.of(trainee));

        TraineeResponseDTO responseDTO = new TraineeResponseDTO();
        when(traineeMapper.toTraineeResponseDTO(trainee)).thenReturn(responseDTO);

        TraineeResponseDTO result = traineeService.getTraineeProfile(authentication);

        assertNotNull(result);
        assertEquals(responseDTO, result);
    }

    @Test
    void shouldUpdateTraineeStatusSuccessfully() {
        User user = new User();
        user.setUsername("john");
        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("john");
        when(traineeRepository.findTraineeByUser_Username("john")).thenReturn(Optional.of(trainee));

        traineeService.updateTraineeStatus(authentication, true);

        assertTrue(trainee.getUser().getIsActive());
    }

    @Test
    void shouldThrowWhenUpdatingStatusForNonExistingTrainee() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("john");
        when(traineeRepository.findTraineeByUser_Username("john")).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class, () -> traineeService.updateTraineeStatus(authentication, true));
    }

    @Test
    void shouldGetTraineeTrainingsSuccessfully() {
        String username = "john";
        List<Training> trainingList = List.of(new Training());
        List<TrainingResponseDTO> trainingDTOs = List.of(new TrainingResponseDTO());

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);
        when(traineeRepository.getTraineeTrainings(username, LocalDateTime.of(LocalDate.parse("2024-01-01"),
                LocalTime.MIDNIGHT), LocalDateTime.of(LocalDate.parse("2024-12-31"), LocalTime.MAX), "Trainer", "Fitness"))
                .thenReturn(Optional.of(trainingList));
        when(trainingMapper.toTrainingResponseDTO(any())).thenReturn(trainingDTOs.get(0));

        List<TrainingResponseDTO> result = traineeService.getTraineeTrainings("2024-01-01", "2024-12-31", "Trainer", "Fitness", authentication);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowWhenGettingTrainingsForNonExistingTrainee() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("john");
        when(traineeRepository.getTraineeTrainings("john", LocalDateTime.of(LocalDate.parse("2024-01-01"), LocalTime.MIDNIGHT),
                LocalDateTime.of(LocalDate.parse("2024-12-31"), LocalTime.MAX), "Trainer", "Fitness"))
                .thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class, () -> traineeService.getTraineeTrainings("2024-01-01", "2024-12-31", "Trainer", "Fitness", authentication));
    }
}