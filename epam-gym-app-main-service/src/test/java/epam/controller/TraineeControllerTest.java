package epam.controller;

import epam.dto.request_dto.UpdateTraineeRequestDTO;
import epam.dto.response_dto.TraineeResponseDTO;
import epam.dto.response_dto.TrainingResponseDTO;
import epam.service.TraineeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

class TraineeControllerTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TraineeController traineeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTraineeProfile_ShouldReturnTraineeResponse() {
        TraineeResponseDTO expectedResponse = new TraineeResponseDTO();
        when(traineeService.getTraineeProfile(authentication)).thenReturn(expectedResponse);

        ResponseEntity<TraineeResponseDTO> response = traineeController.getTraineeProfile(authentication);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void getTrainingByUsername_ShouldReturnTrainingList() {
        List<TrainingResponseDTO> expectedTrainings = Collections.singletonList(new TrainingResponseDTO());

        when(traineeService.getTraineeTrainings(
                any(), any(), any(), any(), eq(authentication)
        )).thenReturn(expectedTrainings);

        ResponseEntity<List<TrainingResponseDTO>> response = traineeController.getTrainingByUsername(
                "2024-01-01", "2024-12-31", "Trainer Name", "Cardio", authentication
        );

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedTrainings, response.getBody());
    }

    @Test
    void update_ShouldReturnUpdatedTrainee() {
        UpdateTraineeRequestDTO requestDTO = new UpdateTraineeRequestDTO();
        TraineeResponseDTO updatedResponse = new TraineeResponseDTO();

        when(traineeService.updateTrainee(authentication, requestDTO)).thenReturn(updatedResponse);

        ResponseEntity<TraineeResponseDTO> response = traineeController.update(requestDTO, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(updatedResponse, response.getBody());
    }

    @Test
    void delete_ShouldReturnOkStatus() {
        doNothing().when(traineeService).deleteTrainee(authentication);

        ResponseEntity<Void> response = traineeController.delete(authentication);

        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void updateTraineeStatus_ShouldReturnOkStatus() {
        Boolean isActive = true;
        doNothing().when(traineeService).updateTraineeStatus(authentication, isActive);

        ResponseEntity<Void> response = traineeController.updateTraineeStatus(isActive, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}
