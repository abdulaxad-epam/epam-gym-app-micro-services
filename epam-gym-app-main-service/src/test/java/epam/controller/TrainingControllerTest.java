package epam.controller;

import epam.dto.response_dto.TrainingRequestDTO;
import epam.dto.response_dto.TrainingResponseDTO;
import epam.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TrainingControllerTest {

    private TrainingController trainingController;

    @Mock
    private TrainingService trainingService;

    @Mock
    private Authentication authentication;

    private TrainingResponseDTO trainingResponse;
    private TrainingRequestDTO trainingRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainingController = new TrainingController(trainingService);

        trainingResponse = new TrainingResponseDTO();
        trainingRequest = new TrainingRequestDTO();
    }

    @Test
    public void testCreateTraining_ShouldReturnCreatedTraining() {
        when(trainingService.createTraining(trainingRequest)).thenReturn(trainingResponse);

        ResponseEntity<TrainingResponseDTO> response =
                trainingController.createTraining(trainingRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(trainingResponse, response.getBody());
    }

    @Test
    public void testDeleteTraining_ShouldReturnSuccessMessage() {
        UUID trainingId = UUID.randomUUID();
        String expectedMessage = "Training removed successfully";

        when(trainingService.deleteTraining(trainingId)).thenReturn(expectedMessage);

        ResponseEntity<String> response =
                trainingController.deleteTraining(trainingId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedMessage, response.getBody());
    }
}
