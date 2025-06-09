package epam.controller;

import epam.dto.response_dto.TrainerResponseDTO;
import epam.service.TraineeTrainerService;
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
import static org.mockito.Mockito.when;

public class TraineeTrainerControllerTest {

    @Mock
    private TraineeTrainerService traineeTrainerService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TraineeTrainerController traineeTrainerController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAssignedTrainers_ShouldReturnNoContent() {
        when(traineeTrainerService.getAllNotAssignedTrainers(authentication)).thenReturn(List.of());

        ResponseEntity<List<TrainerResponseDTO>> response =
                traineeTrainerController.getNotAssignedTrainers(authentication);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(Collections.emptyList(), response.getBody());
    }

    @Test
    public void testGetAssignedTrainers_ShouldReturnTraineeResponse() {
        // Given
        TrainerResponseDTO expectedResponse =
                Instancio.of(TrainerResponseDTO.class).withSettings(settings).create();

        // When
        when(traineeTrainerService.getAllNotAssignedTrainers(authentication))
                .thenReturn(List.of(expectedResponse));

        ResponseEntity<List<TrainerResponseDTO>> response =
                traineeTrainerController.getNotAssignedTrainers(authentication);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertEquals(List.of(expectedResponse), response.getBody());
    }

    @Test
    public void testUpdateTraineeTrainer_ShouldReturnUpdatedTrainerList() {
        // Given
        List<String> trainerUsernames = List.of("trainer1", "trainer2");
        TrainerResponseDTO responseDTO = Instancio.of(TrainerResponseDTO.class).withSettings(settings).create();
        List<TrainerResponseDTO> expectedResponse = List.of(responseDTO);

        // When
        when(traineeTrainerService.updateTraineeTrainer(authentication, trainerUsernames))
                .thenReturn(expectedResponse);

        ResponseEntity<List<TrainerResponseDTO>> response =
                traineeTrainerController.updateTraineeTrainerList(trainerUsernames, authentication);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
    }
}
