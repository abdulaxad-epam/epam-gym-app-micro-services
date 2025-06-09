package epam.service;

import epam.dto.request_dto.TrainerRequestDTO;
import epam.dto.response_dto.RegisterTrainerResponseDTO;
import epam.dto.response_dto.TrainerResponseDTO;
import epam.entity.Trainer;
import epam.entity.TrainingType;
import epam.entity.User;
import epam.exception.exception.TrainerNotFoundException;
import epam.mapper.TrainerMapper;
import epam.mapper.TrainingMapper;
import epam.repository.TrainerRepository;
import epam.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private UserService userService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private Authentication auth;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        trainerRepository = mock(TrainerRepository.class);
        trainingTypeService = mock(TrainingTypeService.class);
        trainerMapper = mock(TrainerMapper.class);
        TrainingMapper trainingMapper = mock(TrainingMapper.class);
        userService = mock(UserService.class);
        trainingService = mock(TrainingService.class);
        trainerService = new TrainerServiceImpl(trainerRepository, trainingTypeService, trainerMapper, trainingMapper, userService, trainingService);
    }

    @Test
    void createTrainer_shouldSaveAndReturnResponse() {
        TrainerRequestDTO requestDTO = new TrainerRequestDTO();
        requestDTO.setSpecialization("Yoga");

        TrainingType trainingType = new TrainingType();
        Trainer trainer = new Trainer();
        RegisterTrainerResponseDTO responseDTO = new RegisterTrainerResponseDTO();

        when(trainingTypeService.getTrainingByTrainingName("Yoga")).thenReturn(trainingType);
        when(trainerMapper.toTrainer(requestDTO, trainingType)).thenReturn(trainer);
        when(trainerRepository.save(trainer)).thenReturn(trainer);
        when(trainerMapper.toRegisterTrainerResponseDTO(trainer)).thenReturn(responseDTO);

        RegisterTrainerResponseDTO result = trainerService.createTrainer(requestDTO);

        assertNotNull(result);
        verify(trainerRepository).save(trainer);
    }

    @Test
    void updateTrainer_shouldUpdateAndReturnDTO() {
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("trainerUser");

        TrainerRequestDTO requestDTO = new TrainerRequestDTO();
        requestDTO.setSpecialization("Pilates");

        TrainingType trainingType = new TrainingType();
        Trainer trainer = new Trainer();
        trainer.setUser(new User());

        when(trainerRepository.findTraineeByUser_Username("trainerUser")).thenReturn(Optional.of(trainer));
        when(trainingTypeService.getTrainingByTrainingName("Pilates")).thenReturn(trainingType);
        when(trainerMapper.toTrainerResponseDTO(trainer)).thenReturn(new TrainerResponseDTO());

        TrainerResponseDTO result = trainerService.updateTrainer(auth, requestDTO);

        assertNotNull(result);
        verify(trainerRepository).findTraineeByUser_Username("trainerUser");
        verify(trainingTypeService).getTrainingByTrainingName("Pilates");
    }

    @Test
    void updateTrainer_shouldThrowException_whenTrainerNotFound() {

        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("missingTrainer");

        when(trainerRepository.findTraineeByUser_Username("missingTrainer")).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class, () -> trainerService.updateTrainer(auth, new TrainerRequestDTO()));
    }

    @Test
    public void testGetTrainerByUsername_Success() {
        Trainer trainer = new Trainer();
        TrainerResponseDTO responseDTO = new TrainerResponseDTO();

        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("trainerUser");
        when(trainerRepository.findTraineeByUser_Username("trainerUser")).thenReturn(Optional.of(trainer));
        when(trainerMapper.toTrainerResponseDTO(trainer)).thenReturn(responseDTO);

        TrainerResponseDTO result = trainerService.getTrainerByUsername(auth);

        assertEquals(responseDTO, result);
    }

    @Test
    public void testGetTrainerByUsername_TrainerNotFound() {
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("trainerUser");
        when(trainerRepository.findTraineeByUser_Username("trainerUser")).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class, () -> trainerService.getTrainerByUsername(auth));
    }

    @Test
    public void testDeleteTrainer_TrainerNotFound() {
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("trainerUser");
        when(userService.findByUsername("trainerUser")).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class, () -> trainerService.deleteTrainer(auth));
    }

    @Test
    public void testUpdateTrainerStatus_TrainerNotFound() {
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("trainerUser");
        when(trainerRepository.findTraineeByUser_Username("trainerUser")).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class, () -> trainerService.updateTrainerStatus(auth, true));
    }

    @Test
    public void testGetTrainerTrainings_TrainerNotFound() {
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("trainerUser");
        when(trainerRepository.getTrainerTrainings(anyString(), any(), any(), anyString())).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class, () -> trainerService.getTrainerTrainings(auth, null, null, "John"));
    }

    @Test
    public void testCreateTrainer_ShouldReturnResponse() {
        TrainerRequestDTO requestDTO = new TrainerRequestDTO();
        Trainer trainer = new Trainer();
        RegisterTrainerResponseDTO expectedResponse = new RegisterTrainerResponseDTO();


        lenient().when(trainingTypeService.getTrainingByTrainingName(anyString())).thenReturn(null);
        when(trainerMapper.toTrainer(eq(requestDTO), any())).thenReturn(trainer);
        when(trainerRepository.save(trainer)).thenReturn(trainer);
        when(trainerMapper.toRegisterTrainerResponseDTO(trainer)).thenReturn(expectedResponse);

        RegisterTrainerResponseDTO response = trainerService.createTrainer(requestDTO);

        assertEquals(expectedResponse, response);
    }


    @Test
    void getTrainerByUsername_shouldReturnDTO_whenFound() {
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        Trainer trainer = new Trainer();

        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("trainerX");
        when(trainerRepository.findTraineeByUser_Username("trainerX")).thenReturn(Optional.of(trainer));
        when(trainerMapper.toTrainerResponseDTO(trainer)).thenReturn(new TrainerResponseDTO());

        TrainerResponseDTO result = trainerService.getTrainerByUsername(auth);

        assertNotNull(result);
        verify(trainerRepository).findTraineeByUser_Username("trainerX");
    }

    @Test
    void getTrainerByUsername_shouldThrowException_whenNotFound() {
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("notFound");

        when(trainerRepository.findTraineeByUser_Username("notFound")).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class, () -> trainerService.getTrainerByUsername(auth));
    }

    @Test
    void updateTrainerStatus_shouldUpdate_whenFound() {
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        Trainer trainer = new Trainer();
        User user = new User();
        trainer.setUser(user);

        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("trainerX");
        when(trainerRepository.findTraineeByUser_Username("trainerX")).thenReturn(Optional.of(trainer));

        trainerService.updateTrainerStatus(auth, true);

        assertTrue(trainer.getUser().getIsActive());
    }

    @Test
    void updateTrainerStatus_shouldThrowException_whenNotFound() {
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("ghost");
        when(trainerRepository.findTraineeByUser_Username("ghost")).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class, () -> trainerService.updateTrainerStatus(auth, true));
    }

}
