package epam.controller;

import epam.dto.request_dto.TrainerRequestDTO;
import epam.dto.response_dto.TrainerResponseDTO;
import epam.dto.response_dto.TrainingResponseDTO;
import epam.service.TrainerService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.List;

import static epam.dto.request_dto.RegisterTraineeRequestDTOTest.settings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrainerControllerTest {

    @Mock
    private TrainerService trainerService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TrainerController trainerController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetTrainerProfile_ShouldReturnTrainerResponseDTO() {
        TrainerResponseDTO expected = Instancio.of(TrainerResponseDTO.class).withSettings(settings).create();
        when(trainerService.getTrainerByUsername(authentication)).thenReturn(expected);

        ResponseEntity<TrainerResponseDTO> response = trainerController.getTrainer(authentication);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    public void testUpdateTrainer_ShouldReturnUpdatedTrainerResponseDTO() {
        TrainerRequestDTO requestDTO = Instancio.of(TrainerRequestDTO.class).withSettings(settings).create();
        TrainerResponseDTO expectedResponse = Instancio.of(TrainerResponseDTO.class).withSettings(settings).create();

        when(trainerService.updateTrainer(authentication, requestDTO)).thenReturn(expectedResponse);

        ResponseEntity<TrainerResponseDTO> response = trainerController.updateTrainer(requestDTO, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    public void testGetTrainerTrainings_ShouldReturnTrainingList() {
        TrainingResponseDTO training = Instancio.of(TrainingResponseDTO.class).withSettings(settings).create();
        List<TrainingResponseDTO> expectedTrainings = List.of(training);

        when(trainerService.getTrainerTrainings(authentication, "2024-01-01", "2024-12-31", "John"))
                .thenReturn(expectedTrainings);

        ResponseEntity<List<TrainingResponseDTO>> response = trainerController.getTrainings("2024-01-01", "2024-12-31", "John", authentication);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedTrainings, response.getBody());
    }

    @Test
    public void testGetTrainerTrainings_ShouldReturnEmptyList() {
        when(trainerService.getTrainerTrainings(authentication, null, null, null)).thenReturn(Collections.emptyList());

        ResponseEntity<List<TrainingResponseDTO>> response = trainerController.getTrainings(null, null, null, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(Collections.emptyList(), response.getBody());
    }

    @Test
    public void testUpdateTrainerStatus_ShouldReturnOk() {
        Boolean isActive = Boolean.TRUE;

        ResponseEntity<Void> response = trainerController.trainerStatus(isActive, authentication);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testDeleteTrainer_ShouldReturnNoContent() {
        ResponseEntity<Void> response = trainerController.delete(authentication);

        assertEquals(204, response.getStatusCode().value());
        verify(trainerService, times(1)).deleteTrainer(authentication);
    }

}
